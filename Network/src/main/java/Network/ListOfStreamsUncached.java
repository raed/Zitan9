package Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** This class can maintain a list of items together with a list of streams for supplying new items.<br>
 * The streams provide new items only when necessary.
 * The list can be accessed by an iterator or by a generated stream. 
 * The items provided by the underlying streams are not chached.
 * Therefore one can traverse the list only once. 
 * After the first traversal the streams are useless.
 * 
 * For each stream a thread is created such that the first stream which can supply a value 
 * actually does supply a value. Therefore the streams can deliver values with quite different pace.
 * <br>
 * If the generated iterator or stream is stopped before all items are traversed one should call close(),
 * otherwise there may be open streams and still waiting threads.
 *
 * @param <T> the type of items in the list.
 */
public class ListOfStreamsUncached<T> extends ListOfStreams<T> {
    /** the list of streams */
    private ArrayList<Stream<T>> streams = new ArrayList();
    /** the only valid iterator */
    private StreamIterator iterator = null;
   
    
    /** clears the list and stops all threads. */ 
    @Override
    public synchronized void clear() {
        items.clear();
        streams.clear();
        if(iterator != null) {iterator.stop(); iterator = null;}}
    
    /** closes the stream, interrupts the thread and removes the stream from the list.
     * 
     * @param stream the stream to be removed.
     */
    public synchronized void remove(Stream stream){
        if(iterator != null) {iterator.remove(stream);}
        else{streams.remove(stream);}}

    /** adds a new stream to the list.
     * 
     * @param stream the stream to be added.
     * @return 'this'.
     */
    public synchronized ListOfStreamsUncached<T> add(Stream<T> stream) {
        streams.add(stream);
        return this;}
    
    /** stops the iterator, closes the streams and kills all threads.
     * The interal list is not cleared.*/
    @Override
    public synchronized void close() {
        if(iterator != null) {iterator.stop();}}
    
    /** This iterator at first iterates through the items list, and then allows the streams' threads
     * to fetch new items (only one when it is needed.)
     */
    public class StreamIterator implements Iterator<T> {
        /** the current item to be returned by the iterator */
        private T currentItem; 
        /** the index for the list */
        private int index = -1; 
        /** stops the iterator */
        private boolean stopped = false; 
        /** allows the hasNext-method of the iterator to control access to the internal cache. */
        private boolean locked = true;
        
        private boolean started = false;
        
        private boolean itemSet = false;
        
        /** maps streams to the corresponding threads. */
        private HashMap<Stream<T>,Thread> threads;
        
        /** constructs a new iterator.<br>
         * For each stream it generates a new thread and starts it.
         * Each thread fetches one item from the stream and then waits for the next hasNext()-call.
         */
        private StreamIterator() {
            iterator = this;}
        
        /** generates for each stream a thread and starts it.*/
        private void makeThreads() {
            threads = new HashMap();
            for(Stream<T> stream : streams) {
                Thread thread = 
                    new Thread(()->{
                        stream.anyMatch((T item) -> setNextItem(item));
                        remove(stream);});
                threads.put(stream, thread);
                thread.start();}}
        
        /** closes the stream and removes it from the list */
        private synchronized void remove(Stream stream) {
            stream.close();
            threads.remove(stream);
            streams.remove(stream);
            if(threads.isEmpty()) {iterator = null; stopped = true; notifyAll();}}
        
         /** sets the next item for the iterator.<br> 
        * This method is called from the threads to set the item which is fetched from the stream.
        * Access to the currentItem is controlled by the variable 'locked'. 
        * It is released by the hasNext-method of the iterators such that an item is set only when the iterator needs one.
        * 
        * @param item the item to be set as next item
        * @return true if the iteration should be stopped.
        */ 
        private synchronized boolean setNextItem(T item){
            while(locked) {
                try{wait();}
                catch(InterruptedException e) {return true;}}
            locked = true;
            currentItem = item;
            itemSet = true;
            notifyAll();
            return false;}
        
    
        
        /** checks if there is another item for the iterator.<br>
         * At first it iterates over the internal list. 
         * When the list is exhausted then the locking is opened and one of the threads can provide
         * another item which had been fetched from the streams to the cache. 
         * 
         * @return true if there is another item in the cache.
         */
        @Override
        public boolean hasNext() {
                if(stopped) {return false;}
                if(items != null && index < items.size()-1) {++index; return true;} 
                if(streams.isEmpty()) {return false;}
                if(!started) {makeThreads(); started = true;}
                ++index;
                synchronized(this){
                    locked = false;
                    notify(); // a thread waiting in setNextItem is awakened.
                    try {if(!streams.isEmpty()){wait();}} 
                    catch (InterruptedException ex) {return false;}
                    return itemSet;}}
        
        /** returns the next item.
         * 
         * @return the next item 
         */
        @Override
        public T next() {
            if(index < items.size()) {return items.get(index);}
            itemSet = false;
            return currentItem;}
        
        /** closes the streams and kills the threads. */
        private synchronized void stop() {
            stopped = true;
            threads.forEach((stream,thread) -> {stream.close();thread.stop();});}
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
        StreamIterator iter = new StreamIterator();
        Stream<T> stream =  StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter,0),false);
        return stream.onClose(()->iter.stop());}
    
    
    
    
    public static void main(String[] args) {
        ArrayList<Integer> ar1 = new ArrayList(); 
        ar1.add(1); ar1.add(2); ar1.add(3); ar1.add(4);
        
        ArrayList<Integer> ar2 = new ArrayList(); 
        ar2.add(5); ar2.add(6); ar2.add(7); ar2.add(8);
        
        ListOfStreamsUncached<Integer> l = new ListOfStreamsUncached();
        
        l.add(ar1.stream());//.peek(c->{try{Thread.sleep(200);}catch(Exception e){}})); 
        l.add(ar2.stream());//.peek(c->{try{Thread.sleep(200);}catch(Exception e){}})); 
        l.add(20);l.add(30);
        
        l.stream().anyMatch(i->{System.out.println(i); return i == 50;});
        
        System.out.println("STOP 1");
        l.close();
    }
    
}
