package Concepts;

import DAGs.Direction;
import DAGs.InnerNode;
import Graphs.Strategy;
import MISC.Context;
import DAGs.DAG;
import Utils.Utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiPredicate;

/** A derived concept is a concept which is a subconcept of some superconcepts, together with a filter on the
 * subconcepts of the derived concept.
 *
 * Example:<br>
 * Concept Student with attribute semester<br>
 * DerivedConcept Freshmen, subconcept of Student, with filter: semester = 1.
 *<br><br>
 * Example:
 * Concepts Ship and Car with attribute age.<br>
 * DerivedConcept: OldAmphibiousVehicle, subconcept of Ship and Car, with filter age &ge; 20.
 * <br>
 * The class provides methods for repositioning the concepts in the concept hierarchy
 * if a new DerivedConcept is defined, or the attributes of existing concepts have been changed.
 *
 */
public class DerivedConcept extends SetConcept {
    /** this is the filter for the sub-concepts */
    private BiPredicate<Concept,Context> filter = null;
    /** theses are the super-concepts. They are autoamtically inserted in the concept hierarchy*/
    private SetConcept[] superconcepts = null;

    /** creates a new DerivedConcept as a sub-concept of a single super-concept.
     *
     * @param id            the identifier of the concept
     * @param context       the context into which the concept is to be inserted.
     * @param superconcept  the single super-concept
     * @param filter        the filter for the new DerivedConcept's sub-concepts.
     */

    public DerivedConcept(String id, Context context, SetConcept superconcept, BiPredicate filter) {
        super(id, context);
        context.conceptHierarchy.addSubnode(superconcept,this);
        superconcepts = new SetConcept[]{superconcept};
        this.filter = filter;}

    /** creates a new DerivedConcept as a sub-concept of a list of super-concepts.
     * The filter must be inserted separately.
     *
     * @param id            the identifier of the concept
     * @param context       the context into which the concept is to be inserted.
     * @param superconcepts the list of super-concepts
     */
    public DerivedConcept(String id, Context context, SetConcept... superconcepts) {
        super(id, context);
        addToSuperconcepts(context,superconcepts);
        this.superconcepts = superconcepts;}

    /** sets the filter for the DerivedConcept.
     *
     * @param filter the filter
     */
    public void setFilter(BiPredicate filter) {
        this.filter = filter;}

    /** repositions all concepts which are affected by the DerivedConcept.
     * This method should be called when the DerivedConcept has just been defined
     * in order to reposition already existing concepts.
     * It does not reposition sub-concepts of 'this' where the filter returns no longer 'true'
     *
     * @param context the context where the concept lives in.
     */
    public void restructureHierarchy(Context context){
        if(superconcepts == null) {return;}
        if(superconcepts.length == 1) {restructureHierarchySingle(context);}
        else                          {restructureHierarchyMultiple(context);}}



    /** repositions for a DerivedConcept with a single super-concept all concepts which are affected by the DerivedConcept.
     * This method should be called when the DerivedConcept has just been defined
     * in order to reposition already existing concepts.
     * It does not reposition sub-concepts of 'this' where the filter returns no longer 'true'
     *
     * @param context the context where the concept lives in.
     */
    private void restructureHierarchySingle(Context context) {
        DAG<Concept> hierarchy = context.conceptHierarchy;
        ArrayList<Concept> toBeRemoved = new ArrayList<>();
        HashSet<Concept> toBeAdded = new HashSet<>();
        for(InnerNode<Concept> node : hierarchy.getInnerNodes(superconcepts[0])) {
            Concept concept = (Concept)node.label;
            if(concept == this) {continue;}
            if(filter.test(concept,context)) {toBeRemoved.add(concept);toBeAdded.add(concept);}
            else {hierarchy.findInInnerLabels(concept, Direction.DOWN, Strategy.DEPTH_FIRST,
                    ((Concept c)-> {
                        if(c == concept) {return null;}
                        if(filter.test(c,context)) {toBeAdded.add(c);}
                        return null;}));}}
        for(Concept concept : toBeRemoved) {
            hierarchy.removeSubnode(superconcepts[0],concept);}
        for(Concept concept : toBeAdded) {
            hierarchy.addSubnode(this,concept);}}


    /** repositions for a DerivedConcept with multiple super-concepts all concepts which are affected by the DerivedConcept.
     * This method should be called when the DerivedConcept has just been defined
     * in order to reposition already existing concepts.
     * It does not reposition sub-concepts of 'this' where the filter returns no longer 'true'
     *
     * @param context the context where the concept lives in.
     */
    private void restructureHierarchyMultiple(Context context) {
        DAG<Concept> hierarchy = context.conceptHierarchy;
        ArrayList<Concept> toBeRemoved = new ArrayList<>();
        HashSet<Concept> toBeAdded = new HashSet<>();
        int length = superconcepts.length;
        for(int c = 0; c < length; ++c) {
            for(InnerNode<Concept> node : hierarchy.getInnerNodes(superconcepts[c])) {
                Concept subconcept = (Concept)node.label;
                if(subconcept == this) {continue;}
                if(isAffected(subconcept,context)) {
                    toBeRemoved.add(superconcepts[c]);
                    toBeRemoved.add(subconcept);
                    toBeAdded.add(subconcept);}
                else {
                    hierarchy.findInInnerLabels(subconcept,Direction.DOWN,Strategy.DEPTH_FIRST,
                            ((Concept concept) -> {
                                if(subconcept == concept) {return null;}
                                if(isAffected(concept,context)){toBeAdded.add(concept);}
                                return null;}));}}}

        for(int i = 0; i < toBeRemoved.size(); i +=2) {
            hierarchy.removeSubnode(toBeRemoved.get(i),toBeRemoved.get(i+1));}
        for(Concept concept : toBeAdded) {
            hierarchy.addSubnode(this,concept);}
    }

    /** checks if the concept is affected by the DerivedConcept.
     *
     * @param concept   the concept to be checked
     * @param context   the context  where the concept lives in
     * @return  true if the concept is affected by 'this'.
     */
    public boolean isAffected(Concept concept, Context context) {
        if(!filter.test(concept,context)) {return false;}
        DAG<Concept> hierarchy = context.conceptHierarchy;
        for(Concept superconcept : superconcepts) {
            if(!hierarchy.isSubnodeOf(concept,superconcept)) {return false;}}
        return true;}

    /** repositions a concept which is affected by 'this'.
     *
     * @param concept the concept to be repositioned
     * @param context the context  where the concept lives in
     */
    public void repositionConcept(Concept concept, Context context) {
        DAG<Concept> hierarchy = context.conceptHierarchy;
        for(Concept superconcept : superconcepts) {
            hierarchy.removeSubnode(superconcept,concept);}
        hierarchy.addSubnode(this,concept);}

    /** moves a concept which might no longer be a sub-concept of 'this' upwards in the concept hierarchy.
     *
     * @param concept the concept to be moved upwards
     * @param context the context  where the concept lives in
     * @return true if the concept is actually moved.
     */
    public boolean moveUpwards(Concept concept, Context context) {
        if(filter.test(concept,context)) {return false;}
        DAG<Concept> hierarchy = context.conceptHierarchy;
        hierarchy.removeSubnode(this,concept);
        for(Concept superConcept : superconcepts) {
            hierarchy.addSubnode(superConcept,concept);}
        return true;}

    @Override
    public String toString() {
        return getName() + ": " + Utilities.join(superconcepts,",",(c-> ((Concept) c).getName()));}

}
