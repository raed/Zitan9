package Network.Registries;

import MISC.Commons;
import MISC.Context;
import Network.Queries.QueryServer;
import Utils.Messanger;
import Utils.Utilities;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/** A registry is a QueryServer with its own context.
 * It can in particular be used to store pointers to hosts which provide information about particular concepts and attributes.
 *
 * Created by ohlbach on 02.03.16.
 */
public class Registry extends QueryServer {
    public String id;

    public static PrintStream systemOut = System.out;
    public static String parameters = "<identifier> [help] port = <port> monitor = <in|port> logging = <logging> file = <file>";

    /** creates a new registry
     *
     * @param id       an identifier for the registry
     * @param context  a context object
     * @param serverSocket the serverSocket where the registry waits for clients.
     */
    public Registry(String id, Context context, ServerSocket serverSocket) {
        super(context,serverSocket);
        this.id = id;}

    /** the main method reads certain parameters,
     * and then starts a registry and, if necessary a monitor.
     * Error messages generated during the start phase are printed to systemOut
     *
     * @param args so far unused
     */
    public static void main(String[] args) {
        args = new String[]{"A","port" ,"=", "4445", "monitor", "=", "5556"};
        runRegistry(args);}

    /** the runRegistry method reads certain parameters,
     * and then starts a registry and, if necessary a monitor.
     * Error messages generated during the start phase are printed to systemOut
     *
     * @param args
     */
    private static void runRegistry(String[] args) {
        int length = args.length;
        String id = null;
        int port = 3333;
        int monitorPort = 0;
        boolean monitorInStream = false;
        for(int i = 0; i < length; ++i) {
            String arg = args[i];
            if(arg.equals("help")) {help(null); return;}

            if(arg.equals("port")) {
                if(i >= length-2) {help("no port number");return;}
                if(!args[i+1].equals("=")) {help("no port number");return;}
                try{port = Integer.parseInt(args[i+2]);}
                catch(Exception ex) {
                    systemOut.println(ex.toString());
                    help(null);
                    return;}
                i += 2;
                continue;}

            if(arg.equals("monitor")) {
                if (i >= length - 2) {help("no monitor specification");return;}
                if (!args[i + 1].equals("=")) {help("no monitor specification");return;}
                if(args[i+2].equals("in")) {monitorInStream = true;}
                else{try{monitorPort = Integer.parseInt(args[i+2]);}
                    catch(Exception ex) {
                        systemOut.println(Utilities.stackTrace(ex));
                        help("");
                        return;}}
                i += 2;
                continue;}

            if(arg.equals("logging")) {
                // to be implemented
                continue;}

            if(arg.equals("file")) {
                // to be implemented, Daten vom File lesen
                continue;}
            if(id != null) {help("Unknown parameter " + arg); return;}
            id = arg;}
        if(id == null) {id = "Registry";}

        ServerSocket ss = null;
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            ss = serverSocket;
            Context context = new Context("Registry");
            initializeContext(context);
            Registry registry = new Registry(id,context,serverSocket);
            pushMessangers(registry.id);
            registry.startMonitor(monitorInStream,monitorPort);
            Thread thread = registry.start();
            Runtime.getRuntime().addShutdownHook(new Thread(()->shutDownHook(thread, serverSocket)));
            systemOut.println("Registry " + id + " is up");
        }
        catch(Exception ex) {systemOut.println(Utilities.stackTrace(ex));}
        finally{try{ss.close();}catch(Exception ex){}}
    }

    /** pushes all the messangers.
     *
     * @param id an identifier.
     */
    public static void pushMessangers(String id) {
        Commons.getMessanger(Messanger.MessangerType.DataErrors).push(id);
        Commons.getMessanger(Messanger.MessangerType.ProgramErrors).push(id);
        Commons.getMessanger(Messanger.MessangerType.IOErrors).push(id);
    }

    /** starts the monitor in a new thread, if necessary.
     *
     * @param monitorInStream if true then System.in and System.out are to be used.
     * @param monitorPort if != 0 then a new ServerSocket at thsi port is to be created.
     */
    private void startMonitor(boolean monitorInStream, int monitorPort)  {
        new Thread(()->{
            try{
                if(monitorInStream) {
                    systemOut.println("Monitor is up");
                    runMonitor(new ObjectInputStream(System.in),new ObjectOutputStream(System.out));}
                else {
                    if(monitorPort != 0) {
                        systemOut.println("Monitor is up");
                        ServerSocket monitorSocket = null;
                        Socket socket = null;
                        try{
                            monitorSocket = new ServerSocket(monitorPort);
                            socket = monitorSocket.accept();
                            runMonitor(new ObjectInputStream(socket.getInputStream()), new ObjectOutputStream(socket.getOutputStream()));}
                        finally{
                            try{socket.close();
                                monitorSocket.close();}
                            catch(Exception ex){}}
                    }}}
            catch(Exception ex) {systemOut.print(Utilities.stackTrace(ex));}}).start();
            }

    /** This method runs the monitor.
     * It reads from the ObjectInputStream and writes to the ObjectOutputStream. <br>
     * The following commands are understood:
     * - "stop"  stops the registry
     * - "errors" writes the collected errors to out
     * - "dump"   makes the context persistent (not yet implemented).
     *
     * @param in from where it reads
     * @param out to which it writes
     */
    private void runMonitor(ObjectInputStream in, ObjectOutputStream out) {
        Object object = null;
        while(true) {
          try {
              object = in.readObject();
              if(object.equals("stop")) {
                  stop();
                  out.writeObject("done");
                  return;}
              if(object.equals("errors")) {
                  out.writeObject(errors(false));
                  continue;}
              if(object.equals("dump")) {
                  // Status sichern
                  continue;
              }
          }
          catch(Exception ex) {systemOut.println(Utilities.stackTrace(ex));}
          }
    }

    /** collects the error messages in a string.
     *
     * @param pop if true then the messages are popped from the stack, otherwise peek is used.
     * @return null or the collected error messages.
     */
    public static String errors(boolean pop) {
        StringBuilder s = new StringBuilder();
        Messanger dataErrors = Commons.getMessanger(Messanger.MessangerType.DataErrors);
        Messanger.Messages messages = pop ? dataErrors.pop() : dataErrors.peek();
        if(!messages.isEmpty()) {s.append("DataOld Errors:\n").append(messages.toString()).append("\n");}
        Messanger programErrors = Commons.getMessanger(Messanger.MessangerType.ProgramErrors);
        messages =  pop ? programErrors.pop() : programErrors.peek();
        if(!messages.isEmpty()) {s.append("Program Errors:\n").append(messages.toString()).append("\n");}
        Messanger ioErrors = Commons.getMessanger(Messanger.MessangerType.IOErrors);
        messages = pop ? ioErrors.pop() :ioErrors.peek();
        if(!messages.isEmpty()) {s.append("IO Errors:\n").append(messages.toString()).append("\n");}
        String errors = s.toString();
        return errors.isEmpty() ? null : errors;}


    /** prints information about the input parameters to System.out
     */
    private static void help(String error) {
        if(error != null && !error.isEmpty()) {System.out.println("Error: " + error);}
        System.out.println("Parameters for starting the registry:");
        System.out.println(parameters);}


    /** closes the serverSocket, waits for the thread to join and then writes
     * the collected error messages to systemOut
     *
     * @param thread  the main thread in the registry
     * @param serverSocket the server Socket.
     */
    private static void shutDownHook(Thread thread, ServerSocket serverSocket) {
        try{
            serverSocket.close();
            thread.join();}
        catch(Exception ex) {}
        String errors = errors(true);
        if(errors != null) {systemOut.println(errors);}}

    /** creates key concepts and attributes.
     * In particular it creates:<br>
     *     1. a concept "Abstract_Host"<br>
     *     2. an attribute "abstract_host" (functional)<br>
     *     3. an attribute "physical_hosts" (relational)
     *     4. an attribute chain "hosts".
     *
     * @param context where the attributes are inserted.
     */
    public static void initializeContext(Context context) {
       /* IndividualConcept abstractHost = new IndividualConcept("Abstract_Host",context);
        ConcreteType physicalHostType = ConcreteType.getConcreteType("PhysicalHost", context);
        ConceptAttribute abstractHosts = new ConceptAttribute("abstract_host",true,null,abstractHost);
        DataAttribute physicalHosts = new DataAttribute("physical_hosts",false,null,null);
        context.putAttribute(abstractHosts);
        context.putAttribute(physicalHosts);
        ArrayList<Attribute> chain = new ArrayList<>(2);
        chain.add(abstractHosts); chain.add(physicalHosts);
        ChainAttribute hosts = new ChainAttribute("hosts",chain);
        context.putAttribute(hosts);
        */
    }




}
