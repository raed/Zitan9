package DAGs;

import MISC.Activity;
import Utils.Timestamped;
import Utils.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Inner Nodes are nodes whose labels do not pass the isLeafLabel predicate.
 * They may have subnodes and supernodes.
 * Their subnodes, which are in fact leaf nodes (their labels pass the isLeafLabel predicate),
 * are stored in separate lists. Thus, navigating through inner nodes is not affected by
 * the number of leaf nodes.
 */

public class InnerNode<N> extends Node<N> implements Timestamped {
    /** the list of inner subnodes */
    public ArrayList<InnerNode<N>> innerNodes = new ArrayList<>();
    /** the list of leaf nodes as subnodes */
    public ArrayList<LeafNode<N>> leafNodes = new ArrayList<>();
    /** a list of observers for the structure changes */
    private ArrayList<BiConsumer<N, Activity>> observers = null;

    /** constucts an inner node.
     *
     * @param label for the node.
     */
    public InnerNode(N label) {
        super(label);}


    /**
     * @return true
     */
    public boolean isInnerNode()  {return true;};

    /** adds an observer to the node.
     * The observer is applied to the label and gets an Activity.<br>
     * The observer is called in three cases: <br>
     *     1. A new subnode is added: Activity = ADD <br>
     *     2. A subnode is removes: Activity: REMOVE<br>
     *     3. the entire node is removed: Activity: DELETE
     *
     * @param observer to be added.
     */
    void addObserver(BiConsumer<N, Activity> observer) {
        if(observers == null) {observers = new  ArrayList<BiConsumer<N, Activity>>();}
        observers.add(observer);}

    /** the observer is removed.
     *
     * @param observer to be removed.
     */
    void removeObserver(BiConsumer<N, Activity> observer) {
        if(observers == null) {return;}
        observers.remove(observer);}

    /** computes the maximal depth of the subtree with 'this' as root.
     * If there are no subnodes then the depth is 1.
     *
     * @return the maximal depth.
     */
    public int depth() {
        int depth = 0;
        for(InnerNode<N> node : innerNodes) {depth = Math.max(depth,node.depth());}
        if(!leafNodes.isEmpty()) {depth = Math.max(depth,1);}
        return  depth+1;
    }

    /* ********************************** structure change *****************************/

    /** removes the node from the DAG.
     * Its subnodes are moved up to its supernodes.
     * The observers are called after the removals with Activity: DELETE
     */
    void remove() {
        for(InnerNode<N> superNode : superEdges) {
            superNode.innerNodes.remove(this);
            for(LeafNode<N> leafNode : leafNodes) {
                if(!superNode.leafNodes.contains(leafNode)){superNode.leafNodes.add(leafNode);}
                if(!leafNode.superEdges.contains(superNode)) {leafNode.superEdges.add(superNode);}}
            for(InnerNode<N> innerNode : innerNodes) {
                if(!superNode.innerNodes.contains(innerNode)) {superNode.innerNodes.add(innerNode);}
                if(!innerNode.superEdges.contains(superNode)) {innerNode.superEdges.add(superNode);}}}
        for(Node<N> subnode : innerNodes) {subnode.superEdges.remove(this);}
        for(Node<N> subnode : leafNodes) {subnode.superEdges.remove(this);}
        if(observers != null) {
            for(BiConsumer<N, Activity> observer : observers) {observer.accept(label,Activity.DELETE);}}}

    /** adds a new node-subnode relationship
     *
     * @param node the new subnode of this
     */
    void addSubnode(Node<N> node) {
        if(node.isLeafNode()) {addLeafSubnode((LeafNode)node);}
        else {this.addInnerSubnode((InnerNode)node);}}

    /** adds a new leaf node as subnode.
     * The observers are call before the new node is added.
     *
     * @param leafNode the new subnode
     */
    private void addLeafSubnode(LeafNode<N> leafNode) {
        if(!leafNodes.contains(leafNode)) {
            if(observers != null) {
                for(BiConsumer<N, Activity> observer : observers) {observer.accept(leafNode.label,Activity.ADD);}}
            leafNodes.add(leafNode);
            leafNode.superEdges.add(this);}}

    /** adds a new inner node as subnode.
     * The observers are call before the new node is added.
     *
     * @param innerNode the new subnode
     */
    private void addInnerSubnode(InnerNode<N> innerNode) {
        if(!innerNodes.contains(innerNode)) {
            if(observers != null) {
                for(BiConsumer<N, Activity> observer : observers) {observer.accept(innerNode.label,Activity.ADD);}}
            innerNodes.add(innerNode);
            innerNode.superEdges.add(this);}}


