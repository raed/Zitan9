package ConcreteDomain.SetTypes;

import ConcreteDomain.AtomicTypes.RelativeTimePoint;
import ConcreteDomain.SetObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** This class represents time intervals like
 *  lecture time: every Monday between 10 and 12 o'clock, from 2016/10/15 until 2017/2/15
 *
 */
public class BoundedTimeInterval extends SetObject implements Serializable {
    /** the earlier bound */
    private LocalDateTime first;
    /** the later bound */
    private LocalDateTime last;
    /** the relative start time of the intervals. */
    private RelativeTimePoint start;
    /** the relative end time of the intervals */
    private RelativeTimePoint end;
    /** the actual list of intervals (autmatically computed) */ 
    private ArrayList<LocalDateTime[]> intervals;
    
    /** constructs a new bounded interval.
     * 
     * @param first the earlier bound
     * @param last the later bound
     * @param start the relative start time of the intervals.
     * @param end the relative end time of the intervals
     */
    public BoundedTimeInterval(LocalDateTime first, LocalDateTime last, RelativeTimePoint start, RelativeTimePoint end) {
        assert(first.isBefore(last));
        this.first = first;
        this.last = last;
        this.start = start;
        this.end = end;
        computeIntervals();}
    
    /** This method computes the list of intervals between the two bounds*/
    private void computeIntervals() {
        intervals = new ArrayList();
        LocalDateTime date = first;
        while(true) {
            LocalDateTime date1 = start.firstDateAfter(date);
            if(!date1.isBefore(last)) {break;}
            LocalDateTime date2 = end.firstDateAfter(date1);
            if(!(date2.isBefore(last) || date2.equals(last))) {break;}
            intervals.add(new LocalDateTime[]{date1,date2});
            date = date2;}}
    
    /**returns the list of intervals as ArrayList[LocalDateTime[]]
     * 
     * @return the list of intervals as ArrayList[LocalDateTime[]].
     */
    @Override
    public Object get() {return intervals;}

   
    /** This method computes the overlappings between two lists of intervals.
     * 
     * @param list1 a list of intervals
     * @param list2 a list of intervals 
     * @return a list of intervals intervalContaining the overlappings between the two lists.
     */
    public static ArrayList<LocalDateTime[]> overlappings(ArrayList<LocalDateTime[]> list1, ArrayList<LocalDateTime[]> list2) {
        ArrayList<LocalDateTime[]> overlapping = new ArrayList();
        for(LocalDateTime[] interval1 : list1) {
            LocalDateTime start1 = interval1[0];
            LocalDateTime end1 = interval1[1];
            for(LocalDateTime[] interval2 : list2) {
                LocalDateTime start2 = interval2[0];
                LocalDateTime end2 = interval2[1];
                boolean s1s2 = start1.equals(start2) || start1.isBefore(start2);
                boolean e1e2 = end1.equals(end2) || end1.isBefore(end2);
                boolean s2s1 = start2.equals(start1) || start2.isBefore(start1);
                boolean e2e1 = end2.equals(end1) || end2.isBefore(end1);
                if(s1s2 && e1e2) {overlapping.add(new LocalDateTime[]{start2,end1}); continue;}
                if(s2s1 && e2e1) {overlapping.add(new LocalDateTime[]{start1,end2}); continue;}
                if(s1s2 && e2e1) {overlapping.add(new LocalDateTime[]{start2,end2}); continue;}
                if(s2s1 && e1e2) {overlapping.add(new LocalDateTime[]{start1,end1}); continue;}}}
        return overlapping;}


    
    /** returns the isConvex hull of the bounded time intervals.<br>
     * As an example: consider the lecture time within a semster. <br>
     * The isConvex hull spans from the start of the first lecture until the end of the last lecture.
     * 
     * @return the isConvex hull as [leftmost LocalDataTime, rightmost LocalDateTime]
     */
    public LocalDateTime[] convexHull() {
        return new LocalDateTime[]{intervals.get(0)[0], intervals.get(intervals.size()-1)[1]};
    }

    @Override
    public boolean isPoint() {
        return false;
    }

    @Override
    public Object getFirst() {
        return null;
    }
}
