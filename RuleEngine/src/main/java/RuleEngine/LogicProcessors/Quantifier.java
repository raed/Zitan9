package RuleEngine.LogicProcessors;

/** This enum provides the usual quantifiers like those used in Decription Logics
 */
public enum Quantifier {
    SOME("\u2203"),    // the existential quantifier
    ALL("\u2200"),     // the universal quantifier
    ATLEAST("\u2265"), // atliast number
    ATMOST("\u2264"),  // atmost number
    EXACT("="),        // exactly number
    RANGE("..");       // from number to number

    /** the string representation of the quantifier */
    private String name;

    Quantifier(String name) {this.name = name;}

    /**
     * @return the string representation
     */
    @Override
    public String toString() {return name;}


}
