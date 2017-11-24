package Network.Queries;

import Concepts.Concept;
import DAGs.Direction;
import DAGs.InnerNode;
import Graphs.Strategy;
import MISC.Commons;
import MISC.Context;
import Utils.Messanger;
import Utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** This class allows for posing queries regarding the concepts and individuals.
 * For a given concept one can ask for <br>
 *     - all individuals <br>
 *     - sub/superconcepts <br>
 *     - results of some application function. <br>
 * The navigation through the concept hierarchy can be controlled by three parameters:<br>
 * - down (default true) moves down the hierarchy<br>
 * - all  (default false) just moves one level<br>
 * - breadthFirst (default false) controls breadth-first or depth-first search.<br>
 * The results are deliverd as stream. The stream can be filtered before delivering.
 * Created by ohlbach on 29.02.16.
 */
public class ConceptQuery extends Query implements Serializable {
    /** the identifier of a concept */
    private String conceptId;

    /** either individuals or subconcepts */
    private ConceptQueryType queryType;
    /** for HIERARCHY-queries: all = true -&gt; transitive. For FIND-queries: find all or just the first one. */
    private boolean all = false;
    /** direction of the search */
    private Direction direction = Direction.DOWN;
    /** breadth first or depth first */
    private Strategy strategy = Strategy.DEPTH_FIRST;

    /** the Groovy code for a filter or application function*/
    private String code = null;
    /** the argument resultType of the filter or application function*/
    private String resultType = null;

    private static String conceptType = "Concepts.Concept";

    /** generates a concept query. <br>
     *
     * @param conceptId the identifier of a concept.
     * @param queryType either INDIVIDUALS or HIERARCHY or FIND
     */
    public ConceptQuery(String conceptId, ConceptQueryType queryType) {
        super(AnswerType.STREAM);
        this.conceptId = conceptId;
        this.queryType = queryType;
    }

    /** sets all-parameter to true.
     *
     * @return the query
     */
    public ConceptQuery all() {all = true; return this;}

    /** sets strategy for the search.
     *
     * @return the query
     */
    public ConceptQuery strategy(Strategy strategy) {this.strategy = strategy; return this;}

    /** sets ths direction of the search
     *
     * @return the query.
     */
    public ConceptQuery direction(Direction direction) {this.direction = direction; return this;}


    /** adds a filter for filtering the answers.
     * Filtering the answers at the server side reduces the network load.
     * An example for a filter code is: "concept.applicationName.startsWith(\"T\");"
     *
     * @param code the Groovy code for the filter. The parameter is "concept".
     * @return true if the code was compiled
     */
    public boolean addFilter(String code) {
        Predicate predicate = Utilities.compilePredicate(code,conceptType,"concept"); // just for checking at client side.
        if(predicate == null) {error = true; return false;}
        this.code = code;
        return true;}

    /** adds a finder. A finder traverses the concept tree, and collects all non-null results of the finder code.
     * An example for a finder code is: "concept.applicationName.charAt(0)"
     * collects the first characters of all concepts in the search area.
     *
     * @param code the Groovy code for the finder. The parameter is "concept".
     * @param resultType the result-type of the finder code.
     * @return true if the code was compiled
     */
    public boolean addFinder(String resultType,String code) {
        Function function = Utilities.compileFunction("", code,conceptType,"concept",resultType,null,0);
        if(function == null) {error = true; return false;}
        this.resultType = resultType;
        this.code = code;
        return true;}


    /** answers the query.
     * The answer can be: <br>
     *     - null if there is no answer<br>
     *     - a stream of concept ids for an HIERARCHY-query<br>
     *     -  a stream of objects for a FIND-query <br>
     *     - an error message (as string) if something went wrong.
     *
     * @param context the context from where to get the concepts
     * @return the answer of the query.
     */
    @Override
    public Object answer(Context context) {
        try{
            Concept concept = context.getConcept(conceptId);
            if(concept == null) {error = true; return "Unknown concept " + conceptId;}
            Stream<Concept> stream = null;
            Messanger messanger = Commons.getMessanger(Messanger.MessangerType.DataErrors);

            switch(queryType) {
                case INDIVDUALS: stream = concept.individuals(context); break;

                case HIERARCHY:
                    if(all) {
                        stream = context.conceptHierarchy.innerNodes(concept,false,Direction.DOWN, Strategy.BREADTH_FIRST);}
                    else {
                        ArrayList<InnerNode<Concept>> subnodes =
                                direction == Direction.DOWN ? context.conceptHierarchy.getInnerNodes(concept):
                                                              context.conceptHierarchy.getSupernodes(concept);
                        if(subnodes == null) {return null;}
                        stream = subnodes.stream().map(n->n.label);}
                    break;

                case FIND:
                    if(code == null) {error = true; return "No finder code specified";}
                    messanger.push("Concept Query: Compiling Function");
                    Function function = Utilities.compileFunction("", code,conceptType,"concept", resultType,null,0);
                    if(function == null) {error = true; return messanger.pop().toString();}
                    messanger.pop();
                    if(all) {
                        //ArrayList result = context.conceptHierarchy.findAll(concept,down,function);
                        //return (result == null) ? null : result.stream();}
                    }
                    else {
                        Object result = context.conceptHierarchy.findInInnerLabels(concept,direction,strategy,function);
                        return (result == null) ? null : Stream.of(result);}
                    }

            if(stream == null) {return null;}
            if(code != null) {
                messanger.push("Concept Query: Compiling Filter");
                Predicate predicate = Utilities.compilePredicate(code,conceptType,"concept");
                if(predicate == null) {error = true; return  messanger.pop().toString();}
                messanger.pop();
                stream = stream.filter(predicate);}
            return stream.map(c -> c.getName());}
        catch(Exception ex) {error = true; return ex.toString();}
    }
    /**
     * @return a description of the query.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Concept Query");
        s.append(" "+queryId+" ").append(queryType.toString()).append(" for concept ").append(conceptId);
        s.append("\nall = "+all).append(", direction = " + direction).append(", strategy = "+strategy).append("\n");
        if(code != null) {s.append(code);}
        return s.toString();
    }

    public enum ConceptQueryType {
        INDIVDUALS,
        HIERARCHY,
        FIND;
    }
}
