package Network.Queries;

import Concepts.DataCarrier;
import MISC.Commons;
import MISC.Context;
import Network.StreamsAndSockets;
import Utils.Messanger;
import Utils.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Stream;

// Noch keine ordentliche Fehlerbehandlung

/** This is a server for remotely accessing attributes of concepts.<br>
 * The server works as follows:<br>
 * It creates a ServerSocket for listing at the given port.<br>
 * As soon as the ServerSocket is connected, it starts a new thread for processing the query.<br>
 * A query must be an instance of a subclass of the Query-class, for example AttributeQuery.<br>
 * The answer can be returned in two ways:<br>
 * 1. If the answer type is DIRECT, the answer is returned immediately at the same socket.<br>
 * 2. If the answer type is STREAM, a new port number is returned, and a new ServerSocket is bound to the
 * answer-stream. The new ServerSocket waits in a new thread for accessing the stream.
 *
 * Created by ohlbach on 27.02.2016.
 */
public class QueryServer {
    /**the context object for answering the query*/
    private Context context = null;
    private ServerSocket serverSocket;
    /**
     * creates a new AccessServer
     *
     * @param context the context object for answering the queries
     * @param serverSocket where the server waits for connections.
     */
    public QueryServer(Context context, ServerSocket serverSocket) {
        this.context = context;
        this.serverSocket = serverSocket;
    }

    /**
     * closes the server socket and causes the server to be stopped
     */
    public void stop() {
        try {serverSocket.close();} catch (IOException e) {}
    }

    /** waits for connections and processes them
     *
     * @return the thread which runs the server.
     */
    public Thread start() {
        return Utilities.runServer(serverSocket,(socket->processQueries(socket)));}

    /**
     * processes a connection.
     * The method processes queries until the "stop" string is read from the socket.
     *
     * @param socket the socket where the queries are to be read from
     */

    private void processQueries(Socket socket) {
        Messanger messangerIO      = Commons.getMessanger(Messanger.MessangerType.IOErrors);
        Messanger messangerData    = Commons.getMessanger(Messanger.MessangerType.DataErrors);
        Messanger messangerProgram = Commons.getMessanger(Messanger.MessangerType.ProgramErrors);
        ObjectInputStream inStream;
        ObjectOutputStream outStream;
        try {inStream  = new ObjectInputStream(socket.getInputStream());
             outStream = new ObjectOutputStream(socket.getOutputStream());}
        catch(IOException ex) {
            messangerIO.insert("Socket",ex.toString());
            return;}

        while (!socket.isClosed()) {
            Object object = null;
            try{object = inStream.readObject();}
            catch(Exception ex) {
                messangerIO.insert("Object Reconstruction",ex.toString());
                writeObject(outStream,"Error: " + ex.toString(),messangerIO);
                continue;}
            if(object.equals("stop")) {break;}
            if(object instanceof DataCarrier) {
                String errors = ((DataCarrier)object).addToContext(context);
                if(errors != null) {messangerData.insert("DataOld Carrier",errors);}
                continue;}
            if(!(object instanceof Query)) {
                messangerProgram.insert("Query Reconstruction",  object.toString() + " is not a query.");
                writeObject(outStream,"Error: " + object.toString() + " is not a query.",messangerIO);
                continue;}
            Query query = (Query) object;
            Object answer = query.answer(context);
            if(answer == null) {writeObject(outStream,null,messangerIO); continue;}

            if(query.error) {
                messangerData.insert("Query Evaluation",(String)answer);
                writeObject(outStream,"Error: " + answer,messangerIO); continue;}

            switch (query.answerType) {
                case DIRECT:
                    if(answer instanceof Stream) {
                        ArrayList list = new ArrayList<>();
                        ((Stream)answer).forEach(item -> list.add(item));
                        writeObject(outStream,list,messangerIO);}
                    else {writeObject(outStream,answer,messangerIO);}
                    break;
                case STREAM:
                    Stream answerStream = (answer instanceof Stream) ? (Stream)answer : Stream.of(answer);
                    ServerSocket streamSocket = Utilities.createServerSocket(serverSocket.getLocalPort() + 1);
                    if(writeObject(outStream,streamSocket.getLocalPort(),messangerIO)) {
                        Thread thread = new Thread(() -> {
                            try {
                                Socket outSocket = streamSocket.accept();
                                StreamsAndSockets.stream2Socket(answerStream, outSocket, null);}
                            catch (Exception ex) {System.err.println(ex.getMessage());}});
                        thread.start();}}}
        }

    private boolean writeObject(ObjectOutputStream out, Object object, Messanger messanger) {
        try{out.writeObject(object); return true;}
        catch(Exception ex) {
            messanger.insert("writeObject",ex.toString());
            return false;}}
}
