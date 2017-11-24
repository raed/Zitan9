package RuleEngine;

import java.util.Arrays;
import java.util.stream.Stream;

/** This is the top class for all processors.
 * It provides a number of auxiliary methods to be used in the subclasses.
 */
public abstract class AbstractProcessor {
    /** the corresponding definition */
    protected AbstractDefinition definition;
    /** the input stream for the processor */
    protected Stream<Object[]> inputStream;
    /** the output stream for the processor */
    protected Stream<Object[]> outputStream;
    /** the parent processor for sub-processors (so fat not used) */
    protected AbstractProcessor parentProcessor;
    /** constraints to be used when accessing the attribute values (may need to be changed in future version) */
    private boolean monitor = false;
    /** an auxiliary query array */
    private Object[] savedQuery = null;


    /** creates a processor
     *
     * @param definition  its definition
     * @param parentProcessor its parent processor
     */
    public AbstractProcessor(AbstractDefinition definition, AbstractProcessor parentProcessor) {
        this.definition = definition;
        this.parentProcessor = parentProcessor;
    }

    /** sets the input stream
     *
     * @param stream the input stream.
     * @return this
     */
    public AbstractProcessor setInputStream(Stream<Object[]> stream) {
        inputStream = stream;
        return this;}


    /** sets the monitor flag
     *
     * @param monitor if true then intermediary results of the processor are logged to Saystem.out
     * @return this
     */
    public AbstractProcessor setMonitor(boolean monitor) {
        this.monitor = monitor;
        return this;}


    /** returns the output stream.
     * This method must be overwritten in the sub-classes.
     * The stream itself must be generated in the overwritten method and stored in outputStream.
     * Afterwardes the overwritten method should call super.getOutputStream().
     * super.getOutputStream() may add a peek-operation to the output stream which prints the
     * current contents of the query array to System.out.
     *
     * @return the output stream.
     */
    public Stream<Object[]> getOutputStream() {
        return monitor ?
                outputStream.peek(query ->
                        System.out.println(Arrays.deepToString(query) + " after " + this.toString())) :
                outputStream;}

    /**
     * @return the definition's toString.().
     */
    @Override
    public String toString() {return definition.toString();}


    /** This method turns the query array into a stream.
     * It circumvents the problem that Stream.of(query) decomposes the query array into its components.
     *
     * @param query
     * @return the query as stream.
     */
    public static Stream<Object[]> queryStream(Object[] query) {
        return Stream.of((Object)query).map(q -> (Object[])q);}

    /** turns a copy of the query into a stream with one element.
     *
     * @param query the query to be copies.
     * @return the stream with the copied query.
     */
    public static Stream<Object[]> copyQuery(Object[] query) {
        return Stream.of((Object)Arrays.copyOf(query,query.length)).map(q -> (Object[])q);}

    /** saves the query into a temporary array
     *
     * @param query the query to be saved.
     */
    protected void saveQuery(Object[] query) {
        int length = query.length;
        if(savedQuery == null) {savedQuery = Arrays.copyOf(query,query.length);}
        else {for(int i = 0; i < length; ++i) {savedQuery[i] = query[i];}}}

    /** copies the saved query from the temporary array into the current query.
     *
     * @param query the query to be restored.
     */
    protected void restoreQuery(Object[] query) {
        int length = query.length;
        for(int i = 0; i < length; ++i) {query[i] = savedQuery[i];}}



}
