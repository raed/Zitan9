package DAGs;

public interface SimpleHierarchy {
    default boolean isBelowOrEqual(SimpleHierarchy other) {return this.equals(other);}
}
