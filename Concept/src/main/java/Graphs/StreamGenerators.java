package Graphs;

import Utils.Timestamped;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** This class provides static methods for turning transitive relations into a stream of items.
 * The relation may be traversed breadth-first or depth-first.
 * The relation may either be a tree or a graph.
 * The relations are specified by providing a successors mapping nodes to either streams or collections of successor nodes.
 */


public class StreamGenerators {

    /* *****************************************Depth First ********************************************/

    /** This class implements an iterator for depth first traversal of a tree or graph.
     * If it is a graph then the visited nodes are internally stored such that they are not visited again.
     *
     * @param <T> the item's type.
     */
    private static class DepthFirstSetIterator<T> implements Iterator<T> {
        Stack<T> stack;
        Set<T> items = null;
        Function successors;
        boolean isStream = false;
        boolean isTree;
        boolean streamCollectionChecked = false;
        T nextItem;

        DepthFirstSetIterator(Stack<T> stack, boolean isTree, Function<T,Object> successors) {
            this.stack    = stack;
            this.successors = successors;
            this.isTree   = isTree;
            if(!isTree) {items = new HashSet<>();}}

        @Override
        public boolean hasNext() {
            if(stack.isEmpty()) {return false;}
            nextItem = stack.pop();
            if(!isTree) {
                while(!stack.isEmpty() && items.contains(nextItem)) {
                    nextItem = stack.pop();}
                if(items.contains(nextItem)) {return false;}}


            Object st = successors.apply(nextItem);
            if(st == null) {return true;}
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = st instanceof Stream;}
            if(isTree) {
                if(isStream) {((Stream<T>)st).forEach(n->stack.push(n));}
                else {for(T n : ((Collection<T>)st)) {stack.push(n);}}}
            else {
                if(isStream) {((Stream<T>)st).forEach(item->{if(!items.contains(item)) {stack.push(item);}});}
                else {for(T item : ((Collection<T>)st))     {if(!items.contains(item)) {stack.push(item);};}}}
            return true;}

