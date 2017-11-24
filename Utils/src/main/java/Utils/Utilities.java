
package Utils;

// import AbstractObjects.Interpretation;
// import MISC.Commons;
import groovy.lang.GroovyShell;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static Utils.Messanger.MessangerType.*;

/** Some useful functions
 *
 */
public class Utilities {

    public static final Comparator<LocalDateTime> timeComparator =
            ((time1,time2)-> {
                if(time1.isBefore(time2)) {return -1;}
                if(time1.equals(time2)) {return 0;}
                return 1;});

    private static GroovyShell shell = new GroovyShell();

    public static boolean equalNumbers(Number n1, Number n2) {
        Class class1 = n1.getClass();
        Class class2 = n2.getClass();
        Class ic = Integer.class;
        if(class1 == class2 || class1 == ic || class2 == ic) {return n1 == n2;}
        return n1.toString().equals(n2.toString());}

    public static boolean equal(float n1, double n2) {
        if(n1 == n2) {return true;}
        return Float.toString(n1).equals(Double.toString(n2));}


    public static double toDouble(float f) {
        return Double.parseDouble(Float.toString(f));}

/*
    public static void  main(String[] args) {
        float f = 3.001f;
        double d = 3.001d;
        System.out.println((double)f);
        System.out.println(toDouble(f));
        System.out.println(d == toDouble(f));
        System.out.println(d == (double)f);
    }
*/

    public static boolean equal(double n1, float n2) {
        if(n1 == n2) {return true;}
        return Double.toString(n1).equals(Float.toString(n2));}


    /** casts the number to an Integer, if this is possible.
     *
     * @param n the number to be casted
     * @return either the casted Integer, or null if the number is not an integer.
     */
    public static Integer toInt(Number n) {
        if(n instanceof Integer) {return (Integer)n;}
        if(n instanceof Float)   {return (int)(float)n == (float)(Float)n ? (int)(float)n : null;}
        if(n instanceof Double)  {return (int)(double)n == (double)(Double)n ? (int)(double)n : null;}
        return null;}

    public static boolean isInt(float n) {return (int)n == n;}
    public static boolean isInt(double n) {return (int)n == n;}

    public static boolean isInt(Float n) {return (int)(float)n == (float)n;}
    public static boolean isInt(Double n) {return (int)(double)n == (double)n;}



    /** joins a collection of items into a string, by applying a given function and inserting a separator.
     *
     * @param <T>   the item type
     * @param collection a collection of T-items
     * @param function   a function that turns items to a string
     * @param separator  any string
     * @return           a string consisting 'separator'-separated function(item) strings.
     */
    public static <T> String join(Collection<T> collection, String separator, Function<T,String> function) {
        if(collection == null) {return "";}
        StringBuilder s = new StringBuilder();
        for(T item : collection) {
            s.append(function.apply(item)).append(separator);}
        int l = s.length();
        if(l == 0) {return "";}
        return s.replace(l-separator.length(), l, "").toString();}

    public static String join(Object[] collection, String separator, Function<Object,String> function) {
        if(collection == null) {return "";}
        StringBuilder s = new StringBuilder();
        for(Object item : collection) {
            s.append(function.apply(item)).append(separator);}
        int l = s.length();
        return s.replace(l-separator.length(), l, "").toString();}


    /**Splits a string key1value1, key2value2,...
     * The keys are identified by the keyIdentifier.
     * The tuples are separated by the given separator surrounded by zero or more whitespaces.
     * The results are put into a hash map.
     * <br>
     * Example:
     * <pre>
     * {@code
     * </pre>"Ak = 5d , Absd < 7dsf ,  A > sdf" ->  {A=> sdf, Ak== 5d, Absd=< 7dsf}
     * if the key identifier is (key->key.startsWith("A"))
     *}
     * </pre>
     * @param string the string to be split
     * @param keyIdentifier the predicate that identifies the substring which are actually keys
     * @return a HashMap key-values.
     */
    public static HashMap<String,String> split(String string, String separator, Predicate<String> keyIdentifier) {
        HashMap<String,String> map = new HashMap<>();
        String[] parts = string.split("\\s*( |"+separator+")\\s*");
        int length = parts.length;
        if(!keyIdentifier.test(parts[0])) {return null;}
        String key = parts[0];
        int index1 = parts[0].length() + 1;
        for(int i = 1; i < length; ++i) {
            String part = parts[i];
            if(keyIdentifier.test(part)) {
                int index2 = string.indexOf(part,index1);
                String value = string.substring(index1,index2).trim();
                if(value.endsWith(",")) {value = value.substring(0,value.length()-1).trim();}
                map.put(key,value);
                key = part;
                index1 = index2+key.length()+1;}}
        map.put(key,string.substring(index1,string.length()));
        return map;
    }



    /** traverses a stream to find the first element for which the function application returns non-null
     *
     * @param <D> the data type in the stream
     * @param <T> the result type of the function
     * @param stream the stream to be traversed.
     * @param function the function to be applied.
     * @return the first non-null function application, or null if there is none.
     */
    public static <D,T> T find(Stream<D> stream, Function<D,T> function) {
        if(stream == null) {return null;}
        Object[] found = new Object[1];
        found[0] = null;
        stream.anyMatch(item -> {
            T value = function.apply(item);
            if(value == null) {return false;}
            found[0] = value;
            return true;});
        return (T)found[0];}

    /** seaches for a pair of opening and closing parentheses
     *
     * @param s      the string to be checked
     * @param index  the start index for the search
     * @param openpar the opening parenthesis
     * @param closepar the closing parenthesis
     * @return the index of the closing parenthesis, or -1 of there is none.
     */
    public static int findClosingParenthesis(String s, int index, char openpar, char closepar) {
        int length = s.length();
        int counter = 0;
        boolean found = false;
        for(int i = index; i < length; ++i) {
            char c = s.charAt(i);
            if(c == openpar) {++counter; found = true; continue;}
            if(c == closepar) {--counter;}
            if(counter == 0 && found) {return i;}}
        return -1;}

