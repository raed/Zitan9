
package ConcreteDomain.AtomicTypes;

import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import MISC.Context;
import MISC.DateParser;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDateTime;

/** This class is a wrapper for LocalDateTime. It represents absolute time points.
 *
 */
public class AbsoluteTimePoint extends AtomicObject implements Serializable {
    private final LocalDateTime value;
    
    public AbsoluteTimePoint(LocalDateTime value) {
        this.value = value;}
    
    public AbsoluteTimePoint(int year, int month, int dayOfMonth, int hour, int minute, int second)throws DateTimeException {
        value = LocalDateTime.of(year,month,dayOfMonth,hour,minute,second); }
    
    public AbsoluteTimePoint(int year, int month, int dayOfMonth, int hour, int minute) throws DateTimeException{
        value = LocalDateTime.of(year,month,dayOfMonth,hour,minute); }
    
    public AbsoluteTimePoint(int year, int month, int dayOfMonth, int hour) throws DateTimeException{
        value = LocalDateTime.of(year,month,dayOfMonth,hour,0); }
    
    public AbsoluteTimePoint(int year, int month, int dayOfMonth) throws DateTimeException {
        value = LocalDateTime.of(year,month,dayOfMonth,0,0); }
    
    public AbsoluteTimePoint(int year, int month) throws DateTimeException {
        value = LocalDateTime.of(year,month,1,0,0); }
    
    public AbsoluteTimePoint(int year) {
        value = LocalDateTime.of(year,1,1,0,0); }
    

    /** returns 'now' as AbsoluteTimePointObject
     * 
     * @return 'now' as AbsoluteTimePointObject
     */
    public static AbsoluteTimePoint now() {
        return new AbsoluteTimePoint(LocalDateTime.now());}
    
    /** checks the two objects for equality */ 
    @Override
    public boolean equals(Object object) {
        if(object == null) {return false;}
        return (object instanceof AbsoluteTimePoint) && value.isEqual(((AbsoluteTimePoint)object).value);}

    @Override
    public int hashCode() {return value.hashCode();}
    
    public Object get() {return value;}
    
    /** parses a string to a AbsoluteTimePointObject
     * 
     * @param string the string to be parsed.
     * @param context not needed here
     * @return the parsed AbsoluteTimePointObject, or null if there was a syntax error
     */
    public static ConcreteObject parseString(String string, Context context) {
        LocalDateTime time = DateParser.parse(string);
        return time == null ? null : new AbsoluteTimePoint(time);}
    
    /** this method generates a string representation for the date-time.
     * It could be improved considerably.
     * 
     * @return a string representation.
     */
    @Override
    public String toString() {return value.toString();}
    
}
