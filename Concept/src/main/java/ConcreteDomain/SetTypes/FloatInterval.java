
package ConcreteDomain.SetTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicTypes.FloatObject;
import ConcreteDomain.AtomicTypes.IntegerObject;
import ConcreteDomain.ConcreteObject;

import java.io.Serializable;
import java.util.ArrayList;

/** This class represents Intervals with Floats as boundaries.
 *
 */
public class FloatInterval extends Interval<Float> implements Serializable {

    /** constructs a FloatInterval with the given boundaries.
     * The boundaries are automatically ordered.
     * 
     * @param from one boundary
     * @param to the other boundaries.
     */
    public FloatInterval(Float from, Float to) {
        super(from, to);}


    /** compares the interval with an integer.
     *
     *  <pre>
     * {@code
     * BEFORE:      to < item
     * STARTS:      from == item
     * CONTAINS:    from <= item && item <= to
     * FINISHES:    to == item
     * AFTER:       item < from
     * DISJOINT:    item < from || item > to;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, CONTAINS, FINISHES, AFTER, DISJOINT
     * @param item the integer object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, IntegerObject item) {
        int value = item.value;
        switch(operator) {
            case BEFORE:   return to < value;
            case STARTS:   return from == value;
            case CONTAINS: return from <= value && value <= to;
            case FINISHES: return to == value;
            case AFTER:    return value < from;
            case EQUALS:   return from == to && value == from;
            case DISJOINT: return value < from || value > to;
        }
        return null;
    }


    /** compares the interval with a float number.
     *
     *  <pre>
     * {@code
     * BEFORE:      to < item
     * STARTS:      from == item
     * CONTAINS:    from <= item && item <= to
     * FINISHES:    to == item
     * AFTER:       item < from
     * DISJOINT:    item < from || item > to;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, CONTAINS, FINISHES, AFTER, DISJOINT
     * @param item the integer object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, FloatObject item) {
        float value = item.value;
        switch(operator) {
            case BEFORE:   return to < value;
            case STARTS:   return from == value;
            case CONTAINS: return from <= value && value <= to;
            case FINISHES: return to == value;
            case AFTER:    return value < from;
            case EQUALS:   return value == from && value == to;
            case DISJOINT: return value < from || value > to;
        }
        return null;
    }



    /** compares the interval(from1,to1)  with integer interval (from2,to2).
     *
     * <pre>
     * {@code
     * BEFORE:      to1 < from2
     * MEETS:       to1 == from2
     * STARTS:      from1 == from2 && to2 < to1
     * CONTAINS:    from1 <= from2 && to2 <= to1
     * FINISHES:    from1 <= from2 && to2 == to1;
     * AFTER:       to2 < from1
     * EQUALS:      from1 == from2 && to1 == to2
     * OVERLAPS:    (from2 <= from1 && from1 < to2) || (to2 >= to1 && from2 < to1);
     * DISJOINT:    to2 < from2 || from2 > to;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, IntegerInterval item) {
        int from2 = item.from;
        int to2   = item.to;
        switch(operator) {
            case BEFORE:   return to < from2;
            case MEETS:    return from2 == to;
            case STARTS:   return false;
            case CONTAINS: return from <= from2 && to2 <= to;
            case IN:       return false;
            case FINISHES: return false;
            case AFTER:    return to2 < from;
            case EQUALS:   return from == to && from2 == to2 && from == from2;
            case OVERLAPS: return to2 >= from && from2 <= to;
            case DISJOINT: return to2 < from || from2 > to;
        }
        return null;}


    /** compares the interval(from1,to1)  with another interval (from2,to2).
     *
     * <pre>
     * {@code
     * BEFORE:      to1 < from2
     * MEETS:       to1 == from2
     * STARTS:      from1 == from2 && to2 < to1
     * CONTAINS:    from1 <= from2 && to2 <= to1
     * FINISHES:    from1 <= from2 && to2 == to1;
     * AFTER:       to2 < from1
     * EQUALS:      from1 == from2 && to1 == to2
     * OVERLAPS:    (from2 <= from1 && from1 < to2) || (to2 >= to1 && from2 < to1);
     * DISJOINT:    to2 < from2 || from2 > to;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, FloatInterval item) {
        float from2 = item.from;
        float to2   = item.to;
        switch(operator) {
            case BEFORE:   return to < from2;
            case MEETS:    return to == from2;
            case STARTS:   return from == from2 && to < to2;
            case CONTAINS: return from <= from2 && to2 <= to;
            case IN:       return from2 <= from && to <= to2;
            case FINISHES: return from2 <= from && to2 == to;
            case AFTER:    return to2 < from;
            case EQUALS:   return from == from2 && to == to2;
            case OVERLAPS: return to2 >= from && from2 <= to;
            case DISJOINT: return to2 < from || from2 > to;
        }
        return null;}



    /** compares the interval(from1,to1)  with a (sorted) integer list (from2,...,to2)
     *
     * <pre>
     * {@code
     * BEFORE:      to1 < from2
     * MEETS:       to1 == from2
     * STARTS:      the interval is a starting sequence of the list
     * CONTAINS:    all list elements are in the interval.
     * FINISHES:    the interval is a finishing sequence of the list
     * AFTER:       to2 < from1
     * EQUALS:      from1 == from2 && to1 == to2 && both sizes are equals
     * OVERLAPS:    some list elements are in the interval
     * DISJOINT:    to2 < from2 || from2 > to1;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, IntegerList item) {
        ArrayList<Integer> list = item.values;  // the list is sorted.
        if(list.isEmpty()) {return operator == Operators.CONTAINS;}
        int size2 = list.size();
        int from2 = list.get(0);
        int to2   = list.get(size2-1);
        switch(operator) {
            case BEFORE:   return to < from2;
            case MEETS:    return from2 == to;
            case STARTS:   return false;
            case CONTAINS: return from <= from2 && to2 <= to;
            case EQUALS:
            case IN:       return from == to && size2 == 1 && (float)list.get(0) == from;
            case FINISHES: return false;
            case AFTER:    return to2 < from;
            case OVERLAPS: for(Integer i : list) {if(from <= i && i <= to) {return true;}}
                return false;
            case DISJOINT: return to < from2 || from2 > to;}
        return null;
    }


    /** compares the interval(from1,to1)  with a (sorted) float list (from2,...,to2)
     *
     * <pre>
     * {@code
     * BEFORE:      to1 < from2
     * MEETS:       to1 == from2
     * STARTS:      the interval is a starting sequence of the list
     * CONTAINS:    all list elements are in the interval.
     * FINISHES:    the interval is a finishing sequence of the list
     * AFTER:       to2 < from1
     * EQUALS:      from1 == from2 && to1 == to2 && both sizes are equals
     * OVERLAPS:    some list elements are in the interval
     * DISJOINT:    to2 < from2 || from2 > to1;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, FloatList item) {
        ArrayList<Float> list = item.values;  // the list is sorted.
        if(list.isEmpty()) {return operator == Operators.CONTAINS;}
        int size2 = list.size();
        float from2 = list.get(0);
        float to2   = list.get(size2-1);
        switch(operator) {
            case BEFORE:   return to < from2;
            case MEETS:    return from2 == to;
            case STARTS:   return false;
            case CONTAINS: return from <= from2 && to2 <= to;
            case EQUALS:   return from == to  && size2 == 1 && list.get(0) == from;
            case IN:       return from == to  && list.get(0) == from;
            case FINISHES: return false;
            case AFTER:    return to2 < from;
            case OVERLAPS: for(Float i : list) {if(from <= i && i <= to) {return true;}}
                return false;
            case DISJOINT: return to < from2 || from2 > to;}
        return null;
    }

    static {
        ConcreteObject.addCompareMethod(FloatInterval.class,IntegerObject.class,IntegerList.class,
            IntegerInterval.class,FloatObject.class,FloatList.class);}


    private static Class[] classes1 = new Class[]{IntegerInterval.class, FloatInterval.class,
            IntegerList.class,FloatList.class};
    private static Class[] classes2 = new Class[]{IntegerObject.class,FloatObject.class,IntegerInterval.class, FloatInterval.class,
            IntegerList.class,FloatList.class};

    /** This method yields for a given operator an array of all those classes the operator is able to compare with 'this'.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
        switch(operator) {
            case IN:
            case MEETS:
            case OVERLAPS: return classes1;

            case BEFORE:
            case STARTS:
            case CONTAINS:
            case EQUALS:
            case FINISHES:
            case AFTER:
            case DISJOINT: return classes2;}
        return null;}


    /** parses a string "from - to" to an FloatIntervalObject
     * 
     * @param string the string to be parsed.
     * @return the parsed FloatIntervalObject or null if there was a syntax error
     */
    public static ConcreteObject parseString(String string)  {
        String[] parts = string.split("\\s+-\\s+");
        if(parts.length != 2) {return null;}
        try{
            float from = Float.parseFloat(parts[0]);
            float to   = Float.parseFloat(parts[1]);
            return new FloatInterval(from,to);}
        catch(Exception ex) {}
        return null;}

    /** checks if the string can be parsed.
     *
     * @param string a string to be tested
     * @return null or an error message.
     */
    public static String parseCheck(String string)  {
        String[] parts = string.split("\\s+-\\s+",2);
        if(parts.length < 2) {return "malformed integer interval: " + string;}
        String errors = "";
        try{Float.parseFloat(parts[0]);}
        catch(Exception ex) {errors = ex.toString()+"\n";}
        try{Float.parseFloat(parts[1]);}
        catch(Exception ex) {errors += ex.toString()+"\n";}
        return errors.isEmpty() ? null : errors;}
    
    /** returns the length of the interval
     * 
     * @return the length of the interval.
     */
    public float length() {return to-from;}
    
}
