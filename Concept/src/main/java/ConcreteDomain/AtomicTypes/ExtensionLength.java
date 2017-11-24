package ConcreteDomain.AtomicTypes;

import ConcreteDomain.ConcreteObject;
import MISC.Commons;
import MISC.Context;
import UnitsOfMeasurement.Length;
import Utils.Utilities;

import java.io.Serializable;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class represents extensions like 3 kilometers or 5 millimeters
 *
 */
public class ExtensionLength extends ExtensionUnit implements Serializable {

    /** constructs an ExtensionLengthObject from a value and a Length unit.
     * 
     * @param value the value
     * @param unit the length unit (meter, kilometer etc.)
     */
    public ExtensionLength(float value, Length unit) {
        super(value, unit);}
    
    /** compares the two objects for equality.
     * For example 1000 meter and 1 kilometer are equal
     * 
     * @param object the other object
     * @return true if the two objects are equals.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof ExtensionLength)) {return false;}
        ExtensionLength other = (ExtensionLength)object;
        Length un = (Length)other.unit;
        return value == un.convert(other.value,(Length)unit); }
    
    /** parses a string to a ExtensionLengthObject
     * 
     * @param string the string to be parsed.
     * @param context not needed here
     * @return the parsed ExtensionLengthObject, or null if a syntax error occurred.
     */
    public static ConcreteObject parseString(String string, Context context)  {
        String[] parts = string.split("\\s+");
        if(parts.length != 2) {
            Commons.getMessanger(DataErrors).insert("Typo", "malformed extension: " + string);
            return null;}
        Float value = Utilities.parseFloat(parts[0]);
        Length unit = Length.parseString(parts[1]);
        return (value == null || unit == null) ? null : new ExtensionLength(value,unit);}
    
    
}
