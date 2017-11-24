package RuleEngine.HierarchyProcessors;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import Attributes.DataAttribute;
import Concepts.Concept;
import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.function.Predicate;
import java.util.stream.Stream;

/** This is the processing unit for accessing a single attribute value for a functional DataAttribute.
 */
public class DataAttributeFunctionalSingleProcessor extends AbstractProcessor {
    /** the context for the objects */
    private Context context;

    /** constructs a processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor not needed
     */
    public DataAttributeFunctionalSingleProcessor(AbstractDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;}

    /** generates the output stream.
     * It adds a filter to the input stream which accesses the attribute value and puts it into the query array.
     * If there is no attribute value then the query filtered out.
     *
     * @return the input stream with an additional filter.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        DataAttributeFunctionalSingleDefinition def = (DataAttributeFunctionalSingleDefinition)definition;
        Concept defConcept                = def.concept;
        int conceptIndex                  = def.conceptIndex;
        DataAttribute defAttribute        = (DataAttribute)def.attribute;
        int attributeIndex                = def.attributeIndex;
        DataObject defCompareObject       = def.compareObject;
        int compareIndex                  = def.compareIndex;
        Operators defOperator             = def.operator;
        int operatorIndex                 = def.operatorIndex;
        AttributeValueList defConstraints = def.constraints;
        int constraintIndex               = def.constraintsIndex;
        int outputIndex                   = def.outputIndex;
        Predicate<DataObject> filter      = def.filter;

        outputStream = inputStream.filter(query->{
            Concept            concept        = conceptIndex    >= 0 ? (Concept)           query[conceptIndex] :    defConcept;
            DataAttribute      attribute      = attributeIndex  >= 0 ? (DataAttribute)     query[attributeIndex] :  defAttribute;
            DataObject         compareObject  = compareIndex    >= 0 ? (DataObject)        query[compareIndex] :    defCompareObject;
            Operators          operator       = operatorIndex   >= 0 ? (Operators)         query[operatorIndex] :   defOperator;
            AttributeValueList constraints    = constraintIndex >= 0 ? (AttributeValueList)query[constraintIndex] : defConstraints;
            if(concept       == null) {concept       = defConcept;}
            if(attribute     == null) {attribute     = defAttribute;}
            if(compareObject == null) {compareObject = defCompareObject;}
            if(operator      == null) {operator      = defOperator;}
            if(constraints   == null) {constraints   = defConstraints;}
            DataObject attributeValue = concept.getFirst(attribute,operator,compareObject,constraints,context);
            if(attributeValue == null) {return false;}
            if(filter != null && !filter.test(attributeValue)) {return false;}
            if(outputIndex > 0) {query[outputIndex] = attributeValue;}
            return true;});
        return super.getOutputStream();
    }
}
