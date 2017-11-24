package RuleEngine.LogicProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;
import Utils.Utilities;

import java.util.ArrayList;

/** This class provides logical connectives for the processors. <br>
 * Example: Filter out cars which are red AND older than 10.<br>
 * The following junctors are supported: NOT, AND, OR, XOR, IMPL, EQUIV
 * <br>
 * Arguments: <br>
 * - NOT takes one argument<br>
 * - XOR and IMPL take two arguments<br>
 * - AND, OR and EQUIV take any number mor than one argument.
 * <br>
 * Except for NOT and IMPL, all test can optionally be done in parallel.
 */
public class JunctorDefinition extends AbstractDefinition {
    /** the junctor */
    Junctor junctor;
    /** the sub-processes which are to be combined by the junctor */
    ArrayList<AbstractDefinition> subDefinitions;
    /** if true then some of the tests are done in parallel. */
    boolean parallel = true;

    /** constructs a definition
     *
     * @param id   the identifier
     * @param junctor  the junctor
     * @param subDefinitions the processes to be combined by the junctor.
     */
    public JunctorDefinition(String id, Junctor junctor, ArrayList<AbstractDefinition> subDefinitions) {
        super(id);
        assert checkConsistency(junctor,subDefinitions);
        this.junctor = junctor;
        this.subDefinitions = subDefinitions;}

    /** enables/disables parallel tests
     *
     * @param parallel if true then some test are done in parallel
     * @return this
     */
    public JunctorDefinition setParallel(boolean parallel) {
        this.parallel = parallel;
        return this;}

    /** checks the consistency of the number of subDefinition with the junctor.
     *
     * @param junctor  the junctor
     * @param subDefinitions the sub-definitions
     * @return true if the check is okay.
     */
    private boolean checkConsistency(Junctor junctor, ArrayList<AbstractDefinition> subDefinitions) {
        int size = subDefinitions.size();
        switch(junctor) {
            case NOT: return size == 1;
            case IMPL:
            case XOR: return size == 2;
            default: return size > 1;}}

    /** generates the processor
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processing unit
     * @return this
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new JunctorProcessor(this,context,parentProcessor);}

    /**
     * @return 'junctor' applicationName subDefintion-ids
     */
    @Override
    public String toString() {
        return "junctor " + junctor.toString() + "("+ Utilities.join(subDefinitions,",",(d -> d.getName()))+")";}
}
