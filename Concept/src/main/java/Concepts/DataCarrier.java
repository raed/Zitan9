package Concepts;

import AbstractObjects.DataObject;
import Attributes.Attribute;
import Attributes.AttributeValueList;
import MISC.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

/** Instances of this class can be used to fill them with data, transfer them via sockets,
 * and inject them at the destination system into a context.
 *
 * The DataCarrier is filled by the add... methods.
 * It then can be serialized, sent to a destination, deserialized.
 * By calling addToContext the data are then inserted into the Context of the target system.
 *
 * Created by ohlbach on 02.04.2016.
 */
public class DataCarrier implements Serializable {
    /** just a list of concept identifiers */
    private ArrayList<String> concepts = new ArrayList<>();
    /** a list of pairs: superconcept,subconcept */
    private ArrayList<String> conceptHierarchy = new ArrayList<>();
    /** a list of attributes (they must be serializable) */
    private ArrayList<Attribute> attributes = new ArrayList<>();
    /** a list of pairs: superattribute,subattribute */
    private ArrayList<String> attributeHierarchy = new ArrayList<>();
    /** a list of quintuples: concept identifier, attribute identifier, value, scope, constraint */
    private ArrayList<Object> attributeValues = new ArrayList<>();

    /** injects the data into a context.
     *
     * @param context the context into which the data is to be injected
     * @return null or an error message.
     */
    public String addToContext(Context context) {
        StringBuilder errors = new StringBuilder();

        for(String conceptId : concepts) {
            String[] parts = conceptId.split("@");
            Concept concept = context.getConcept(parts[0]);
            if(concept == null) {
                switch(parts[1]){
                    case "SetConcept":  new IndividualConcept(parts[0],context); break;
                    case "IndividualConcept": new IndividualConcept(parts[0],context); break;}}}

        int length = conceptHierarchy.size();
        for(int i = 0; i < length; i += 2) {
            String superConceptId = conceptHierarchy.get(i);
            String subConceptId = conceptHierarchy.get(i+1);
            Concept superConcept = context.getConcept(superConceptId);
            if(superConcept == null) {errors.append("Unknown concept: ").append(superConceptId);}
            Concept subConcept = context.getConcept(subConceptId);
            if(subConcept == null) {errors.append("Unknown concept: ").append(subConceptId);}
            if(superConcept != null && subConcept != null) {
                context.conceptHierarchy.addSubnode(superConcept, subConcept);}}

        for(Attribute attribute : attributes) {
            Attribute oldAttribute = context.getAttribute(attribute.getName());
            if(oldAttribute != null) {
                errors.append("Attribute.rel ").append(attribute.getName()).append(" is already known");}
            else {context.putAttribute(attribute);}}

        length = attributeHierarchy.size();
        for(int i = 0; i < length; i += 2) {
            String superAttributeId = attributeHierarchy.get(i);
            String subAttributeId = attributeHierarchy.get(i+1);
            Attribute superAttribute = context.getAttribute(superAttributeId);
            if(superAttribute == null) {errors.append("Unknown attribute: ").append(superAttributeId);}
            Attribute subAttribute = context.getAttribute(subAttributeId);
            if(subAttribute == null) {errors.append("Unknown attribute: ").append(subAttributeId);}
            if(superAttribute != null && subAttribute != null) {
                context.attributeHierarchy.addSubnode(superAttribute,subAttribute);}}

        length = attributeValues.size();
        for(int i = 0; i < length; i += 5) {
            String conceptId = (String)attributeValues.get(i);
            String attributeId = (String)attributeValues.get(i+1);
            DataObject value = null;
            Object valu = attributeValues.get(i+2);
            if(valu instanceof String) {
                value = context.getConcept((String)valu);
                if(value == null) {errors.append("Unknown concept: ").append(valu); continue;}}
            else {value = (DataObject)valu;}
            Scope scope = (Scope)attributeValues.get(i+3);
            AttributeValueList constraint = (AttributeValueList)attributeValues.get(i+4);
            Concept concept = context.getConcept(conceptId);
            if(concept == null) {errors.append("Unknown concept: ").append(conceptId);}
            Attribute attribute = context.getAttribute(attributeId);
            if(attribute == null) {errors.append("Unknown attribute: ").append(attributeId);}
            if(concept == null || attribute == null) {continue;}
            if(constraint == null) {
                concept.add(attribute,value,scope,context,errors);}
            else {
                //concept.add(attribute,value,context,scope,
                //constraint.transform(Attribute.rel.Id2Attribute(context,errors),DataObject.Id2Value(context,errors)));
            }}
        return (errors.length() == 0) ? null : errors.toString();}