    /** removes a node-subnode relationship.
     * The observers are call after the new node is removed.
     *
     * @param node the node to be removed.
     */
    boolean removeSubnode(Node<N> node) {
        boolean removed = node.isLeafNode() ? removeSubnode((LeafNode<N>)node) : removeSubnode((InnerNode<N>)node);
        if(removed && observers != null) {
            for(BiConsumer<N, Activity> observer : observers) {observer.accept(node.label,Activity.REMOVE);}}
        return removed;}

    /** removes a leaf node.
     * The observers are call after the new node is removed.
     * @return true if the leafNode has in fact been removed.
     *
     * @param leafNode the node to be removed.
     */
    private boolean removeSubnode(LeafNode<N> leafNode) {
        int index = leafNodes.indexOf(leafNode);
        if(index < 0) {return false;}
        leafNodes.remove(index);
        leafNode.superEdges.remove(this);
        for(InnerNode<N> supernode : superEdges) {
            if(!leafNode.isSubnodeOf(supernode)){
                supernode.leafNodes.add(leafNode);
                leafNode.superEdges.add(supernode);}}
        return true;}


    /** removes an node-subnode relationship for an inner node
     *
     * @param subnode the node to be removed.
     * @return true if the subnode has in fact been removed.
     */
    private boolean removeSubnode(InnerNode<N> subnode) {
        int index = innerNodes.indexOf(subnode);
        if(index < 0) {return false;}
        innerNodes.remove(index);
        subnode.superEdges.remove(this);
        for(InnerNode<N> supernode : superEdges) {
            if(!subnode.isSubnodeOf(supernode)){
                supernode.innerNodes.add(subnode);
                subnode.superEdges.add(supernode);}}
        return true;}

    /** removes the entire subtree.
     * Subnodes of nodes outside the removed subtree are not removed.
     *
     * @param toBeRemoved the nodes to be removed.
     */
    void removeSubtree(ArrayList<Node> toBeRemoved) {
        toBeRemoved.add(this);
        for(InnerNode<N> superNode : superEdges) {superNode.innerNodes.remove(this);}
        for(InnerNode<N> subNode : innerNodes) {
            subNode.superEdges.remove(this);
            if(subNode.superEdges.isEmpty()) {subNode.removeSubtree(toBeRemoved);}}
        for(LeafNode<N> subNode : leafNodes) {
            subNode.superEdges.remove(this);
            if(subNode.superEdges.isEmpty()) {toBeRemoved.add(subNode);}}}



    /** @return a simple string representation of the DAG */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        toStringRec(string,0);
        return string.toString();}

    /** recursively generates a string representation
     *
     * @param string the string to be extended
     * @param indent the number of blanks for new lines.
     */
    private void toStringRec(StringBuilder string, int indent) {
        String blanks = StringUtils.repeat(' ',indent);
        string.append(blanks).append(label).append("\n");
        if(!leafNodes.isEmpty()) {
            string.append(blanks+"  ").append(Utilities.join(leafNodes,",",(ln->ln.toString()))).append("\n");}
        indent += 2;
        for(InnerNode node : innerNodes) {node.toStringRec(string,indent);}}

    /* ****************************  access to the DAG *************************************/

    /** collect the leaf-nodes in a stream.
     * Multiple occurrences are filtered out in the DAG's method.
     *
     * @return the stream of leaf nodes.
     */
    Stream<LeafNode<N>> leafNodes() {
        return Utilities.streamConcat(
                leafNodes.isEmpty() ? null : leafNodes.stream(),
                Utilities.streamConcat(innerNodes,(node->node.leafNodes())));}



    /** finds the first leaf node where the function returns non-null when applied to the label.
     *
     * @param function to be applied to the node's label.
     * @return the first non-null function value.
     */
    <V> V findInLeafNodes(Function<N,V> function) {
        for(LeafNode<N> node : leafNodes) {
            V value = function.apply(node.label);
            if(value != null) {return value;}}
        for(InnerNode<N> node : innerNodes) {
            V value = node.findInLeafNodes(function);
            if(value != null) {return value;}}
        return null;}

    /** applies the consumer to all leaf nodes.
     *
     * @param consumer to be applied to the leaf node's labels.
     */
    void applyToLeafNodes(int timestamp,Consumer<N> consumer) {
        for(LeafNode<N> node : leafNodes) {
            if(node.timestamp != timestamp) {
                node.timestamp = timestamp;
                consumer.accept(node.label);}}
        for(InnerNode<N> node : innerNodes) {node.applyToLeafNodes(timestamp,consumer);}
    }


}
