package ConcreteDomain.AtomicTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;

import java.io.Serializable;

/** This is just a wrapper for a Boolean value.
 *
 */
public class BooleanObject extends AtomicObject implements Serializable {
    /** the actual value */
    public boolean value;

    public static BooleanObject trueObject = new BooleanObject(true);
    public static BooleanObject falseObject = new BooleanObject(false);

    /** the empty constructor */
    public BooleanObject() {}

    /** the constructor with the boolean value
     *
     * @param value the boolean value.
     */
    public BooleanObject(boolean value) {this.value = value;}
    
    /** returns the Boolean value
     * 
     * @return the Boolean value
     */
    @Override
    public Object get() {return value;}

    /** @return the boolean value as boolean */
    public boolean getValue() {return value;}
    
    /** returns "true" or "false"
     * 
     @return "true" or "false".*/
    @Override
    public String toString() {return Boolean.toString(value);}
    
    /** checks the boolean values for equality.
     * 
     * @param object the other object to be checked
     * @return true if the two boolean values are equal
     */
    @Override
    public boolean equals(Object object) {
        return (object != null) && (object instanceof BooleanObject) && value == ((BooleanObject)object).value;}

    /** generates the hash code of the boolean value
     * 
     * @return the hash code of the boolean value.
     */
    @Override
    public int hashCode() {return Boolean.hashCode(value);}

    /** identity of BooleanObjects
     *
     * @param operator just EQUALS
     * @param other the other object
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, BooleanObject other) {
        switch(operator) {
            case EQUALS:  return value == other.value;}
        return null;}

    static {
        ConcreteObject.addCompareMethod(BooleanObject.class);}




}
