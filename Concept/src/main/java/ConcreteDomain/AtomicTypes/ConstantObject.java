package ConcreteDomain.AtomicTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import MISC.Context;

import java.io.Serializable;


/** This class is just a wrapper for strings. 
 * If two Constants are parsed with parseString then two strings which are equal become identical objects.
 * Therefore they can be compared with ==.<br>
 * Example: Nationalities: German, French, Italian etc. 
 *
 */
public class ConstantObject extends AtomicObject implements Serializable{
    /** the actual string */
    public String value = null;

    /** the empty constructor */
    public ConstantObject(){}

    /** constructs a new ConstantObject
     * 
     * @param value the string that becomes a constant object.
     */
    public ConstantObject(String value) {
        this.value = value;}

    /** returns just the string
     * 
     * @return the string
     */
    @Override
    public Object get() {return value;}

    /** returns just the string
     *
     * @return the string*/
    @Override
    public Object getFirst() {return value;}
    
    /** returns the string.
     * 
     * @return the string. 
     */
    @Override
    public String toString() {return value;}
    
    /** checks equality with ==
     * 
     * @param object the object to be compared
     * @return true if this = object
     */
    @Override
    public boolean equals(Object object) {return this == object;}

    /** returns the string's hashCode
     * 
     * @return the string's hashCode
     */
    @Override
    public int hashCode() {return value.hashCode();}


    /** identity of ConstantObjects
     *
     * @param operator just EQUALS
     * @param constant the other object
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, ConstantObject constant) {
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

    /** checks equality with an EnumerationObject
     *
     * @param operator just EQUALS
     * @param item the other object
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, EnumerationObject item) {
        switch(operator) {
            case EQUALS:  return value.equals(item.value);}
        return null;}


    static {
        ConcreteObject.addCompareMethod(EnumerationObject.class, StringObject.class, ConstantObject.class);}

    private static Class[] classes = new Class[]{EnumerationObject.class, StringObject.class, ConstantObject.class};

    /**This method yields for a given operator an array of all those classes the operator is able to compare with 'this'.
     *
     * @param operator any operator
     * @return an array of all classes C sich that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
         switch(operator) {
              case EQUALS: return classes;}
        return null;}

    /** parses a string as ConstantType and ensures that equal strings become identical objects.
     * 
     * @param s the string to be parsed.
     * @param context the context for the objects.
     * @return the corresponding ConstantObject.
     */
    
    public static ConcreteObject parseString(String s, Context context) {
        ConstantObject object = context.getConstant(s);
        if(object != null) {return object;}
        object = new ConstantObject(s);
        context.putConstant(s,object);
        return object;}

    /** checks if the string can be parsed.
     *
     * @param string a string to be tested
     * @return null
     */
    public static String parseCheck(String string)  {
        return null;}
}
