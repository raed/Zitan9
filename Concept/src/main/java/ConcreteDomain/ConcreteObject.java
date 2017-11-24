package ConcreteDomain;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;

import java.lang.reflect.Method;
import java.util.HashMap;


/** This is the abstract superclass for all concrete objects (atomic and set objects).
 * It provides the set-predicates for the Number and String-Types.
 * The methods equals, isDisjoint and isSubset should be the only public methods.
 * The other ones are public just to enable unit tests.
 *
 */
public abstract class ConcreteObject implements DataObject {
    private static HashMap<Class,HashMap<Class,Method>> compareMethods = new HashMap<>();

    /** @return true if the SetObject consists of a single point */
    public abstract boolean isPoint();
    /** @return the fist object (should be called only if isPoint() returns true.*/
    public abstract Object getFirst();


    /** This method can be called for the ConcreteDomain classes to announce the available compare methods.
     * Example from the IntegerObject class.<br>
     * A call
     * <pre>
     * {@code
     * static {
     * ConcreteObject.addCompareMethod(IntegerObject.class, FloatObject.class,
     * IntegerInterval.class, IntegerList.class, FloatInterval.class,FloatList.class);
     * }}
     </pre>
     * announces that there are methods <br>
     *   public Boolean compare(Operators operator, IntegerObject number)<br>
     *   public Boolean compare(Operators operator, FloatObject number) <br>
     * etc.
     *
     * @param class1 the calling class
     * @param classes the other classes for which a compare-method is available.
     */
    protected static void addCompareMethod(Class class1, Class... classes) {
        HashMap<Class,Method> map = compareMethods.get(class1);
        if(map == null) {map = new HashMap<>(); compareMethods.put(class1,map);}
        try {
            Method method = class1.getDeclaredMethod("compare", Operators.class, class1);
            map.put(class1,method);
            for(Class clazz : classes) {
                method = class1.getDeclaredMethod("compare", Operators.class, clazz);
                map.put(clazz,method);}}
        catch(Exception ex) {System.out.println(ex.toString());}
    }

    /** compares 'this' operator 'value'
     *
     * @param operator for comapring the items
     * @param value the value to be compared with 'this'
     * @return the result of the comparison, or null if the operator is not applicable.
     */
    public Boolean compare(Operators operator, ConcreteObject value)  {
        try{Method method = compareMethods.get(this.getClass()).get(value.getClass());
            return (Boolean)method.invoke(this,operator,value);}
        catch(Exception ex) {
            System.out.println(this.toString() + " " + operator + " " +  value);
            System.out.println(ex.toString());
            return null;}
    }


    @Override
    public boolean equals(Object object) {
        return object != null && this.getClass() == object.getClass() && get().equals(((DataObject)object).get());
    }

   
}


