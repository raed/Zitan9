package DAGs;

import Utils.Timestamped;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This is the abstract superclass of InnerNode and LeafNode.
 * These three classes together make up the DAG.
 *
 * Most of the methods in this class and its subclasses are package-intern.
 */


public abstract class Node<N> implements Timestamped {
    /** each node carries a label of type N */
    public N label;
    /** the timestamp can in particular be used to filter out multiple occurrences of the same node
     * when traversing the DAG.*/
    protected int timestamp = 0;
    /** backpointers to supernodes */
    public ArrayList<InnerNode<N>> superEdges = new ArrayList<>();

    /** constructs a node with the given label
     *
     * @param label
     */
    Node(N label) {this.label = label;}

    public int getTimestamp() {return timestamp;}

    public void setTimestamp(int timestamp) {this.timestamp = timestamp;}



    /* ********************* Information about the node *************************/

    /** @return true if the node is a root node */
    public boolean isRootNode() {return superEdges.isEmpty();}

    /** @return true if the node of type LeafNode */
    public boolean isLeafNode() {return false;};

    /** @return true if the node is of type InnerNode */
    public boolean isInnerNode()  {return false;};

    /** a leaf node has depth 1 */
    public int depth() {return 1;}


    /** gets a stream of leaf nodes.
     * The stream may contain multiple occurrences of the same node.
     *
     * @return the stream of leaf nodes.
     */
    abstract Stream<LeafNode<N>> leafNodes();

    /** checks if this is a subnode of node.
     * The algorithm searches from 'this' upwards until it possibly meets 'node'
     *
     * @param node the supernode to be tested
     * @return true if this is a subnode of node.
     */
    boolean isSubnodeOf(InnerNode<N> node) {
        if(this == node) {return true;}
        if(superEdges.isEmpty()) {return false;}
        for(InnerNode<N> supernode : superEdges) {if(node == supernode)           {return true;}}
        for(InnerNode<N> supernode : superEdges) {if(supernode.isSubnodeOf(node)) {return true;}}
        return false;}

    /* ************************************* Modifications of the DAG ****************************/

    /** removes the node from the tree.
     * Its subnodes become new subnodes of the node's supernodes.
     */
    abstract void remove();

    /** traverses the subtree of 'this' and adds all nodes to be removed.
     * It is used to remove an entire subtree.
     * subnodes of nodes outside the subtree to be removed, however, must not be removed.
     *
     * @param toBeRemoved the nodes to be removed.
     */
    abstract void removeSubtree(ArrayList<Node> toBeRemoved);




    /* ****************************************** Traversal of the DAG *****************************/


    /** The method searches through the leaf nodes to find the first one where the function applied to the
     * node's label returns non-null.
     *
     * @param function to be applied to the node's label.
     * @return the first non-null function value.
     */
    abstract <V> V findInLeafNodes(Function<N,V> function);

    /** applies the consumer to the leaf node's labels
     *
     * @param consumer to be applied to the leaf node's labels.
     */
    abstract void applyToLeafNodes(int timestamp, Consumer<N> consumer);



}