    /** removes comments from a string
     *
     * @param text the string to be cleaned from comments
     * @param openComment the opening comment string
     * @param closeComment the closing comment string
     * @return the cleaned string
     */
    public static String removeComments(String text, String openComment, String closeComment) {
        int closeLength = closeComment.length();
        int pos1 = text.indexOf(openComment);
        if(pos1 < 0) {return text;}
        int pos2 = text.indexOf(closeComment,pos1);
        while(pos2 >= 0) {
            text = text.substring(0,pos1) + text.substring(pos2+closeLength,text.length());
            pos1 = text.indexOf(openComment);
            if(pos1 < 0) {return text;}
            pos2 = text.indexOf(closeComment,pos1);}
        return text;}

    /** splits a comma or blank separated string into its components.
     * The components can be just strings without comma or blank,
     * or they can be equations applicationName = value.
     * The value can either be just a string or an arbitrary text enclosed in parentheses.
     *
     * @param text the text to be parsed
     * @param openpar the opening parenthesis
     * @param closepar the closing parenthesis
     * @return a Map applicationName - value. If there was no value on the string, then the map contains the empty string as value for the applicationName.
     */
    public static HashMap<String,String> parseString(String text, char openpar, char closepar) {
        int scriptCounter = 1;
        HashMap<String,String> map = new HashMap();
        int index1 = 1;
        int index2 = 0;
        while(true) {
            index1 = text.indexOf(openpar,index1);
            if(index1 < 0) {break;}
            index2 = findClosingParenthesis(text,index1,openpar,closepar);
            if(index2 < 0) {break;}
            String key = "@@@"+scriptCounter++;
            map.put(key,text.substring(index1+1,index2));
            text = text.substring(0,index1)+key+text.substring(index2+1,text.length());
            index1++;}
        String[] parts = text.split("(\\s|,)+");
        HashMap<String,String> result = new HashMap();
        int length = parts.length;
        for(int i = 0; i < length; ++i) {
            String part = parts[i];
            if(i < length-2 && parts[i+1].equals("=")) {
                result.put(part, map.containsKey(parts[i+2]) ? map.get(parts[i+2]) : parts[i+2]); i += 2; continue;}
            result.put(part,"");}
        return result;}

    /** This method parses a string intervalContaining hierarchy declarations.
     * The string starts with a applicationName of the hierarchy.
     * In the next lines there are declarations of the kind<br>
     * supernode : subnode1, subnode2, ....<br>
     * The subnodes can be distributed over several lines.
     * Two declarations must be separated by a blank line.
     *
     * @param <T>       the type of the store into which the hierarchy is to be inserted.
     * @param hierarchy the string with the hiuerarchy declarations
     * @param storing   a function mapping the applicationName of the hierarchy to the corresponding store
     * @param inserter  a consumer function: accept(store,supernode,subnode)
     */
    public static<T> void parseHierarchy(String hierarchy,Function<String,T> storing, TriConsumer<T,String,String> inserter) {
        String[] parts = hierarchy.split("\\s+",2);
        if(parts.length < 2) {return;}
        T store = storing.apply(parts[0].trim());
        for(String declaration : parts[1].split("\\n\\n+")) {
            parts = declaration.trim().split("\\s*(:|,)\\s*");
            String supernode = parts[0];
            for(int i = 1; i < parts.length; ++i) {inserter.accept(store,supernode,parts[i]);}}}

    /** tries to find a class with the given applicationName.
     * The method hides the ClassNotFoundException.
     *
     * @param prefix usually a package prefix
     * @param name   the main part of the class' applicationName
     * @param postfix optionally a trailer for the applicationName
     * @return the class, or null if there is none with this applicationName.
     */
    public static Class getClass(String prefix, String name, String postfix) {
        Class clazz = null;
        try{clazz = Class.forName(prefix+name+postfix);}
        catch(ClassNotFoundException ex) {}
        return clazz;}

    /** parses a string into a Java object.
     * The class into which the string is to be parsed must have a static method parseString(String,Interpretation)
     *
     * @param string  the string to be parsed
     * @param clazz   the class with the static parseString(String,Interpretation)-method
     * @param interpretation the interpretations, or null if there is a parser without interpretation parameter
     * @return the parsed object, or null.
     */
    /*
    public static Object parseString(String string, Class clazz, Interpretation interpretation) {
        Method parser = null;
        try{parser = clazz.getDeclaredMethod("parseString", String.class);
            try {return parser.invoke(null,string);}
            catch (Exception e) {
                Commons.getMessanger(ProgramErrors).insert("Method Invocation Error",e.toString());
                return null;}}
        catch(Exception e) {
            try {parser = clazz.getDeclaredMethod("parseString", String.class, interpretation.getClass());}
            catch(NoSuchMethodException ex) {
                Commons.getMessanger(ProgramErrors).insert("Unknown Method","parseString in class " + clazz.getName());
                return null;}}
        try {return parser.invoke(null,string,interpretation);}
        catch (Exception e) {
            Commons.getMessanger(ProgramErrors).insert("Method Invocation Error",e.toString());}
        return null;}
*/


    /** creates a new ServerSocket with a port number 'startPort' or above
     *
     * @param startPort a port number.
     * @return a new ServerSocket
     */
    public static ServerSocket createServerSocket(int startPort) {
        for(int port = startPort; port < 64000; ++port) {
            try {return new ServerSocket(port);}
            catch(IOException ex) {}}
        return null;
    }


    /** evaluates the Groovy code
     *
     * @param code the code to be evaluated
     * @return the evaluated code, or null if an error occurred.
     */
    public static Object evaluate(String code)  {
        synchronized(shell) {
            try{return shell.evaluate(code);}
           catch(Exception ex) {
              //  Commons.getMessanger(DataErrors).insertCompilationError(code,ex);
           }
            return null;}}

    /** evaluates the Groovy code
     *
     * @param code the code to be evaluated
     * @return the evaluated code, or null if an error occurred.
     */
    public static Object evaluate(String code, StringBuilder errors)  {
        synchronized(shell) {
            try{return shell.evaluate(code);}
            catch(Exception ex) {errors.append(ex.toString());}
            return null;}}


