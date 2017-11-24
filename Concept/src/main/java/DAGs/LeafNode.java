package DAGs;

import Utils.Timestamped;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class represents leaf nodes.
 * Which is a leaf node or not is determined by the isLeafLabel predicate applied to the node's label.
 */

public class LeafNode<N> extends Node<N> implements Timestamped {

    /** constructs a leaf node for the given label.
     *
     * @param label the node's label.
     */
    public LeafNode(N label) {
        super(label);}

    /**
     * @return true
     */
    public boolean isLeafNode() {return true;};

    /** removes 'this' from the leafEdges lists of its supernodes.
     */
    public void remove() {
        for(InnerNode<N> superNode : superEdges) {superNode.leafNodes.remove(this);}}

    /** removes 'this' from the leafEdges lists of its supernodes,
     * and adds 'this' to the toBeRemoved list.
     *
     * @param toBeRemoved the nodes to be removed.
     */
    public void removeSubtree(ArrayList<Node> toBeRemoved) {
        toBeRemoved.add(this);
        for(InnerNode<N> superNode : superEdges) {superNode.leafNodes.remove(this);}}

    /**
     * @return Stream.of(this)
     */
    public Stream<LeafNode<N>> leafNodes() {return Stream.of(this);}


    /** applies the predicate to the label.
     *
     * @param function to be applied to the node's label.
     * @return 'this' if the test returns true, otherwise null.
     */
    public <V> V findInLeafNodes(Function<N,V> function) {
        return function.apply(label);}


    /** applies the consumer of the label.
     *
     * @param consumer to be applied to the leaf node's labels.
     */
    public void applyToLeafNodes(int timestamp,Consumer<N> consumer) {
        if(this.timestamp != timestamp) {
            this.timestamp = timestamp;
            consumer.accept(label);}}


    /** @return the label's string. */
    @Override
    public String toString() {return label.toString();}
}
