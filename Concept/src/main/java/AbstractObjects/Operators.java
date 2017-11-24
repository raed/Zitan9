package AbstractObjects;


import java.io.Serializable;

/**
 * Enum for all operations
 */
public enum Operators implements Serializable {
    LESS("<"),
    LESSEQUALS("<="),
    EQUALS("="),
    GREATER(">"),
    GREATEREQUALS(">="),
    IN("in"),
    CONTAINS("contains"),
    BEFORE("before"),
    MEETS("meets"),
    OVERLAPS("overlaps"),
    STARTS("starts"),
    FINISHES("finishes"),
    AFTER("after"),
    DISJOINT("disjoint"),
    SUBSET("subset"),
    SUPERSET("superset")
    ;


    private String name;

    Operators(String name) {this.name = name;}

    public static Operators getOperator(String name) {
        switch(name.trim()) {
            case "<" :        return LESS;
            case "<=" :       return LESSEQUALS;
            case "=" :        return EQUALS;
            case ">" :        return GREATER;
            case ">=" :       return GREATEREQUALS;
            case "in" :       return IN;
            case "contains" : return CONTAINS;
            case "before" :   return BEFORE;
            case "meets" :    return MEETS;
            case "overlaps" : return OVERLAPS;
            case "starts" :   return STARTS;
            case "finishes" : return FINISHES;
            case "after" :    return AFTER;
            case "subset":    return SUBSET;
            case "superset":  return SUPERSET;
        }
        return null;}

    public String toString() {return name;}
}
