package Concepts;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import MISC.Context;

import java.util.function.Function;
import java.util.stream.Stream;


/** This class represents the attribute values of some concepts or individuals.
 * <p>
 * Example: IndividualConcept car, attribute: number-of-wheels,  then AttributeValue could be just the number 4.
 * The AttributeValue can be just a single value or a list of values.
 * The values can be constrained.
 * </p>
 * <p>
 * Example:<br>
 * IndividualConcept House, attribute price = 200000, constraint year = 2015 <br>
 * which means the price attribute was valid in the year 2015
 * </p>
 * <p>
 * Other example:<br>
 * IndividualConcept House, attribute owner = Mayer, constraint url = LandRegistry<br>
 * which means that according to the land registry, the owner of the house was Mayer.
 * </p>
 * <p>
 * The AttributeValues have a flag 'scope'.<br>
 * scope = LOCAL means that the value holds for the concept itself. <br>
 * scope = DEFAULT means the the value is the default value for the concept's subconcepts. <br>
 * scope = ALL means the the value holds for the concept and the concept's subconcepts. <br>
 * </p>
 * <p>
 * Example for concepts: <br>
 * IndividualConcept BMW, attribute share-value, local = true indicates an attribute for BMW, not for its cars.
 * </p>
 * <p>
 * AttributeValues for functional attributes may yet contain several entries,
 * but in this case the constraints must be disjoint.
 * </p>
 * <p>
 * Example: <br>
 * attribute price: <br>
 *  value 10000 constraint year = 2000 <br>
 *  value 20000 constraint year = 2001 <br>
 * </p>
 */
public class AttributeValue {
    /** indicates that the attribute is for the concept itself, not for its instances */
    public Scope scope;   // default is a keyword
    /** contains the attribute values (maybe with constraints). */
    private AVObject value = null;

    /** constructs a new AttributeValue with scope = LOCAL */
    public AttributeValue(){scope = Scope.LOCAL;}

    /** constructs a new AttributeValue
     *
     * @param scope declares the scope of the attribute value.*/
    public AttributeValue(Scope scope){this.scope = scope;}

    /** constructs a new unconstrained AttributeValue with a given value.
     *
     * @param scope declares the scope of the attribute value
     * @param value     the actual value (IndividualConcept or ConcreteDomain) without constraints.*/
    public AttributeValue(AVObject value, Scope scope) {
        this.scope = scope;
        this.value = value;}


    /** @return true if the value is default. */
    public boolean isDefault() {return scope == Scope.DEFAULT;}

    /** sets the value.
     *
     * @param value the value to be set
     * @param scope LOCAL,DEFAULT or ALL*/
    public void setValue(AVObject value, Scope scope) {
        this.scope = scope;
        this.value = value;}

    /** @return the value. */
    public AVObject get() {return value;}

    /**  @return true if the value is null.*/
    public boolean isEmpty() {return value == null;}

    /** compares two AttributeValues for equality
     *
     * @param object the other object
     * @return true if the two object are equal.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || object.getClass() != AttributeValue.class) {return false;}
        AttributeValue other = (AttributeValue)object;
        return scope == other.scope && value.equals(other.value);}

    /** Returns the first value which satisfies the given conditions.
     * These are:<br>
     *     - if operator != null then 'value operator otherValue' must return true <br>
     *     - if otherConstraints != null the 'this.constraints implies otherConstraints' must return true.
     *
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return the first value that satisfies the conditions, or null.*/
    public DataObject getFirst(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        return (value == null) ? null : value.getFirst(operator,otherValue,otherConstraints,context);}

    /** searches through the attribute values to find the first one satisfying the conditions and where the function returns not null.
     *
     *  The conditions are:<br>
     *     - if operator != null then 'value operator otherValue' must return true <br>
     *     - if otherConstraints != null the 'this.constraints implies otherConstraints' must return true.
     *
     * @param <T> the result type of the function
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return the result of the first non-null function application.
     */
    public <T> T find(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject,T> function) {
        return (value == null) ? null : value.find(operator,otherValue,otherConstraints,context,function);}

    /** turns those attribute values for which satisfy the conditions into a stream of DataObjects.
     * The stream may be empty.
     *
     *  The conditions are:<br>
     *     - if operator != null then 'value operator otherValue' must return true <br>
     *     - if otherConstraints != null the 'this.constraints implies otherConstraints' must return true.
     * <br>
     *
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return the generated stream (possibly empty), or null.
     */
    public Stream<DataObject> stream(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        return (value == null) ? null : value.stream(operator,otherValue,otherConstraints,context);}

    /** returns the values as a string of lines.
     * If the values are not local the string starts with 'd:' (default).
     *
     * @return the string representation of the values.*/
    @Override
    public String toString() {return (scope != Scope.LOCAL ? scope.toString() + " " : "") + value.toString();}
}
