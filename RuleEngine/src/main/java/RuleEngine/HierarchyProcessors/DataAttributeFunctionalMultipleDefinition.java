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
import Utils.Utilities;

import java.util.Arrays;
import java.util.function.Predicate;

/** This class provides a processor which can access for a concept several functional DataAttributes
 * and filter them simultaneously. The attributes can be predefined or they can be taken from the query array.
 * If both, predefined attributes and attribute indices are defined then the attributes
 * are taken from the query array, if the corresponding index is greater 0.
 * The filter is applied to a corresponding array of attribute values, and therefore can compare the different
 * values.
 */
public class DataAttributeFunctionalMultipleDefinition extends AbstractDefinition {
    Concept concept = null;
    /** the index in the query array where the concept is taken from */
    int conceptIndex;
    /** an array of predefined functional data attributes. */
    DataAttribute[] attributes = null;
    /** an array of indices in the query array to access data attributes */
    int[] attributeIndices = null;
    /** the DataObject against which an attribute value is compared using the specified operator */
    protected DataObject[] compareObjects  = null;
    /** the position in the query array where the compareObject is stored */
    protected int[]        compareIndices   = null;
    /** the operator to be used for comparing the attribute value with the compareObject. */
    protected Operators[] operators       = null;
    /** the position in the query array where the operator is stored */
    protected int[]        operatorIndices  = null;
    /** the constraints for filtering the attribute values */
    protected AttributeValueList[] constraints = null;
    /** the position in the query array where the constraints are stored */
    protected int[]        constraintsIndices = null;

    /** the array of indices where the attribute values are are to be written */
    int[] outputIndices = null;
    /** a predicate for filtering the attribute values. */
    Predicate<DataObject[]> filter = null;


    /** constructs a definition with the only mandatory parameter
     *
     * @param id   an identifier for the processor
     */
    public DataAttributeFunctionalMultipleDefinition(String id) {
        super(id);}

    /** sets the concept for which the attribute value is to be obtained.
     *
     * @param concept the concept for which the attribute value is to be obtained.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setConcept(Concept concept) {
        this.concept = concept;
        return this;}

    /** sets the index in the query array where to find the concept.
     *
     * @param conceptIndex the concept index in the query array
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setConceptIndex(int conceptIndex) {
        assert conceptIndex >= 0;
        this.conceptIndex = conceptIndex;
        return this;}
    /** sets the attributes
     *
     * @param attributes the attributes to be accessed.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setAttributes(DataAttribute[] attributes) {
        assert allFunctional(attributes);
        this.attributes = attributes;
        return this;}


    /** tests if the attributes are all functional
     *
     * @param attributes the attributes to be tested
     * @return true if the attributes are functional.
     */
    private boolean allFunctional(DataAttribute[] attributes) {
        for(DataAttribute attribute :attributes) {if(!attribute.isFunctional()) {return false;}}
        return true;}

    /** sets the indices in the query array where to get the attribute from.
     *
     * @param attributeIndices the indices where to get the attributes from.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setAttributeIndices(int[] attributeIndices) {
        assert allGreaterZero(attributeIndices);
        this.attributeIndices = attributeIndices;
        return this;}

    /** sets the object to be used for comparing the attribute values.
     *
     * @param compareObjects the object to be used for comparing the attribute values.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setCompareObject(Concept[] compareObjects) {
        this.compareObjects = compareObjects;
        return this;}

    /** sets the index in the query array where to find the compareObject
     *
     * @param compareIndices the concept index in the query array
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setCompareIndex(int[] compareIndices) {
        this.compareIndices = compareIndices;
        return this;}


    /** sets the operator to be used for comparing the attribute values.
     *
     * @param operators the operator to be used for comparing the attribute values.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setOperator(Operators[] operators) {
        this.operators = operators;
        return this;}

    /** set the index in the query array where the operator is to be obtained
     *
     * @param operatorIndices the index in the query array where the operator is to be obtained.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setOperatorIndex(int[] operatorIndices) {
        this.operatorIndices = operatorIndices;
        return this;}

    /** sets the constraints to be used for filtering the attribute values.
     *
     * @param constraints the the constraints to be used for filtering the attribute values.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setConstraints(AttributeValueList[] constraints) {
        this.constraints = constraints;
        return this;}

    /** set the index in the query array where to find the constraints.
     *
     * @param constraintsIndices the concept index in the query array
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setConstraintsIndex(int[] constraintsIndices) {
        this.constraintsIndices = constraintsIndices;
        return this;}


    /** sets the indices in the query array where to get the attribute from.
     *
     * @param outputIndices the indices where to get the attributes from.
     * @return this.
     */
    public DataAttributeFunctionalMultipleDefinition setOutputIndices(int[] outputIndices) {
        assert allGreaterZero(outputIndices);
        this.outputIndices = outputIndices;
        return this;}


    /** checks if all indices are greater 0
     *
     * @param indices the indices to be checked.
     * @return true if the indices are greater 0.
     */
    private boolean allGreaterZero(int[] indices) {
        for(int i : indices) {if(i < 0) {return false;}}
        return true;}


    /** sets a filter for answer concepts in the query stream.
     *
     * @param filter the filter to be set.
     * @return this
     */
    public DataAttributeFunctionalMultipleDefinition setFilter(Predicate<DataObject[]> filter) {
        this.filter = filter;
        return this;}

    /**
     * @return 'attributes(dfm)' plus names or indices
     */
    @Override
    public String toString() {
        String s = "attributes(dfm) " + getName() + " ";
        if(attributes != null) {s += Utilities.join(attributes,",",(a-> ((Attribute) a).getName()));}
        else {s += Arrays.toString(attributeIndices);}
        return s;}

    /** constructs a processor
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processing unit
     * @return the new processor.
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new DataAttributeFunctionalMultipleProcessor(this,context,parentProcessor);
    }
}
