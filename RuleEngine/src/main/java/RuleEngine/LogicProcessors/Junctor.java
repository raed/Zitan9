package RuleEngine.LogicProcessors;


/** This enum represents the usual propositional logical connectives
 */
public enum Junctor {
    NOT("\u2310"),
    AND("\u2227"),
    OR("\u2228"),
    XOR("^"),
    IMPL("\u21D2"),
    EQUIV("\u21D4");

    /** the mathematical notation for the junctor */
    private String name;

    /** constructs the junctor with the mathematical notation
     *
     * @param name the mathematical notation.
     */
    Junctor(String name) {this.name = name;}


    /** @return the mathematical applicationName for the junctor. */
    @Override
    public String toString() {
        return name;}
}
