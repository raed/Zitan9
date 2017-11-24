package RuleEngine.PipelineProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.function.BiPredicate;

/** This class allows one to define a filter processor, i.e. a processor that applies a filter to the query stream.
 */
public class FilterDefinition extends AbstractDefinition {
    /** any object to be supplied to the predicate. */
    Object parameter;
    /** the predicate to be applied to the query stream */
    BiPredicate<Object,Object[]> predicate;

    /** creates a FilterDefinition.
     *
     * @param id the identifier for the processor.
     * @param parameter any object, for example an identifier string.
     * @param predicate  the function to be applied to the query-array.
     */
    public FilterDefinition(String id, Object parameter, BiPredicate<Object,Object[]> predicate) {
        super(id);
        this.parameter = parameter;
        this.predicate = predicate;}


    /**
     * @return 'filter' parameter
     */
    public String toString() {
        String s = "filter " + getName();
        if(parameter != null) {s += " (" + parameter.toString()+")";}
        return s;}
    /** creates the corresponding FilterProcess
     *
     * @param context      can be null
     * @param parentProcessor  can be null
     * @return the generated FilterProcess.
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new FilterProcessor(this,parentProcessor);
    }
}
