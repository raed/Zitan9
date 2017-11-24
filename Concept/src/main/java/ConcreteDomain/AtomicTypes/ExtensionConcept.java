package ConcreteDomain.AtomicTypes;

import Concepts.Concept;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import MISC.Commons;
import MISC.Context;
import Utils.Utilities;

import java.io.Serializable;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class models extensions like "3 semesters" or "5 working days".
 *
 */
public class ExtensionConcept extends AtomicObject implements Serializable {
    float value;
    Concept unit;
    
    /** constructs an ExtensionConceptObject with a value and a IndividualConcept
     * Example: 3.5 Semester
     * 
     * @param value the value as float
     * @param unit the concept as unit.
     */
    public ExtensionConcept(float value, Concept unit) {
        this.value = value;
        this.unit = unit;}
    
    /** returns [value,unit]
     * 
     * @return [value,unit]
     */
    @Override
    public Object get() {return new Object[]{value,unit};}
    
    @Override
    public String toString() {
        String s = Float.toString(value);
        if(s.endsWith(".0")) {s = s.substring(0,s.length()-2);}
        return s + " " + unit.getName();}
    /**
     * 
     * @return the hash code 
     */
    @Override
    public int hashCode() {
        return Float.hashCode(value) * unit.getName().hashCode();}

    /** tests the two objects for equality
     * 
     * @param obj the second object
     * @return true if the two objects are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {return false;}
        if (getClass() != obj.getClass()) {return false;}
        final ExtensionConcept other = (ExtensionConcept) obj;
        if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {return false;}
        return unit.getName().equals(other.unit.getName());}
    
    /** parses a string to an ExtensionConceptObject
     * 
     * @param string the string to be parsed.
     * @param context not needed here
     * @return the parsed ExtensionConceptObject or null if there was a syntax error.
     */
    public static ConcreteObject parseString(String string, Context context)  {
        String[] parts = string.split("\\s+");
        if(parts.length != 2) {
            Commons.getMessanger(DataErrors).insert("Typo", "malformed extension: " + string);
            return null;}
        Float value = Utilities.parseFloat(parts[0]);
        if(value == null) {return null;}
        Concept unit = context.getConcept(parts[1]);
        if(unit == null) {
            Commons.getMessanger(DataErrors).insert("Typo","unkonwn concept: " + parts[1]);
            return null;}
        return new ExtensionConcept(value,unit);}
}
