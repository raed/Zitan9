package DAGs;

import AbstractObjects.ItemWithId;
import Graphs.GraphTraversal;
import Graphs.Strategy;
import Graphs.StreamGenerators;
import MISC.Activity;
import Utils.TriConsumer;
import Utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** This class is the interface to a Directed Acyclic Graph (DAG).
 * The graph itself is represented by the abstract class Node together with the two subclasses
 * LeafNode and InnerNode.
 * Each node carries a label of type N (for example, Concept).
 * The distinction between InnerNode and LeafNode is determined by the predicate isLeafLabel
 * to be applied to the labels.
 * <br>
 * Example: Labels are concepts like Person, Student, Car etc as well as, e.g. Paul, John, Herbie.
 * The distinction between InnerNode and LeafNode comes from the labels which either denote sets or individuals.
 * <br>
 * Therefore in the DAG itself there may be leaf nodes (e.g. Student), which are not leaf-nodes by the labels
 * (there are no students yet).
 * <br>
 * The DAG is optimized for a situations where there are few inner nodes, but many leaf nodes.
 * Therefore each node has two lists of subnodes, one with leaf nodes, and one with inner nodes.
 * <br>
 * The public DAG-methods are synchronized, whereas the Node-methods are not.
 * Thus, access to the DAG should be only via the methods in this class.
 * <br>
 * Changes to the structure of the DAG can be observed by various observers.
 */

public class DAG<N> extends ItemWithId {

    /** a predicate for testing if a label is a leaf label */
    private final Predicate<N> isLeafLabel;

    /** the root nodes of the DAG */
    private final ArrayList<Node> roots = new ArrayList();

    /** for mapping node labels to Node objects */
    private final HashMap<N,InnerNode<N>> innerNodes = new HashMap();

    /** for mapping node labels to Node objects */
    private final HashMap<N,LeafNode<N>> leafNodes = new HashMap();

    private final HashMap<N,MetaData<N>> metaData = new HashMap<>();

    /** a timestamp to be used by some of the algorithms. */
    private int timestamp = 0;

    /** constructs a new DAG with the given applicationName and a leaf label predicate.
     *
     * @param id an identifier for the DAG.
     * @param isLeafLabel a predicate for testing if a label is a leaf label.
     * */
    public DAG(String id,Predicate<N> isLeafLabel) {
        super(id);
        this.isLeafLabel = isLeafLabel;}

    /* ---------------------------------------  Meta Data -------------------------------- */

    /** This method attaches some metadata to a node, typically a root node.
     *
     * @param label the node's label
     * @param metaData the metadata to be added
     * @return true if there was a node at which the metadata could be added, otherwise false.
     */
    public boolean setMetaData(N label, MetaData<N> metaData) {
        Node<N> node = getNode(label);
        if(node == null) {return false;}
        metaData.setNode(node);
        this.metaData.put(label,metaData);
        return true;}

    /** returns the attached metadata.
     *
     * @param label a node's label
     * @return the metadata for the node, or null if there are none
     */
    public MetaData<N> getMetaData(N label) {
        return metaData.get(label);}

    /** filters nodes with metadata intervalContaining the given tag.
     *
     * @param tag any String
     * @return a stream of nodes with metadata intervalContaining this tag.
     */
    public Stream<Node<N>> nodesWithTag(String tag) {
        return metaData.values().stream().filter(m->m.containsTag(tag)).map(md->md.getNode());}

    /** filters nodes with metadata intervalContaining the given author.
     *
     * @param author any String
     * @return a stream of nodes with metadata intervalContaining this author.
     */
    public Stream<Node<N>> nodesWithAuthor(String author) {
        return metaData.values().stream().filter(m->m.containsAuthor(author)).map(md->md.getNode());}


    /* ---------------------------------------  Observer -------------------------------- */

    /** These observers are called when a label is inserted/removed.*/
    private final ArrayList<BiConsumer<N, Activity>> nodeObserver = new ArrayList<>();

    /** These observers are called when a super/subnode relationship is inserted/removed.*/
    private final ArrayList<TriConsumer<N,N, Activity>> subNodeObserver = new ArrayList<>();

    /** adds an observer for adding and removing nodes
     *
     * @param observer
     */
    public synchronized void addNodeObserver(BiConsumer<N, Activity> observer) {
        nodeObserver.add(observer);}

