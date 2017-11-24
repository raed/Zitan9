package ConcreteDomain.AtomicTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;

import java.io.Serializable;

/** This class is a wrapper for a concrete object of the EnumerationType
 * Example: for the colors: red,green,blue, it might represent green
 *
 */
public class EnumerationObject extends AtomicObject implements Serializable{
    /** the corresponding type applicationName */
    public String applicationName;
    /** the index into the type's items-list */
    public String value;
    
    /** constructs an Enumeration object.
     * 
     * @param item the enumeration item.
     * @param applicationName the corresponding type
     */
    public EnumerationObject(String item, String applicationName) {
        value = item;
        this.applicationName = applicationName;}

    
    /** returns the String which represents the item.
     * The string is unique.
     * 
     * @return the String which represents the item.
     */
    @Override
    public Object get() {return value;}
    
    /** compares two enumeration type objects.
     * Items of two different enumerations are not equal even if their string-representation is equal.
     * 
     * @param object any object
     * @return true if the two objects are equal
     */
    @Override
    public boolean equals(Object object) {
        return object != null && (object instanceof EnumerationObject) &&
                value.equals(((EnumerationObject)object).value) &&
                applicationName.equals(((EnumerationObject)object).applicationName);}

    /** return the index' hash code.
     * 
     * @return the index' hash code.
     */
    @Override
    public int hashCode() {return value.hashCode();}

    /** identity of ConstantObjects
     *
     * @param operator just EQUALS
     * @param constant the other object
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, EnumerationObject constant) {
        switch(operator) {
            case EQUALS:  return value == constant.value;}
        return null;}

    /** checks equality with a StringObject
     *
     * @param operator just EQUALS
     * @param string the other object
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, StringObject string) {
        switch(operator) {
            case EQUALS:  return value.equals(string.value);}
        return null;}

    /** checks equality with a StringObject
     *
     * @param operator just EQUALS
     * @param constant the other object
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, ConstantObject constant) {
        switch(operator) {
            case EQUALS:  return value.equals(constant.value);}
        return null;}


    static {
        ConcreteObject.addCompareMethod(EnumerationObject.class, StringObject.class, ConstantObject.class);}





    
    /** returns the corresponding value as string.
     * 
     * @return the corresponding value as string.
     */
    @Override
    public String toString() {return value;}
}
