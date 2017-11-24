
package Network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** This class provides static methods for connecting streams via sockets.
 * One can connect a sending stream to a receiving stream.
 *
 */
public class StreamsAndSockets {
    
    /** The method connects a stream to a socket.
     * In order to enable lazy access to the stream, the next item from the stream is sent only after
     * the other side of the socket has sent a flag.
     * 
     * @param <T> the type of the stream's elements. The elements must be serializable
     * @param stream the stream to be sent into a socket.
     * @param socket the socket to which the stream is to be connected
     * @param onClose a piece of code to be run after closing the socket.
     * @throws Exception if the connection fails.
     */
    public static <T extends Serializable> void stream2Socket(Stream<T> stream, Socket socket, Runnable onClose) throws Exception {
        //System.out.println("Sender Start at " + socket.getLocalPort());
        ObjectOutputStream  out = new ObjectOutputStream(socket.getOutputStream());
        InputStream in = socket.getInputStream();
        Exception[] exception = new Exception[]{null};
        stream.forEach((T object) -> {
            try{int i = in.read();// wait until you are asked to send something.
                if(i < 0) {return;}}
            catch(IOException ex) {return;}
            try{
                out.writeObject(object);
                out.flush();}
            catch(Exception e){exception[0] = e;};});
        stream.close();
        socket.close();
        if(onClose != null) {onClose.run();}
        if(exception[0] != null) {throw exception[0];}}
    
    /** This method turns a socket into a stream.
     * The stream reads from the socket until it receives null.
     * 
     * @param <T> the type of the items sent through the socket
     * @param socket the socket for receiving objects which are sent to the stream.
     * @return a socket which reads from the stream. 
     * @throws IOException if the connection fails.
     */
    public static<T extends Serializable>  Stream<T> socket2Stream(Socket socket) throws IOException {
        //System.out.println("Receiver start at port " + socket.getLocalPort());
        OutputStream      out = socket.getOutputStream();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        InIterator  iterator = new InIterator(out,in);
        Stream<T> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,0),false);
        return stream.onClose(()->{try{socket.close();}catch(IOException ex){}});}
     
    /** This iterator is the basis for the stream which reads from the socket.
     * 
     * @param <T> The type of objects sent through the socket. 
     */
    private static class  InIterator<T extends Serializable> implements Iterator {
        private final OutputStream out;
        private final ObjectInputStream in;
        private T value;
        
        public InIterator(OutputStream out, ObjectInputStream in) {
            this.out = out; this.in = in;}
        
        @Override
        public boolean hasNext() {
            try{
                out.write(1);out.flush(); // send a message 'I am ready to receive something'.
                value = (T)in.readObject();
                if(value == null) {out.close(); in.close();return false;} // stop when null is read.
                return true;}
            catch(EOFException ex) {return false;}
            catch(Exception e) {e.printStackTrace();} // this is to be improved
            return false;}

        @Override
        public Object next() {return value;}
    }


    /** This method establishes a service for listening at some port, transforming the incoming messages and sending them to some other port.
     * The method waits for connections and starts a thread for each connection.
     * The waiting can be stopped by closing the serverSocket.
     *
     * @param serverSocket the socket where the service waits for connections.
     * @param remotePort the port number where to send the results of the transformation.
     * @param function the function that transforms the data
     * @param <In> type of the incoming data.
     * @param <Out> type of the outgoing data.
     * @throws Exception if some transmission error occurs.
     */

    public static <In extends Serializable,Out extends Serializable> void
    computeService(ServerSocket serverSocket,int remotePort, Function function) throws Exception {
        while(!serverSocket.isClosed()) {
            Socket inSocket = serverSocket.accept();
            new Thread(() -> {
                try {
                    Stream<In> inStream = socket2Stream(inSocket).map(function);
                    Socket outSocket = new Socket(inSocket.getInetAddress(), remotePort);
                    stream2Socket(inStream, outSocket, null);
                } catch (Exception ex) {
                    System.out.println(ex);
                }}).start();}
    }

}
