package RuleEngine.PipelineProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.Arrays;

/** This class defines a processor which transfers a query array to a new query array.
 * It can be used to forward the query array from one processor to a processor with different query array structure.
 * <br>
 * Example: Suppose the query array is [1,2,3,4,5]<br>
 * where 1 is the control item.<br>
 * Suppose the transfer indices are [-1,4,-1,2].<br>
 * This means that the resulting query array has 4 elements.
 * The second position should be filled withe 4th position of the original query array,
 * and the fourth position should be filled with the 2nd position of the original query. <br>
 * The result would be [null, 5, null, 3]
 * <br>
 * It is possible to add some extra array positions, filled with 'null', to the generated query array.
 */
public class TransferDefinition extends AbstractDefinition {
    /** the indices for the transfer of the query array elements. */
    int[] indices;

    /** constructs a transfer definition
     *
     * @param indices the indices for the transfer of the query array elements.
     * @param extras the number of additional positions in the generated query array.
     */
    public TransferDefinition(String id, int[] indices, int extras) {
        super(id);
        assert indices != null;
        assert indices.length >= 0;
        assert extras >= 0;
        this.indices = indices;}


    /**
     * @return the indices and extra as a string.
     */
    @Override
    public String toString() {
        return "transfer " + Arrays.toString(indices);}

    /** This method constructs a new TransferProcessor.
     *
     * @param context     can be null
     * @param parentProcessor can be null
     * @return a new TransferProcessor.
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new TransferProcessor(this,parentProcessor);
    }
}