    /** compiles a code-string into a one-place predicate
     *
     * @param predicate the code string, e.g. "i &gt; 5".
     * @param type  the argument type, e.g. Integer
     * @param parameter the applicationName of the argument.
     * @return the compiled predicate, or null if an error occurred.
     */
    public static Predicate compilePredicate(String predicate, String type, String parameter) {
        String pred = "new java.util.function.Predicate<"+type+">() {"+
                "public boolean test("+type +" "+ parameter + ") {" + predicate+"}}";
        synchronized(shell) {
            try{return (Predicate)shell.evaluate(pred);}
            catch(Exception ex) {
                System.out.println(ex);
           //  Commons.getMessanger(DataErrors).insertCompilationError(pred,ex);
            }
            return null;}}

    /** compiles a code-string into a one-place function
     ** @param imports optional import statements
     * @param code the code string, e.g. "i * i".
     * @param argType  the argument type, e.g. Integer
     * @param parameter the applicationName of the argument.* @param resultType the result type of the function application.
     * @return the compiled function or null if an error occurred.
     */
    public static Function compileFunction(String imports, String code, String argType, String parameter, String resultType, StringBuilder errors, int lineNumber) {
        String function = imports + " new java.util.function.Function<"+argType+","+resultType+"> () {"+
                "public " + resultType +" apply("+argType +" "+ parameter + ") {" + code+"}}";
        synchronized(shell) {
            try{return (Function)shell.evaluate(function);}
            catch(Exception ex) {errors.append("Line " + lineNumber + " error when compiling " + function +"\n "+ex.toString());}
            return null;}}



    /** compiles a code-string into a two-place function
     *
     * @param imports optional import statements
     * @param code the code string, e.g. "i * i".
     * @param argType1  the first argument type, e.g. Integer
     * @param parameter1 the applicationName of the first argument.
     * @param argType2  the second argument type, e.g. Integer
     * @param parameter2 the applicationName of the second argument.
     * @param resultType the result type of the function application.
     * @return the compiled function or null if an error occurred.
     */
    public static BiFunction compileFunction(String imports, String code, String argType1, String parameter1, String argType2, String parameter2, String resultType, StringBuilder errors, int lineNumber) {
        String function = imports + " new java.util.function.BiFunction<"+argType1+","+argType2+","+resultType+">() {"+
                "public " + resultType +" apply("+argType1 +" "+ parameter1 + ","+argType2 +" "+ parameter2 +") {" + code+"}}";
        synchronized(shell) {
            try{return (BiFunction)shell.evaluate(function);}
            catch(Exception ex) {errors.append("Line " + lineNumber + " error when compiling " + function +"\n "+ex.toString());}
            return null;}}


    /** runs a server at the given port.<br>
     * It opens a server socket at a given port and starts a new thread.
     * The thread waits for connections.
     * For each connection it starts another thread and applies the serverProcess to this socket.
     * <br>
     * The loop can be stopped  by closing the returned serverSocket.
     * <br>
     * The method returns immediately whereas the thread runs indefinitely.
     *
     * @param serverSocket the serverSocket where the server waits
     * @param serverProcess the consumer function to be applied to the socket.
     * @return the thread which serves the server
     */
    public static Thread runServer(ServerSocket serverSocket, Consumer<Socket> serverProcess) {
        Thread serverThread = new Thread(()-> {
            try{
                while (!serverSocket.isClosed()) {
                    Socket socket;
                    try {socket = serverSocket.accept();}
                    catch (SocketException ex) {
                        return;}
                    Thread thread = new Thread(() -> {
                        serverProcess.accept(socket);
                        try{socket.close();}catch(Exception ex){}});
                    thread.start();}}
            catch (IOException ex) {
              //  Commons.getMessanger(IOErrors).insert("SocketError",stackTrace(ex));
            }
            finally {try {serverSocket.close();} catch (IOException e) {}}});
        serverThread.start();
        return serverThread;}

    /** This method can be used to test the serilization of a class.
     * It sends a serializable object through a socket and returns the deserialized version
     *
     * @param object the object to be serilized
     * @return the deserialized copy
     * @throws Exception if IO-Errors occur
     */
    public static Object testSerialization(Object object) throws Exception {
        int port = 3333;
        ServerSocket serverSocket = createServerSocket(port);
        InetAddress address = InetAddress.getLocalHost();
        Thread thread = new Thread(()-> {
            ObjectOutputStream out = null;
            try{
                Socket socket = new Socket(address,serverSocket.getLocalPort());
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(object);
                out.close();}
            catch(Exception ex) {System.out.println(ex.toString());}
            finally{try{out.close();}catch(Exception ex){}}});
        thread.start();
        ObjectInputStream in = null;
        Object result = null;
        try{
            Socket socket = serverSocket.accept();
            in = new ObjectInputStream(socket.getInputStream());
            result = in.readObject();}
        finally{in.close();}
        return result;}


    /** returns the exception's stack trace as a string.
     *
     * @param ex an exception
     * @return the stack trace as a string.
     */
    public static String stackTrace(Exception ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream s = new PrintStream(baos);
        ex.printStackTrace(s);
        return baos.toString();
    }

    /** parses a string as Integer and writes error messages into a messanger
     *
     * @param s the string to be parsed
     * @return the parsed Integer or null.
     */
    public static Integer parseInt(String s) {
        try{return Integer.parseInt(s);}
        catch(Exception ex) {
           // Commons.getMessanger(DataErrors).insert("Typo",ex.getMessage());
        }
        return null;}

    /** parses a string as Float and writes error messages into a messanger
     *
     * @param s the string to be parsed
     * @return the parsed Float or null.
     */
    public static Float parseFloat(String s) {
        try{return Float.parseFloat(s);}
        catch(Exception ex) {
         //   Commons.getMessanger(DataErrors).insert("Typo",ex.getMessage());
        }
        return null;}


