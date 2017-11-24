package Concepts;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import ConcreteDomain.ConcreteObject;
import MISC.Context;

import java.util.function.Function;
import java.util.stream.Stream;

/** This is the abstract top class for attribute values.
 * Attribute values can be just single concrete values like integers, or concepts.
 * They can be constrained values like in Peter married-to Mary years 2000-2010
 * "years 2000-2010" is the constraint.
 * <br>
 * They also can be lists of such values.
 * Created on 11.03.16.
 */
public abstract class AVObject {

    /** @return true if there is no attribute value */
    boolean isEmpty() {return false;}

    /** @return the attribute value if there is just a single value.
     */
    public abstract DataObject get();

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
    abstract DataObject getFirst(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context);

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
    abstract <T> T find(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject,T> function);

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
    abstract Stream<DataObject> stream(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context);

    /** This method compares two sets of possibly constrained data.
     * It checks:<br>
     *     - thisValue operator otherValue <br>
     *     - thisConstraints implies otherConstraints <br>
     *
     *
     * @param thisValue        a first DataObject
     * @param thisConstraints  a first set of constraints
     * @param operator         an operator (or null)
     * @param otherValue       another DataObject
     * @param otherConstraints another set of constraints
     * @param context          where the objects live in
     * @return                 true if the two checks return true.
     */
    protected static boolean implies(DataObject thisValue, Operators operator, DataObject otherValue,
                                     AttributeValueList thisConstraints, AttributeValueList otherConstraints,
                                     Context context) {
        assert (operator == null) == (otherValue == null);
        Boolean okay = true;
        if(operator != null) {
            okay = (thisValue instanceof ConcreteObject) ?
                    ((ConcreteObject) thisValue).compare(operator, (ConcreteObject) otherValue) :
                    ((Concept) thisValue).compare(operator, (Concept) otherValue, context);}
        if(!okay) {return false;}
        if(thisConstraints == null || otherConstraints == null) {return true;}
        return thisConstraints.implies(otherConstraints,context);
    }
}
