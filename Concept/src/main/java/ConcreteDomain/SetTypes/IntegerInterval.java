package ConcreteDomain.SetTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicTypes.FloatObject;
import ConcreteDomain.AtomicTypes.IntegerObject;
import ConcreteDomain.ConcreteObject;
import Utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;

/** This class represents intervals with Integer boundaries
 *
 */
public class IntegerInterval extends Interval<Integer> implements Serializable {

    /** constructs an IntegerIntervalObject
     * 
     * @param from one boundary
     * @param to another boundary (the boundaries are automatically sorted)
     */
    public IntegerInterval(Integer from, Integer to) {
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
     * @param operator BEFORE, STARTS, CONTAINS, FINISHES, AFTER, EQUALS, DISJOINT
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
            case EQUALS:   return value == from && value == to;
            case DISJOINT: return value < from || value > to;
        }
        return null;
    }

    /** compares the interval with a float.
     *
     * <pre>
     * {@code
     * BEFORE:      to < item
     * STARTS:      from == item
     * CONTAINS:    from <= item && item <= to
     * FINISHES:    to == item
     * AFTER:       item < from
     * DISJOINT:    item < from || item > to;
     *     }
     *</pre>
     *
     * @param operator  BEFORE, STARTS, CONTAINS, FINISHES, AFTER, EQUALS, DISJOINT
     * @param item the float object
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
            case EQUALS:   return from == to && from == value;
            case DISJOINT: return value < from || value > to;
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
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, IN, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, IntegerInterval item) {
        int from2 = item.from;
        int to2   = item.to;
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
     * DISJOINT:    to2 < from1 || from2 > to;
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, IN, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, FloatInterval item) {
        float from1 = (float)from;
        float to1   = (float)to;
        float from2 = item.from;
        float to2   = item.to;
        switch(operator) {
            case BEFORE:   return to1 < from2;
            case MEETS:    return to1 == from2;
            case STARTS:   return from1 == from2 && to1 < to2;
            case CONTAINS: return from1 <= from2 && to2 <= to1;
            case IN:       return from2 <= from1 && to1 <= to2;
            case FINISHES: return from2 < from1 && to2 == to1;
            case AFTER:    return to2 < from1;
            case EQUALS:   return from1 == from2 && to1 == to2;
            case OVERLAPS: return to2 >= from1 && from2 <= to1;
            case DISJOINT: return to2 < from1 || from2 > to1;
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
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, IN, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, IntegerList item) {
        ArrayList<Integer> list = item.values;  // the list is sorted.
        if(list.isEmpty()) {return operator == Operators.CONTAINS;}
        int size1 = to-from+1;
        int size2 = list.size();
        int from2 = list.get(0);
        int to2   = list.get(size2-1);
        switch(operator) {
            case BEFORE:   return to < from2;
            case MEETS:    return to == from2;
            case STARTS:   return from == from2 && size1 < size2 && list.get(size1-1) == to;
            case CONTAINS: return from <= from2 && to2 <= to;
            case IN:
                if(size1 > size2) {return false;}
                int ind_from = list.indexOf(from);
                if(ind_from < 0) {return false;}
                int ind_to = list.indexOf(to);
                if(ind_to < 0) {return false;}
                return ind_to-ind_from == size1-1;
            case FINISHES: return to == to2 && size1 < size2 && from == list.get(size2-size1);
            case AFTER:    return to2 < from;
            case EQUALS:   return from == from2 && to == to2 && size1 == size2;
            case OVERLAPS:
                if(to < from2 || to2 < from) {return false;}
                for(Integer i : list) {if(from <= i && i <= to) {return true;}}
                return false;
            case DISJOINT:
                if(to < from2 || to2 < from) {return true;}
                for(Integer i : list) {if(from <= i && i <= to) {return false;}}
                return true;}

        return null;
    }


    /** compares the interval(from1,to1)  with a (sorted) float list (from2,...,to2).
     * Float numbers which are equal to integers are treated like integers.
     *
     * <pre>
     * {@code
     * BEFORE:      to1 < from2
     * MEETS:       to1 == from2
     * STARTS:      the interval is a starting sequence of the list
     * CONTAINS:    all list elements are in the interval.
     * FINISHES:    the interval is a finishing sequence of the list
     * AFTER:       to2 < from1
     * EQUALS:      The list contains only integers and is equal to the interval, considered as list.
     * OVERLAPS:    some list elements are in the interval
     * DISJOINT:    The interval and the list are disjoint.
     * }
     * </pre>
     *
     * @param operator BEFORE, MEETS, STARTS, CONTAINS, IN, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the double object
     * @return the result of the comparison, or null if the operator is not applicable
     */
    public Boolean compare(Operators operator, FloatList item) {
        ArrayList<Float> list = item.values;  // the list is sorted.
        if(list.isEmpty()) {return operator == Operators.CONTAINS;}
        int size1 = to-from+1;
        int size2 = list.size();
        float from1 = (float)from;
        float to1   = (float)to;
        float from2 = list.get(0);
        float to2   = list.get(size2-1);
        switch(operator) {
            case BEFORE:   return to1 < from2;
            case MEETS:    return to == from2;
            case STARTS:   if(!(from ==from2 && size1 < size2 && (float)list.get(size1-1) == to)) {return false;}
                for(int i = 0; i < size1; ++i) { if(!Utilities.isInt(list.get(i))) {return false;}}
                return true;
            case CONTAINS: if(!(from1 <= from2 && to2 <= to1)) {return false;}
                for(Float f : list) {if(!Utilities.isInt(f)) {return false;}}
                return true;
            case IN:
                if(!(from2 <= from1 && to1 <= to2 && size1 <= size2)) {return false;}
                int ind_from = list.indexOf(from1);
                if(ind_from < 0) {return false;}
                int ind_to = list.indexOf(to1);
                if(ind_to < 0) {return false;}
                if(ind_to - ind_from != size1-1) {return false;}
                for(int i = ind_from; i <= ind_to; ++i) {
                    if(!Utilities.isInt(list.get(i))) {return false;}}
                return true;
            case FINISHES:
                if(!(to == to2 && size1 < size2 &&
                   from == (float)list.get(size2-size1))) {return false;}
                for(int i = 1; i <= size1; ++i) {
                    {if(!Utilities.isInt(list.get(size2-i))) {return false;}}}
                return true;
            case AFTER:    return to2 < from2;
            case EQUALS:
                if(!(size1 == size2 && from == from2 && to == to2)) {return false;}
                for(Float i : list) {if(!Utilities.isInt(i)) {return false;}}
                return true;
            case OVERLAPS:
                if(to1 < from2 || to2 < from1) {return false;}
                for(Float i : list) {if(Utilities.isInt(i) && from1 <= i && i <= to1) {return true;}}
                return false;
            case DISJOINT:
                if(to1 < from2 || to2 < from1) {return true;}
                for(Float i : list) {if(Utilities.isInt(i) && from1 <= i && i <= to1) {return false;}}
                return true;}
        return null;
    }

    static {
        ConcreteObject.addCompareMethod(IntegerInterval.class,IntegerObject.class,IntegerList.class,
                                            FloatInterval.class,FloatObject.class,FloatList.class);}


    private static Class[] classes1 = new Class[]{IntegerInterval.class, FloatInterval.class,IntegerList.class,FloatList.class};
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
            case CONTAINS:
            case STARTS:
            case EQUALS:
            case FINISHES:
            case AFTER:
            case DISJOINT: return classes2;}
        return null;}


    /** parses a string "from - to" to an IntegerIntervalObject.
     * 
     * @param string the string to be parsed.
     * @return the parsed IntegerIntervalObject or null
     */
    public static ConcreteObject parseString(String string)  {
        String[] parts = string.split("\\s+-\\s+");
        if(parts.length != 2) {return null;}
        try{
            int from = Integer.parseInt(parts[0]);
            int to   = Integer.parseInt(parts[1]);
            return new IntegerInterval(from,to);}
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
        try{Integer.parseInt(parts[0]);}
        catch(Exception ex) {errors = ex.toString()+"\n";}
        try{Integer.parseInt(parts[1]);}
        catch(Exception ex) {errors += ex.toString()+"\n";}
        return errors.isEmpty() ? null : errors;}

    /** returns the length of the interval
     * 
     * @return the length of the interval.
     */
    public int length() {return to-from;}

    /** returns the number of the numbers in the interval.
     *
     * @return the number of numbers in the interval.
     */
    public int size() {return to-from+1;}
}