    /** parses a string as Double and writes error messages into a messanger
     *
     * @param s the string to be parsed
     * @return the parsed Double or null.
     */
    public static Double parseDouble(String s) {
        try{return Double.parseDouble(s);}
        catch(Exception ex) {
          //  Commons.getMessanger(DataErrors).insert("Typo",ex.getMessage());
        }
        return null;}

    /** parses a string "int1,...,intn" to an Array
     *
     * @param <T> the type of the list items.
     * @param string the string to be parsed.
     * @param separator the separator for the items in the string
     * @param parser the parser for the items.
     * @return the parsed items in an ArrayList
     */
    public static <T> ArrayList<T> parseList(String string,String separator,Function<String,T> parser)  {
        String[] parts = string.split("\\s*"+separator+"\\s*");
        ArrayList<T> array = new ArrayList();
        boolean error = false;
        for(String part : parts) {
            T value = parser.apply(part);
            if(value == null) {error = true;}
            else{array.add(value);}}
        return error ? null : array;}

    /** checks whether the two lists are disjoint, given the equality tester.
     * An empty list is disjoint with everything
     *
     * @param list1 the first list
     * @param list2 the second list
     * @param equal the equality tester
     * @param <A> the item type of the first list
     * @param <B> the item type of the second list
     * @return true if the lists are disjoint
     */
    public static <A, B>  boolean isDisjoint(ArrayList<A> list1, ArrayList<B> list2, BiPredicate<A,B> equal) {
        if(list1 == null || list1.isEmpty() || list2 == null || list2.isEmpty()) {return true;}
        int size1 = list1.size();
        int size2 = list2.size();
        for(int i = 0; i < size1; ++i) {
            A item1 = list1.get(i);
            for(int j = 0; j < size2; ++j) {
                if(equal.test(item1,list2.get(j))) {return false;}}}
        return true;}

    /** checks if the interval [start1,end1[ is a subset of [start2,end2[
     *
     * @param start1 the start time of the first interval
     * @param end1   the end time of the first interval
     * @param start2 the start time of the second interval
     * @param end2   the end time of the second interval
     * @return true if the interval [start1,end1[ is a subset of [start2,end2[
     */
    public static boolean isSubset(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return (start2.isBefore(start1) || start2.equals(start1)) && (end1.isBefore(end2) || end1.equals(end2));}

    /** checks if the two intervals are disjoint.
     *
     * @param start1 start of the first half-open temporal interval
     * @param end1  end of the first  half-open temporal interval
     * @param start2 start of the second half-open temporal interval
     * @param end2  end of the second  half-open temporal interval
     * @return true if the two intervals are disjoint
     */
    public static boolean disjointWith(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return end1.isBefore(start2) || end1.equals(start2) || end2.isBefore(start1) || end2.equals(start1); }

    /** computes the intersection of two half-open intervals
     *
     * @param start1 start of the first half-open temporal interval
     * @param end1  end of the first  half-open temporal interval
     * @param start2 start of the second half-open temporal interval
     * @param end2  end of the second  half-open temporal interval
     * @return [start,end] of the intersection, or null if the intervals do not intersect.
     */
    public static LocalDateTime[] intersection(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        if(start1.isBefore(start2)) {start1 = start2;}
        if(end2.isBefore(end1)) {end1 = end2;}
        return start1.isBefore(end1) ? new LocalDateTime[]{start1,end1} : null;}

    /** checks whether the first list is a subset of the second one, given the equality tester.
     * An empty list is subset of everything.
     *
     * @param list1 the first list
     * @param list2 the second list
     * @param equal the equality tester
     * @param <A> the item type of the first list
     * @param <B> the item type of the second list
     * @return true if the lists are disjoint
     */
    public static <A, B>  boolean isSubset(ArrayList<A> list1, ArrayList<B> list2, BiPredicate<A,B> equal) {
        if(list1 == null || list1.isEmpty()) {return true;}
        if(list2 == null || list2.isEmpty()) {return false;}
        int size1 = list1.size();
        int size2 = list2.size();
        for(int i = 0; i < size1; ++i) {
            A item1 = list1.get(i);
            boolean found = false;
            for(int j = 0; j < size2; ++j) {
                if(equal.test(item1,list2.get(j))) {found = true; break;}}
            if(!found) {return false;}}
        return true;}

    public static int compareTo(Number a, Number b) {
        if(a instanceof Integer) {
            if(b instanceof Integer) {return ((Integer)a).compareTo((Integer)b);}
            if(b instanceof Float) {
                float ai = (float)(Integer)a;
                float bi = (float)(Float)b;
                if(ai == bi) {return 0;}
                return (ai < bi) ? -1 : 1;}
            if(b instanceof Double) {
                double ai = (double)(Integer)a;
                double bi = (double)(Double)b;
                if(ai == bi) {return 0;}
                return (ai < bi) ? -1 : 1;}}
        if(a instanceof Float) {
            if(b instanceof Integer) {
                float ai = (float)(Float)a;
                float bi = (float)(Integer)b;
                if(ai == bi) {return 0;}
                return (ai < bi) ? -1 : 1;}
            if(b instanceof Float) {return ((Float)a).compareTo((Float)b);}
            if(b instanceof Double) {
                double ai = (double)(Float)a;
                double bi = (double)(Double)b;
                if(ai == bi) {return 0;}
                return (ai < bi) ? -1 : 1;}}
        if(a instanceof Double) {
            if(b instanceof Integer) {
                double ai = (double)(Double)a;
                double bi = (double)(Integer)b;
                if(ai == bi) {return 0;}
                return (ai < bi) ? -1 : 1;}
            if(b instanceof Float) {
                double ai = (double)(Double)a;
                double bi = (double)(Float)b;
                if(ai == bi) {return 0;}
                return (ai < bi) ? -1 : 1;}
            if(b instanceof Double) {return ((Double)a).compareTo((Double)b);}}

        return -1;}

    public static <T> Stream<T> streamsConcat(Stream<T> stream1, Stream<T> stream2) {
        if(stream1 == null) {return stream2;}
        if(stream2 ==  null) {return stream1;}
        return Stream.concat(stream1,stream2);}


