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

/** This class provides the processor for accessing multiple functional data attributes simultaneusly.
 */
public class DataAttributeFunctionalMultipleProcessor extends AbstractProcessor {
    /** the context for the objects */
    private Context context;

    /** constructs the processor
     *
     * @param definition its definition
     * @param context    the context
     * @param parentProcessor not needed here
     */

    public DataAttributeFunctionalMultipleProcessor(AbstractDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;
    }

    /** The method adds a filter to the input stream which filters the query array or writes the attribute values into the query array
     *
     * @return the inputStream with an extra filter.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        DataAttributeFunctionalMultipleDefinition def = (DataAttributeFunctionalMultipleDefinition)definition;
        Concept defConcept                = def.concept;
        int conceptIndex                  = def.conceptIndex;
        DataAttribute[] defAttributes       = def.attributes;
        int[] attributeIndices     = def.attributeIndices;
        DataObject[] defCompareObjects       = def.compareObjects;
        int[] compareIndices                  = def.compareIndices;
        Operators[] defOperators             = def.operators;
        int[] operatorIndices                 = def.operatorIndices;
        AttributeValueList[] defConstraints = def.constraints;
        int[] constraintIndices               = def.constraintsIndices;
        int attributesSize = (defAttributes != null) ? defAttributes.length : attributeIndices.length;
        int[] outputIndices        = def.outputIndices;
        Predicate<DataObject[]> filter = def.filter;
        DataObject[] values = new DataObject[attributesSize];

        outputStream = inputStream.filter(query-> {
            Concept concept = conceptIndex >= 0 ? (Concept)query[conceptIndex] : defConcept;
            if(concept == null) {concept = defConcept;}
            if(concept == null) {return false;}

            for(int i = 0; i < attributesSize; ++i) {
                DataAttribute attribute  = (attributeIndices  != null && attributeIndices[i]  >= 0) ? (DataAttribute)query[attributeIndices[i]] : defAttributes[i];
                if(attribute     == null) {attribute     =  defAttributes[i];}
                if(attribute == null) {return false;}
                DataObject  compareObject  = null;
                if(compareIndices != null && compareIndices[i] >= 0) {compareObject =  (DataObject)query[compareIndices[i]];}
                if(compareObject == null && defCompareObjects != null) {compareObject = defCompareObjects[i];}
                Operators operator = null;
                if(operatorIndices != null && operatorIndices[i] >= 0) { operator =  (Operators)query[operatorIndices[i]];}
                if(operator == null && defOperators != null) {operator = defOperators[i];}
                AttributeValueList constraints = null;
                if(constraintIndices != null && constraintIndices[i] >= 0) {constraints =  (AttributeValueList)query[constraintIndices[i]];}
                if(constraints == null && defConstraints != null) {constraints = defConstraints[i];}
                values[i] = concept.getFirst(attribute,operator,compareObject,constraints,context);}
            if(filter != null &&!filter.test(values)) {return false;}

            if(outputIndices != null) {
                for(int i = 0; i < attributesSize; ++i) {
                    int index = outputIndices[i];
                    if(index > 0) {query[index] = values[i];}}}
            return true;
        });
        return super.getOutputStream();
    }


}
