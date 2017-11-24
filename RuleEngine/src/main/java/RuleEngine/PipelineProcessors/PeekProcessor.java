package RuleEngine.PipelineProcessors;

import RuleEngine.AbstractProcessor;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

/** This is the processor that applies a consumer function to a query array.
 */
public class PeekProcessor extends AbstractProcessor {

    /** constructs a PeekProcess for the Peek definition.
     *
     * @param definition the peek definition
     * @param parentProcessor not needed
     */
    public PeekProcessor(PeekDefinition definition, AbstractProcessor parentProcessor) {
        super(definition,parentProcessor);}


    /** adds a peek-operation to the query stream which applies the consumer function to the parameter and the query array.
     *
     * @return the query stream with an additional peek-operation.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        assert inputStream != null;
        PeekDefinition def = (PeekDefinition)definition;
        Object                     parameter = def.parameter;
        BiConsumer<Object,Object[]> consumer = def.consumer;
        outputStream = inputStream.peek(query->consumer.accept(parameter,query));
        return super.getOutputStream();}
}