    /** adds some concepts into the context.
     *
     * @param concepts the concepts to be added
     * @return 'this'.
     */
    public DataCarrier addConcept(Concept... concepts) {
        for(Concept concept : concepts) {
            String[] parts =  concept.getClass().getName().split("\\.");
            this.concepts.add(concept.getName() + "@" + parts[parts.length-1]);}
        return this;}



    /** adds a concept-relation into the context.
     *
     * @param superconcept the superconcept of the relation
     * @param subconcept the subconcept of the relation
     * @return 'this'.
     */
    public DataCarrier addSuperSubConcept(Concept superconcept, Concept subconcept) {
        conceptHierarchy.add(superconcept.getName());
        conceptHierarchy.add(subconcept.getName());
        return this;}

    /** adds a concept-relation into the context.
     *
     * @param superconceptId the superconcept-identifier of the relation
     * @param subconceptId the subconcept-identifier of the relation
     * @return 'this'.
     */
    public DataCarrier addSuperSubConcept(String superconceptId, String subconceptId) {
        conceptHierarchy.add(superconceptId);
        conceptHierarchy.add(subconceptId);
        return this;}

    /** adds some attributes into the context.
     *
     * @param attributes the attributes to be added
     * @return 'this'.
     */
    public DataCarrier addAttribute(Attribute... attributes) {
        for(Attribute attribute : attributes) {
            //attribute.prepareForSerilialization();
            this.attributes.add(attribute);}
        return this;}

    /** adds an attribute hierarchy relation to the DataCarrier
     *
     * @param superattribute the super-attribute
     * @param subattribute  the sub-attribute
     * @return 'this'
     */
    public DataCarrier addSuperSubAttribute(Attribute superattribute, Attribute subattribute) {
        attributeHierarchy.add(superattribute.getName());
        attributeHierarchy.add(subattribute.getName());
        return this;}

    /** adds a concept value to the DataCarrier
     *
     * @param concept    the concept
     * @param attribute  the attribute
     * @param value      the value
     * @param scope      the scope status
     * @param constraint the constraint (or null)
     * @return           the DataCarrier
     */
    public DataCarrier addValue(Concept concept, Attribute attribute, DataObject value,
                                Scope scope, AttributeValueList constraint) {
        attributeValues.add(concept.getName());
        attributeValues.add(attribute.getName());
        attributeValues.add((value instanceof Concept) ? ((Concept) value).getName() : value);
        attributeValues.add(scope);
        if(constraint == null) {attributeValues.add(null);}
        else {
            //Function<DataObject,Object> f = (val-> (val instanceof Concept) ? ((Concept)val).applicationName : val);
            //attributeValues.add(constraint.transform((att->((Attribute.rel)att).applicationName),f));
        }
        return this;}

    /** adds a concept value to the DataCarrier
     *
     * @param conceptId    the concept-identifier
     * @param attributeId  the attribute-identifier
     * @param value      the value
     * @param scope      the scope status
     * @param constraint the constraint (or null)
     * @return           the DataCarrier
     */
    public DataCarrier addValue(String conceptId, String attributeId, DataObject value,
                                Scope scope, AttributeValueList constraint) {
        attributeValues.add(conceptId);
        attributeValues.add(attributeId);
        attributeValues.add((value instanceof Concept) ? ((Concept) value).getName() : value);
        attributeValues.add(scope);
        if(constraint == null) {attributeValues.add(null);}
        else {
            Function<DataObject,Object> f = (val-> (val instanceof Concept) ? ((Concept) val).getName() : val);
            //attributeValues.add(constraint.transform((att->((Attribute.rel)att).applicationName),f));
        }
        return this;}



}
