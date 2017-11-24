package DAGs;

import Graphs.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * This class is essentially a HashMap, but the keys are the labels of a DAG.
 * One can put things into the map in the same way as in any other map.<br>
 * The difference is the get-Method.
 * If get does not return a value immediately, it traverses the inner nodes of the DAG (breadth-first)
 * to find a key with a corresponding value.
 *
 * @param <N> the type of the node label
 * @param <V> the value type
 */

public class DAGMap<N,V> {
    /** the ordinary map */
    private final HashMap<N,V> map = new HashMap<>();
    /** the DAG where the map is based on */
    private DAG<N> dag;

    /** constructs a new map
     *
     * @param dag the DAG where it is based on.
     */
    public DAGMap(DAG<N> dag) {
        this.dag = dag;}

    /** clears the map */
    public void clear() {map.clear();}

    /**
     * @return true if the map is empty.
     */
    public boolean isEmpty() {return map.isEmpty();}

    /** puts a key-value pair into the map.
     *
     * @param key the key of the pair
     * @param value the value of the pair*/
    public void put(N key,V value) {map.put(key,value);}

    /** just gets the value fro the key from the map.
     * The DAG is not involved.
     *
     * @param key for accessing the map
     * @return the corresponding value in the map, or null
     */
    public V get(N key) {return map.get(key);}

    /** traverses the DAG breadth-first to find the first value in the map.
     *
     * @param key the label in the DAG
     * @param direction the direction of the search (UP or DOWN)
     * @param inner if true then inner nodes are checked, otherwise leaf nodes.
     * @return the first value which is found, or null if nothing is found.
     */
    public V getFirst(N key, Direction direction, boolean inner) {
        if(map.isEmpty()) {return null;}
        return inner ?
                dag.findInInnerLabels(key, direction, Strategy.BREADTH_FIRST,(n->map.get(n))) :
                dag.findInLeafNodes(key,(n->map.get(n)));}


    /** traverses the inner nodes of the DAG breadth-first to find the nearest values in the map.
     *
     * @param key the label in the DAG
     * @param direction if true then the traversal is downwards, otherwise upwards
     * @return the list of nearest values in the dag.
     */

    public ArrayList<V> getAll(N key, Direction direction) {
        if(map.isEmpty()) {return null;}
        ArrayList<V> values = new ArrayList<>();
        dag.innerNodes(key,true,direction, Strategy.BREADTH_FIRST).
                forEach(n -> {
                    V value = map.get(n);
                    if(value != null) {values.add(value);}});
        return values.isEmpty() ? null : values;}




    public <W> W find(N key, Direction direction, Strategy strategy , Function<V,W> function) {
        return dag.findInInnerLabels(key,direction,strategy,(k-> {
            V v = map.get(k);
            if(v != null) {return function.apply(v);}
            return null;}));}

    /** @return the map as a string */
    @Override
    public String toString() {
        return map.toString();}

    /** turns the map into a string by applying different functions to key and value.
     *
     * @param key2String   a function for mapping the keys to strings
     * @param separator separates key and value
     * @param value2String a function for mapping the values to strings.
     * @return the generated string.
     */
    public String toString(Function<N,String> key2String, String separator, Function<V,String> value2String) {
        StringBuilder s = new StringBuilder();
        map.forEach((key,value)->s.append(key2String.apply(key)).append(separator).append(value2String.apply(value)));
        return s.toString();}

}

