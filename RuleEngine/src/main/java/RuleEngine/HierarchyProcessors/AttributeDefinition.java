package RuleEngine.HierarchyProcessors;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.Attribute;
import Attributes.AttributeValueList;
import Concepts.Concept;
import RuleEngine.AbstractDefinition;

/** This is an abstract superclass for most of the AttributeProcessor-Definitions.
 * It is used to store a number of common parameters for these classes.
 * <br>
 * Except outputIndex, ll parameters come in pairs:<br>
 * - one can either specify the parameter explicitly in the definition, or <br>
 * - one can take the parameter from a certain position in the query array.
 * <br>
 */
public abstract class AttributeDefinition extends AbstractDefinition {
    /** the concept for which the attribute value is to be obtained */
    protected Concept    concept        = null;
    /** the position in the query array where the concept is stored */
    protected int        conceptIndex   = -1;
    /** the attribute for which the value is to be obtained */
    protected Attribute  attribute      = null;
    /** the position in the query array where the attribute is stored */
    protected int        attributeIndex = -1;
    /** the DataObject against which an attribute value is compared using the specified operator */
    protected DataObject compareObject  = null;
    /** the position in the query array where the compareObject is stored */
    protected int        compareIndex   = -1;
    /** the operator to be used for comparing the attribute value with the compareObject. */
    protected Operators  operator       = null;
     /** the position in the query array where the operator is stored */
    protected int        operatorIndex  = -1;
    /** the constraints for filtering the attribute values */
    protected AttributeValueList constraints = null;
     /** the position in the query array where the constraints are stored */
    protected int        constraintsIndex = -1;
    /** the index where to put the attribute value into the query array. */
    protected int       outputIndex;

    /** constructs an AttributeProcessor definition and gives it an identifier.
     *
     * @param id the identifier for the definition.
     */
    public AttributeDefinition(String id) {
        super(id);}

    /** sets the concept for which the attribute value is to be obtained.
     *
     * @param concept the concept for which the attribute value is to be obtained.
     * @return this.
     */
    public AttributeDefinition setConcept(Concept concept) {
        this.concept = concept;
        return this;}

    /** sets the index in the query array where to find the concept.
     *
     * @param conceptIndex the concept index in the query array
     * @return this.
     */
    public AttributeDefinition setConceptIndex(int conceptIndex) {
        assert conceptIndex >= 0;
        this.conceptIndex = conceptIndex;
        return this;}

    /** sets the concept for which the attribute value is to be obtained.
     *
     * @param attribute the concept for which the attribute value is to be obtained.
     * @return this.
     */
    public AttributeDefinition setAttribute(Attribute attribute) {
        this.attribute = attribute;
        return this;}

    /** sets the index in the query array where to find the attribute.
     *
     * @param attributeIndex the concept index in the query array
     * @return this.
     */
    public AttributeDefinition setAttributeIndex(int attributeIndex) {
        assert attributeIndex >= 0;
        this.attributeIndex = attributeIndex;
        return this;}

    /** sets the object to be used for comparing the attribute values.
     *
     * @param compareObject the object to be used for comparing the attribute values.
     * @return this.
     */
    public AttributeDefinition setCompareObject(Concept compareObject) {
        this.compareObject = compareObject;
        return this;}

    /** sets the index in the query array where to find the compareObject
     *
     * @param compareIndex the concept index in the query array
     * @return this.
     */
    public AttributeDefinition setCompareIndex(int compareIndex) {
        assert compareIndex >= 0;
        this.compareIndex = compareIndex;
        return this;}


    /** sets the operator to be used for comparing the attribute values.
     *
     * @param operator the operator to be used for comparing the attribute values.
     * @return this.
     */
    public AttributeDefinition setOperator(Operators operator) {
        this.operator = operator;
        return this;}

    /** set the index in the query array where the operator is to be obtained
     *
     * @param operatorIndex the index in the query array where the operator is to be obtained.
     * @return this.
     */
    public AttributeDefinition setOperatorIndex(int operatorIndex) {
        assert operatorIndex >= 0;
        this.operatorIndex = operatorIndex;
        return this;}

    /** sets the constraints to be used for filtering the attribute values.
     *
     * @param constraints the the constraints to be used for filtering the attribute values.
     * @return this.
     */
    public AttributeDefinition setConstraints(AttributeValueList constraints) {
        this.constraints = constraints;
        return this;}

    /** set the index in the query array where to find the constraints.
     *
     * @param constraintsIndex the concept index in the query array
     * @return this.
     */
    public AttributeDefinition setConstraintsIndex(int constraintsIndex) {
        assert constraintsIndex >= 0;
        this.constraintsIndex = constraintsIndex;
        return this;}


    /** set the index in the query array where the value is written into.
     *
     * @param outputIndex the output index in the query array
     * @return this.
     */
    public AttributeDefinition setOutputIndex(int outputIndex) {
        assert outputIndex >= 0;
        this.outputIndex = outputIndex;
        return this;}
}