    /** removes an observer for adding and removing nodes
     *
     * @param observer
     */
    public synchronized void removeNodeObserver(BiConsumer<N, Activity> observer) {
        nodeObserver.remove(observer);}

    /** adds an observer for adding and removing super/subnode relationships.
     * A more fine grained observer management can be obtained by attaching an observer
     * directly at a node. These observers are called only when a the subnode-relationship
     * is changed for this node.
     *
     * @param observer
     */
    public synchronized void addSubNodeObserver(TriConsumer<N,N, Activity> observer) {
        subNodeObserver.add(observer);}

    /** adds an observer for adding and removing super/subnode relationships
     *
     * @param observer
     */
    public synchronized void removeSubNodeObserver(TriConsumer<N,N, Activity> observer) {
        subNodeObserver.remove(observer);}

    /** adds an observer to the label's node.
     * This observer is called when the node adds/removes a new subnode.
     *
     * @param label   the node's label
     * @param observer the observer to be added.
     */
    public synchronized void addNodeObserver(N label, BiConsumer<N, Activity> observer) {
        assert !isLeafLabel.test(label);
        InnerNode node = innerNodes.get(label);
        if(node != null) {node.addObserver(observer);}
    }

    /** removes an observer from the label's node.
     *
     * @param label   the node's label
     * @param observer the observer to be added.
     */
    public synchronized void removeNodeObserver(N label, BiConsumer<N, Activity> observer) {
        assert !isLeafLabel.test(label);
        InnerNode node = innerNodes.get(label);
        if(node != null) {node.removeObserver(observer);}
    }


    /* ---------------------------------------  Node Operations -------------------------------- */

    /** yields the Node with the given label.
     *
     * @param label any String
     * @return the Node with the given label or null if there is none.
     */
    public synchronized Node getNode(N label) {
        if(label == null) {return null;}
        return isLeafLabel.test(label) ? leafNodes.get(label) : innerNodes.get(label);}

    /** yields the Node with the given label.
     *
     * @param label any String
     * @return the Node with the given label or null if there is none.
     */
    public synchronized ArrayList<InnerNode<N>> getInnerNodes(N label) {
        InnerNode<N> node = innerNodes.get(label);
        return (node == null) ? null : node.innerNodes;}

    /** yields the Node with the given label.
     *
     * @param label any String
     * @return the Node with the given label or null if there is none.
     */
    public synchronized ArrayList<InnerNode<N>> getSupernodes(N label) {
        InnerNode<N> node = innerNodes.get(label);
        return (node == null) ? null : node.superEdges;}



    /** adds a new root node.
     * If the label already exists, nothing is changed.
     * All nodeObservers are called before the label is inserted.
     *
     * @param label for the new node.
     */
    public synchronized Node<N> addNode(N label) {
        Node node = getNode(label);
        if(node == null) {
            for(BiConsumer<N,Activity> observer : nodeObserver) {observer.accept(label,Activity.ADD);}
            if(isLeafLabel.test(label)) {
                node =  new LeafNode(label);
                leafNodes.put(label,(LeafNode)node);}
            else {node = new InnerNode(label);
                innerNodes.put(label,(InnerNode)node);}
            roots.add(node);}
        return node;}

    /** removes the given node.
     * All nodeObservers are called after the node is removed.
     *
     * @param node for the new node.
     */
    public synchronized void removeNode(Node<N> node) {
        node.remove();
        if(node.isInnerNode()) {innerNodes.remove(node.label);}
        else {leafNodes.remove(node.label);}
        if(node.isRootNode()) {
            roots.remove(node);
            if(node.isInnerNode()) {
                for(Node<N> subnode : ((InnerNode<N>)node).innerNodes) {if(subnode.superEdges.isEmpty()) {roots.add(subnode);}}
                for(Node<N> subnode : ((InnerNode<N>)node).leafNodes) {if(subnode.superEdges.isEmpty()) {roots.add(subnode);}}}}
        for(BiConsumer<N,Activity> observer : nodeObserver) {observer.accept(node.label,Activity.DELETE);}}

    /** removes the node with the given label.
     * All nodeObservers are called after the node is inserted.
     *
     * @param label the label of the node to be removed.
     * @return the removed node (or null).
     */
    public synchronized Node<N> removeNode(N label) {
        Node node = isLeafLabel.test(label) ? leafNodes.get(label) : innerNodes.get(label);
        if(node != null) {removeNode(node);}
        return node;}


