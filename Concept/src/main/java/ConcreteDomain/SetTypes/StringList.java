package ConcreteDomain.SetTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicTypes.StringObject;
import ConcreteDomain.ConcreteObject;

import java.io.Serializable;
import java.util.ArrayList;

/** This class represents Lists of Strings as Concrete Type
 *
 */
public class StringList extends ListObject<String> implements Serializable{
    
    /** constructs a StringListObject from an array of strings. 
     * 
     * @param values the list of String values.
     */
    public StringList(ArrayList<String> values) {
        super(values);}
    /** constructs a StringListObject from an array of strings.
     *
     * @param values the list of String values.
     */
    public StringList(String... values) {
        super(values);}

    /** compares 'this' with the given StringObject.
     *
     *  'this' CONTAINS list if the list contains the element<br>
     *
     * @param operator CONTAINS
     * @param item the element
     * @return the result of the comparison, or null if the operator is not applicable.
     */
     public Boolean compare(Operators operator, StringObject item) {
        switch(operator) {
            case CONTAINS: return values.contains(item.value);}
        return null;
        }


    /** compares 'this' with the given StringList.
     *
     *  'this' OVERLAPS list if they have common elements<br>
     *  'this' DISJOINT list if they are disjoint
     *  'this' EQUALS list if they are equal<br>
     *
     * @param operator OVERLAPS, DISJOINT, EQUALS
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, StringList item) {
        ArrayList<String> values2 = item.values;
        switch(operator) {
            case OVERLAPS:
                for(String value : values) {if(values2.contains(value)) {return true;}}
                return false;
            case DISJOINT:
                for(String value : values) {if(values2.contains(value)) {return false;}}
                return true;
            case EQUALS:   return values.equals(values2);
            case IN:       return values2.containsAll(values);
            case CONTAINS: return values.containsAll(values2);}

        return null;
    }

    static {
        ConcreteObject.addCompareMethod(StringList.class,StringObject.class);}

    private static Class[] classes1 = new Class[]{StringList.class};

    private static Class[] classes2 = new Class[]{StringObject.class,StringList.class};

    /** This method yields for a given operator an array of all those classes the operator is able to compare with 'this'.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
        switch(operator) {
            case CONTAINS: return classes2;

            case OVERLAPS:
            case EQUALS:
            case IN:
            case DISJOINT: return classes1;}
        return null;}





    /** parses a string "string_1,...,string_n" to a StringListObject
     * 
     * @param string the string to be parsed.
     * @return the parsed StringListObject
     */
    public static ConcreteObject parseString(String string) {
        String[] parts = string.split("\\s*,\\s*");
        ArrayList<String> array = new ArrayList();
        for(String part : parts) {array.add(part);}
        return new StringList(array);}

    /** checks if the string can be parsed.
     *
     * @param string a string to be tested
     * @return null
     */
    public static String parseCheck(String string)  {
        return null;}
   
}
