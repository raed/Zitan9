package ConcreteDomain.AtomicTypes;

import AbstractObjects.Operators;
import ConcreteDomain.ConcreteObject;
import ConcreteDomain.SetTypes.*;

import java.io.Serializable;
import java.util.ArrayList;

/** This is a wrapper class for float values.
 *
 */
public class FloatObject extends NumberObject implements Serializable {
    /** the value itself */
    public float value;

    /** constructs a new FloatObject
     *
     * @param value the float value*/
    public FloatObject(float value) {
       this.value = value;}
   
    /** returns the float value as Float object */
    @Override
    public Object get() {return (Float)value;}

    /** return the int value as string.
     *
     * @return the int value as string.
     */
    @Override
    public String toString() {
        String s = Float.toString(value);
        if(s.endsWith(".0")) {s = s.substring(0,s.length()-2);}
        return s;}


    /** returns the hash code of the float value
     *
     * @return the hash code of the float value.
     */
    @Override
    public int hashCode() {return Float.hashCode(value);}



    /** arithmetic comparisons between 'this' and the given IntegerObject.
     *
     * @param operator one of the arithmetic operators
     * @param number the other number
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, IntegerObject number) {
        int n = number.value;
        switch(operator) {
            case LESS:          return value < n;
            case LESSEQUALS:    return value <= n;
            case EQUALS:        return value == n;
            case GREATER:       return value > n;
            case GREATEREQUALS: return value >= n;}
        return null;
    }

    /** arithmetic comparisons between 'this' and the given FloatObject.
     *
     * @param operator one of the arithmetic operators
     * @param number the other number
     * @return a truth value for the comparison, and null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, FloatObject number) {
        float n = number.value;
        switch(operator) {
            case LESS:          return value < n;
            case LESSEQUALS:    return value <= n;
            case EQUALS:        return value == n;
            case GREATER:       return value > n;
            case GREATEREQUALS: return value >= n;}
        return null;
    }



    /** compares 'this' with the interval(from,to).
     *  'this' BEFORE interval if 'this' &lt; from<br>
     *  'this' STARTS interval if 'this' = from<br>
     *  'this' IN interval if from %le; 'this'  and 'this' &le; to<br>
     *  'this' FINISHES interval if 'this' = to<br>
     *  'this' AFTER interval if to &lt; 'this' <br>
     *
     * @param operator BEFORE, STARTS, IN, FINISHES, AFTER
     * @param interval the interval
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, IntegerInterval interval) {
        int from = interval.from;
        int to = interval.to;
        switch(operator) {
            case BEFORE:   return value < from;
            case STARTS:   return value == from;
            case IN:       return from <= value && value <= to;
            case FINISHES: return value == to;
            case AFTER:    return to < value;
        }
        return null;
    }

    /** compares 'this' with the given list.
     * The list need not be ordered, but has a minimum value (min) and a maximum value (max).
     *
     *  'this' BEFORE list if 'this' &lt; min<br>
     *  'this' STARTS list if 'this' = min<br>
     *  'this' IN list if the list contains 'this'
     *  'this' FINISHES list if 'this' = max<br>
     *  'this' AFTER list if max &lt; 'this' <br>
     *
     * @param operator BEFORE, STARTS, IN, FINISHES, AFTER
     * @param list the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, IntegerList list) {
        ArrayList<Integer> values = list.values;
        if(values.isEmpty()) {return false;}
        int min = values.get(0);
        int max = values.get(values.size()-1);
        switch(operator) {
            case BEFORE:   return value < min;
            case STARTS:   return value == min;
            case FINISHES: return value == max;
            case AFTER:    return max < value;
            case IN:
                int v = (int)value;
                if((float)v != value) {return false;}
                return values.contains(v);
        }
        return null;
    }

    /** compares 'this' with the interval(from,to).
     *  'this' BEFORE interval if 'this' &lt; from<br>
     *  'this' STARTS interval if 'this' = from<br>
     *  'this' IN interval if from %le; 'this'  and 'this' &le; to<br>
     *  'this' FINISHES interval if 'this' = to<br>
     *  'this' AFTER interval if to &lt; 'this' <br>
     *
     * @param operator BEFORE, STARTS, IN, FINISHES, AFTER
     * @param interval the interval
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, FloatInterval interval) {
        float from = interval.from;
        float to = interval.to;
        switch(operator) {
            case BEFORE:   return value < from;
            case STARTS:   return value == from;
            case IN:       return from <= value && value <= to;
            case FINISHES: return value == to;
            case AFTER:    return to < value;
        }
        return null;
    }

    /** compares 'this' with the given list.
     * The list need not be ordered, but has a minimum value (min) and a maximum value (max).
     *
     *  'this' BEFORE list if 'this' &lt; min<br>
     *  'this' STARTS list if 'this' = min<br>
     *  'this' IN list if the list contains 'this'
     *  'this' FINISHES list if 'this' = max<br>
     *  'this' AFTER list if max &lt; 'this' <br>
     *
     * @param operator BEFORE, STARTS, IN, FINISHES, AFTER
     * @param list the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, FloatList list) {
        ArrayList<Float> values = list.values;
        if(values.isEmpty()) {return false;}
        if(operator == Operators.IN) {return values.contains(value);}
        float min = values.get(0);
        float max = values.get(values.size()-1);
        switch(operator) {
            case BEFORE:   return value < min;
            case STARTS:   return value == min;
            case FINISHES: return value == max;
            case AFTER:    return max < value;
        }
        return null;
    }

    static {
        ConcreteObject.addCompareMethod(FloatObject.class,IntegerObject.class,
                IntegerInterval.class, IntegerList.class, FloatInterval.class,FloatList.class);
    }
    /** This method yields for a given operator an array of all those classes the operator is able to compare with 'this#.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
        return NumberObject.comparable(operator);}

    /** parses a string to a FloatObject
     * 
     * @param string the string to be parsed.
     * @return the parsed FloatObject or null if it cannot be parsed.
     */
    public static ConcreteObject parseString(String string)  {
        try{return  new FloatObject(Float.parseFloat(string));}
        catch(Exception ex) {return null;}}

    /** checks if the string can be parsed.
     *
     * @param string a string to be tested
     * @return null or an error string.
     */
    public static String parseCheck(String string)  {
        try {Float.parseFloat(string);}
        catch(Exception ex) {return ex.toString();}
        return null;}
}