    /** removes the entire subtree from the node downwards.
     *
     * @param node the root node of the subtree to be removed.
     * @return the number of removed nodes.
     */
    public synchronized int removeSubtree(Node<N> node) {
        ArrayList<Node> toBeRemoved = new ArrayList<>();
        node.removeSubtree(toBeRemoved);
        for(Node<N> n : toBeRemoved) {
            if(n.isLeafNode()) {leafNodes.remove(n.label);}
            else {innerNodes.remove(n.label);}
            roots.remove(n);
            for(BiConsumer<N,Activity> observer : nodeObserver) {observer.accept(n.label,Activity.DELETE);}}
        return toBeRemoved.size();}

    /** removes the entire subtree from the node with the given label downwards.
     *
     * @param label the label of the node to be removed.
     * @return the number of removed nodes.
     */
    public synchronized int removeSubtree(N label) {
        Node<N> node = getNode(label);
        if(node == null) {return 0;}
        return removeSubtree(node);}

    /** adds the node-subnode relationship to the DAG.
     *
     * @param superNode,  the super-node
     * @param subNode  the sub-node
     */
    public synchronized void addSubnode(Node<N> superNode, Node<N> subNode) {
        assert superNode.isInnerNode();
        if(subNode.isRootNode()){roots.remove(subNode);}
        for(TriConsumer<N,N,Activity> observer : subNodeObserver) {
            observer.accept(superNode.label,subNode.label,Activity.ADD);}
        ((InnerNode<N>)superNode).addSubnode(subNode);}


    /** adds the node-subnode relationship to the DAG
     * If the superLabel is not yet present in the DAG it becomes a new root node.
     * If the subnode is already present in the DAG, it is reused
     * (the first time this happens, the tree becomes a DAG).
     *
     * @param superLabel  the label of an existing node.
     * @param subLabel  the label of the subnode.
     */
    public synchronized void addSubnode(N superLabel, N subLabel) {
        assert !isLeafLabel.test(superLabel);
        addSubnode((InnerNode)addNode(superLabel),addNode(subLabel));}

    /** removes the node-subnode relationship from the DAG.
     * If the subnode is an inner node, and has no supernodes anymore, it becomes a root node.
     * The observers are called after the removal.
     *
     * @param superNode,  the super-node
     * @param subNode  the sub-node
     */
    public synchronized void removeSubnode(InnerNode<N> superNode, Node<N> subNode) {
        if(!superNode.removeSubnode(subNode)) {return;}
        if(subNode.superEdges.isEmpty()) {roots.add(subNode);}
        for(TriConsumer<N,N,Activity> observer : subNodeObserver) {
            observer.accept(superNode.label,subNode.label,Activity.REMOVE);}}

    /** removes the node-subnode relationship from the DAG.
     * If the subnode is an inner node, and has no supernodes anymore, it becomes a root node.
     * The observers are called after the removal.
     *
     * @param superlabel,  the super-node's label
     * @param sublabel  the sub-node's label
     */
    public synchronized void removeSubnode(N superlabel,N sublabel) {
        assert !isLeafLabel.test(superlabel);
        InnerNode<N> supernode = innerNodes.get(superlabel);
        if(supernode == null) {return;}
        Node<N> subnode = getNode(sublabel);
        if(sublabel == null) {return;}
        removeSubnode(supernode,subnode);
    }


    /* ****************************** Tests *********************************/


    /** @return true if the DAG is empty */
    public synchronized boolean isEmpty() {return roots.isEmpty();}

    /** @return the number of nodes in the DAG */
    public synchronized int size() {return innerNodes.size() + leafNodes.size();}

    /**
     * @return the maximal depth of the DAG.
     */
    public synchronized int depth() {
        int depth = 0;
        for(Node<N> node : roots) {depth = Math.max(depth,node.depth());}
        return depth;}

    /**
     * @return the maximal depth of the node.
     */
    public synchronized int depth(Node<N> node) {
        return node.depth();}

    /**
     * @return the maximal depth of the node with the given label, or -1 if the label is unknown.
     */
    public synchronized int depth(N label) {
        Node<N> node = getNode(label);
        if(node == null) {return -1;}
        return node.depth();}


    /** checks if the node with the given label is a root node.
     *  If the label is unknown then false is returned.
     *
     * @param label a node's label
     * @return true if the node is a root node.
     */
    public synchronized boolean isRootNode(N label) {
        Node node = getNode(label);
        if(node == null) {return false;}
        return node.superEdges.isEmpty();}

