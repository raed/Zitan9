package ConcreteDomain.SetTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicTypes.FloatObject;
import ConcreteDomain.AtomicTypes.IntegerObject;
import ConcreteDomain.ConcreteObject;

import java.io.Serializable;
import java.util.ArrayList;

/** This is a wrapper for lists of integers.
 */
public class IntegerList extends ListObject<Integer> implements Serializable {

    /** constructs a new IntegerListObject
     * 
     * @param values a list of integers.
     */
    public IntegerList(ArrayList<Integer> values) {
        super(values);}

    public IntegerList(Integer... values){
        super(values);}

    public boolean isNumberList() {return true;}



    /** compares 'this' with the given Integer.
     *
     *  'this' BEFORE value if max &lt; value<br>
     *  'this' STARTS value if  min = value<br>
     *  'this' FINISHES value if  max = value<br>
     *  'this' CONTAINS value if the list contains value<br>
     *  'this' AFTER value if value &lt; min <br>
     *
     * @param operator BEFORE, STARTS, FINISHES, AFTER, CONTAINS
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, IntegerObject item) {
        if(values.isEmpty()){return false;}
        int value = item.value;
        int min = values.get(0);
        int max = values.get(values.size()-1);
        switch(operator) {
            case BEFORE:   return max < value;
            case STARTS:   return min == value;
            case FINISHES: return max == value;
            case AFTER:    return value < min;
            case CONTAINS: return values.contains(item.value);
        }
        return null;
    }


    /** compares 'this' with the given Float.
     *
     *  'this' BEFORE value if max &lt; value<br>
     *  'this' STARTS value if  min = value<br>
     *  'this' FINISHES value if  max = value<br>
     *  'this' CONTAINS value if the list contains value<br>
     *  'this' AFTER value if value &lt; min <br>
     *
     * @param operator BEFORE, STARTS, FINISHES, AFTER, CONTAINS
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, FloatObject item) {
        if(values.isEmpty()){return false;}
        float value = item.value;
        int min = values.get(0);
        int max = values.get(values.size()-1);
        switch(operator) {
            case BEFORE:   return max < value;
            case STARTS:   return min == value;
            case FINISHES: return max == value;
            case AFTER:    return value < min;
            case CONTAINS:
                int k = (int)value;
                if(k != value) {return false;}
                return values.contains(k);
        }
        return null;
    }



    /** compares 'this' with the given IntegerInterval.
     *
     *  'this' BEFORE interval <br>
     *  'this' MEETS interval <br>
     *  'this' IN interval if all elements of the list are in the interval<br>
     *  'this' CONTAINS interval if all elements of the interval are in the list.
     *  'this' STARTS interval if the list is a starting segment of the interval<br>
     *  'this' FINISHES interval if the list is a finishing segment of the interval<br>
     *  'this' AFTER interval if the list is entirely after the interval<br>
     *  'this' OVERLAPS interval if they have common elements<br>
     *   'this' AFTER interval if they are disjoint<br>
     *
     * @param operator BEFORE, MEETS, STARTS, IN, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, IntegerInterval item) {
        if(values.isEmpty()) {return operator == Operators.DISJOINT;}
        int from1 = values.get(0);
        int to1   = values.get(values.size()-1);
        int from2 = item.from;
        int to2   = item.to;

        switch(operator) {
            case BEFORE:   return to1 < from2;
            case MEETS:    return to1 == from2;
            case STARTS:   return from1 == from2 && to1 < to2 && values.indexOf(to1) == (to1-from1);
            case CONTAINS:
                int index1 = values.indexOf(from2);
                if(index1 < 0) {return false;}
                int index2 = values.indexOf(to2);
                if(index2 < 0) {return false;}
                return (index2-index1) == item.length();

            case IN: return from1 >= from2 && to1 <= to2;

            case FINISHES: return to1 == to2 && from1 > from2 && values.indexOf(to1) == (to1-from1);
            case AFTER:    return to2 < from1;
            case EQUALS:   return from1 == from2 && to1 == to2 && size() == item.size();
            case OVERLAPS: if(to1 < from2 || from2 > to1) {return false;}
                for(Integer i : values) {if(from2 <= i && i <= to2) {return true;}}
                           return false;
            case DISJOINT: if(to1 < from2 || from2 > to1) {return true;}
                    for(Integer i : values) {if(from2 <= i && i <= to2) {return false;}}
                           return true;}
        return null;
    }


    /** compares 'this' with the given FloatInterval.
     *
     *  'this' BEFORE interval <br>
     *  'this' MEETS interval <br>
     *  'this' IN interval if all elements of the list are in the interval<br>
     *  'this' CONTAINS interval if all elements of the interval are in the list.
     *  'this' STARTS interval if the list is a starting segment of the interval<br>
     *  'this' FINISHES interval if the list is a finishing segment of the interval<br>
     *  'this' AFTER interval if the list is entirely after the interval<br>
     *  'this' OVERLAPS interval if they have common elements<br>
     *   'this' AFTER interval if they are disjoint<br>
     *
     * @param operator BEFORE, MEETS, STARTS, IN, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, FloatInterval item) {
        if(values.isEmpty()) {return operator == Operators.DISJOINT;}
        int from1 = values.get(0);
        int to1   = values.get(values.size()-1);
        float from2 = item.from;
        float to2   = item.to;

        switch(operator) {
            case BEFORE:   return to1 < from2;
            case MEETS:    return to1 == from2;
            case STARTS:   return from1 == from2 && from1 == to1;
            case CONTAINS: return from2 == to2 && from1 <= from2 && to1 >= to2;
            case IN:       return from1 >= from2 && to1 <= to2;
            case FINISHES: return to1 == to2 && from1 == to1;
            case AFTER:    return to2 < from1;
            case EQUALS:   return from1 == to1 && from1 == from2 && from2 == to2;
            case OVERLAPS: if(to1 < from2 || from2 > to1) {return false;}
                for(Integer i : values) {if(from2 <= i && i <= to2) {return true;}}
                return false;
            case DISJOINT: if(to1 < from2 || from2 > to1) {return true;}
                for(Integer i : values) {if(from2 <= i && i <= to2) {return false;}}
                return true;}
        return null;
    }


    /** compares 'this' with the given IntegerList.
     *
     *  'this' BEFORE interval <br>
     *  'this' MEETS interval <br>
     *  'this' IN interval if all elements of the list are in the interval<br>
     *  'this' CONTAINS interval if all elements of the interval are in the list.
     *  'this' STARTS interval if the list is a starting segment of the interval<br>
     *  'this' FINISHES interval if the list is a finishing segment of the interval<br>
     *  'this' AFTER interval if the list is entirely after the interval<br>
     *  'this' OVERLAPS interval if they have common elements<br>
     *   'this' AFTER interval if they are disjoint<br>
     *
     * @param operator BEFORE, MEETS, STARTS, IN, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, IntegerList item) {
        ArrayList<Integer> values2 = item.values;
        if(values.isEmpty()) {
                if(values2.isEmpty()) {return operator == Operators.EQUALS;}
                else {return operator == Operators.DISJOINT;}}

        int size1 = values.size();
        int from1 = values.get(0);
        int to1   = values.get(size1-1);

        int size2 = values2.size();
        int from2 = values2.get(0);
        int to2   = values2.get(size2-1);

        switch(operator) {
            case BEFORE:   return to1 < from2;
            case MEETS:    return to1 == from2;
            case STARTS:   if(from1 != from2 || to2 < to1 || size1 >= size2) {return false;}
                    int index = values2.indexOf(to1);
                    if(index < 0) {return false;}
                    return values.equals(values2.subList(0,index+1));
            case CONTAINS:
                if(!(from1 <= from2 && to2 <= to1 && size1 >= size2)) {return false;}
                return values.containsAll(values2);

            case IN:  if(!(from2 <= from1 && to1 <= to2 && size1 <= size2)) {return false;}
                return values2.containsAll(values);

            case FINISHES: if(to1 != to2 || from1 < from2 || size1 >= size2) {return false;}
                index = values2.indexOf(from1);
                if(index < 0) {return false;}
                return values.equals(values2.subList(index,size2));
            case AFTER:    return to2 < from1;
            case EQUALS:   return values.equals(values2);
            case OVERLAPS: if(to1 < from2 || from2 > to1) {return false;}
                for(Integer i : values) {if(values2.contains(i)){return true;}}
                return false;
            case DISJOINT: if(to1 < from2 || from2 > to1) {return true;}
                for(Integer i : values) {if(values2.contains(i)) {return false;}}
                return true;}
        return null;
    }


    /** compares 'this' with the given FloatList.
     *
     *  'this' BEFORE interval <br>
     *  'this' MEETS interval <br>
     *  'this' IN interval if all elements of the list are in the interval<br>
     *  'this' CONTAINS interval if all elements of the interval are in the list.
     *  'this' STARTS interval if the list is a starting segment of the interval<br>
     *  'this' FINISHES interval if the list is a finishing segment of the interval<br>
     *  'this' AFTER interval if the list is entirely after the interval<br>
     *  'this' OVERLAPS interval if they have common elements<br>
     *   'this' AFTER interval if they are disjoint<br>
     *
     * @param operator BEFORE, MEETS, STARTS, IN, CONTAINS, FINISHES, AFTER, EQUALS, OVERLAPS, DISJOINT
     * @param item the list
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, FloatList item) {
        ArrayList<Float> values2 = item.values;
        if(values.isEmpty()) {
            if(values2.isEmpty()) {return operator == Operators.EQUALS;}
            else {return operator == Operators.DISJOINT;}}
        int size1 = values.size();
        int from1 = values.get(0);
        int to1   = values.get(size1-1);

        int size2 = values2.size();
        float from2 = values2.get(0);
        float to2   = values2.get(values2.size()-1);

        switch(operator) {
            case BEFORE:   return to1 < from2;
            case MEETS:    return to1 == from2;
            case STARTS:   if(from1 != from2 || to2 < to1 || size1 >= size2) {return false;}
                for(int i = 0; i < size1; ++i) {if((int)values.get(i) != (float)values2.get(i)) {return false;}}
                return true;
            case CONTAINS:
                if(!(from1 <= from2 && to2 <= to1 && size1 >= size2)) {return false;}
                for(int i = 0; i < size2; ++i) {
                    float k = values2.get(i);
                    int ki = (int)k;
                    if(ki != k || !values.contains(ki)){return false;}}
                return true;

            case IN:
                if(!(from2 <= from1 && to1 <= to2 && size1 <= size2)) {return false;}
                for(int i = 0; i < size1; ++i) {
                    int k = values.get(i);
                    if(!values2.contains((Float)(float)k)){return false;}}
                return true;

            case FINISHES: if(to1 != to2 || from1 < from2 || size1 >= size2) {return false;}
                for(int i = 1; i <= size1; ++i) {if((int)values.get(size1-i) != (float)values2.get(size2-i)) {return false;}}
                return true;

            case AFTER: return to2 < from1;

            case EQUALS:
                if(size1 != size2 || from1 != from2 || to1 != to2) {return false;}
                for(int i = 0; i < size1; ++i) {if((int)values.get(i) != (float)values2.get(i)) {return false;}}
                return true;

            case OVERLAPS: if(to1 < from2 || from2 > to1) {return false;}
                for(Integer i : values) {if(values2.contains((float)(int)i)) {return true;}}
                return false;

            case DISJOINT: if(to1 < from2 || from2 > to1) {return true;}
                for(Integer i : values) {if(values2.contains((float)(int)i)) {return false;}}
                return true;}
        return null;
    }



    static {
        ConcreteObject.addCompareMethod(IntegerList.class,IntegerObject.class,FloatObject.class,
                IntegerInterval.class, FloatInterval.class,FloatList.class);}

    private static Class[] classes1 = new Class[]{IntegerObject.class, FloatObject.class,
            IntegerInterval.class,IntegerList.class ,FloatInterval.class ,FloatList.class };
    private static Class[] classes2 = new Class[]{IntegerInterval.class,IntegerList.class ,FloatInterval.class ,FloatList.class};


    /** This method yields for a given operator an array of all those classes the operator is able to compare with 'this#.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
        switch(operator) {
            case BEFORE:
            case STARTS:
            case FINISHES:
            case CONTAINS:
            case AFTER:    return classes1;

            case IN:
            case MEETS:
            case EQUALS:
            case OVERLAPS:
            case DISJOINT: return classes2;
        }
        return null;
    }



    /** parses a string "int1,...,intn" to an IntegerList
     * 
     * @param string the string to be parsed.
     * @return the parsed IntegerIntervalObject or null if a syntax error occurred.
     */
    public static ConcreteObject parseString(String string)  {
        boolean error = false;
        ArrayList<Integer> array = new ArrayList<>();
        for(String part : ListObject.removeBrackets(string).split("\\s*,\\s*")) {
            try{array.add(Integer.parseInt(part));}
            catch(Exception ex) {error = true;}}
        return error ? null : new IntegerList(array);}

    /** checks if the string can be parsed.
     *
     * @param string a string to be tested
     * @return null or an error message.
     */
    public static String parseCheck(String string)  {
        StringBuilder errors = new StringBuilder();
        for(String part : ListObject.removeBrackets(string).split("\\s*,\\s*")) {
            try{Integer.parseInt(part);}
            catch(Exception ex) {errors.append(ex.toString()).append("\n");}}
        return (errors.length() == 0) ? null : errors.toString();}

}
