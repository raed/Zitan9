package Concepts;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import MISC.Context;
import Utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

/** This class represents lists of attribute values.
 * Each attribute value is a constrained or unconstrained singleton attribute value.
 */
public class AVList extends AVObject implements Serializable {
    /** the list of attribute values */
    ArrayList<AVSingleton> values = new ArrayList<>();

    /** contructs an empty list
     */
    public AVList() {}

    /** constructs a list with a single value
     *
     * @param value the value to be added to the list
     */
    public AVList(AVSingleton value) {values.add(value);}

    /** @return true if there is no value in the list */
    public boolean isEmpty() {return values.isEmpty();}

    /** adds a new attribute value.
     *
     * @param value the value to be added.*/
    public void addValue(AVSingleton value) {values.add(value);}

    /** removes the value.
     *
     * @param value
     */
    public void removeValue(AVSingleton value) {values.remove(value);}

    /** exchanges the old value with the new value.
     * If the old value is not in the list, the new value is just added.
     *
     * @param oldValue the old value
     * @param newValue the new value
     * @return true if the values had been exchanged.
     */
    public boolean exchangeValue(AVSingleton oldValue, AVSingleton newValue) {
        int index = values.indexOf(oldValue);
        if(index < 0) {values.add(newValue); return false;}
        values.set(index, newValue);
        return true;}


    /** @return the first element in the list */
    public DataObject get() {
        return values.isEmpty() ? null : values.get(0).get();}

    /** checks equality of two lists
     *
     * @param object the other object to be compared
     * @return true if the lists are equal
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || object.getClass() != AVList.class || values.size() != ((AVList)object).values.size()) {return false;}
        for(AVSingleton avs : values) {
            if(!((AVList)object).values.contains(avs)) {return false;}}
        for(AVSingleton avs : ((AVList)object).values) {
            if(!values.contains(avs)) {return false;}}
        return true;
    }

    /** returns the first value satisfying the constraint
     *
     * @param operator for comparing value with otherValue (may be null)
     * @param otherValue to be compared with value
     * @param otherConstraints to be satisfied
     * @param context where the objects live in
     * @return the first value satisfying the constraint, or null.
     */
    @Override
    DataObject getFirst(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        if(values.isEmpty()) {return null;}
        for(AVSingleton value : values) {
            DataObject first = value.getFirst(operator,otherValue,otherConstraints,context);
            if(first != null) {return first;}}
        return null;}

    /** returns the first non-null function application to a value satisfying the constraint.
     *
     * @param operator for comparing value with otherValue (may be null)
     * @param otherValue to be compared with value
     * @param otherConstraints the constraints to be satisfied.
     * @param context  where the objects live in.
     * @param function a function to be applied to the values.
     * @param <T> the return type of the function
     * @return the first non-null function application to a value satisfying the constraint.
     */
    @Override
    <T> T find(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject,T> function) {
        for(AVSingleton value : values) {
            T first = value.find(operator,otherValue,otherConstraints,context,function);
            if(first != null) {return first;}}
        return null;}


    /** returns the stream of attribute values satisfying the constraint.
     *
     * @param operator for comparing value with otherValue (may be null)
     * @param otherValue to be compared with value
     * @param otherConstraints the constraints to be satisfied
     * @param context where the objects live in
     * @return the stream of attribute values satisfying the constraint.
     */
    @Override
    Stream<DataObject> stream(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        if(values.isEmpty()) {return null;}
        return values.stream().
                map(value->value.getFirst(operator,otherValue,otherConstraints,context)).
                filter(value-> value !=null);}

    /** @return the values in separate lines. */
    @Override
    public String toString() {return Utilities.join(values,"\n",(v->v.toString()));}
}
