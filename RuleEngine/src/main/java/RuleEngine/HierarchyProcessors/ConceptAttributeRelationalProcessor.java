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

/** This is the processor for relational concept attributes
 */
public class ConceptAttributeRelationalProcessor extends AbstractProcessor {
    /** the context for the objects */
    private Context context;
    /** a processor for the attribute values */
    private AbstractProcessor subProcessor;

    /** constructs a new processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor not needed here
     */
    public ConceptAttributeRelationalProcessor(ConceptAttributeRelationalDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;
        if(definition.subProcessorDefinition != null) {
            subProcessor = definition.subProcessorDefinition.makeProcessor(context,this);}
    }

    /** appends a flatMap to the inputStream.
     * This map extends the query stream by the attribute values and the results of the sub-processor
     *
     * @return the extended input stream.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        ConceptAttributeRelationalDefinition def = (ConceptAttributeRelationalDefinition) definition;
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

        outputStream = inputStream.flatMap(query -> {
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

            Stream<DataObject> otherConcepts = concept.stream(attribute,operator,compareObject,constraints,context);
            if(otherConcepts == null) {return Stream.empty();}
            if(filter != null) {otherConcepts = otherConcepts.filter(concep-> filter.test((Concept)concep,context));}

            saveQuery(query);
            Stream<Object[]> auxStream = otherConcepts.map(concep -> {
                restoreQuery(query);
                query[outputIndex] = concep;
                return query;});

            if(subProcessor == null) {return auxStream;}
            subProcessor.setInputStream(auxStream);
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
