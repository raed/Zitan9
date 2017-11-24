
package ConcreteDomain.SetTypes;

import ConcreteDomain.SetObject;

import java.io.Serializable;
import java.util.Objects;

/** This class represents isConvex intervals of comparable other types.
 *
 * @param <T> the type of the interval boundaries.
 */
public class Interval<T extends Comparable> extends SetObject implements Serializable {
    public T from;
    public T to;
    
     /** constructs a new interval
     * 
     * @param from start
     * @param to   end
     */
    public Interval(T from, T to) {
        switch(from.compareTo(to)) {
            case -1: this.from = from; this.to = to; break;
            case 0: this.from = from; this.to = from; break;
            case 1: this.from = to; this.to = from;}}
    
     /** returns an array [from,to]
     * 
     * @return an array [from,to]
     */
    @Override
    public Object get() {return new Object[]{from,to};}  
    
    /** checks if the interval is actually just a point 
     * 
     * @return true if the interval is actually just a point.
     */
    public boolean isPoint(){return from == to;}

    public Object getFirst() {return from;}

    /** compares two intervals
     *
     * @param other the other opject to be compared
     * @return true if both are equal.
     */
    @Override
    public boolean equals(Object other) {
        return other != null && (other instanceof Interval) && from.equals(((Interval)other).from) && to.equals(((Interval)other).to);
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.from);
        hash = 17 * hash + Objects.hashCode(this.to);
        return hash;}
    
    /** generates from-to
     * 
     * @return from-to
     */
    @Override
    public String toString() {
        String froms = from.toString(); if(froms.endsWith(".0")) {froms = froms.substring(0,froms.length()-2);}
        String tos = to.toString();     if(tos.endsWith(".0")) {tos = tos.substring(0,tos.length()-2);}
        return froms + " - " + tos;}
    
 
        
}