        @Override
        public T next() {
            if(!isTree) {items.add(nextItem);}
            return nextItem;}
    }

    /** This class implements an iterator for depth first traversal of a tree or graph.
     * The visited nodes are timestamped such that they are not visited again.
     *
     * @param <T> the item's type.
     */
    private static class DepthFirstTimestampIterator<T extends Timestamped> implements Iterator<T> {
        Stack<T> stack;
        Function successors;
        boolean isStream = false;
        boolean streamCollectionChecked = false;
        int timestamp;
        T nextItem;

        DepthFirstTimestampIterator(Stack<T> stack, int timestamp, Function<T,Object> successors) {
            this.stack    = stack;
            this.successors = successors;
            this.timestamp   = timestamp;}

        @Override
        public boolean hasNext() {
            if(stack.isEmpty()) {return false;}
            nextItem = stack.pop();
            while(!stack.isEmpty() && nextItem.getTimestamp() == timestamp) {nextItem = stack.pop();}
            if(nextItem.getTimestamp() == timestamp) {return false;}

            Object st = successors.apply(nextItem);
            if(st == null) {return true;}
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = st instanceof Stream;}

            if(isStream) {((Stream<T>)st).forEach(item->{if(item.getTimestamp() != timestamp) {stack.push(item);}});}
            else {for(T item : ((Collection<T>)st))     {if(item.getTimestamp() != timestamp) {stack.push(item);};}}
            return true;}

        @Override
        public T next() {nextItem.setTimestamp(timestamp); return nextItem;}
    }

    /** This method generates a stream which traverses a transitive relation depth-first. <br>
     * If the relation is not a tree then its elements are internally cached in order to avoid
     * multiple occurrences of the same node in the stream, and to deal with cycles in the relation.
     *
     * @param <T>      The type of the relation's elements.
     * @param start    The start object of the relation.
     * @param inclusive if false then the start node does not become part of the stream.
     * @param successors A function for mapping an element of the relation to a stream or collection of immediate successor elements.
     * @return         a stream that traverses all elements of the relation in depth-first way.
     */
    public static <T> Stream<T> depthFirst(T start, boolean inclusive, boolean isTree, Function<T,Object> successors) {
        Stack<T> stack = new Stack();
        if (inclusive) {stack.push(start);}
        else {
            Object st = successors.apply(start);
            if (st == null) {return null;}
            if(st instanceof Stream) {((Stream<T>)st).forEach(n->stack.push(n));}
            else {for(T n : ((Collection<T>)st)) {stack.push(n);}}}

        DepthFirstSetIterator iterator = new DepthFirstSetIterator(stack,isTree,successors);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    /** This method generates a stream which traverses a transitive relation depth-first. <br>
     * Its elements are timestamped in order to avoid
     * multiple occurrences of the same node in the stream, and to deal with cycles in the relation.
     *
     * @param <T>      The type of the relation's elements.
     * @param start    The start object of the relation.
     * @param inclusive if false then the start node does not become part of the stream.
     * @param successors A function for mapping an element of the relation to stream or collection of immediate successor elements.
     * @return         a stream that traverses all elements of the relation in depth-first way.
     */
    public static <T extends Timestamped> Stream<T> depthFirst(T start, boolean inclusive, int timestamp, Function<T,Object> successors) {
        Stack<T> stack = new Stack();
        if (inclusive) {stack.push(start);}
        else {
            Object st = successors.apply(start);
            if (st == null) {return null;}
            if(st instanceof Stream) {((Stream<T>)st).forEach(n->stack.push(n));}
            else {for(T n : ((Collection<T>)st)) {stack.push(n);}}}

        DepthFirstTimestampIterator iterator = new DepthFirstTimestampIterator(stack,timestamp,successors);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }



    /* ********************************* Iterators for Breadth-First traversal ********************************************/

    /** This class implements an iterator as basis for a breadth-first stream.
     * The iterator maintains two lists:<br>
     *     1. a list of items to be inserted into the stream. <br>
     *     2. a list of either streams, or collections to be inserted when the current items list is empty.
     *     <br>
     *  If the isTree-flag is false then all items returned by has-next are put into a set.
     *  These are then ignored by the hasNext method.
     *
     * @param <T> The item type of the relation.
     */
    private static class BreadthFirstSetIterator<T> implements Iterator<T> {
        ArrayList<T> items;
        LinkedList tail;
        int index = 0;
        boolean isStream = false;
        Set<T> allItems = null;
        boolean isTree;
        boolean streamCollectionChecked = false;
        Function<T, Object> function;

        public BreadthFirstSetIterator(ArrayList<T> items, LinkedList tail,
                                       boolean isTree, Function<T, Object> successors) {
            this.items = items;
            this.tail = tail;
            this.isTree = isTree;
            if(!isTree) {allItems = new HashSet<>();}
            this.function = successors;}

        @Override
        public boolean hasNext() {
            if(!isTree){while(index < items.size() && allItems.contains(items.get(index))) {++index;}}
            if(index < items.size()) {return true;}
            Object successors = tail.pollFirst();
            if(successors == null) {return false;}
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = successors instanceof Stream;}
            items.clear();
            index = 0;
            if (isStream) {((Stream<T>)successors).collect((() -> items), ArrayList::add, ArrayList::addAll);}
            else {items.addAll((Collection<T>)successors);}
            return hasNext();}

        @Override
        public T next() {
            T item = items.get(index++);
            if(!isTree) {allItems.add(item);}
            Object st = function.apply(item);
            if (st != null) {tail.add(st);}
            return item;}}



    /** This class implements an iterator as basis for a breadth-first stream.
     * The iterator maintains two lists:<br>
     *     1. a list of items to be inserted into the stream. <br>
     *     2. a list of either streams, or collections to be inserted when the current items list is empty.
     *     <br>
     *  Timestamps are used to avoid duplicate items in the stream.
     *
     * @param <T> The item type of the relation.
     */
    private static class BreadthFirstTimestampIterator<T extends Timestamped> implements Iterator<T> {
        ArrayList<T> items;
        LinkedList tail;
        int index = 0;
        boolean isStream = false;
        boolean streamCollectionChecked = false;
        int timestamp;
        Function<T, Object> function;

        public BreadthFirstTimestampIterator(ArrayList<T> items, LinkedList tail,
                                             int timestamp, Function<T, Object> successors) {
            this.items = items;
            this.tail = tail;
            this.timestamp = timestamp;
            this.function = successors;
        }

        @Override
        public boolean hasNext() {
            while (index < items.size() && items.get(index).getTimestamp() == timestamp) {++index;}
            if (index < items.size()) {return true;}

            Object successors = tail.pollFirst();
            if (successors == null) {return false;}
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = successors instanceof Stream;}

            items.clear();
            index = 0;
            if (isStream) {((Stream<T>) successors).collect((() -> items), ArrayList::add, ArrayList::addAll);}
            else {items.addAll((Collection<T>) successors);}
            return hasNext();}

        @Override
        public T next() {
            T item = items.get(index++);
            item.setTimestamp(timestamp);
            Object st = function.apply(item);
            if (st != null) {tail.add(st);}
            return item;}
    }

 /* ***************************************** Breadth First ********************************************/

    /** This method generates a stream which traverses a transitive relation breadth-first.
     *  The successor nodes for each node are provided by a successors,
     *  which either maps the node to a stream of nodes (isNode = true) or to a collection of nodes (isNode = false).
     *  <br>
     *  If isTree == true then it is assumed that the graph is a tree, and there will be no check for duplicates.<br>
     *  It isTree == false then all expanded nodes are put into a HashSet in order to avoid duplicates in the stream.
     *  This way the graph may even contain cycles.
     *
     * @param <T>      The type of the relation's elements.
     * @param start    The start object of the relation.
     * @param inclusive if false then 'start' is NOT inserted into the stream.
     * @param isTree   if true then it is assumed that the graph is a tree, and no duplicates are inserted into the stream.
     * @param successors A function for mapping an element of the relation to a collection or a stream for the immediate successor elements.
     * @return         a stream that traverses all elements of the relation in a breadth-first way.
     */
    public static <T> Stream<T> breadthFirst(T start, boolean inclusive,
                                             boolean isTree, Function<T,Object> successors){
        ArrayList<T> items = new ArrayList();
        LinkedList tail = new LinkedList<>();
        if(inclusive) {items.add(start);}
        else {
            Object stream = successors.apply(start);
            if(stream == null) {return null;}
            tail.add(stream);}

        BreadthFirstSetIterator<T> iterator = new BreadthFirstSetIterator(items,tail,isTree,successors);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,0),false);}

    /** This method generates a stream which traverses a transitive relation breadth-first.
     *  The successor nodes for each node are provided by a successors,
     *  which either maps the node to a stream of nodes (isNode = true) or to a collection of nodes (isNode = false).
     *  <br>
     *  Duplicates in the stream are avoided using a timestamp mechanism.
     *
     * @param <T>      The type of the relation's elements.
     * @param start    The start object of the relation.
     * @param inclusive if false then 'start' is NOT inserted into the stream.
     * @param timestamp an integer greater than the timestamps in all other items.
     * @param successors A function for mapping an element of the relation to a collection or a stream for the immediate successor elements.
     * @return         a stream that traverses all elements of the relation in a breadth-first way.
     */

    public static <T extends Timestamped> Stream<T> breadthFirst(T start,  boolean inclusive,
                                                                 int timestamp, Function<T,Object> successors){
        ArrayList<T> items = new ArrayList();
        LinkedList tail = new LinkedList<>();
        if(inclusive) {items.add(start);}
        else {
            Object stream = successors.apply(start);
            if(stream == null) {return null;}
            tail.add(stream);}

        BreadthFirstTimestampIterator<T> iterator = new BreadthFirstTimestampIterator(items,tail,timestamp,successors);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator,0),false);}

    /* ********************************** Parameterized Methods ******************************* */

    /** This method generates a stream of nodes for a tree.
     *
     * @param start the start node of the tree
     * @param inclusive if false then the start node is not included
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST
     * @param successors a function for mapping a node to either a stream of nodes or a collection of nodes.
     * @param <T> the item's type
     * @return a stream of nodes.
     */
    public static <T> Stream<T> streamForTree(T start, boolean inclusive, Strategy strategy,
                                              Function<T,Object> successors){
        switch(strategy) {
            case BREADTH_FIRST: return breadthFirst(start,inclusive,true,successors);
            case DEPTH_FIRST:   return depthFirst(start,inclusive,true,successors);}
        return null;}

    /** This method generates a stream of nodes for a tree.
     *
     * @param start the start node of the tree
     * @param inclusive if false then the start node is not included
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST
     * @param timestamp an integer greater than the timestamps in all the nodes.
     * @param successors a function for mapping a node to either a stream of nodes or a collection of nodes.
     * @param <T> the item's type
     * @return a stream of nodes.
     */
    public static <T extends Timestamped> Stream<T> streamForTree(T start,  boolean inclusive, Strategy strategy,
                                                                  int timestamp, Function<T,Object> successors){
        switch(strategy) {
            case BREADTH_FIRST: return breadthFirst(start,inclusive,timestamp,successors);
            case DEPTH_FIRST:   return depthFirst(start,inclusive,timestamp,successors);}
        return null;}

    /** This method generates a stream of nodes for a graph.
     * Duplicates are avoided in the stream by storing all already delivered items in a set, and ignoring the items in this set.
     *
     * @param start the start node of the graph
     * @param inclusive if false then the start node is not included
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST
     * @param successors a function for mapping a node to either a stream of nodes or a collection of nodes.
     * @param <T> the item's type
     * @return a stream of nodes.
     */
    public static <T> Stream<T> streamForGraph(T start,  boolean inclusive, Strategy strategy,
                                               Function<T,Object> successors){
        switch(strategy) {
            case BREADTH_FIRST: return breadthFirst(start,inclusive,false,successors);
            case DEPTH_FIRST:   return depthFirst(start,inclusive,false,successors);}
        return null;}

    /** This method generates a stream of nodes for a graph.
     * Duplicates are avoided in the stream using timestamps
     *
     * @param start the start node of the tree
     * @param inclusive if false then the start node is not included
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST
     * @param timestamp an integer greater than the timestamps of all nodes in the tree.
     * @param successors a function for mapping a node to either a stream of nodes or a collection of nodes.
     * @param <T> the item's type
     * @return a stream of nodes.
     */
    public static <T extends Timestamped> Stream<T> streamForGraph(T start,  boolean inclusive, Strategy strategy,
                                                                   int timestamp, Function<T,Object> successors){
        switch(strategy) {
            case BREADTH_FIRST: return breadthFirst(start,inclusive,timestamp,successors);
            case DEPTH_FIRST:   return depthFirst(start,inclusive,timestamp,successors);}
        return null;}







    /* ********************************* Stream with key **************************************/

    /** This method generates for a tree with labelled edges a stream of nodes at a certain level.<br>
     * Example: <br>
     * IndividualConcept Jack: attribute friends attribute parents attribute age <br>
     * stream(Jack,[friends,parents,age], (concept,attribute to values)) <br>
     * yields a stream of the ages of the parents of Jack's friends.
     *
     * @param <T> the node type
     * @param start the start node
     * @param maxLevel the list of edge labels
     * @param function a successors (node,label to Stream of successor nodes)
     * @return a stream of the nodes accessible via the edge labels
     */
    public static <T> Stream<T> stream(T start, int maxLevel, BiFunction<T,Integer,Stream<T>> function) {
        return stream(Stream.of(start),0,maxLevel,function);}

    /** This is the recursive version of the stream method.
     *
     * @param <T> the node type
     * @param start the start node
     * @param maxLevel the list of edge labels
     * @param function a successors (node,label -&gt; Stream&lt;node&gt;)
     * @param level the current index in the keys list.
     * @return a stream of the nodes accessible via the edge labels
     */
    private static <T> Stream<T> stream(Stream<T> start, int level, int maxLevel, BiFunction<T,Integer,Stream<T>> function) {
        return start.flatMap(node->{
            if(level == maxLevel) {return function.apply(node,level);}
            Stream<T> stream = function.apply(node,level);
            return stream == null ? Stream.empty() : stream(stream,level+1,maxLevel,function);});}


    //breadthFirst(T start, boolean inclusive,
    //boolean isTree, Function<T,Object> successors){

}
