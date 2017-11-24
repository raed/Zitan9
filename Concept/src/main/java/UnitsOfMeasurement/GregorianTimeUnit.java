package UnitsOfMeasurement;

import MISC.Commons;

import java.io.Serializable;
import java.util.ArrayList;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class represents the time units in the Gregorian Calendar.
 *
 * @author ohlbach
 */
public class GregorianTimeUnit extends UnitOfMeasurement implements Serializable {
    private final long conversionFactor;
    /** contains all time units in ascending order */
    public static ArrayList<GregorianTimeUnit> timeUnits = new ArrayList();
    
    public static GregorianTimeUnit microsecond = new GregorianTimeUnit("microsecond","mc",1L);
    public static GregorianTimeUnit millisecond = new GregorianTimeUnit("millisecond","ms",1000L);
    public static GregorianTimeUnit second      = new GregorianTimeUnit("second","s",1000000L);
    public static GregorianTimeUnit minute      = new GregorianTimeUnit("minute","min",60000000L);
    public static GregorianTimeUnit hour        = new GregorianTimeUnit("hour","h",3600000000L);
    public static GregorianTimeUnit day         = new GregorianTimeUnit("day","d",24*3600000000L);
    public static GregorianTimeUnit week        = new GregorianTimeUnit("week","w",7*24*3600000000L);
    public static GregorianTimeUnit month       = new GregorianTimeUnit("month","m",1L);
    public static GregorianTimeUnit year        = new GregorianTimeUnit("year","y",12L);
    public static GregorianTimeUnit decade      = new GregorianTimeUnit("decade","dec",120L);
    public static GregorianTimeUnit century     = new GregorianTimeUnit("century","cen",1200L);
    public static GregorianTimeUnit millenium   = new GregorianTimeUnit("millenium","mil",12000L);
    
    
    
    /** contructs a time unit.
     * Only the predefined ones can be created.
     * 
     * @param id the applicationName of the time unit
     * @param shortId the abbreviation for the time unit
     * @param conversionFactor for converting to seconds or months.
     */
    private GregorianTimeUnit(String id, String shortId, long conversionFactor) {
        super(id, shortId);
        this.conversionFactor = conversionFactor;
        timeUnits.add(this);}
    
    /** checks if the unit is month, year or decade.
     * 
     * @return true if the unit is month, year or decade.
     */
    public boolean isAboveWeek() {
        return this == month || this == year || this == decade || this == century || this == millenium;}
    
     /** converts the given measure from 'this' to unit. 
     * Example: 1 minute becomes 3 seconds.
     * 
     * @param measure any double
     * @param unit one of the WeightMetric units
     * @return the converted measure or NaN if it cannot be converted.
     */
    public double convert(double measure, GregorianTimeUnit unit) {
        if((isAboveWeek() && unit.isAboveWeek()) ||  (!isAboveWeek() && !unit.isAboveWeek())) {
            return (double)(measure*conversionFactor)/unit.conversionFactor;}
        return Double.NaN;}
    
    
    /** normalizes a list of value-time units such that they stay within their limits
     *  and that they are ordered top-down.<br>
     * Example: 5000 days, 500 months becomes: 41.0 year, 8.0 month, 714.0 week, 2.0 day<br>
     * months cannot be converted to weeks because they are not synchronous.
     * 
     * @param values a list of time unit values
     * @param units the corresponding list of time units
     * @return an array [newValues,timeUnits]
     */
    public static Object[] normalize(double[] values, GregorianTimeUnit[] units) {
        assert(values.length == units.length);
        int length = values.length;
        ArrayList<Double> newValues = new ArrayList();
        ArrayList<GregorianTimeUnit> newUnits = new ArrayList();
        double months = 0L;
        long microseconds = 0L;
        for(int i = 0; i < length; ++i) {
            GregorianTimeUnit unit = units[i];
            if(unit.isAboveWeek()) {months  += values[i]*unit.conversionFactor;;}
            else                   {microseconds += values[i]*unit.conversionFactor;}}
        if(months >= 12) {
            newUnits.add(year);
            long years = (long)(months/12);
            newValues.add((double)years);
            months -= 12*years;}
        
        if(months > 0) {newUnits.add(month);newValues.add(months);}
        System.out.println("MC " + microseconds);
        if(microseconds > 0) {
            if(microseconds >= week.conversionFactor) {
                newUnits.add(week);
                long weeks = microseconds/week.conversionFactor;
                newValues.add((double)weeks);
                microseconds -= week.conversionFactor*weeks;}
            
            if(microseconds >= day.conversionFactor) {
                newUnits.add(day);
                long days = microseconds/day.conversionFactor;
                newValues.add((double)days);
                microseconds -= day.conversionFactor*days;}
            
            if(microseconds >= hour.conversionFactor) {
                newUnits.add(hour);
                long hours = microseconds/hour.conversionFactor;
                newValues.add((double)hours);
                microseconds -= hour.conversionFactor*hours;}
            
            if(microseconds >= minute.conversionFactor) {
                newUnits.add(minute);
                long minutes = microseconds/minute.conversionFactor;
                newValues.add((double)minutes);
                microseconds -= minute.conversionFactor*minutes;}
            
            if(microseconds >= second.conversionFactor) {
                newUnits.add(second);
                long seconds = microseconds/second.conversionFactor;
                newValues.add((double)seconds);
                microseconds -= second.conversionFactor*seconds;}
            
            if(microseconds >= millisecond.conversionFactor) {
                newUnits.add(millisecond);
                long milliseconds = microseconds/millisecond.conversionFactor;
                newValues.add((double)milliseconds);
                microseconds -= millisecond.conversionFactor*milliseconds;}
            
            if(microseconds != 0) {
                newValues.add((double)microseconds);
                newUnits.add(microsecond);}}
        
        length = newValues.size();
        double[] values1 = new double[length];
        GregorianTimeUnit[] units1 = new GregorianTimeUnit[length];
        for(int i = 0; i < length; ++i) {
            values1[i] = newValues.get(i);
            units1[i]  = newUnits.get(i);}
        
        return new Object[]{values1,units1};}
    
    /** parses a string into a time unit.
     * 
     * @param string the string to be parsed.
     * @return one of the predefined time units or null if none can be identified.
     */
    public static GregorianTimeUnit parseString(String string) {
        string = string.toLowerCase();
        if(string.startsWith("microsecond") || string.equals("mcs")) return microsecond;
        if(string.startsWith("millisecond") || string.equals("ms"))  return millisecond;
        if(string.startsWith("second")      || string.equals("s"))   return second;
        if(string.startsWith("minute")      || string.equals("min")) return minute;
        if(string.startsWith("hour")        || string.equals("h"))   return hour;
        if(string.startsWith("day")         || string.equals("d"))   return day;
        if(string.startsWith("week")        || string.equals("w"))   return week;
        if(string.startsWith("month")       || string.equals("m"))   return month;
        if(string.startsWith("year")        || string.equals("s"))   return year;
        if(string.startsWith("decade")      || string.equals("s"))   return decade;
        if(string.startsWith("century")     || string.equals("cen")) return century;
        if(string.startsWith("millenium")   || string.equals("mil")) return millenium;
        Commons.getMessanger(DataErrors).insert("Typo","Unknown time unit: " + string);
        return null;}
     
}
