package RuleEngine.LogicProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

/** With this class one can define quantifications: SOME,ATLEAST,ATMOST,EXACT,RANGE
 */
public class QuantificationDefinition extends AbstractDefinition {
    /** the quantification */
    Quantification quantification;
    /** where to get the min-value for the quantification */
    int minIndex = -1;
    /** where to get the max-value for a RANGE quantifier */
    int maxIndex = -1;
    /** if true then the quantification is tested and the query just filtered */
    boolean testOnly = true;
    /** if true then all values satisfying the quantifier are sent */
    boolean sendAll = false;
    /** the definition of the process over which is quantified */
    AbstractDefinition subProcessorDefinition;

    /** creates a new definition
     *
     * @param id its identifier
     * @param quantification its quantification
     * @param subProcessorDefinition the processor definintion over which it is quentified
     */
    public QuantificationDefinition(String id, Quantification quantification, AbstractDefinition subProcessorDefinition) {
        super(id);
        assert quantification.quantifier != Quantifier.ALL;
        this.quantification = quantification;
        this.subProcessorDefinition = subProcessorDefinition;}

    /** sets the index where to get the min-value for the quantifiers
     *
     * @param minIndex where to get the min-value for the quantifiers
     * @return this
     */
    public QuantificationDefinition setMinIndex(int minIndex) {
        assert minIndex >= 0;
        this.minIndex = minIndex;
        return this;}

    /** sets the index where to get the max-value for the quantifiers
     *
     * @param maxIndex where to get the max-value for the quantifiers
     * @return this
     */
    public QuantificationDefinition setMaxIndex(int maxIndex) {
        assert maxIndex >= 0;
        this.maxIndex = maxIndex;
        return this;}

    /** controls if the processsor works as filter only or inserts values
     *
     * @param testOnly if true the processor works as filter only
     * @return this
     */
    public QuantificationDefinition setTestOnly(boolean testOnly) {
        this.testOnly = testOnly;
        return this;}

    /** controls if the processor sends all values satisfying the quantification, or just the minimum value
     *
     * @param sendAll if true then all values are sent.
     * @return
     */
    public QuantificationDefinition setSendAll(boolean sendAll) {
        testOnly = false;
        this.sendAll = sendAll;
        return this;}

    /** generates the processor
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processing unit
     * @return the new processor
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new QuantificationProcessor(this,context,parentProcessor);}

    /**
     * @return quantification +applicationName + quantifier
     */
    @Override
    public String toString() {
        return "quantification " + getName() + " " + quantification.toString();}
}
