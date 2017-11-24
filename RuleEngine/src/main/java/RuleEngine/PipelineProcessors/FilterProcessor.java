package RuleEngine.PipelineProcessors;


import RuleEngine.AbstractProcessor;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

/** This is the process that applies a filter to a query array.
 */
public class FilterProcessor extends AbstractProcessor {

    /** constructs a FilterProcess for the FilterDefinition.
     *
     * @param definition the filter definition
     * @param parentProcessor can be null
     */
    public FilterProcessor(FilterDefinition definition, AbstractProcessor parentProcessor) {
        super(definition,parentProcessor);}


    /** adds a filter to the query stream which applies the predicate to the parameter and the query array.
     *
     * @return the query stream with an additional filter.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        assert inputStream != null;
        FilterDefinition def = (FilterDefinition)definition;
        Object                       parameter = def.parameter;
        BiPredicate<Object,Object[]> predicate = def.predicate;
        outputStream =  inputStream.filter(query-> predicate.test(parameter,query));
        return super.getOutputStream();}
}
