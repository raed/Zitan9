package ConcreteDomain.AtomicTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import ConcreteDomain.SetTypes.StringList;

import java.io.Serializable;
import java.util.ArrayList;

/** This is a wrapper for strings.
 *
 */
public class StringObject extends AtomicObject implements Serializable {
    /** the wrapped string */
    public String value = null;

    static {
        ConcreteObject.addCompareMethod(StringObject.class,StringList.class);}


    /** the empty constructor */
    public StringObject() {super();}

    /** constructs a StringObject with the given string
     *
     * @param value the string to be wrapped.
     */
    public StringObject(String value) { this.value = value;}
    
    /** just returns the string
     * 
     * @return the string. 
     */
    @Override
    public Object get() {return value;}

    /** compares two string objects.
     * STARTS:         object starts with 'this' <br>
     * IN:            'this' is a substring in the object<br>
     * FINISES:        object ends with 'this' <br>
     * EQUALS:         both are equal.<br>
     * LESS:          'this' is lexicographic smaller than object<br>
     * GREATER:       'this' is lexicographic greater than object<br>
     * LESSEQUALS:    'this' is lexicographic smaller or equal to  object<br>
     * GREATEREQUALS: 'this' is lexicographic greater or equal to object<br>
     *
     * @param operator STARTS,IN,FINISHES,EQUALS
     * @param stringObject the other string object
     * @return the result of the comparison or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, StringObject stringObject) {
        String string = stringObject.value;
        switch(operator) {
            case STARTS:   return string.startsWith(value);
            case IN:       return string.contains(value);
            case FINISHES: return string.endsWith(value);
            case EQUALS:   return string.endsWith(value);
            case LESS:     return value.compareTo(string) < 0;
            case GREATER:  return value.compareTo(string) > 0;
            case LESSEQUALS:     return value.compareTo(string) <= 0;
            case GREATEREQUALS:  return value.compareTo(string) >= 0;
        }
        return null;}

    /** compares 'this' with a list of strings
     * IN:     the list contains 'this'
     * EQUALS: the list is a singleton intervalContaining 'this'
     *
     * @param operator STARTS,IN,FINISHES,EQUALS
     * @param stringList the list of strings.
     * @return the result of the comparison or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, StringList stringList) {
        ArrayList<String> list = stringList.values;
        switch(operator) {
            case IN:       return list.contains(value);
            case EQUALS:   return list.size() == 1 && value.equals(list.get(0));}
        return null;}


    private static Class[] classes1 = new Class[]{StringObject.class};
    private static Class[] classes2 = new Class[]{StringObject.class,StringList.class};

    /** This method yields for a given operator an array of all those classes the operator is able to compare with 'this'.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
         switch(operator) {
             case IN:
             case EQUALS: return classes2;
             case STARTS:
             case FINISHES:
             case LESS:
             case GREATER:
             case LESSEQUALS:
             case GREATEREQUALS: return classes1;}
         return null;}


    /** compares 'this' and objects
     *
     * @param object the object to be comapred
     * @return true if they are equal.
     */
    @Override
    public boolean equals(Object object) {
        return object != null && object.getClass() == StringObject.class && value.equals(((StringObject)object).value);}
    
    /** just returns the value
     * 
     * @return the value.
     */
    @Override
    public String toString() {return value;}
    


    @Override
    public int hashCode() {return value.hashCode();}
    
    /** parses a string to a StringObject
     * 
     * @param string the string to be parsed.
     * @return the StringObject
     */
    public static ConcreteObject parseString(String string) {return new StringObject(string);}

    /** checks if the string can be parsed.
     *
     * @param string a string to be tested
     * @return null
     */
    public static String parseCheck(String string)  {return null;}
    
}
