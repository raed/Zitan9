package Network.Queries;

import MISC.Context;

import java.io.Serializable;

/** This is the superclass for query objects to be sent to an QueryServer
 */
public abstract class Query implements Serializable {
    /** used to give the queries an identifier. */
    private static int counter = -1;

    /** the expected answer type (direct of stream) */
    public AnswerType answerType;

    /** an identifier for the query */
    public int queryId = 0;

    /** is set to true if an error was encountered */
    boolean error = false;

    public Query(){}
    /** constructs a query
     *
     * @param answerType the type of the expected answer.
     */
    public Query(AnswerType answerType) {
        queryId = ++counter;
        this.answerType = answerType;}

    /** must return the answer to the query
     *
     * @param context the context from where the answer is to be generated.
     * @return the answer.
     * */
    public abstract Object answer(Context context);

    /** for indicating the expected answer type
     */
    public enum AnswerType {
        /** the answer is to be sent back immediately */
        DIRECT,
        /** the a stream is to be established for the answer */
        STREAM
    }

}
