package Network.Queries;

import Attributes.Attribute;
import Attributes.AttributeValueList;
import Concepts.Concept;
import MISC.Commons;
import MISC.Context;
import Utils.Messanger;
import Utils.Utilities;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** This is a query argumentType for accessing an QueryClient to retrieve attribute values.
 * The following queries are supported:<br>
 *     - FIRST returns the first attribute value (maybe subject to the constraints)
 *     - STREAM returns a stream of argument values (maybe subject to the constraints)
 *     - FIND returns the first non-null function application.
 */
public class AttributeQuery extends Query implements Serializable {
    /** the concept identifier for which the query is to be asked */
    private String conceptId;
    /** the identifier for the attribute to be retrieved */
    private String attributeId;
    /** a list of attribute applicationName, attribute value pairs for the attribute constraints */
    private AttributeValueList constraints = null;
    /** either FIRST,STREAM or FIND */
    private AttributeQueryType queryType;
    /** the Groovy code for a filter and finder */
    private String code = null;
    /** the argument argumentType of the finder and filter */
    private String argumentType = null;
    /** the result type for the finder */
    private String resultType = null;

    private static String argumentName = "object";

    /** constructs a query
     *
     * @param conceptId the applicationName of the concept for which the attribute is to be retrieved.
     * @param attributeId the identifier for the attribute.
     * @param queryType the argumentType of the expected answer (direct or stream)
     * @param answerType either DIRECT or STREAM
     */
    public AttributeQuery(String conceptId, String attributeId, AttributeQueryType queryType, AnswerType answerType) {
        super(answerType);
        this.conceptId = conceptId;
        this.attributeId = attributeId;
        this.queryType = queryType;
    }

    /** adds a constraint to the query.
     *
     * @param constraints the applicationName of the constraint attribute
     * @return the query itself.
     */
    public AttributeQuery addConstraint(AttributeValueList constraints) {
        this.constraints = constraints;
        return this;}

    /** adds a filter for filtering the answers.
     * Filtering the answers at the server side reduces the network load.
     *
     * @param code the Groovy code for the filter
     * @param argumentType the argumentType of the values to be filtered.
     * @return true if the code could be compiled and the filter adde.
     */
    public boolean addFilter(String argumentType, String code) {
        Predicate predicate = Utilities.compilePredicate(code,argumentType,argumentName); // just for checking at client side.
        if(predicate == null) {return false;}
        this.argumentType = argumentType;
        this.code = code;
        return true;}

    /** adds a finder. A finder traverses argument values and returns the first non-null value.
     *
     * @param argumentType the argumentType of the attribute values.
     * @param code the Groovy code for the finder. The parameter is "concept".
     * @param resultType the result-type of the finder code.
     * @return true if the code was compiled
     */
    public boolean addFinder(String argumentType, String resultType,String code) {
        Function function = Utilities.compileFunction("", code,argumentType,argumentName,resultType,null,0);
        if(function == null) {error = true; return false;}
        this.argumentType = argumentType;
        this.resultType = resultType;
        this.code = code;
        return true;}

    /** This method is called by the server to generate an answer to the query.
     *
     * @param context the context for generating the answer
     * @return either the answer object, or the stream of answer objects
     */
    public Object answer(Context context) {
        Concept concept = context.getConcept(conceptId);
        if (concept == null) {
            error = true;
            return "Unknown individualConcept " + conceptId;}
        Attribute attribute = context.getAttribute(attributeId);
        if (attribute == null) {
            error = true;
            return "Unknown attribute " + attributeId;}
        Messanger messanger = Commons.getMessanger(Messanger.MessangerType.DataErrors);
        try {
            switch (queryType) {
                case FIRST:
                    return concept.getFirst(attribute, null,null, constraints,context);
                case STREAM:
                    Stream stream = concept.stream(attribute, null,null, constraints,context);
                    if(stream == null) {return null;}
                    if (code != null) {
                        messanger.push("Attribute.rel Query: Compiling Filter");
                        Predicate predicate = Utilities.compilePredicate(code, argumentType,argumentName);
                        if(predicate == null) {error = true; return messanger.pop().toString();}
                        messanger.pop();
                        return stream.filter(predicate);}
                    return stream;
                case FIND:
                    if(code == null) {error = true; return "no finder code specified";}
                    messanger.push("Attribute.rel Query: Compiling Function");
                    Function function = Utilities.compileFunction("", code, argumentType,argumentName, resultType,null,0);
                    if(function == null) {error = true; return messanger.pop().toString();}
                    messanger.pop();
                    return concept.find(attribute,context,function);
            }}
        catch (Exception ex) {error = true; return ex.toString();}
        return null;
    }

    /** returns a string representation of the query.
     *
     * @return a string representation of the query.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("AttributeQuery "+queryId + " for concept: " + conceptId + ", Attribute.rel: " + attributeId);
        if(constraints != null) {s.append(" if ").append(constraints.toString());}
        if(code != null) {s.append("\n").append(code);}
        return s.toString();
    }

    public enum AttributeQueryType {
        FIRST,
        STREAM,
        FIND;
    }
}
