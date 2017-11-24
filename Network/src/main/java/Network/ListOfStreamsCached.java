package Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/** This class maintains an unordered list of items which can either be given explicitely, or be provided on demand by streams.<br>
 * The list can only be accessed by means of an iterator or a stream.<br>
 * The idea of this class is to provide a common interface (iterator or stream) to sources of objects,
 * which can come from wherever the streams get them, even from a remote url.
 * The common iterator (and the stream) access the underlying streams only when they need a further item. 
 * The items delivered by the streams are locally cached such that the iterator can be called severals times
 * without activating the streams again.
 * One can even use nested iterators or streams. 
 * <br>
 * Each stream is processed in its own thread. One can therefore start and stop the iterations independently 
 * of the streams threads. An iterator which finished its work does not stop the thread, 
 * because a new iterator may need more items from the streams. 
 * <br>
 * In order to stop all threads one must call close() or clear().
 * The underlying streams should have an onClose-handler.
 * These are called from the close()-method.
 * <br>
 * A once closed list can again be filled with new streams while the prviously cached items are still there. 
 *
 * @param <T> the item type
 */ 
public class ListOfStreamsCached <T> extends ListOfStreams<T> {
    /** maps streams to the corresponding threads. */
    private HashMap<Stream<T>,Thread> streams = new HashMap();
    /** allows the hasNext-method of the iterator to control access to the internal cache. */
    private boolean locked = true;
    /** indicates that the list is closed */
    private boolean closed = false;
   
    /** closes all underlying streams, sends each thread the interrupt signal and removes all streams and threads.
     * The close-method of the underlying streams should really terminate everything associated with this stream.
     */
    @Override
    public synchronized void close() {
        streams.forEach((stream,thread) -> {stream.close(); thread.interrupt();});
        streams.clear();
        closed = true;}
    
    /** reopends the list again such that it can be filled with new streams. */ 
    public void reopen() {
        locked = true;
        closed = false;}
    
    /** closes the list (@see close()) and clears the cache */ 
    @Override
    public synchronized void clear() {close(); items.clear();}
    
    /** closes the stream, interrupts the thread and removes the stream from the list.<br>
     * When all streams are gone it calls notifyAll to wake up all hasNext-methods.
     * 
     * @param stream the stream to be removed.
     */
    private synchronized void remove(Stream stream){
        Thread thread = streams.get(stream);
        if(thread == null) {return;}
        stream.close();
        thread.interrupt();
        streams.remove(stream);
        if(streams.isEmpty()) {notifyAll();}
    } 
    
    /** adds an item to the local cache.<br> 
     * This method is called from the threads to add the item which is fetched from the stream to the cache.
     * Access to the cache is controlled by the variable 'locked'. 
     * It is released by the hasNext-method of the iterators such that an item is added only when the iterator needs one.
     * <br>
     * This method cases that all threads fetch just one item from their streams.
     * They then wait until the hasHext-method allows them to add one item to the list.
     * 
     * @param item the item to be added to the stream.
     * @return true if the iteration should be stopped.
     */ 
    private synchronized boolean addItem(T item){
            if(closed){items.add(item);return true;}
            while(locked) {
                try{wait();}
                catch(InterruptedException e) {return true;}}
            locked = true;
            add(item); 
            notifyAll();
            return false;}
    

    /** adds a new stream to the list.<br>
     * For the new stream a new thread is created, but not yet started.
     * 
     * @param stream the stream to be added.
     * @return 'this'.
     */
    public synchronized ListOfStreamsCached<T> add(Stream<T> stream) {
        streams.put(stream, 
                new Thread(()->{ // it fetches one item from the stream and then waits until the cache is unlocked. 
                    stream.anyMatch((T item) -> {return addItem(item) || Thread.currentThread().isInterrupted();});
                    remove(stream);}));
        return this;}
    
    /** This iterator at first iterates through the cache, and then allows the streams' threads
     * to add new items to the cache (only one when it is needed.)
     */
    public class StreamIterator implements Iterator<T> {
        /** the index for the cache */
        private int index = -1; 
        /** stops the iterator */
        private boolean stopped = false;
        
        private boolean started = false;
        
        /** checks if there is another item for the iterator.<br>
         * At first it iterates over the cache. 
         * When the cache is empty then threads are started. 
         * They fetch one item from their streams and then wait until the hasNext method 
         * allows them to put one item into the cache.
         * 
         * @return true if there is another item in the cache.
         */
        @Override
        public boolean hasNext() {
            if(stopped || closed) {return false;}
            if(items != null && index < items.size()-1) {++index; return true;} 
            if(streams.isEmpty()) {return false;}
            if(!started) {
                streams.forEach((st,thread) -> {if(thread.getState() == Thread.State.NEW) {thread.start();}});
                started = true;}
            synchronized(ListOfStreamsCached.this){
                locked = false;
                ListOfStreamsCached.this.notify();
                try {if(!streams.isEmpty()){
                    ListOfStreamsCached.this.wait();}}
                catch (InterruptedException ex) {return false;} 
                ++index; 
                return index < items.size();}
                }
        
        /** returns the next item.
         * 
         * @return the next item 
         */
        @Override
        public T next() {return items.get(index);}
        
        /** is called by the onClose-method of the generated stream to stop the iteration. */
        private void stop() {stopped = true;}
    }

    /** returns a new StreamIterator
     * 
     * @return a new StreamIterator 
     */
    @Override
    public Iterator iterator() {
        return new StreamIterator();}
    
    /** generates a new stream which is based on the StreamIterator.
     * 
     * @return a new stream for the list.
     */
    @Override
    public Stream<T> stream() {
        StreamIterator iterator = new StreamIterator();
        Stream<T> stream =  StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,0),false);
        return stream.onClose(()->iterator.stop());}
    
    
    
    
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> ar1 = new ArrayList(); 
        ar1.add(1); ar1.add(2); ar1.add(3); ar1.add(4);
       
        ArrayList<Integer> ar2 = new ArrayList(); 
        ar2.add(5); ar2.add(6); ar2.add(7); ar2.add(8);
         ArrayList<Integer> ar3 = new ArrayList(); 
        ar3.add(10); ar3.add(11); ar3.add(12); ar3.add(13);
        
        ListOfStreamsCached<Integer> l = new ListOfStreamsCached();
        
        l.add(ar1.stream());//.peek(c->{try{Thread.sleep(200);}catch(Exception e){}})); 
       l.add(ar2.stream());//.peek(c->{try{Thread.sleep(300);}catch(Exception e){}})); 
       l.add(ar3.stream());
        l.add(20);l.add(30);
        
        l.stream().sorted().forEach(System.out::println);
       
        System.out.println("STOP 1");
       
        Stream<Integer> st = l.stream();
        st.sorted().anyMatch(i->{System.out.println(i);if(i == 1) {return false;}return false;});
        l.close();
        System.out.println("STOP 2");
               
    }
    
}
