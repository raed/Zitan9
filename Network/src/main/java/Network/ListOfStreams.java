package Network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/** This is a superclass for ListOfStreamsCached and ListOfStreamsUncached.
 * It can be used on its own if there are no streams in the list.
 *
 * @param <T> the type of the items in the list.
 */
public class ListOfStreams<T> implements Iterable<T> {
    /** a list of items. It may also server as cache for the streams */
    protected ArrayList<T> items = new ArrayList();

    /** generates a cached or uncached list of streams. 
     * 
     * @param <T> the type of the items.
     * @param cached true if the list should be cached.
     * @return a cached or uncached list of streams.
     */
    public static <T> ListOfStreams<T> getListOfStreams(boolean cached) {
        return cached ? new ListOfStreamsCached() : new ListOfStreamsUncached();}
    
    
    /** adds an item to the list.
     * 
     * @param item the item to be added.
     * @return the list itself.
     */
    public synchronized ListOfStreams<T> add(T item) {
        items.add(item);
        return this;}
    
    /** removes an item from the list
     * 
     * @param item the item to be removed
     * @return the list itself.
     */
    public synchronized ListOfStreams<T> remove(T item) {
        items.remove(item);
        return this;}
    
    /** closes all operations on the stream (the method is to be overwritten)
     */
    public synchronized void close() {}
    
    /** empties the list */
    public synchronized void clear() {items.clear();}
    
     
    /** returns the iterator for list.
     * 
     * @return the iterator for list */
    @Override
    public Iterator<T> iterator() {return items.iterator();}
    
    /** returns the stream for the list.
     * @return  the stream for the list.*/
    public Stream<T> stream() {return items.stream();}
}
