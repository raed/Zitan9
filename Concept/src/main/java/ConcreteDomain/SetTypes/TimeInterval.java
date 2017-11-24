package ConcreteDomain.SetTypes;

import ConcreteDomain.AtomicTypes.AbsoluteTimePoint;
import ConcreteDomain.AtomicTypes.Duration;
import ConcreteDomain.ConcreteObject;
import MISC.Commons;
import MISC.Context;
import MISC.DateParser;

import java.time.LocalDateTime;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class models time intervals between two LocalDateTime points.
 *
 */
public class TimeInterval extends Interval<LocalDateTime> {

    /** constructs a TimeIntervalObject from two LocalDateTimes
     * 
     * @param from the start time (inclusive)
     * @param to   the end time (exclusive)
     */
    public TimeInterval(LocalDateTime from, LocalDateTime to) {
        super(from, to);}
    
    /** constructs a TimeIntervalObject from two AbsoluteTimePointObject objects
     * 
     * @param from the start time (inclusive)
     * @param to   the end time (exclusive)
     */
    public TimeInterval(AbsoluteTimePoint from, AbsoluteTimePoint to) {
        super((LocalDateTime)from.get(), (LocalDateTime)to.get());}
    
    // ES FEHLT NOCH EIN KONSTRUKTOR FÜR DAS INTERVALL ZWISCHEN RELATIVEN ZEITPUNKTEN
    
    /** parses a string "from - to" to a TimeIntervalObject
     * 
     * @param string the string to be parsed.
     * @param context not needed here
     * @return the parsed TimeIntervalObject or null if there was a syntax error.
     */
    public static ConcreteObject parseString(String string, Context context)  {
        String[] parts = string.split("\\s*-\\s*");
        if(parts.length != 2) {
            Commons.getMessanger(DataErrors).insert("Typo","malformed integer interval: " + string);
            return null;}
        LocalDateTime from = DateParser.parse(parts[0]);
        LocalDateTime to   = DateParser.parse(parts[1]);
        return (from == null || to == null) ? null : new TimeInterval(from,to);}
    
    /** computes the duration between the start- and endpoints of the interval
     * 
     * @return the duration between the start- and endpoints of the interval.
     */
    public Duration duration() {return new Duration(from,to);}
    
    
}
