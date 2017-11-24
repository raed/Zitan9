package ConcreteDomain.AtomicTypes;

import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import MISC.Commons;
import MISC.Context;
import UnitsOfMeasurement.GregorianTimeUnit;
import Utils.Utilities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static Utils.Messanger.MessangerType.DataErrors;

/** The class models temporal extensions in the Gregorian Calendar.<br>
 * Example: 3 months, 55.7 minutes
 *
 */
public class Duration extends AtomicObject {
    private  double[] values = null;
    private  GregorianTimeUnit[] units = null;
    
    /** constructs a ExtensionGregorianTimeObject.
     * The dates are normalized, e.g. 8 days becomes 1 week, 1 day.
     * 
     * @param values the values 
     * @param units  the corresponding time units.
     */
    public Duration(double[] values, GregorianTimeUnit[] units) {
        assert(values.length == units.length);
        assert(values.length > 0);
        Object[] normalized = GregorianTimeUnit.normalize(values, units);
        this.values = (double[])normalized[0];
        this.units = (GregorianTimeUnit[])normalized[1];}
    
     public Duration(LocalDateTime from, LocalDateTime to) {
         initialize(from,to);
     }
    
    /** calcuates the duration between two absolute time points. 
     * The normalized result is either year,month if there are o days left,<br>
     * or otherwise week,day,hour,minute,second<br>
     * since the number of days in a month or year differs, a large amount of days 
     * cannot be converted to months and years.<br>
     * If toTime is before fromTime, they are switched. 
     * 
     * @param fromTime a concrete time point
     * @param toTime another concrete time point (before or after fromTime)
     */
    public Duration(AbsoluteTimePoint fromTime, AbsoluteTimePoint toTime) {
        initialize((LocalDateTime)fromTime.get(),(LocalDateTime)toTime.get());}
        
    private void initialize(LocalDateTime from, LocalDateTime to) {
        if(to.isBefore(from)) {LocalDateTime dummy = to; to = from; from = dummy;}
        long months = from.until(to, ChronoUnit.MONTHS); 
        if(from.plusMonths(months).isEqual(to)) {
            values = new double[]{(double)months};
            units = new GregorianTimeUnit[]{GregorianTimeUnit.month};}
        else{long microseconds = from.until(to,ChronoUnit.MICROS);
            values = new double[]{(double)microseconds};  
            units = new GregorianTimeUnit[]{GregorianTimeUnit.microsecond};}
        Object[] normalized = GregorianTimeUnit.normalize(values, units);
        this.values = (double[])normalized[0];
        this.units = (GregorianTimeUnit[])normalized[1];
    }
    
    
    /** checks the two objects for equality.
     * The algorithm exploits that the dates are normalized.
     * 
     * @param object the other DurationObject 
     * @return true if the two are equal.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof Duration)) {return false;}
        Duration other = (Duration)object;
        if(values.length != other.values.length) {return false;}
        for(int i = 0; i < values.length; ++i) {
            if(values[i] != other.values[i]) {return false;}
            if(units[i]  != other.units[i])  {return false;}}
        return true;}

    /**
     * @return the hash code for the object 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Arrays.hashCode(this.values);
        hash = 89 * hash + Arrays.deepHashCode(this.units);
        return hash;
    }
    
    /** generates a string representation of the time object
     * 
     * @return the time object as a string.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < values.length; ++i) {
            String val = Double.toString(values[i]);
            if(val.endsWith(".0")) {val = val.substring(0,val.length()-2);}
            s.append(val).append(" ");
            s.append(units[i].shortString());
            if(i < values.length-1) {s.append(", ");}}
        return s.toString();}

    /** parses a string to a DurationObject
     *
     * @param string the string to be parsed.
     * @param context not needed here
     * @return the parsed Duration, or null if a syntax error occurred.
     */
    public static ConcreteObject parseString(String string, Context context)  {
        String[] parts = string.split("\\s*,\\s*");
        int length = parts.length;
        double[] values = new double[length];
        GregorianTimeUnit[] units= new GregorianTimeUnit[length];
        boolean error = false;
        for(int i = 0; i < length; ++i) {
            String[] p = parts[i].split("\\s+");
            if(p.length != 2) {
                Commons.getMessanger(DataErrors).insert("Typo", "malformed extension: " + string);
                continue;}
            Double value = Utilities.parseDouble(p[0]);
            if(value == null) {error = true;}
            else values[i] = value;
            units[i] = GregorianTimeUnit.parseString(p[1]);
            if(units[i] == null) {error = true;}}
        return error ? null : new Duration(values,units);}
    
}