    /** checks if the given node is a root node.
     *
     * @param node a node
     * @return true if the node is a root node.
     */
    public synchronized boolean isRootNode(Node<N> node) {
        return node.superEdges.isEmpty();}


    /** checks if subnode is in fact below the supernode (or equal)
     *
     * @param subNode   a node
     * @param superNode a node
     * @return true if the subNode is below or equal the superNode
     */
    public synchronized boolean isSubnodeOf(Node<N> subNode, Node<N> superNode) {
        if(subNode == superNode) {return true;}
        if(superNode.isLeafNode()) {return false;}
        return subNode.isSubnodeOf((InnerNode)superNode);}

    /** checks if the node with sublabel is in fact below or equal the node with the superlabel.
     *
     * @param sublabel   a node label
     * @param superlabel a node label
     * @return true if the sublabel is below or equal the superlabel
     */
    public synchronized boolean isSubnodeOf(N sublabel, N superlabel) {
        if(sublabel.equals(superlabel)) {return true;}
        Node subNode = getNode(sublabel);
        if(subNode == null) {return false;}
        Node superNode = getNode(superlabel);
        if(superNode == null) {return false;}
        return isSubnodeOf(subNode,superNode);}

    /** checks whether the two nodes have a common sub/supernode.
     *
     * @param label1 the first node label
     * @param label2 the second node label
     * @param direction the direction of the check
     * @return true if the two nodes have a common sub/supernode.
     */
    public boolean hasCommonNode(N label1, N label2, Direction direction) {
        if(label1.equals(label2)) {return true;}
        Node n1 = getNode(label1);
        if(n1 == null) {return false;}
        Node n2 = getNode(label2);
        if(n2 == null) {return false;}
        return hasCommonNode(n1,n2,direction);}

    /** checks whether the two nodes have a common sub/supernode.
     *
     * @param node1 the first node
     * @param node2 the second node
     * @param direction the direction of the check
     * @return true if the two nodes have a common sub/supernode.
     */
    public boolean hasCommonNode(Node<N> node1, Node<N> node2, Direction direction) {
        int ts = getTimestamp();
        markInnerNodes(node1,direction, Strategy.DEPTH_FIRST,ts);
        return findInInnerNodes(node2,direction,Strategy.DEPTH_FIRST,(node->node.getTimestamp() == ts));

    }



    /** @return a cycle if the DAG ist actually cyclic (should not happen), otherwise null */
    public List<InnerNode<N>> getCycle() {
        ArrayList<InnerNode<N>> path = new ArrayList();
        for(Node<N> node : roots) {
            if(node.isLeafNode()) {continue;}
            path.clear();
            path.add((InnerNode<N>)node);
            for(InnerNode<N> subnode : ((InnerNode<N>)node).innerNodes) {
                List<InnerNode<N>> cycle = getCycle(subnode,path);
                if(cycle != null) {return cycle;}}}
        return null;}

    /** recursive cycle test
     *
     * @param node a node in the DAG
     * @param path the path to that node.
     * @return the cycle if there is a cycle.
     */
    private  List<InnerNode<N>> getCycle(InnerNode<N> node, ArrayList<InnerNode<N>> path) {
        int i = path.indexOf(node);
        if(i >= 0) {return path.subList(i, path.size());}
        path.add(node);
        if(node.innerNodes != null) {
            for (InnerNode<N> n : node.innerNodes) {
                List<InnerNode<N>> cycle = getCycle(n,path);
                if(cycle != null) {return cycle;}}}
        path.remove(path.size()-1);
        return null;}


    /** increments and returns the timestamp
     *
     * @return the incremented timestamp.
     */
    public int getTimestamp() {return ++timestamp;}

    /* ******************************* Stream-Access to the DAG structure *******************************/

    /** returns a stream of leaf nodes below the given node.
     * Multiple occurrences caused by the DAG structure are filtered out
     * using a timestamp mechanism.
     * Two different streams of leaf nodes therefore MUST NOT be processed
     * by different threads in parallel!
     *
     * @param node any node
     * @return the stream of leaf node labels.
     */
    public synchronized Stream<LeafNode<N>> leafNodes(Node<N> node) {
        return Stream.of(node).flatMap(n-> {
            getTimestamp(); // increment the timestamp when the stream is processed.
            Stream<LeafNode<N>> stream =  n.leafNodes();
            return stream == null ? Stream.empty() : stream;}).
                filter(n->{
                    if(n.timestamp == timestamp) {return false;}
                    n.timestamp = timestamp;
                    return true;});}

