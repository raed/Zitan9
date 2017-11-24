package RuleEngine;

import MISC.Context;

import java.util.stream.Stream;

/** This is actually a sequence processor.
 * The output stream of processor i becomes the input stream of processor i+1
 */
public class SequenceProcessor extends AbstractProcessor {
    /** the sequence of processors */
    private AbstractProcessor[] processors = null;

    /** constructs a new sequence processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor the parent processor
     */
    public SequenceProcessor(SequenceDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition,parentProcessor);
        processors = new AbstractProcessor[definition.definitions.size()];
        for(int i = 0; i < definition.definitions.size(); ++i) {
            processors[i] = definition.definitions.get(i).makeProcessor(context,this);
        }
    }

    /** sets the input stream of processor[0] and forwards the output streams of processor i as imput stream of processor i+1
     *
     * @param stream the stream for processor 0
     * @return this
     */
    @Override
    public AbstractProcessor setInputStream(Stream<Object[]> stream) {
        processors[0].setInputStream(stream);
        for(int i = 1; i < processors.length; ++i) {processors[i].setInputStream(processors[i-1].getOutputStream());}
        return this;}

    /** gets the output stream of the last processor in the chain
     *
     * @return the output stream of the last processor in the chain
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        outputStream = processors[processors.length-1].getOutputStream();
        return outputStream;}

    @Override
    public SequenceProcessor setMonitor(boolean monitor) {
        for(AbstractProcessor subProcessor : processors) {subProcessor.setMonitor(monitor);}
        super.setMonitor(monitor);
        return this;}
}
