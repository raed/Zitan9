package ConcreteDomain.AtomicTypes;

import AbstractObjects.Operators;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.SetTypes.*;


/** This is the abstract superclass of all number objects (Integer, Float, Double).
 * The set methods in this class allows for comparing all Number types, Interval types and Lists of Number types.
 *
 */
public abstract class NumberObject extends AtomicObject {

    private static Class[] classes1 = new Class[]{IntegerObject.class, FloatObject.class,};
    private static Class[] classes2 = new Class[]{IntegerObject.class, FloatObject.class,
            IntegerInterval.class,IntegerList.class ,FloatInterval.class ,FloatList.class};


 /** This method yields for a given operator an array of all those classes the operator is able to compare with 'this#.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    public static Class[] comparable(Operators operator) {
         switch(operator) {
            case LESS:
            case LESSEQUALS:
            case EQUALS:
            case GREATER:
            case GREATEREQUALS: return classes1;

            case BEFORE:
            case STARTS:
            case IN:
            case FINISHES:
            case AFTER:  return classes2;
         }
        return null;
    }



}