    /** returns a stream which delivers the intersection of the contents of the streams
     * The resulting stream must be closed explicitly.
     * If the first stream is infinite, the resulting stream will also be infinite.
     * There should be at least one finite stream among the second - last streams,
     * otherwise the intersection test might not terminate.
     *
     * @param streams a list of streams
     * @param <T> the type of the streams' items
     * @return a stream for delivering the intersection of the streams' items.
     */
    public static <T> Stream<T> intersection(ArrayList<Stream<T>> streams) {
        int size = streams.size();
        if(size == 1) {return streams.get(0);}
        ArrayList<BufferedStream<T>> bufferedStreams = new ArrayList<>(size-1);
        for(int i = 1; i < size; ++i) {bufferedStreams.add(new BufferedStream<T>(streams.get(i)));}
        return streams.get(0).
                onClose(()->{for(BufferedStream bstr : bufferedStreams){bstr.close();}}).
                filter(item -> {
                    boolean found = true;
                    for(BufferedStream<T> bstr : bufferedStreams) {
                        bstr.reset();
                        if(bstr.containedInBuffer(item)) {continue;}
                        else{found = false;}
                        if(bstr.allInBuffer()) {return false;}}
                    if(found) {return true;}

                    while(true) {
                        found = true;
                        for(BufferedStream<T> bstr : bufferedStreams) {
                            if(bstr.hasNext()) {
                                if(item.equals(bstr.next())){continue;}
                                else{found = false;}}
                            else{return false;}}
                        if(found) {return true;}}});
    }

    /** conctenates for all items in the collection the non-null streams generated by the streamGenerator
     *
     * @param collection any collection of items
     * @param streamGenerator maps items to streams (or null)
     * @param <N> the item type in the collection
     * @param <M> the item type in the stream
     * @return the concatenated stream, or null
     */
    public static <N,M> Stream<M> streamConcat(Collection<N> collection, Function<N,Stream<M>> streamGenerator) {
        if(collection == null || collection.isEmpty()) {return null;}
        Stream<M> stream = null;
        for(N item : collection) {
            Stream<M> str = streamGenerator.apply(item);
            if(stream == null) {stream = str;}
            else {
                if(str != null) {stream = Stream.concat(stream,str);}}}
        return stream;}

    /** concatenates two streams. They both can be null
     *
     * @param stream1 a stream or null
     * @param stream2 a stream or null
     * @param <N> the type of the stream's items
     * @return the concatenated stream, or null.
     */
    public static <N> Stream<N> streamConcat(Stream<N> stream1, Stream<N> stream2) {
        if(stream1 == null) {return stream2;}
        if(stream2 == null) {return stream1;}
        return Stream.concat(stream1,stream2);
    }

