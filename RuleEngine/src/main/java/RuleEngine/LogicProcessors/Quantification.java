package RuleEngine.LogicProcessors;


/** This class provides quantifications consisting of quantifiers together with numbers
 */
public class Quantification {
    /** the quantifier itself */
    public Quantifier quantifier;
    /** used by atleast, atmost, exactly and range */
    public int min;
    /** used only by range */
    public int max;

    /** constructs a quantification with quantifier and min and may value (for RANGE)
     *
     * @param quantifier  the quantifier
     * @param min         the value for ATLEAST, ATMOST, EXACTLY and RANGE
     * @param max         the value for RANGE
     */
    public Quantification(Quantifier quantifier, int min, int max) {
        this.quantifier = quantifier;
        this.min = min;
        this.max = max;
    }

    /** constructs a quantification with quantifier and min value
     *
     * @param quantifier  the quantifier
     * @param min         the value for ATLEAST, ATMOST, EXACTLY
     */
    public Quantification(Quantifier quantifier, int min) {
        this.quantifier = quantifier;
        this.min = min;
        this.max = min;
    }

    /** constructs a quantification for ALL and SOME
     *
     * @param quantifier  the quantifier
     */
    public Quantification(Quantifier quantifier) {
        this.quantifier = quantifier;
        this.min = 0;
        this.max = 0;}

    /** turns the quantification into a string
     *
     * @return a string representation of the quantification.
     */
    @Override
    public String toString() {
        String q = quantifier.toString();
        switch(quantifier) {
            case ATLEAST:
            case ATMOST:
            case EXACT:   return q + " " + min;
            case RANGE:   return  min + q + max;
            default: return q;}}

}
