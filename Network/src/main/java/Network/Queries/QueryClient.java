package Network.Queries;
import MISC.Commons;
import Utils.Messanger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static Network.StreamsAndSockets.socket2Stream;

/** This is the client side for sending queries over the network to a QueryServer.
 *
 * Created by ohlbach on 01.04.2016.
 */
public class QueryClient {
    /** the server's address */
    private InetAddress serverAddress;
    /** the client socket */
    private Socket clientSocket;
    /** the socket's output stream */
    private ObjectOutputStream out;
    /** the socket's input stream */
    private ObjectInputStream in;
    /** indicates errors during processing of a query */
    public boolean error = false;

    /** constructs a new client.
     * One can send several queries via the same client.
     *
     * @param serverAddress the server's address.
     * @param serverPort    the server's port.
     * @throws IOException  if the communication fails.
     */
    public QueryClient(InetAddress serverAddress, int serverPort) throws IOException {
        this.serverAddress = serverAddress;
        clientSocket = new Socket(this.serverAddress,serverPort);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    /** sends a query to the server.
     * All errors are recorded in the messangers.
     * In addition the error flag is set if an error occurred.
     *
     * @param query the query to be sent
     * @return the answer, either an object, or a stream.
     */
    public Object sendQuery(Query query){
        // hier logging einbauen
        assert !query.error;
        error = false;
        Messanger messangerIO      = Commons.getMessanger(Messanger.MessangerType.IOErrors);
        Messanger messangerProgram = Commons.getMessanger(Messanger.MessangerType.ProgramErrors);

        if(!writeObject(out,query,messangerIO)) {return null;}
        Object result = null;

        try{result = in.readObject();}
        catch(IOException ex) {
            error = true;
            messangerIO.insert("reading object",ex.toString());
            return null;}
        catch(ClassNotFoundException ex) {
            messangerProgram.insert("reconstructing object",ex.toString());
            return null;}

        if((result instanceof String) && ((String) result).startsWith("Error")) {
            error = true;
            Commons.getMessanger(Messanger.MessangerType.DataErrors).
                    insert("query server", (String)result);
            return null;}

        switch(query.answerType) {
            case DIRECT: return result;
            case STREAM:
                if(!(result instanceof Integer)) {
                    error = true;
                    messangerProgram.insert("Stream-Query","port number " + result.toString() + " is not an integer");
                    return null;}
                try {
                    Socket socket = new Socket(serverAddress,(int)(Integer)result);
                    return socket2Stream(socket);}
                catch(IOException ex) {
                    error = true;
                    messangerIO.insert("stream2Socket",ex.toString());
                    return null;}}
        return null;}

    /** closes the connection to the server.
     */
    public void stop() {
        writeObject(out,"stop",Commons.getMessanger(Messanger.MessangerType.IOErrors));
        try{clientSocket.close();} catch(Exception ex) {};}

    private boolean writeObject(ObjectOutputStream out, Object object, Messanger messanger) {
        try{out.writeObject(object);
            return true;}
        catch(Exception ex) {
            error = true;
            messanger.insert("writeObject",ex.toString());
            return false;}}
}
