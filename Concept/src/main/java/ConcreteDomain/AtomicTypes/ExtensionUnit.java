package ConcreteDomain.AtomicTypes;

import ConcreteDomain.AtomicObject;
import UnitsOfMeasurement.UnitOfMeasurement;

/** This is the abstract superclass for those extension types consisting of a value a unit of measurement.
 *
 */
public abstract class ExtensionUnit extends AtomicObject {
    public float value;
    public UnitOfMeasurement unit;
    
    /** constructs a ExtensionUnitObject from a value and a unit of measurement
     * 
     * @param value the flaot value
     * @param unit the unit of measurement
     */
    public ExtensionUnit(float value, UnitOfMeasurement unit) {
        this.value = value;
        this.unit = unit;}
    
    /** returns [value,unit]
     * 
     * @return [value,unit]
     */
    @Override
    public Object get() {return new Object[]{value,unit};}
    
    /** returns "value unit", for example "3.5 km" 
     * 
     * @return "value unit", for example "3.5 km" 
     */
    @Override
    public String toString() {
        String val = Float.toString(value);
        if(val.endsWith(".0")) {val = val.substring(0,val.length()-2);}
        return val+ " " + unit.shortString();}

    /** returns the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Float.hashCode(value) * unit.id.hashCode();}
}