    /** This method serializes any serializable object to a string.
     *
     * @param object any serializable object
     * @return a string representation of the object
     * @throws IOException if some error occurs
     */
    public static String objectToString(Serializable object) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(object);
        return Base64.getEncoder().encodeToString(os.toByteArray());
    }

    /** This method turns a serialized object back into an object.
     *
     * @param string the string representation of a serialized object
     * @return the deserialized object
     * @throws IOException for read errors
     * @throws ClassNotFoundException for deserialize errors
     */
    public static Object stringToObject(String string) throws IOException, ClassNotFoundException {
        Base64.Decoder dec = Base64.getDecoder();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dec.decode(string)));
        return in.readObject();
    }

    /** This method computes the calendar week number for a given time point.
     * Week 0 means actually the last week of the previous year.
     *
     * @param time a time instant
     * @return the week number of this time
     */
    public static int getWeek(LocalDateTime time) {
        int year = time.getYear();
        int dayOfWeek = LocalDate.of(year,1,1).getDayOfWeek().getValue();
        int dayOfYear = time.getDayOfYear()+dayOfWeek-1;
        int week = dayOfYear/7;
        if(dayOfYear % 7 != 0) {++week;}
        if(dayOfWeek > 4) {--week;}
        return week;}

    /** generates a string of random capital characters
     *
     * @param size the desired length of the string
     * @return the generated string.
     */
    public static String randomName(int size) {
        String s = "";
        Random rnd = new Random();
        for(int i = 0; i < size; ++i) {
            int c = rnd.nextInt(26)+65;
            s += (char)c;}
        return s;}

    public static LocalDateTime parseDateTime(String text, DateTimeFormatter formatter, StringBuilder errors) {
        TemporalAccessor accessor = null;
        try{accessor = formatter.parse(text);}
        catch(Exception e) {errors.append(e.toString()).append("\n"); return null;}
        int year = 0, month = 1, day = 1;
        try{LocalDate date = LocalDate.from(accessor);
            year = date.getYear(); month = date.getMonthValue(); day = date.getDayOfMonth();}
        catch(Exception e) {
            if(accessor.isSupported(ChronoField.YEAR_OF_ERA))   {year = accessor.get(ChronoField.YEAR_OF_ERA);}
            if(accessor.isSupported(ChronoField.YEAR))          {year = accessor.get(ChronoField.YEAR);}
            if(accessor.isSupported(ChronoField.MONTH_OF_YEAR)) {month = accessor.get(ChronoField.MONTH_OF_YEAR);}
            if(accessor.isSupported(ChronoField.DAY_OF_MONTH))  {day = accessor.get(ChronoField.DAY_OF_MONTH);}}
        if(year == 0) {
            int dates[] = extractYearWeek(accessor);
            year = dates[0]; month = dates[1]; day = dates[2];}
        int hour = 0,minute = 0,second =0;
        if(accessor.isSupported(ChronoField.HOUR_OF_AMPM)) {hour = accessor.get(ChronoField.HOUR_OF_AMPM);}
        if(accessor.isSupported(ChronoField.MINUTE_OF_HOUR)) {minute = accessor.get(ChronoField.MINUTE_OF_HOUR);}
        if(accessor.isSupported(ChronoField.SECOND_OF_MINUTE)) {second = accessor.get(ChronoField.SECOND_OF_MINUTE);}
        return LocalDateTime.of(year,month,day,hour,minute,second);}

    private static int[] extractYearWeek(TemporalAccessor accessor) {
        String[] parts = accessor.toString().split("=");
        String year = parts[1].split(",|}",2)[0];
        String week = "1";
        if(parts.length > 2) {week = parts[2].split("(,|})",2)[0];}
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Y ww e");
        if(week.length() == 1) {week = "0"+week;}
        String dates = year + " " + week + " 1";
        LocalDate date = LocalDate.from(formatter.parse(dates));
        return new int[]{date.getYear(),date.getMonthValue(),date.getDayOfMonth()};
    }

    /** This method recursively splits a string with equations 'variable = term, variable = term,...' into the individual assignments
     * The terms may be enclosed by parentheses containing further equations: These are split recursively
     *
     * @param code a string with equations 'variable = term, variable = term,...'
     * @param separator a character which separates the equations.
     * @param openpar   an opening parentheses in the terms.
     * @param closepar  an closing parentheses in the terms.
     * @param errors for inserting error messages
     * @return a HashMap with variable -&gt; term,...  or null, if there were errors. The Hash-values are either Strings or HashMaps again.
     */
    public static HashMap<String,Object> splitEquations(String code, char separator, char openpar, char closepar, StringBuilder errors) {
        boolean okay = true;
        HashMap<String,Object> map = new HashMap<>();
        ArrayList<String> eqs = new ArrayList<>();
        int index = 0;
        while(true) {
            int nextIndex = findCharacter(code,separator,openpar,closepar,index);
            if(nextIndex >= 0) {eqs.add(code.substring(index,nextIndex)); index = nextIndex+1;}
            else {eqs.add(code.substring(index,code.length()).trim()); break;}}

        for(String part : eqs) {
            String[] parts = part.split("\\s*=\\s*",2);
            if(parts.length < 2) {okay = false; errors.append("'"+part+"' is not an equation: <variable> = <term>\n"); continue;}
            String rightSide = parts[1];
            if(rightSide.charAt(0) == openpar) {
                int last = rightSide.lastIndexOf(closepar);
                if(last < 0) {errors.append("No closing parenthesis in '" + rightSide+"'"); okay = false; continue;}
                HashMap<String,Object> map1 = splitEquations(rightSide.substring(1,last),separator,openpar,closepar,errors);
                if(map1 != null) {map.put(parts[0].trim(),map1);}
                else {okay = false;}}
            else {map.put(parts[0].trim(),rightSide);}}
        return okay ? map : null;}

    public static void main1(String[] args) {
        StringBuilder errors = new StringBuilder();
        //DateTimeFormatter f = DateTimeFormatter.ofPattern("Y - w e hh mm ss");
        //DateTimeFormatter f = DateTimeFormatter.ofPattern("u M d hh mm ss");
        DateTimeFormatter f = DateTimeFormatter.ofPattern("u M");
        //System.out.println(parseDateTime("2017",f,errors));
        //System.out.println(parseDateTime("2015 - 53 1 10 15 46",f,errors));
        System.out.println(parseDateTime("2015",f,errors));
        System.out.println(errors.toString());

    }

    /** The method searches the first occurrence of the character, while skipping substrings enclosed in openpar, closepar.
     * Example: "a = (b = c, d = (e = k, f = h)), e = f, h= g". The first occurrence of ',' is at position 31
     *
     * @param string    the string to be checked.
     * @param character the character to be searched
     * @param openpar   the opening parenthesis, e.g. (
     * @param closepar  the closing parenthesis, e.g. )
     * @param start     the start position of the search
     * @return          the position of the character, or -1 if it is not found.
     */
    public static int findCharacter(String string, char character, char openpar, char closepar, int start) {
        int pars = 0;
        for(int i = start; i< string.length(); ++i) {
            char c = string.charAt(i);
            if(c == character && pars == 0) {return i;}
            if(c == openpar) {++pars;}
            else {if(c == closepar) {--pars;}}}
        return -1;}


    /** The methor retrieves a public or private method from the given class.
     * private methods are made accessible
     *
     * @param clazz any class
     * @param name  any method name
     * @return the first method with this name, or null.
     */
    public static Method getMethod(Class clazz, String name) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.getName().equals(name)) {
                method.setAccessible(true);
                return method;}}
        return null;}

    /** quotes a string
     *
     * @param s a string
     * @return the quoted string.
     */
    public static String quote(String s) {
        s = s.replaceAll("\"","\\\\\"");
        return "\""+s+"\"";}


    /** returns the easter date for a given year.
     *
     * @param year the year in the Gregorian Calendar.
     * @return the easter date [year,month,day].
     */
    static int[] easter(int year) {
        int a, b, c, d, e, p, q, r, x, y, day, month ;
        //Es geht um die Berechnung der Groessen d und e
        //Dazu braucht man die 9 Hilfsgroessen a, b, c, p, n, q, r, x, y !!
        p = year/100 ;
        q = p/3 ;       r = p/4 ;
        x = (15+p-q-r)%30 ;     y = (4+p-r)%7 ;
        a = year%19 ;   b = year%4 ;    c = year%7 ;
        d = (19*a+x)%30 ;
        e = (2*b+4*c+6*d+y)%7 ;
        if (d==29 && e==6){ //=> Ostern am 19.April
            day=19; month=4;}
        else if (d==28 && e==6){ //=> Ostern am 18.April
            day=18; month=4;}
        else if (22+d+e < 32) { //ansonsten gilt Ostern am (22+d+e).Maerz
            day=22+d+e; month=3;}
        else{day=d+e-9; month=4;} // =>  Ostern am (d+e-9).April
        return new int[]{year,month,day};}


    /** returns the pentecost date for a given year.
     *
     * @param year the year in the Gregorian Calendar.
     * @return the pentecost date [year,month,day].
     */
    static int[] pentecost(int year) {
        int a, b, c, d, e, p, q, r, x, y, day, month;
        int pday, pmonth ;
        //Es geht um die Berechnung der Groessen d und e
        //Dazu braucht man die 9 Hilfsgroessen a, b, c, p, n, q, r, x, y !!
        p = year/100 ;
        q = p/3 ;       r = p/4 ;
        x = (15+p-q-r)%30 ;     y = (4+p-r)%7 ;
        a = year%19 ;   b = year%4 ;    c = year%7 ;
        d = (19*a+x)%30 ;
        e = (2*b+4*c+6*d+y)%7 ;
        if (d==29 && e==6){// Ostern am 19.April
            day=19; month=4;}
        else if (d==28 && e==6){// Ostern am 18.April
            day=18; month=4;}
        else if (22+d+e < 32){ //ansonsten gilt Ostern am (22+d+e). Maerz
            day=22+d+e; month=3;}
        else{// =>  Ostern am (d+e-9).April
            day=d+e-9; month=4;}
        // Berechnung des Pfingsdatums
        if (month==3){pday = day-12; pmonth=5;}
        else if (month==4 && day < 13){pday = day+19 ; pmonth=5;}
        else {pday = day-12 ; pmonth = 6 ;}
        return new int[]{year,pmonth,pday};}


    /** This method normalizes a date, given by year, month, day, hour, minute, second
     * and turns it into a LocalDateTime. The date parameters may be arbitrary positive of negative integers.
     *
     * @param year   a year
     * @param month  a month
     * @param day    a day of the month
     * @param hour   an hour
     * @param minute a minute
     * @param second a second
     * @return       the normalized LocalDateTime
     */
    public static LocalDateTime normalizedByMonth(int year, int month, int day, int hour, int minute, int second, StringBuilder errors, String source) {
        if(second > 59)       {minute += second / 60; second %= 60;}
        else {if(second < 0)  {minute += (second-60) / 60; second = (second % 60) + 60;}}
        if(minute > 59)       {hour += minute / 60; minute %= 60;}
        else {if(minute < 0)  {hour += (minute-60) / 60; minute = (minute % 60) + 60;}}
        if(hour > 23)         {day += hour / 24; hour %= 24;}
        else {if(hour < 0)    {day += (hour-24) / 24; hour = (hour % 24) + 24;}}

        --month;
        if(month > 11)       {year += month / 12; month %= 12;}
        else {if(month < 0)  {year += (month-12) / 12; month = (month % 12) + 12;}}
        ++month;

        LocalDate d = LocalDate.of(year,month,1).plusDays(day-1);
        return LocalDateTime.of(d.getYear(),d.getMonthValue(),d.getDayOfMonth(),hour,minute,second);
    }

    /** This method normalizes a date, given by year, monthName, day, hour, minute, second
     * and turns it into a LocalDateTime. The date parameters may be arbitrary positive of negative integers.
     *
     * @param source (for error messages)
     * @param year   a year
     * @param monthName  a month
     * @param day    a day of the month
     * @param hour   an hour
     * @param minute a minute
     * @param second a second
     *@param errors for inserting error messages
     * @return       the normalized LocalDateTime
     */
    public static LocalDateTime normalizedByMonth(int year, String monthName, int day, int hour, int minute, int second, StringBuilder errors, String source) {
        int month;
        try{month = Month.valueOf(monthName.toUpperCase()).getValue();}
        catch(IllegalArgumentException ex) {errors.append("Unknown month name '" + monthName + "' in '"+source+"'\n"); return null;}
        return normalizedByMonth(year,month,day,hour,minute,second,errors,source);
    }

    /** This method normalizes a date, given by year, week, day, hour, minute, second
     * and turns it into a LocalDateTime. The date parameters may be arbitrary positive of negative integers.
     *
     * @param year   a year
     * @param week   a week
     * @param day    a day of the week
     * @param hour   an hour
     * @param minute a minute
     * @param second a second
     * @return       the normalized LocalDateTime
     */
    public static LocalDateTime normalizedByWeek(int year, int week, int day, int hour, int minute, int second,StringBuilder errors, String source) {
        if(second > 59)       {minute += second / 60; second %= 60;}
        else {if(second < 0)  {minute += (second-60) / 60; second = (second % 60) + 60;}}
        if(minute > 59)       {hour += minute / 60; minute %= 60;}
        else {if(minute < 0)  {hour += (minute-60) / 60; minute = (minute % 60) + 60;}}
        if(hour > 23)         {day += hour / 24; hour %= 24;}
        else {if(hour < 0)    {day += (hour-24) / 24; hour = (hour % 24) + 24;}}
        int dow = LocalDate.of(year,1,1).getDayOfWeek().getValue();
        int days = (dow <= 4) ? ((week-1)*7+day - dow) : (week*7+day - dow);
        return LocalDateTime.of(year,1,1,hour,minute,second).plusDays(days);
    }

    /** This method normalizes a date, given by year, week, dayName, hour, minute, second
     * and turns it into a LocalDateTime. The date parameters may be arbitrary positive of negative integers.
     *
     * @param source for error messages.
     * @param year   a year
     * @param week   a week
     * @param dayName    a day name of the month
     * @param hour   an hour
     * @param minute a minute
     * @param second a second
     * @param errors for inserting error messages
     * @return       the normalized LocalDateTime
     */
    public static LocalDateTime normalizedByWeek(int year, int week, String dayName, int hour, int minute, int second, StringBuilder errors, String source) {
        int day;
        try{day = DayOfWeek.valueOf(dayName.toUpperCase()).getValue();}
        catch(IllegalArgumentException ex) {errors.append("Unknown day name '" + dayName + "' in '"+source+"'\n"); return null;}
        return normalizedByWeek(year,week,day,hour,minute,second,errors,source);}


    /** This method turns an InputStream into a Stream&lt;String%gt where optionally comments are removed and lines are concatenated.
     *
     * @param stream the input stream
     * @param comment      a comment string like '//' which means that all character after this string in the line are removed as comments.
     * @param commentStart a comment string like '[[', together with ']]' which  means that all characters between these strings are removed as comments.
     * @param commentEnd   the end characters for comments.
     * @param concat       a character like '\' which, placed at a line's end means the current line and the next line are to be concatenated.
     * @param trim         if true then leading and trailing spaces in lines are removed. Empty lines are removed as well.
     * @return             a Stream which yields the sequence of reduced lines.
     */
    public static Stream<String> getLines(InputStream stream, String comment, String commentStart, String commentEnd, char concat, boolean trim) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        int endLength = commentEnd.length();
        Stream<String> lines = reader.lines();
        if(comment != null) {
            lines = lines.filter(line-> !line.startsWith(comment)).
                    map(line-> {int index = line.indexOf(comment); return (index < 0) ? line : line.substring(0,index);});}
        if(commentStart != null && commentEnd != null) {
            boolean[] inside = new boolean[]{false};
            lines = lines.map(line -> {
                if(inside[0]) {
                    int end = line.indexOf(commentEnd);
                    if(end < 0) {return "";}
                    else {line = line.substring(end+endLength,line.length()); inside[0] = false;}}
                int start = line.lastIndexOf(commentStart);
                if(start < 0) {return line;}
                int end = line.indexOf(commentEnd,start+1);
                if(end < 0) {inside[0] = true; line = line.substring(0,start);}
                return removeComments(line,commentStart,commentEnd);}).
                    filter(line->line.length()!=0);};
        if(trim) {lines = lines.map(line->line.trim()).filter(line->line.length() != 0);}

        if(concat != 0) {
            StringBuilder[] longLine = new StringBuilder[]{new StringBuilder()};
            lines = lines.map(line-> {
                if(line.charAt(line.length()-1) == concat){
                    longLine[0].append(line.substring(0,line.length()-1));
                    return "";}
                else {if(longLine[0].length() > 0) {line = longLine[0].toString()+line; longLine[0]=new StringBuilder(); return line;}
                else {return line;}}}).filter(line-> line.length() != 0);}
        return lines;}


    /** This method parses a Stream&lt;String&gt; with lines consisting of 'key separator value' entries into a HashMap.
     *
     * @param stream    the stream of lines to be parsed
     * @param separator a separator, e.g. '='
     * @param errors    for inserting error messages
     * @return          the parsed key-value maps as a HashMap.
     */
    public static HashMap<String,String> parseKeyValueLines(Stream<String> stream, String separator, StringBuilder errors) {
        HashMap<String,String> map = new HashMap<>();
        stream.forEach(line-> {
            if(line.length() == 0) {return;}
            String[] parts = line.split(separator,2);
            if(parts.length == 1) {errors.append("'"+line+"' does not have the required structure '<string> " + separator +" <string>'\n"); return;}
            String key   = parts[0].trim();
            String value = parts[1].trim();
            if(key.length() == 0 || value.length() == 0) {errors.append("'"+line+"' does not have the required structure '<string> " + separator +" <string>'\n");return;}
            else {map.put(key,value);}});
        return map;}

    /** This method parses a Stream&lt;String&gt; with lines consisting of 'left relation right', where 'left' and 'right' are 'separator' separated strings without blanks.
     * Example: line: 'IFI, IFM, IST in Institute, LMU'
     * is parsed as [in,[IFI,IFM,IST],[Institute,LMU]]
     * where the second and third components are ArrayList&lt;String&gt;.
     *
     * @param stream    the stream of lines to be parsed
     * @param separator a separator, e.g. ','
     * @param errors    for inserting error messages
     * @return          the parsed lines as ArrayList: [relation, left, right]
     */
    public static ArrayList<Object[]> parseRelationLines(Stream<String> stream, String separator, StringBuilder errors) {
        String sep = "\\s*"+separator + "\\s*";
        ArrayList<Object[]> lines = new ArrayList<>();
        stream.forEach(line-> {
            if(line.length() == 0) {return;}
            StringBuilder string = new StringBuilder(line);
            HashMap<String,String> replaced = replaceStrings(string);
            if(!replaced.isEmpty()) {line = string.toString();}
            ArrayList<String> left = new ArrayList<>();
            ArrayList<String> right = new ArrayList<>();
            boolean front = true;
            Object[] components = new Object[]{null,null,null};
            for(String part : line.split(sep)) {
                if(part.isEmpty()) {continue;}
                if(!front) {right.add(part); continue;}
                String[] def = part.split("\\s+");
                if(def.length == 3) {
                    components[0] = def[1];
                    left.add(restoreStrings(def[0],replaced));
                    right.add(restoreStrings(def[2],replaced)); front = false;}
                else {left.add(restoreStrings(part,replaced));}}
            if(components[0] == null || left.isEmpty() || right.isEmpty()) {errors.append("Wrong structure of '" + line + "'\n");}
            else {components[1] = left; components[2] = right; lines.add(components);}});
        return lines;}

    private static HashMap<String,String> replaceStrings(StringBuilder string) {
        HashMap<String,String> map = new HashMap<>();
        int position = 0;
        int counter = 0;
        while(true) {
            int start = string.indexOf("\"",position);
            if(start < 0) {return map;}
            position = start+1;
            while(true) {
                int end = string.indexOf("\"",position);
                if(end < 0) {return map;}
                if(string.charAt(end-1) == '\\') {position = end+1; continue;}
                String key = "REPLACED_ABCDE_" + counter++;
                map.put(key,string.substring(start+1,end));
                string.replace(start,end+1,key);
                position = start+key.length();
                break;}}
    }
    private static String restoreStrings(String string, HashMap<String,String> map) {
        for(Map.Entry entry : map.entrySet()) {string = string.replace((String)entry.getKey(),(String)entry.getValue());}
        return string;}


    /** returns the temp-directory
     *
     * @return the temp directory
     */
    public static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));}

    /** Writes the string to the temporary file with the given name and filename extension.
     *  The method should basically be used for test purposes.
     *
     * @param name      the name of the file
     * @param extension the filename extension
     * @param content   the string to be written on the file
     * @return          the new File
     * @throws IOException
     */
    public static File writeTempFile(String name, String extension, String content) throws IOException {
        File tempfile = File.createTempFile(name,extension);
        FileOutputStream stream = new FileOutputStream(tempfile);
        stream.write(content.getBytes());
        stream.close();
        return tempfile;}

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperties().toString().replaceAll(",","\n"));

    }



}
