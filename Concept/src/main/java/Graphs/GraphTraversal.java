package Graphs;

import Utils.Timestamped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/** This class implements some of the basic traversal and search methods: depth-first, breadth-first and iterative deepening.
 * The implementation is generic and therefore usable for all kinds of search trees and search DAGs.
 */


public class GraphTraversal {

    /** This is the classical breadth-first search algorithm without any optimizations.
     * It stops the first time the 'successors' returns a non-null value.
     * If the structure is actually a DAG then nodes which are reachable via different paths
     * may actually be visited multiple times.
     * <br>
     * A breadth-first search without any optimization needs an array where the
     * levels are stored. Therefore breadth-first search may require lots of memory.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the function.
     * @return          the first non-null successors value.
     */

    public static <N,V> V breadthFirst(N node, boolean inclusive, Function<N,Object> successors, Function<N,V> function) {
        ArrayList<N> nodes = new ArrayList<>();
        LinkedList tail    = new LinkedList<>();
        boolean isStream   = false;
        boolean streamCollectionChecked = false;
        Object succ;
        if(inclusive) {nodes.add(node);}
        else {
            succ = successors.apply(node);
            if(succ == null) {return null;}
            isStream = succ instanceof Stream;
            streamCollectionChecked = true;
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}

        while(true){
            for(int i = 0; i < nodes.size(); ++i) {
                N n = nodes.get(i);
                V value = function.apply(n);
                if(value != null) {return value;}
                succ = successors.apply(n);
                if(succ != null) {tail.add(succ);}}

            succ = tail.poll();
            if(succ == null) {return null;}
            nodes.clear();
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = succ instanceof Stream;}
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}}




    /** This is the classical breadth-first traversal algorithm without any optimizations.
     * If the structure is actually a DAG then nodes which are reachable via different paths
     * may actually be visited multiple times.
     * <br>
     * A breadth-first traversal without any optimization needs a items array where the
     * levels are stored. Therefore breadth-first traversal may require lots of memory.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param consumer  a successors to be applied to the nodes.
     * @param <N>       the node type.
     */
    public static <N> void breadthFirst(N node, boolean inclusive, Function<N,Object> successors, Consumer<N> consumer) {
        ArrayList<N> nodes = new ArrayList<>();
        LinkedList tail    = new LinkedList<>();
        boolean isStream   = false;
        boolean streamCollectionChecked = false;
        Object succ;
        if(inclusive) {nodes.add(node);}
        else {
            succ = successors.apply(node);
            if(succ == null) {return;}
            isStream = succ instanceof Stream;
            streamCollectionChecked = true;
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}

        while(true){
            for(int i = 0; i < nodes.size(); ++i) {
                N n = nodes.get(i);
                consumer.accept(n);
                succ = successors.apply(n);
                if(succ != null) {tail.add(succ);}}

            succ = tail.poll();
            if(succ == null) {return;}
            nodes.clear();
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = succ instanceof Stream;}
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}}


    /** This is the classical breadth-first search algorithm, but with an optimization for DAGs.
     * It stops the first time the 'successors' returns a non-null value.
     * The methods works for classes which implement the Timestamped interface.
     * Timestamps are used to avoid visiting the same node multiple times.
     * <br>
     * A breadth-first search without any optimization needs a items array where the
     * levels are stored. Therefore breadth first-search may double the DAG's memory.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param timestamp a timestamp value, larger than the timestamps of all nodes in the search tree.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    public static <N extends Timestamped,V> V breadthFirst(N node, boolean inclusive, int timestamp,
                                                           Function<N,Object> successors, Function<N,V> function) {
        ArrayList<N> nodes = new ArrayList<>();
        LinkedList tail    = new LinkedList<>();
        boolean isStream   = false;
        boolean streamCollectionChecked = false;
        Object succ;
        if(inclusive) {nodes.add(node);}
        else {
            succ = successors.apply(node);
            if(succ == null) {return null;}
            isStream = succ instanceof Stream;
            streamCollectionChecked = true;
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}

        while(true){
            for(int i = 0; i < nodes.size(); ++i) {
                N n = nodes.get(i);
                if(n.getTimestamp() == timestamp) {continue;}
                n.setTimestamp(timestamp);
                V value = function.apply(n);
                if(value != null) {return value;}
                succ = successors.apply(n);
                if(succ != null) {tail.add(succ);}}

            succ = tail.poll();
            if(succ == null) {return null;}
            nodes.clear();
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = succ instanceof Stream;}
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}}




    /** This is the classical breadth-first traversal algorithm optimized for DAGs.
     * A Timestamp mechanism avoids visiting the same node twice.
     * <br>
     * A breadth-first traversal without any optimization needs a items array where the
     * levels are stored. Therefore breadth-first traversal may require lots of memory.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param consumer  a successors to be applied to the nodes.
     * @param <N>       the node type.
     */
    public static <N extends Timestamped> void breadthFirst(N node, boolean inclusive, int timestamp, Function<N,Object> successors, Consumer<N> consumer) {

        ArrayList<N> nodes = new ArrayList<>();
        LinkedList tail    = new LinkedList<>();
        boolean isStream   = false;
        boolean streamCollectionChecked = false;
        Object succ;
        if(inclusive) {nodes.add(node);}
        else {
            succ = successors.apply(node);
            if(succ == null) {return;}
            isStream = succ instanceof Stream;
            streamCollectionChecked = true;
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}

        while(true){
            for(int i = 0; i < nodes.size(); ++i) {
                N n = nodes.get(i);
                if(n.getTimestamp() == timestamp) {continue;}
                n.setTimestamp(timestamp);
                consumer.accept(n);
                succ = successors.apply(n);
                if(succ != null) {tail.add(succ);}}

            succ = tail.poll();
            if(succ == null) {return;}
            nodes.clear();
            if(!streamCollectionChecked) {streamCollectionChecked = true; isStream = succ instanceof Stream;}
            if(isStream) {((Stream<N>)succ).collect((() -> nodes), ArrayList::add, ArrayList::addAll);}
            else {nodes.addAll((Collection<N>)succ);}}}

    /* ****************************************   Depth-First *************************************/

    /** This is the classical depth-first search algorithm without any optimizations.
     * It stops the first time the 'successors' returns a non-null value.
     * If the structure is actually a DAG then nodes which are reachable via different paths
     * may actually be visited multiple times.
     * <br>
     * The algorithm is recursive and therefore uses only the stack as items memory.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    public static <N,V> V depthFirst(N node, boolean inclusive, Function<N,Object> successors, Function<N,V> function) {
        if(inclusive) {
            V value = function.apply(node);
            if(value != null) {return value;}}
        Object succ = successors.apply(node);
        if(succ == null) {return null;}
        if(succ instanceof Stream) {
            Object[] dummy = new Object[]{null};
            ((Stream<N>)succ).anyMatch(n -> {
                dummy[0] = depthFirst(n,true,successors,function);
                return dummy[0] != null;});
            return (V)dummy[0];}
        for(N item : (Collection<N>)succ) {
            V value = depthFirst(item,true,successors,function);
            if(value != null) {return value;}}
        return null;}


    /** This is the classical depth-first traversal algorithm without any optimizations.
     * If the structure is actually a DAG then nodes which are reachable via different paths
     * may actually be visited multiple times.
     * <br>
     * The algorithm is recursive and therefore uses only the stack as items memory.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param consumer  a successors to be applied to the nodes.
     * @param <N>       the node type.
     */
    public static <N> void depthFirst(N node, boolean inclusive, Function<N,Object> successors, Consumer<N> consumer) {
        if(inclusive) {consumer.accept(node);}
        Object succ = successors.apply(node);
        if(succ == null) {return;}
        if(succ instanceof Stream) {((Stream<N>)succ).forEach(n -> depthFirst(n,true,successors,consumer));}
        else {for(N item : (Collection<N>)succ) {depthFirst(item,true,successors,consumer);}}}

    /** This is the classical depth-first search algorithm, but with an optimization for DAGs.
     * It stops the first time the 'successors' returns a non-null value.
     * The methods works for classes which implement the Timestamped interface.
     * Timestamps are used to avoid visiting the same node multiple times.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param timestamp a timestamp value, larger than the timestamps of all nodes in the search tree.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    public static <N extends Timestamped, V> V depthFirst(N node, boolean inclusive, int timestamp, Function<N,Object> successors, Function<N,V> function) {
        if(inclusive) {
            if(node.getTimestamp() == timestamp) {return null;}
            node.setTimestamp(timestamp);
            V value = function.apply(node);
            if(value != null) {return value;}}
        Object succ = successors.apply(node);
        if(succ == null) {return null;}
        if(succ instanceof Stream) {
            Object[] dummy = new Object[]{null};
            ((Stream<N>)succ).anyMatch(n -> {
                dummy[0] = depthFirst(n,true,successors,function);
                return dummy[0] != null;});
            return (V)dummy[0];}
        for(N item : (Collection<N>)succ) {
            V value = depthFirst(item,true,timestamp,successors,function);
            if(value != null) {return value;}}
        return null;}


    /** This is the classical depth-first traversal algorithm, but with an optimization for DAGs.
     * The methods works for classes which implement the Timestamped interface.
     * Timestamps are used to avoid visiting the same node multiple times.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param timestamp a timestamp value, larger than the timestamps of all nodes in the search tree.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param consumer  a successors to be applied to the nodes.
     * @param <N>       the node type.
     */
    public static <N extends Timestamped> void depthFirst(N node, boolean inclusive, int timestamp, Function<N,Object> successors, Consumer<N> consumer) {
        if(inclusive) {
            if(node.getTimestamp() == timestamp) {return;}
            node.setTimestamp(timestamp);
            consumer.accept(node);}
        Object succ = successors.apply(node);
        if(succ == null) {return;}
        if(succ instanceof Stream) {((Stream<N>)succ).forEach(n -> depthFirst(n,true,successors,consumer));}
        else {for(N item : (Collection<N>)succ) {depthFirst(item,true,timestamp,successors,consumer);}}}



    /** This is the classical iterative deepening search algorithm.
     * It stops the first time the 'successors' returns a non-null value.
     * Iterative deepening is a depth-limited depth-first search, where depth-limit is
     * gradually increased. It therefore looks like a breadth-first search,
     * but needs no extra memory.
     * If the branching rate of the nodes is larger than 2 then the
     * iteration at most doubles the time compared to the unbounded depth-first search.
     *
     * @param node      the start node of the search tree
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    public static <N,V> V iterativeDeepening(N node, boolean inclusive, Function<N,Object> successors, Function<N,V> function) {
        if (inclusive) {
            V value = function.apply(node);
            if (value != null) {return value;}}
        int[]depth = new int[]{0};
        for(int level = 0; depth[0] == 0; ++level) {
            V value = limitedDepthFirst(node,level,depth,successors,function);
            if(value != null) {return value;}}
        return null;}

    /** This is the recursive part of the breadth-first search in the iterative-deepening approach.
     *
     * @param node       the current node to be checked
     * @param level      the level in the search tree (or DAG)
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param successors maps a node to a collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    private static <N,V> V limitedDepthFirst(N node, int level, int[] depth, Function<N,Object> successors, Function<N,V> function) {
        Object succ = successors.apply(node);
        if(succ == null) {return null;}
        if(succ instanceof Stream) {
            Object[] dummy = new Object[]{null};
            ((Stream<N>)succ).anyMatch(item->{
                dummy[0] = function.apply(item);
                return dummy[0] != null;});
            if(dummy[0] != null) {return (V)dummy[0];}}
        else{
            for(N item : (Collection<N>)succ) {
                V value = function.apply(item);
                if(value != null) {return value;}}}
        depth[0] = level;
        if(level == 0) {return null;}

        if(succ instanceof Stream) {
            Object[] dummy = new Object[]{null};
            ((Stream<N>)succ).anyMatch(item->{
                dummy[0] = limitedDepthFirst(item,level-1,depth,successors,function);
                return dummy[0] != null;});
            if(dummy[0] != null) {return (V)dummy[0];}}
        else{
            for(N item : (Collection<N>)succ) {
                V value = limitedDepthFirst(item,level-1,depth,successors,function);
                if(value != null) {return value;}}}
        return null;}

    /** This is the interface method for the three unoptimized, uninformed search algorithms,
     * DEPTH_FIRST, BREADTH_FIRST or ITERATIVE_DEEPENING
     * It stops the first time the 'successors' returns a non-null value.
     *
     * @param node      the start node of the search tree
     * @param strategy  one of DEPTH_FIRST, BREADTH_FIRST or ITERATIVE_DEEPENING
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    public static <N,V> V uninformedSearch(N node, Strategy strategy, boolean inclusive, Function<N,Object> successors, Function<N,V> function) {
        switch(strategy) {
            case DEPTH_FIRST:         return depthFirst(node,inclusive,successors,function);
            case BREADTH_FIRST:       return breadthFirst(node,inclusive,successors,function);
            case ITERATIVE_DEEPENING: return iterativeDeepening(node,inclusive,successors,function);}
        return null;}

    /** This is the interface method for the two uninformed search algorithms, working with timestamps,
     * DEPTH_FIRST, BREADTH_FIRST
     * It stops the first time the 'successors' returns a non-null value.
     *
     * @param node      the start node of the search tree
     * @param strategy  one of DEPTH_FIRST, BREADTH_FIRST or ITERATIVE_DEEPENING
     * @param inclusive if true then the start node is checked as well.
     * @param timestamp an int greater than all the object's timestamps
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param function  a successors to be applied to the nodes.
     * @param <N>       the node type.
     * @param <V>       the result type of the successors.
     * @return          the first non-null successors value.
     */
    public static <N extends Timestamped,V> V uninformedSearch(N node, Strategy strategy, boolean inclusive, int timestamp, Function<N,Object> successors, Function<N,V> function) {
        assert strategy != Strategy.ITERATIVE_DEEPENING;
        switch(strategy) {
            case DEPTH_FIRST:    return depthFirst(node,inclusive,timestamp,successors,function);
            case BREADTH_FIRST:  return breadthFirst(node,inclusive,timestamp,successors,function);}
        return null;}

    /** This is the interface method for the two unoptimized traversal algorithms,
     * DEPTH_FIRST or BREADTH_FIRST
     *
     * @param node      the start node of the search tree
     * @param strategy  one of DEPTH_FIRST, BREADTH_FIRST
     * @param inclusive if true then the start node is checked as well.
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param consumer  a successors to be applied to the nodes.
     * @param <N>       the node type.
     */
    public static <N> void apply(N node, Strategy strategy, boolean inclusive, Function<N,Object> successors, Consumer<N> consumer) {
        switch(strategy) {
            case DEPTH_FIRST:   depthFirst(node,inclusive,successors,consumer); return;
            case BREADTH_FIRST: breadthFirst(node,inclusive,successors,consumer); return;}}

    /** This is the interface method for the traversal algorithms which are optimized for DAGs.
     * DEPTH_FIRST or BREADTH_FIRST.
     * They use timestamps to avoid visiting nodes multiple times.
     *
     * @param node      the start node of the search tree
     * @param strategy  one of DEPTH_FIRST, BREADTH_FIRST
     * @param inclusive if true then the start node is checked as well.
     * @param timestamp an int larger than the timestamps of all objects in the DAG
     * @param successors maps a node to a stream or collection of successor nodes.
     * @param consumer  a successors to be applied to the nodes.
     * @param <N>       the node type.
     */
    public static <N extends Timestamped> void apply(N node, Strategy strategy, boolean inclusive, int timestamp, Function<N,Object> successors, Consumer<N> consumer) {
        switch(strategy) {
            case DEPTH_FIRST:   depthFirst(node,inclusive,timestamp,successors,consumer); return;
            case BREADTH_FIRST: breadthFirst(node,inclusive,timestamp,successors,consumer); return;}}


}
