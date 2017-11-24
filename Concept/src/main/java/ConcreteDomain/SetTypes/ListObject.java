package ConcreteDomain.SetTypes;

import ConcreteDomain.SetObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/** This is a ConcreteDomain type which consists of lists of objects.
 * The items must be Comparable, and the lists are internally sorted.
 *
 * @param <T> the element type
 */
public class ListObject<T extends Comparable> extends SetObject implements Serializable {
    public ArrayList<T> values;
    
    /** constructs a ListObject with a given list of values.
     * The values are sorted
     * 
     * @param values a list of values
     */
    public ListObject(ArrayList<T> values) {
        this.values = values;
        this.values.sort(null);}

    public ListObject(Object[] values) {
        this.values = new ArrayList<>(values.length);
        for(Object value: values) {this.values.add((T)value);}
        this.values.sort(null);}
    
    /** yields the sorted list of values. 
     * 
     * @return the sorted list of values.
     */
    @Override
    public Object get() {return values;}

    public boolean isEmpty() {return values.isEmpty();}

    public boolean isNumberList() {return false;}

    public boolean isPoint() {return values.size() == 1;}

    public Object getFirst() {return values.get(0);}

    /** compares the two lists with the ArrayList.equals-method
     *
     * @param object the object to be compared
     * @return true if the lists are equal.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof ListObject)) {return false;}
        return values.equals(((ListObject)object).values);}
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.values);
        return hash;}
    
    /** generates a comma separated string of values
     * 
     * @return a comma separated string of values.
     */
    @Override
    public String toString() {
        return Utils.Utilities.join(values, ", ", (p->p.toString()));}

    /**
     * 
     * @return the number of elements in the list. 
     */
    public int size() {return values.size();}


    /** removes brackets
     *
     * @param string any string
     * @return teh string without brackets.
     */
    protected static String removeBrackets(String string) {
        if((string.startsWith("(") && string.endsWith(")")) ||
                (string.startsWith("[") && string.endsWith("]")) ||
                (string.startsWith("{") && string.endsWith("}"))) {
            return string.substring(1,string.length()-1); }
        return string;}

}
