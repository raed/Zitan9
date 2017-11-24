package AccessManagement;

public enum AccessSequence {

    CONCEPT,
    CONCEPT_ATTRIBUTE,
    CONCEPT_ATTRIBUTE_CONSTRAINT,
    ATTRIBUTE,
    ATTRIBUTE_CONSTRAINT,
    CONSTRAINT;

    public static int size() {return values().length;}

}
