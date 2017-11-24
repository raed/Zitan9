package RuleEngine.HierarchyProcessors;

import Concepts.Concept;
import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.function.BiPredicate;

/** This class provides a processor for accessing functional concept attributes.
 * Since the attribute values of concept attributes are again concepts, one can apply further processors to the attribute values.
 * <br>
 * The processor can do the following operations:<br>
 *     - it can access the (functional)  attribute value and put it into the query stream.<br>
 *     - it can filter the attribute value and, thus, discard it from the stream. <br>
 *     - it can call a sub-processor to further process the attribute value.<br>
 * <br>
 * The parameters for the processor are:<br>
 *     - the concept is taken from the query stream (at conceptIndex)<br>
 *     - the attribute is either predefined, or taken from the query stream (at attribute Index)<br>
 *     - the output is either written to the query stream, or ignored. In this case the processor acts as filter.<br>
 *     - filter and subProcessor must be defined explicitly.<br>
 *  <br>
 *  Note: If a sub-processor is defined, then the query array structure of the  main processor and of the sub-processor must be identical.
 *  This can be achieved by adding a transformer to the sub-processor.
 *
 *     */
public class ConceptAttributeFunctionalDefinition extends AttributeDefinition {
    /** a filter for the concepts */
    BiPredicate<Concept,Context> filter = null;
    /** a sub-processor for the attribute values */
    AbstractDefinition subProcessorDefinition;

    /** constructs a new definition
     *
     * @param id its identifier
     */
    public ConceptAttributeFunctionalDefinition(String id) {
        super(id);
    }

    /** sets the filter
     *
     * @param filter a filter for the attribute values
     * @return this
     */
    public ConceptAttributeFunctionalDefinition setFilter(BiPredicate<Concept,Context> filter) {
        this.filter = filter;
        return this;}

    /** sets the sub-processor definition
     *
     * @param subProcessorDefinition the definition of a sub-processor-
     * @return this
     */
    public ConceptAttributeFunctionalDefinition setSubProcessorDefinition(AbstractDefinition subProcessorDefinition) {
        this.subProcessorDefinition = subProcessorDefinition;
        return this;}


    /** contructs the processor
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processing unit
     * @return the new processor.
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        assert attribute != null || attributeIndex >= 0;
        return new ConceptAttributeFunctionalProcessor(this,context,parentProcessor);
    }

    /**
     * @return attribute(r) + applicationName + attribute
     */
    @Override
    public String toString() {
        String s = "attribute(cf)" + getName() + " ";
        if(attribute != null) {s += attribute.getName();}
        else {s += Integer.toString(attributeIndex);}
        return s;}
}
