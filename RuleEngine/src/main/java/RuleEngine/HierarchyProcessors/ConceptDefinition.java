package RuleEngine.HierarchyProcessors;

import Concepts.Concept;
import DAGs.Direction;
import Graphs.Strategy;
import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.function.BiPredicate;


/** A processor for this class deals with the concept hierarchy.
 * It always has a reference concept, which is either predefined, or taken from the query array.
 * <br>
 * It can work in different modes:<br>
 *     - it can check if a concept in the query array is  a subconcept of a reference concept<br>
 *     - from the reference concept in the query array it can <br>
 *         - fill the query stream with all individuals of this concept <br>
 *         - fill the query stream with all concepts up/down the concept hierarchy, <br>
 *           including/excluding the individuals.
 *           <br>
 *  The resulting concepts can be filtered with a given filter.
 */
public class ConceptDefinition extends AbstractDefinition {
    /** the reference concept. If it is null, it is taken from the query array. */
    Concept concept = null;
    /** the index in the query array where the reference concept is to be taken from */
    int inputIndex = -1;
    /** the index in the query array where the resulting concepts are to be written to. */
    int outputIndex = -1;
    /** the direction of the traversal in the hierarchy */
    Direction direction = Direction.DOWN;
    /** the way the hierarchy is to be traversed */
    Strategy strategy = Strategy.BREADTH_FIRST;
    /** this controls if the individuals are to be included or not */
    boolean individuals = true;
    /** a filter for filtering the resulting concepts */
    BiPredicate<Concept,Context> filter = null;

    /** creates a new ConceptDefintion with an identifier
     *
     * @param id the identifier
     */
    public ConceptDefinition(String id) {
        super(id);}

    /** sets a predefined reference concept.
     *
     * @param concept the reference concept.
     * @return this.
     */
    public ConceptDefinition setConcept(Concept concept) {
        this.concept = concept;
        return this;}

    /** set the index in the query array where the reference concept is to be taken from.
     *
     * @param inputIndex the index in the query array where the reference concept is to be taken from.
     * @return this.
     */
    public ConceptDefinition setInputIndex(int inputIndex) {
        assert inputIndex >= 0;
        this.inputIndex = inputIndex;
        return this;}

    /** set the index in the query array where the concept is written into.
     *
     * @param outputIndex the output index in the query array
     * @return this.
     */
    public ConceptDefinition setOutputIndex(int outputIndex) {
        assert outputIndex >= 0;
        this.outputIndex = outputIndex;
        return this;}

    /** If 'down' is true then the hierarchy is traversed downwards, otherwise upwards.
     *
     * @param direction for controlling the direction of the traversal in the concept hierarchy.
     * @return this
     */
    public ConceptDefinition setDirection(Direction direction) {
        this.direction = direction;
        return this;}

    /** If breadthfirst = true then the concept hierarchy is traversed breadth first, otherwise depth first.
     *
     * @param strategy controls if the hierarchy is traversed breadth first or depth first.
     * @return this
     */
    public ConceptDefinition setStrategy(Strategy strategy) {
        this.strategy = strategy;
        return this;}

    /** This parameter controls whether the individuals should be included or not.
     *
     * @param individuals if true then the individuals are included, otherwise not.
     * @return this
     */
    public ConceptDefinition setIndividuals(boolean individuals) {
        this.individuals = individuals;
        return this;}

    /** sets a filter for answer concepts in the query stream.
     *
     * @param filter the filter to be set.
     * @return this
     */
    public ConceptDefinition setFilter(BiPredicate<Concept,Context> filter) {
        this.filter = filter;
        return this;}

    /**
     * @return 'concept' index or concept
     */
    @Override
    public String toString() {
        String s = "concept " + getName();
        if(concept == null) {return s += " from " + Integer.toString(inputIndex);}
        s += " " + concept.getName();
        return s;}

    /** generates a new ConceptProcessor
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processor
     * @return the new processor
    */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new ConceptProcessor(this,context,parentProcessor);
    }
}