    /** returns a stream of leaf nodes below the node with the given label.
     * Multiple occurrences caused by the DAG structure are filtered out
     * using a timestamp mechanism.
     * Two different streams of leaf nodes therefore MUST NOT be processed
     * by different threads in parallel!
     *
     * @param label the label of some node.
     * @return the stream of leaf node labels, or null.
     */
    public synchronized Stream<N> leafNodes(N label) {
        Node<N> node = getNode(label);
        if(node == null) {return null;}
        return leafNodes(node).map(n->n.label);}

    /** generates a stream of all leaf nodes.
     * Multiple occurrences caused by the DAG structure are filtered out
     * using a timestamp mechanism.
     * Two different streams of leaf nodes therefore MUST NOT be processed
     * by different threads in parallel!
     *
     * @return the stream of all leaf nodes.
     */
    public synchronized Stream<N> leafNodes() {
        if(roots.isEmpty()) {return null;}
        Stream<LeafNode<N>> stream = Stream.of(roots.get(0)).flatMap(n-> {
            getTimestamp(); // increment the timestamp when the stream is processed.
            Stream<LeafNode<N>> str = n.leafNodes();
            return str == null ? Stream.empty() : str;});
        for(int i = 1; i < roots.size(); ++i) {
            stream = Utilities.streamConcat(stream, roots.get(i).leafNodes());}
        return stream.
                filter(n->{
                    if(n.timestamp == timestamp) {return false;}
                    n.timestamp = timestamp;
                    return true;}).
                map(node -> node.label);}

    /** returns a stream of leaf nodes below the given node.
     * Multiple occurrences caused by the DAG structure are NOT filtered out
     * (add .distinct() to the stream).
     * <br>
     * Two different threads may evaluate two such streams in parallel.
     * While one thread evaluates the stream, another thread, however, must not change the DAG's structure!
     *
     * @param node any node
     * @return the stream of leaf nodes (maybe with multiple occurrences of the same label).
     */
    public synchronized Stream<LeafNode<N>> leafNodesRaw(Node<N> node) {
        return node.leafNodes();}

    /** returns a stream of leaf nodes below the node with the given label.
     * Multiple occurrences caused by the DAG structure are NOT filtered out
     * (add .distinct() to the stream)
     * <br>
     * Two different threads may evaluate two such streams in parallel.
     * While one thread evaluates the stream, another thread, however, must not change the DAG's structure!
     *
     * @param label the label of some node.
     * @return the stream of leaf node labels (maybe with multiple occurrences of the same label), or null.
     */
    public synchronized Stream<N> leafNodesRaw(N label) {
        Node<N> node = getNode(label);
        if(node == null) {return null;}
        Stream<LeafNode<N>> stream = node.leafNodes();
        return stream == null ? null : stream.map(n->n.label);}

    /** generates a stream of all leaf nodes.
     * It may contain multiple occurrences
     *
     * @return the stream of all leaf nodes
     */
    public synchronized Stream<N> leafNodesRaw() {
        Stream<LeafNode<N>> stream = Utilities.streamConcat(roots, (node -> node.leafNodes()));
        return stream == null ? null : stream.map(node->node.label);
    }


    /** This method computes a stream of inner nodes below the node with the given label.
     * Double occurrences of the same node are filtered out.
     *
     * @param node        a node
     * @param inclusive   if true then this node becomes the first node in the stream.
     * @param direction   controls the direction of the traversal through the DAG
     * @param strategy    controls breadth-first / depth-first traversal.
     * @return the stream of inner nodes.
     */
    public synchronized Stream<InnerNode<N>> innerNodes(Node<N> node, boolean inclusive, Direction direction, Strategy strategy) {
        int ts = getTimestamp();
        if(node.isLeafNode()) {
            switch(direction) {
                case DOWN: return null;
                case UP:   return StreamGenerators.streamForGraph(node,false,strategy,ts,(n -> n.superEdges)).map(n->(InnerNode<N>)n);}}
        Function<InnerNode<N>,Object> successors = null;
        switch(direction) {
            case UP:   successors = (n->n.superEdges); break;
            case DOWN: successors = (n->n.innerNodes); break;}
        return StreamGenerators.streamForGraph((InnerNode<N>)node,inclusive,strategy,ts,successors);}


