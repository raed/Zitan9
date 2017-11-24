package AbstractObjects;

/**
 * This interface should be implemented by classes whose instances denote sets in some interpretation
 */
public interface SemanticSet {
    /** checks for subset-relationship
     *
     * @param other the other semantic set
     * @param interpretation the interpretation where the objects live in
     * @return true if 'this' is subset of 'other'
     */
    boolean isSubset(SemanticSet other, Interpretation interpretation);

    /** checks for disjointenss-relationship
     *
     * @param other the other semantic set
     * @param interpretation the interpretation where the objects live in
     * @return true if 'this' is disjoint with 'other'
     */
    boolean isDisjoint(SemanticSet other, Interpretation interpretation);

    /** checks for overlapping-relationship.
     * The default implementation just returns !isDisjoint.
     *
     * @param other the other semantic set
     * @param interpretation the interpretation where the objects live in
     * @return true if 'this' is overlapping with 'other'
     */
    default boolean isOverlapping(SemanticSet other, Interpretation interpretation) {
        return !isDisjoint(other,interpretation);}

    /**
     * @return true if the set is actually a singleton set.
     */
    default boolean isSingleton() {return false;}
}
