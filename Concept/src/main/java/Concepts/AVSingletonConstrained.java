package Concepts;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import MISC.Context;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

/** This class represents constrained functional attribute values.
 * Example: Peter marriedto (Mary years 2010-2015, Jane years 2015-2017)
 * New constrained values are added at the end of the list.
 * The retrieval of a value therefore always starts at the end of the list.
 * Created by on 11.03.16.
 */
public class AVSingletonConstrained extends AVSingleton implements Serializable {
    /** the attribute values */
    ArrayList<DataObject> values = new ArrayList<>();
    /** the corresponding constraints */
    ArrayList<AttributeValueList> constraints = new ArrayList<>();

    /** constructs an empty list */
    public AVSingletonConstrained() {}

    /** constructs a new list with a single value-constraint tuple
     *
     * @param value      the actual value
     * @param constraint the constraint.
     */
    public AVSingletonConstrained(DataObject value, AttributeValueList constraint) {
        values.add(value);
        constraints.add(constraint);}

    /** @return true if there is no value in the list */
    public boolean isEmpty() {return values.isEmpty();}

    /** adds a new constrained value at the end of the list.
     *
     * @param value      the attribute value
     * @param constraint the corresponding constraint
     */
    public void setValue(DataObject value, AttributeValueList constraint) {
        values.add(value);
        constraints.add(constraint);}

    /** @return the last attribute value, or null*/
    public DataObject get() {
        return values.isEmpty() ? null : values.get(values.size()-1);}

    /** compares the two objects for equality
     *
     * @param object the object to be compared
     * @return true if the two objects ar equal.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || object.getClass() != AVSingletonConstrained.class) {return false;}
        if(this == object) {return true;}
        AVSingletonConstrained other = (AVSingletonConstrained)object;
        int size = values.size();
        if(size != other.values.size()) {return false;}
        for(int i = 0; i < size; ++i) {
            if(!values.get(i).equals(other.values.get(i))) {return false;}
            if(!constraints.get(i).equals(other.constraints.get(i))) {return false;}}
        return true;}

    /** returns the first value satisfying the constraints
     *
     * @param operator for comparing value with otherValue (may be null)
     * @param otherValue to be compared with value
     * @param otherConstraints the constraint to be satisfied
     * @param context where the objects live in
     * @return the first value satisfying the constraints
     */
    @Override
    public DataObject getFirst(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        if(values.isEmpty()) {return null;}
        for(int i = values.size()-1; i >= 0; --i) {
            DataObject value = values.get(i);
            if(AVObject.implies(value,operator,otherValue,constraints.get(i),otherConstraints,context)) {return value;}}
        return null;}

    /** returns the first non-null function application to the values satisfying the constraints
     *
     * @param operator for comparing value with otherValue (may be null)
     * @param otherValue to be compared with value
     * @param otherConstraints  the constraint to be satisfied
     * @param context  where the objects live in.
     * @param function a function to be applied to the values.
     * @param <T> the return type of the function
     * @return the first non-null function application to the values satisfying the constraints, or null
     */
    @Override
    public <T> T find(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject, T> function) {
        DataObject result = getFirst(operator,otherValue,otherConstraints,context);
        return (result == null) ? null : function.apply(result);}

    /** returns a stream of values where the constraints overlap.
     * Example: Peter marriedto (Mary years 2010-2015, Jane years 2016-2017)
     * If the constraint is "years 2012-2018" then both, Mary and Jane are returned.
     *
     * @param operator for comparing value with otherValue (may be null)
     * @param otherValue to be compared with value
     * @param otherConstraints the constraint which overlaps with the strored constraint
     * @param context where the objects live in
     * @return the stream of values where the constraints overlap.
     */
    @Override
    public Stream<DataObject> stream(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        DataObject result = getFirst(operator,otherValue,otherConstraints,context);
        return (result == null) ? null : Stream.of(result);}



    /** @return all the values with their constraints */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < values.size(); ++i) {
            s.append(values.get(i).toString()).append(" if ").append(constraints.get(i).toString()).append("\n");}
        s.deleteCharAt(s.length()-1);
        return s.toString();}

    /** just writes the object to the output stream
     *
     * @param out the ObjectInputStream
     * @throws IOException if something goes wrong
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(values);
        out.writeObject(constraints);}


    /** reads the object from the input stream.
     * It makes sure that concepts are not duplicated.
     * Unknown concepts are inserted into the currentContext.
     *
     * @param in the ObjectInputStream
     * @throws IOException if something goes wrong
     * @throws ClassNotFoundException should not happen.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        values = (ArrayList<DataObject>)in.readObject();
        constraints = (ArrayList<AttributeValueList>)in.readObject();
        for(int i = 0; i < values.size(); ++i) {
            DataObject object = values.get(i);
            if(object instanceof Concept) {
                Concept concept = Context.currentContext.getConcept(((Concept) object).getName());
                if(concept == null) {Context.currentContext.putConcept((Concept)object);}
                else {values.set(i,concept);}}}}
}
