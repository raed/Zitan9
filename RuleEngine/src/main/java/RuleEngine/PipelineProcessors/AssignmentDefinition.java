package RuleEngine.PipelineProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

/** With this processor one can realize something like X := Y,
 * i.e. the contents of one query array cell is copied to another query array cell.
 */
public class AssignmentDefinition extends AbstractDefinition {
    int inIndex;
    int outIndex;

    /** creates a AssignmentDefinition.
     *
     * @param id the identifier for the processor.
     * @param inIndex the read index
     * @param outIndex the write index
     */
    public AssignmentDefinition(String id, int inIndex, int outIndex) {
        super(id);
        assert inIndex >= 0;
        assert outIndex >= 0;
        this.inIndex = inIndex;
        this.outIndex = outIndex;}


    /** creates the corresponding AssignmentProcess
     *
     * @param context      can be null
     * @param parentProcessor  can be null
     * @return the generated AssignmentProcess.
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new AssignmentProcessor(this,parentProcessor);}

    /**
     * @return 'assignment' outIndex := inIndex
     */
    public String toString() {
        return "assignment " + outIndex + " := " + inIndex;}

}
