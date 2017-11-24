package RuleEngine.PipelineProcessors;

import RuleEngine.AbstractProcessor;

import java.util.stream.Stream;

/**
 * Created by ohlbach on 05.05.2016.
 */
public class AssignmentProcessor extends AbstractProcessor {

    /** constructs a PeekProcess for the Peek definition.
     *
     * @param definition the peek definition
     */
    public AssignmentProcessor(AssignmentDefinition definition, AbstractProcessor parentProcess) {
        super(definition,parentProcess);}


    /** adds a peek-operation to the query stream which copies a cell into another cell.
     *
     * @return the query stream with an additional peek-operation.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        assert inputStream != null;
        AssignmentDefinition def = (AssignmentDefinition)definition;
        int inIndex  = def.inIndex;
        int outIndex = def.outIndex;
        outputStream = inputStream.peek(query-> query[outIndex] = query[inIndex]);
        return super.getOutputStream();}
}
