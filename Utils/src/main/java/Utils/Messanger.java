package Utils;


import java.util.ArrayList;
import java.util.Stack;


/** This class can be used to recorde sequences of errors.
 * A Messanger has a type, for example DataErrors,
 * and a stack of Messages, which themselves are lists.
 * A typical intended application works as follows:
 * The program starts executing a piece of code where user inputs are analysed,
 * and several errors may be detected.
 * Bofore starting the piece of code, it fetches a DataError messanger and
 * calls push(context), where 'context' describes the situation where errors may occur.
 * Then the piece of code is executed, and several errors may be inserted.
 * After finishing the execution, the program calls pop() to get and remove the sequence of
 * error messages. Inside the piece of code one may call push(into)-pop() sequences again.
 */

public class Messanger {
    /** the type of the messanger */
    private MessangerType type;
    /* the message stack */
    private Stack<Messages> messageStack = new Stack();

    /** constructs a new messager of the given type
     *
     * @param type the type of the messager, for example DataError
     * */
    public Messanger(MessangerType type) {
        this.type = type;}

    /** pushes a new Message-List on the stack
     *
     * @param context some information about the context of the messages.
     */
    public synchronized void push(String context) {
        messageStack.push(new Messages(context));}

    /** pops the Messages-list from the stack
     *
     * @return the last Messages-list on the stack.
     */
    public synchronized Messages pop() {
        return messageStack.pop();}

    /** returns the last Messages-list from the stack
     *
     * @return the last Messages-list on the stack.
     */
    public synchronized Messages peek() {
        return messageStack.peek();}


    /** if the last element on the messages stack is empty, it is removed, otherwise nothing is changed.
     *
     * @return true if the last element on the message stack is empty.
     */
    public synchronized boolean popIfEmpty() {
        Messages messages = messageStack.peek();
        if(messages.isEmpty()) {messageStack.pop(); return true;}
        return false;}

    /** joins the two top-elements of the stack.*/
    public synchronized void join() {
        if(messageStack.size() < 2) {return;}
        Messages m2 = messageStack.pop();
        messageStack.peek().append(m2);}

    /** inserts a message into the top-element of the stack, if there is a top-element.
     *
     * @param messageType any string
     * @param message  the actual message.
     */
    public synchronized void insert(String messageType, String message) {
        if(!messageStack.isEmpty()) {
            messageStack.peek().insert(messageType,message);}}

    /** inserts an error resulting from a Groovy compilation
     *
     * @param code the code to be compiled
     * @param exception the exception thrown by the compiler.
     */
    public void insertCompilationError(String code, Exception exception) {
        insert("Compilation Error", "\nCode: " + code+"\n" + exception.getMessage());}

    /** This class comprises the data of one stack element in the messageStack
     */

    public static class Messages {

        /** the context for the messages */
        String context;

        /** Strings like "Compilation Error" */
        ArrayList<String> messageTypes = new ArrayList<>();

        /** the actual error messages */
        ArrayList<String> messages = new ArrayList<>();

        /** constructs a new Messages element
         *
         * @param context some information about the context of the messages
         */
        public Messages(String context) {
            this.context = context;}

        /** inserts a message
         *
         * @param messageType a string like "Compilation Error"
         * @param message  the actual message
         */
        public synchronized void insert(String messageType, String message) {
            messageTypes.add(messageType);
            messages.add(message);}

        /** @return true if there are no messages.
         */
        public boolean isEmpty() {
            return messages.isEmpty();}

        /** appends 'other to 'this'
         *
         * @param other another Messages object
         */
        public synchronized void append(Messages other) {
            context += "\n"+other.context;
            messageTypes.addAll(other.messageTypes);
            messages.addAll(other.messages);
        }

        /** collects all the messages into a string.
         *
         * @return the string representation of the messages.
         */
        @Override
        public synchronized String toString() {
            StringBuilder s = new StringBuilder();
            s.append(context).append("\n");
            for(int i = 0; i < messages.size(); ++i) {
                s.append(messageTypes.get(i)).append(": ").append(messages.get(i)).append("\n");}
            return s.toString();}
    }

    /** can be used to distinguish different message types.*/
    public enum MessangerType {
        DataErrors, ProgramErrors, IOErrors;
    }

}