    /** This method computes a stream of inner nodes below the node with the given label.
     * Double occurrences of the same node are filtered out.
     *
     * @param label     a node's label
     * @param inclusive if true then this node becomes the first node in the stream.
     * @param direction   controls the direction of the traversal through the DAG
     * @param strategy    controls breadth-first / depth-first traversal.
     * @return the stream of inner nodes, or null if the label is unknown.
     */
    public synchronized Stream<N> innerNodes(N label, boolean inclusive, Direction direction, Strategy strategy) {
        if(label == null) {return null;}
        Node<N> node = getNode(label);
        if(node == null) {return null;}
        Stream<InnerNode<N>> stream = innerNodes(node,inclusive,direction,strategy);
        return stream == null ? null : stream.map(n -> n.label);}



    /* ******************************* Traversal through the DAG structure *******************************/

    /** searches through all leaf nodes to find one where the function applied to its label returns non-null.
     *
     * @param function a predicate to be applied to the labels.
     * @return the first non-null function value.
     */
    public synchronized <V> V  findInLeafNodes(Function<N,V> function) {
        for(Node<N> node : roots) {
            V value = node.findInLeafNodes(function);
            if(value != null) {return value;}}
        return null;}



    /** searches through the leaf nodes below the given node to find one where the function applied to its label returns non-null.
     *
     * @param node any node of the DAG
     * @param function a predicate to be applied to the labels.
     * @return the first non-null function value.
     */
    public synchronized <V> V  findInLeafNodes(Node<N> node, Function<N,V> function) {
        return node.findInLeafNodes(function);}

    /** searches through the leaf nodes below the node with the given label to find one where the function applied to its label returns non-null.
     *
     * @param label a label for a node in the DAG
     * @param function a predicate to be applied to the labels.
     * @return the first non-null function value.
     */
    public synchronized <V> V findInLeafNodes(N label, Function<N,V> function) {
        Node<N> node = getNode(label);
        if(node == null) {return null;}
        return node.findInLeafNodes(function);}


    /** applies the consumer to all leaf node labels
     *
     * @param consumer a function to be applied to the labels.
     */
    public synchronized void applyToLeafNodes(Consumer<N> consumer) {
        int ts = getTimestamp();
        for(Node<N> node : roots) {node.applyToLeafNodes(ts,consumer);}}

    /** applies the consumer to all leaf node labels below the given node.
     *
     * @param node a node in the DAG.
     * @param consumer a function to be applied to the labels.
     */
    public synchronized void applyToLeafNodes(Node<N> node, Consumer<N> consumer) {
        node.applyToLeafNodes(getTimestamp(),consumer);}

    /** applies the consumer to all leaf node labels below the node with the given label.
     *
     * @param label a label of some node
     * @param consumer a function to be applied to the labels.
     */
    public synchronized void applyToLeafNodes(N label, Consumer<N> consumer) {
        Node<N> node = getNode(label);
        if(node == null) {return;}
        node.applyToLeafNodes(getTimestamp(),consumer);}

    /** searches through the inner nodes below/above the given nodes, and including the given node,
     *  to find an inner node where the predicate returns true.
     *
     * @param node the node from where the search starts.
     * @param direction  either UP or DOWN
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST.
     * @param function to be applied to the inner nodes.
     * @return the first inner node where the predicate returns true, or null.
     */
    public synchronized <V> V findInInnerNodes(Node<N> node, Direction direction, Strategy strategy, Function<Node<N>,V> function) {
        int ts = getTimestamp();
        if(node.isLeafNode()) {
            switch(direction) {
                case DOWN: return null;
                case UP:   return GraphTraversal.uninformedSearch(node,strategy,false,ts,(n->n.superEdges),(n -> function.apply(n)));}}
        Function<InnerNode<N>,Object> successors = null;
        switch(direction) {
            case UP:   successors = (n -> n.superEdges); break;
            case DOWN: successors = (n -> n.innerNodes); break;}
        return GraphTraversal.uninformedSearch((InnerNode<N>)node,strategy,true,ts,successors,(n->function.apply(n)));}

