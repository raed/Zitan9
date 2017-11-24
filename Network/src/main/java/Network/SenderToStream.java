package Network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/** This class can be used to send information via sockets to a client which then puts it into a stream.
 * The send-method sends only if the receiver signals readyness.
 *
 */
public class SenderToStream<T extends Serializable> {
    /** the address of the other side */
    private  InetAddress address;
    /** the port number of the other side */
    private int port;
    /** the client socket */
    private Socket socket = null;
    /** the corresponding output stream */
    private ObjectOutputStream out;
    /** the corresponding input stream */
    private InputStream in;

    /**
     * constructs a SenderToStream for the given host address and port.
     * The other side is not yet connected.
     *
     * @param address the address of the other side.
     * @param port the port from which the sender sends.
     */
    SenderToStream(InetAddress address, int port) {
        this.address = address;
        this.port = port;}

    /** connects the sender to the other side.
     *
     * @throws IOException if the connection attempt fails.
     */
    public void connect() throws IOException {
        try{
            if(socket!= null) {socket.close();}
            socket = new Socket(address,port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = socket.getInputStream();}
        catch(IOException ex) {
            if(socket != null) {
                try{socket.close();}catch(IOException e){}
                out = null;
                in = null;
                socket = null;}
            throw ex;}}

    /** sends an object through the socket to the other side.
     * The method waits until the other side has sent an integer.
     * After receiving a positive integer, the object is written into the socket.
     * If the other sides sends a negative integer the connection is closed.
     *
     * @param object the object to be sent.
     * @return true if the object was sent.
     * @throws IOException if something goes wrong.
     */
    public boolean send(T object) throws IOException {
        //System.out.println("SEND " + object);
        if(socket == null || socket.isClosed()) {return false;}
        int i = in.read(); // wait until the receiver is ready to read something.
        if(i < 0) {socket.close(); return false;}
        else {out.writeObject(object);
            out.flush();
            return true;}}

    /** closes the corresponding socket
     */
    public void close() {
        try{socket.close();} catch(IOException e) {}}

}


