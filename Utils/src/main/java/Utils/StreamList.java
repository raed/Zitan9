package Utils;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class implements a bidirectionally linked list,
 * where the elements may be concrete items or streams.
 * The main purpose of this class is to implement streams
 * which traverse a tree or graph depth-first or breadth-first.
 *
 *  @param <T> the type of the list elements.
 */
public class StreamList<T> implements Iterable {
    private StreamListElement first = null;
    private StreamListElement last = null;


    /** this class implements the elements of the list.*/
    private class StreamListElement {
        /** either the actual data in this list element, or a stream */
        Object item;
        /** back-link */
        StreamListElement previous = null;
        /** link to the rest-list or a stream */
        StreamListElement next = null;

        /** constructs a new list element with an item.*/
        StreamListElement(T item) {this.item = item;}
        /** constructs a new list element with a stream.*/
        StreamListElement(Stream<T> stream) {this.item = stream;}
    }


    /** This class implements a forward-iterator which evaluates the stream if necessary */
    public class SLIterator implements Iterator {
        /** pointer to the current element */
        StreamListElement currentElement = null;
        /** indicates start of the iteration */
        boolean first = true;


        public SLIterator() {
        }

        /** checks if there is a further element, and evaluates the stream if necessary
         *
         * @return true if there is another element.
         */
        @Override
        public boolean hasNext() {
            if(StreamList.this.first == null) {return false;}
            if(first){
                StreamList.this.expand(StreamList.this.first);
                currentElement = StreamList.this.first;
                first = false;
                return currentElement != null;}
            if(currentElement == null) {return false;}
            currentElement = currentElement.next;
            if(currentElement == null) {return false;}
            StreamListElement pre = currentElement.previous;
            StreamList list = StreamList.this.expand(currentElement);
            if(list == null)   {return true;}
            if(list.first == null) {currentElement = pre; return hasNext();}
            currentElement = list.first;
            return true;}

        /** returns the next element
         *
         * @return the next element.
         */
        @Override
        public T next() {return (T)currentElement.item;}

        /** removes the part of the list up to the current element */
        public void cutLeft() {
            if(currentElement.previous != null) {
                currentElement.previous.next = null;
                currentElement.previous = null;}
            StreamList.this.first = currentElement;}
    }


    /** adds an item at the end of the list
     *
     * @param item the item to be added.
     */
    public void addLast(T item) {
        StreamListElement element = new StreamListElement(item);
        if(first == null) {first = element; last = element;}
        else {last.next = element; element.previous = last; last = element;}}

    /** adds a stream at the end of the list.
     *
     * @param stream the stream to be added.
     */
    public void addLast(Stream<T> stream) {
        StreamListElement element = new StreamListElement(stream);
        if(first == null) {first = element; last = element;}
        else {last.next = element; element.previous = last; last = element;}}

    /** adds a new item as first element of the list.
     *
     * @param item the element to be added.
     */
    public void addFirst(T item) {
        StreamListElement element = new StreamListElement(item);
        if(first == null) {first = element; last = element;}
        else {first.previous = element; element.next = first; first = element;}}

    /** adds a stream as first element of the list.
     *
     * @param stream the stream to be added.
     */
    public void addFirst(Stream<T> stream) {
        StreamListElement element = new StreamListElement(stream);
        if(first == null) {first = element; last = element;}
        else {first.previous = element; element.next = first; first = element;}}


    /** expands the element if it is a stream.
     * The expanded stream elements replace the stream element.
     *
     * @param element the element to be expanded.
     * @return null, if it had not to be expanded, otherwise the list with the expanded stream elements.
     */
    private StreamList expand(StreamListElement element) {
        if(!(element.item instanceof Stream)) {return null;}
        Stream<T> stream = (Stream)element.item;
        StreamList<T> list = new StreamList();
        stream.forEach(item -> list.addLast(item));
        if(list.first == null) {remove(element); return list;}
        if(element.previous != null) {
            element.previous.next = list.first;
            list.first.previous = element.previous;}
        if(element.next != null) {
            list.last.next = element.next;
            element.next.previous = list.last;}
        if(element == first) {first = list.first;}
        if(element == last) {last = list.last;}
        element.previous = null; // for garbage collection.
        element.next = null;
        return list;}

    /** removes the element from the list.
     *
     * @param element to be removed.
     */
    private void remove(StreamListElement element) {
        if(element.previous != null) {element.previous.next = element.next;}
        if(element.next != null) {element.next.previous = element.previous;}
        if(first == element) {first = element.next;}
        if(last == element)  {last  = element.previous;}
        element.previous = null; // for garbage collection.
        element.next = null;}

    /** returns a stream of list elements.
     *
     * @return the stream of list elements.
     */
    public Stream<T> stream() {return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(),0),false);}

    /** generates a stream where the elements are provided by a function that produces a stream.
     * This method can be used to traverse trees or dags in breadth-first or depth-first way.
     * In this case the function must generate the next layer of nodes in the tree or dag.
     *
     * @param <T> the type of the elements
     * @param start a start node
     * @param back if true then the new elements are treated later (breadth-first), otherwise they are treated first (depth-first).
     * @param function the function that generates for each element a new stream.
     * @return a stream of the elements which ar generated by the function.
     */
    public static <T> Stream<T> stream(T start, boolean back, Function<T,Stream<T>> function) {
        StreamList<T> list = new StreamList();
        list.addFirst(start);

        Utils.StreamList.SLIterator iterator =  list.iterator();
        Stream<T> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,0),false);
        return stream.peek(i -> {
            iterator.cutLeft();
            Stream<T> st = function.apply(i);
            if(st == null) {return;}
            if(back) {list.addLast(st);}
            else {list.first.item = st;
                iterator.first = true;}});
    }

    /** produces a string of elements. The streams are not expanded.
     *
     * @return a comma-separated string of elements.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(first == null) {s.append("");}
        else {
            StreamListElement element = first;
            while(element != last) {
                s.append(element.item.toString()).append(",");
                element = (StreamListElement)element.next;}
            s.append(last.item.toString());
            if(last.next != null) {s.append(",").append(last.next.toString());}}
        return s.toString();}

    /** genertes a new iterator for the list.
     *
     * @return the new iterator.
     */
    @Override
    public SLIterator iterator() {return new SLIterator();}

}
