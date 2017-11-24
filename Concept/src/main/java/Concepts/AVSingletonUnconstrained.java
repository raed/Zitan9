package Concepts;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Attributes.AttributeValueList;
import ConcreteDomain.ConcreteObject;
import MISC.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Stream;

/** This is just a single unconstrained attribute value.
 * It can be a Concept or a ConcreteDomain object
 * Created on 11.03.16.
 */
public class AVSingletonUnconstrained extends AVSingleton implements Serializable {
    /** the actual value */
    private DataObject value;

    /** constructs a new object with the given value.
     *
     * @param value the attribute value.*/
    public AVSingletonUnconstrained(DataObject value) {this.value = value;}


    /** @return the attribute value */
    public DataObject get() {return value;}

    /** sets the value if constraints == null.
     *
     * @param value      the value to be set
     * @param constraint not needed here
     */
    public void setValue(DataObject value, AttributeValueList constraint)  {
        this.value = value;}

    /** compares 'value' with object's value.
     *
     * @param object the object to be compared.
     * @return true if the two are equal.
     */
    public boolean equals(Object object) {
        if(object == null || object.getClass() != AVSingletonUnconstrained.class) {return false;}
        return value.equals(((AVSingletonUnconstrained)object).value);
    }

    /** Returns the value, but if the operator != null, it checks 'value operator otherValue' first.
     *
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue'
     * @param otherConstraints not needed
     * @param context the context
     * @return the value if it satisfies the condition, or null.
     * */
    @Override
    DataObject getFirst(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        assert (operator == null) == (otherValue == null);
        if (operator == null) {return value;}
        Boolean result = (value instanceof ConcreteObject) ?
                ((ConcreteObject)value).compare(operator,(ConcreteObject)otherValue) :
                ((Concept)value).compare(operator,(Concept)otherValue,context);
        return (result != null && result) ? value : null;}


    /** If operator != null, it checks the condition 'value operator otherValue', and then applies the function.
     *
     * @param <T> the result type of the function
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints not needed
     * @param context the context
     * @return the result of the function application, which may be null.
     */
    @Override
    <T> T find(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject,T> function) {
        DataObject result = getFirst(operator,otherValue,otherConstraints,context);
        return (result == null) ? null : function.apply(result);}

    /** If operator != null, it checks the condition 'value operator otherValue', and then generates a stream of the single value.
     *
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return the generated stream (possibly empty), or null.
     */
    @Override
    Stream<DataObject> stream(Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context) {
        DataObject result = getFirst(operator,otherValue,otherConstraints,context);
        return (result == null) ? null : Stream.of(result);}

    /**@return the attribute value as string. */
    @Override
    public String toString() {return value.toString();}

    /** This method is necessary because a value can be a concept, and concepts need to be unique in the current context.
     *
     * @param out an ObjectOutputStream for writing the value.
     * @throws IOException if sonething goes wrong.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(value);}

    /** This method reconstructs the value.
     * If the value is a concept, it ensures that there are no duplicates in the current context.
     *
     * @param in an ObjectInputStream for reading the objects.
     * @throws IOException  if reading the object goes wrong
     * @throws ClassNotFoundException should never be thrown.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        value = (DataObject)in.readObject();
        if(value instanceof Concept) {
            Concept concept = Context.currentContext.getConcept(((Concept) value).getName());
            if(concept != null) {value = concept;}
            else {Context.currentContext.putConcept((Concept)value);}}}
}
