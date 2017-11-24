package RuleEngine.PipelineProcessors;

import RuleEngine.AbstractProcessor;

import java.util.stream.Stream;

/** This is the process for transferring query arrays between rules with different query structure.
 */
public class TransferProcessor extends AbstractProcessor {

    /** constructs a TransferProcess.
     * It just stores the definition's parameters.
     *
     * @param definition the TransferDefinition
     */
    public TransferProcessor(TransferDefinition definition, AbstractProcessor parentProcessor) {
        super(definition,parentProcessor);}


    /** This method adds a map-operation to the input stream.
     * The map-operation generates for a query-array of the input stream a new
     * query array and transfers the contents of the original query-array to the
     * new query-array.
     *
     * @return the input stream with an additional map-operation.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        assert inputStream != null;
        TransferDefinition def = (TransferDefinition)definition;
        int[] indices = def.indices;
        int size = indices.length;
        outputStream =  inputStream.map(query-> {
            Object[] newQuery = new Object[size];
            for(int i = 0; i < size; ++i) {
                int index = indices[i];
                if(index >= 0) {newQuery[i] = query[index];}}
            return newQuery;});
        return super.getOutputStream();
    }



}
