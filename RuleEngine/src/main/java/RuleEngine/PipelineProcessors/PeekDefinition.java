package RuleEngine.PipelineProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.Arrays;
import java.util.function.BiConsumer;

/** This class allows one to define a peek processor, i.e. a processor that applies a consumer function
 * to the query array. The consumer may in fact also change the query array.
 */
public class PeekDefinition extends AbstractDefinition {
    /** any object to be supplied to the predicate, for example an identifier string. */
    Object parameter;
    /** the function to be applied to the query array */
    BiConsumer<Object,Object[]> consumer;

    /** creates a PeekDefinition.
     * If the predicate is null, a new one is created that prints the query array to System.out.
     *
     * @param id the identifier for the processor.
     * @param parameter any object, for example an identifier string.
     * @param consumer  the function to be applied to the query-array.
     */
    public PeekDefinition(String id, Object parameter, BiConsumer<Object,Object[]> consumer) {
        super(id);
        this.parameter = parameter;
        if(consumer == null) {
            this.consumer = ((identifier,query) -> {
                if(identifier == null) {System.out.println(Arrays.deepToString(query));}
                else {System.out.println(identifier.toString() + " " + Arrays.deepToString(query));}});}
        else {this.consumer = consumer;}}


    /**
     * @return 'peek' parameter
     */
    public String toString() {
        String s = "peek";
        if(parameter != null) {s += " " + getName() + "(" +parameter.toString() +")";}
        return s;}

    /** creates the corresponding PeekProcessor
     *
     * @param context      can be null
     * @param parentProcessor  can be null
     * @return the generated PeekProcess.
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new PeekProcessor(this,parentProcessor);
    }
}
