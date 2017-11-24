package RuleEngine.HierarchyProcessors;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import Attributes.ConceptAttribute;
import Concepts.Concept;
import MISC.Context;
import RuleEngine.AbstractProcessor;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

/** This class is the actual processor for functional concept attributes
 */
public class ConceptAttributeFunctionalProcessor extends AbstractProcessor {
    /** the context for the objects */
    private Context context;
    /** a sub-processor for the attribute values */
    private AbstractProcessor subProcessor = null;

    /** constructs the processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor nto needed here
     */
    public ConceptAttributeFunctionalProcessor(ConceptAttributeFunctionalDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;
        if(definition.subProcessorDefinition != null) {
            subProcessor = definition.subProcessorDefinition.makeProcessor(context,this);}}

    /** This method adds a flatMap to the inputStream which fills the query-array up and extends by the sub-processor
     *
     * @return the extended input stream.
     */
    public Stream<Object[]> getOutputStream() {
        ConceptAttributeFunctionalDefinition def = (ConceptAttributeFunctionalDefinition)definition;
        Concept defConcept                = def.concept;
        int conceptIndex                  = def.conceptIndex;
        ConceptAttribute defAttribute        = (ConceptAttribute)def.attribute;
        int attributeIndex                = def.attributeIndex;
        DataObject defCompareObject       = def.compareObject;
        int compareIndex                  = def.compareIndex;
        Operators defOperator             = def.operator;
        int operatorIndex                 = def.operatorIndex;
        AttributeValueList defConstraints = def.constraints;
        int constraintIndex               = def.constraintsIndex;
        int outputIndex                   = def.outputIndex;
        BiPredicate<Concept,Context> filter = def.filter;

        outputStream = inputStream.flatMap(query-> {
            Concept            concept        = conceptIndex    >= 0 ? (Concept)           query[conceptIndex] :    defConcept;
            ConceptAttribute   attribute      = attributeIndex  >= 0 ? (ConceptAttribute)  query[attributeIndex] :  defAttribute;
            DataObject         compareObject  = compareIndex    >= 0 ? (DataObject)        query[compareIndex] :    defCompareObject;
            Operators          operator       = operatorIndex   >= 0 ? (Operators)         query[operatorIndex] :   defOperator;
            AttributeValueList constraints    = constraintIndex >= 0 ? (AttributeValueList)query[constraintIndex] : defConstraints;
            if(concept       == null) {concept       = defConcept;}
            if(attribute     == null) {attribute     = defAttribute;}
            if(compareObject == null) {compareObject = defCompareObject;}
            if(operator      == null) {operator      = defOperator;}
            if(constraints   == null) {constraints   = defConstraints;}

            Concept otherConcept = (Concept)concept.getFirst(attribute,operator,compareObject,constraints,context);
            if(otherConcept == null) {return Stream.empty();}
            if(filter != null && !filter.test(otherConcept,context)) {return Stream.empty();}
            Stream<Object[]> qstream = queryStream(query);;
            if(outputIndex < 0) {return qstream;}
            query[outputIndex] = otherConcept;
            if(subProcessor == null) {return qstream;}
            subProcessor.setInputStream(qstream);
            return subProcessor.getOutputStream();
        });
        return super.getOutputStream();
    }

    @Override
    public AbstractProcessor setMonitor(boolean monitor) {
        if(subProcessor != null) {subProcessor.setMonitor(monitor);}
        super.setMonitor(monitor);
        return this;}
}