    /** searches through the inner nodes below/above the given nodes, and including the given node,
     *  to find an inner node where the predicate applied to its label returns true.
     *
     * @param node the node from where the search starts.
     * @param direction  either UP or DOWN
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST.
     * @param function to be applied to the inner nodes.
     * @return the first inner node where the predicate returns true, or null.
     */
    public synchronized <V> V findInInnerLabels(Node<N> node, Direction direction, Strategy strategy, Function<N,V> function) {
        int ts = getTimestamp();
        if(node.isLeafNode()) {
            switch(direction) {
                case DOWN: return null;
                case UP:   return GraphTraversal.uninformedSearch(node,strategy,false,ts,(n->n.superEdges),(n -> function.apply(n.label)));}}
        Function<InnerNode<N>,Object> successors = null;
        switch(direction) {
            case UP:   successors = (n -> n.superEdges); break;
            case DOWN: successors = (n -> n.innerNodes); break;}
        return GraphTraversal.uninformedSearch((InnerNode<N>)node,strategy,true,ts,successors,(n->function.apply(n.label)));}

    /** searches through the inner nodes below/above the given nodes, and including the given node,
     *  to find an inner node where the predicate applied to its label returns true.
     *
     * @param label the node's label from where the search starts.
     * @param direction  either UP or DOWN
     * @param strategy either BREADTH_FIRST or DEPTH_FIRST.
     * @param function to be applied to the inner nodes.
     * @return the first inner node where the predicate returns true, or null.
     */
    public synchronized <V> V findInInnerLabels(N label, Direction direction, Strategy strategy, Function<N,V> function) {
        Node<N> node = getNode(label);
        if(node == null) {return null;}
        return findInInnerLabels(node,direction,strategy,function);}


    /** This method applies a consumer to the node labels of all nodes below/above (inclusive) the given node.
     *
     * @param node any node of the DAG
     * @param direction  either UP or DOWN
     * @param strategy controls breadth-first / depth-first traversal.
     * @param timestamp a timestamp to be set to the inner nodes.
     */
    public synchronized void markInnerNodes(Node<N> node, Direction direction, Strategy strategy, int timestamp) {
        if(node.isLeafNode()) {
            switch(direction) {
                case DOWN: return;
                case UP:GraphTraversal.uninformedSearch(node,strategy,false,(n->n.superEdges),(n -> {n.setTimestamp(timestamp); return false;}));
                    return;}}
        Function<InnerNode<N>,Object> successors = null;
        switch(direction) {
            case UP:   successors = (n -> n.superEdges); break;
            case DOWN: successors = (n -> n.innerNodes); break;}
        GraphTraversal.uninformedSearch((InnerNode<N>)node,strategy,true,successors,(n -> {n.setTimestamp(timestamp); return false;}));}

    /** This method applies a consumer to the node labels of all nodes below/above (inclusive) the given node.
     *
     * @param node any node of the DAG
     * @param direction  either UP or DOWN
     * @param strategy controls breadth-first / depth-first traversal.
     * @param consumer a function to be applied to the nodes label.
     */
    public synchronized void applyToInnerLabels(Node<N> node, Direction direction, Strategy strategy, Consumer<N> consumer) {
        int ts = getTimestamp();
        if(node.isLeafNode()) {
            switch(direction) {
                case DOWN: return;
                case UP:GraphTraversal.apply(node,strategy,false,ts,(n->n.superEdges),(n -> consumer.accept(n.label)));
                    return;}}
        Function<InnerNode<N>,Object> successors = null;
        switch(direction) {
            case UP:   successors = (n -> n.superEdges); break;
            case DOWN: successors = (n -> n.innerNodes); break;}
        GraphTraversal.apply((InnerNode<N>)node,strategy,true,ts,successors,(n->consumer.accept(n.label)));}



    /** This method applies a consumer to the node labels of all all nodes below/above (inclusive) the node with the given label.
     *
     * @param label the label of any node of the DAG
     * @param direction either UP or DOWN
     * @param strategy controls breadth-first / depth-first traversal.
     * @param consumer a function to be applied to the nodes label.
     */
    public synchronized void applyToInnerLabels(N label, Direction direction, Strategy strategy, Consumer<N> consumer) {
        Node<N> node = getNode(label);
        if(node == null) {return;}
        applyToInnerLabels(node,direction,strategy,consumer);}

    /** @return a string representation of the DAG */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getName() +"\n");
        for(Node<N> node : roots) {
            s.append(node.toString()).append("\n");}
        return s.toString();}



}
