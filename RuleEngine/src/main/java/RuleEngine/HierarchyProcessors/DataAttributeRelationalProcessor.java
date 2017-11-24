package RuleEngine.HierarchyProcessors;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.Attribute;
import Attributes.AttributeValueList;
import Attributes.DataAttribute;
import Concepts.Concept;
import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.function.Predicate;
import java.util.stream.Stream;

/** This is the processor for relational data attributes.
 * It extends the input stream by inserting the filtered attribute values.
 */
public class DataAttributeRelationalProcessor extends AbstractProcessor {
    /** the context for the objects */
    private Context context;

    /** constructs the processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor not needed
     */
    public DataAttributeRelationalProcessor(AbstractDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;}

    /** the method extends the input stream by filling all filtered attribute values into the query.
     *
     * @return the extended input stream.
     */
    public Stream<Object[]> getOutputStream() {
        DataAttributeRelationalDefinition def = (DataAttributeRelationalDefinition)definition;
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

        outputStream = inputStream.flatMap(query->{
            Concept            concept        = conceptIndex    >= 0 ? (Concept)           query[conceptIndex] :    defConcept;
            DataAttribute      attribute      = attributeIndex  >= 0 ? (DataAttribute)     query[attributeIndex] :  defAttribute;
            DataObject         compareObject  = compareIndex    >= 0 ? (DataObject)        query[compareIndex] :    defCompareObject;
            Operators          operator       = operatorIndex   >= 0 ? (Operators)         query[operatorIndex] :   defOperator;
            AttributeValueList constraints    = constraintIndex >= 0 ? (AttributeValueList)query[constraintIndex] : defConstraints;
            Predicate<DataObject> filter = def.filter;
            if(concept       == null) {concept       = defConcept;}
            if(attribute     == null) {attribute     = defAttribute;}
            if(compareObject == null) {compareObject = defCompareObject;}
            if(operator      == null) {operator      = defOperator;}
            if(constraints   == null) {constraints   = defConstraints;}

            if(concept == null) {return Stream.empty();}
            Attribute att = null;
            if(att == null) {att = attribute;}
            if(att == null) {return Stream.empty();}

            Stream<DataObject> stream = concept.stream(att,operator,compareObject,constraints,context);
            if(stream == null) {return Stream.empty();}
            if(filter != null) {stream = stream.filter(filter);}

            saveQuery(query);
            return stream.map(concep-> {
                restoreQuery(query);
                query[outputIndex] = concep;
                return query;});
        });
        return super.getOutputStream();
    }

}
