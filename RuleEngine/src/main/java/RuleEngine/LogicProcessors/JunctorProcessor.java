package RuleEngine.LogicProcessors;

import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.ArrayList;
import java.util.stream.Stream;

/** This is the processor for the logical connectives.
 */
public class JunctorProcessor extends AbstractProcessor {
    /** the context for the objects */
    private Context context;
    /** the list of processors to be combined by the junctor */
    private ArrayList<AbstractProcessor> subProcessors = new ArrayList<>();
    /** if true then some tests are done in parallel */
    private boolean parallel;

    /** constructs the processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor its parent processor
     */
    public JunctorProcessor(JunctorDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;
        for(AbstractDefinition def : definition.subDefinitions) {
            subProcessors.add(def.makeProcessor(context,this));}}

    /** This method adds a filter to the input stream which calls the sub-processes and connects the results with the junctor.
     * The query array is not changed, but only blocked or passed.
     *
     * @return the filtered input stream.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        JunctorDefinition def = (JunctorDefinition)definition;
        Junctor junctor = def.junctor;
        switch(junctor) {
            case NOT:    outputStream = inputStream.filter(query-> notCondition(query));   break;
            case AND:    outputStream = inputStream.filter(query-> andCondition(query));   break;
            case OR:     outputStream = inputStream.filter(query-> orCondition(query));    break;
            case XOR:    outputStream = inputStream.filter(query-> xorCondition(query));   break;
            case IMPL:   outputStream = inputStream.filter(query-> implCondition(query));  break;
            case EQUIV:  outputStream = inputStream.filter(query-> equivCondition(query)); break;
        }
        return super.getOutputStream();
    }

    private boolean notCondition(Object[] query) {
        AbstractProcessor subProcessor = subProcessors.get(0);
        subProcessor.setInputStream(copyQuery(query));
        return !subProcessor.getOutputStream().findFirst().isPresent();}

    private boolean andCondition(Object[] query) {
        if(parallel) {
            return subProcessors.parallelStream().allMatch(subProcessor -> {
                subProcessor.setInputStream(copyQuery(query));
                return subProcessor.getOutputStream().findFirst().isPresent();});}

        for(AbstractProcessor subProcessor : subProcessors) {
            subProcessor.setInputStream(copyQuery(query));
            if(!subProcessor.getOutputStream().findFirst().isPresent()){return false;}}
        return true;}

    private boolean orCondition(Object[] query) {
        if(parallel) {
            return subProcessors.parallelStream().anyMatch(subProcessor -> {
                subProcessor.setInputStream(copyQuery(query));
                return subProcessor.getOutputStream().findFirst().isPresent();});}

        for(AbstractProcessor subProcessor : subProcessors) {
            subProcessor.setInputStream(copyQuery(query));
            if(subProcessor.getOutputStream().findFirst().isPresent()){return true;}}
        return false;}


    private boolean xorCondition(Object[] query) {
        if(parallel) {
            Object[] result = subProcessors.parallelStream().map(subProcessor -> {
                subProcessor.setInputStream(copyQuery(query));
                return subProcessor.getOutputStream().findFirst().isPresent();}).toArray();
            return !result[0].equals(result[1]);}

        AbstractProcessor subProcessor = subProcessors.get(0);
        subProcessor.setInputStream(copyQuery(query));
        boolean left =  subProcessor.getOutputStream().findFirst().isPresent();
        subProcessor = subProcessors.get(1);
        subProcessor.setInputStream(copyQuery(query));
        boolean right =  subProcessor.getOutputStream().findFirst().isPresent();
        return left != right;}

    private boolean implCondition(Object[] query) {
        AbstractProcessor antecedent = subProcessors.get(0);
        antecedent.setInputStream(copyQuery(query));
        if(!antecedent.getOutputStream().findFirst().isPresent()){return true;}
        AbstractProcessor succedent = subProcessors.get(1);
        succedent.setInputStream(copyQuery(query));
        if(succedent.getOutputStream().findFirst().isPresent()){return true;}
        return false;}

    private boolean equivCondition(Object[] query) {
        AbstractProcessor subProcessor = subProcessors.get(0);
        subProcessor.setInputStream(copyQuery(query));
        boolean result =  subProcessor.getOutputStream().findFirst().isPresent();

        if(parallel) {
            return !subProcessors.subList(1,subProcessors.size()).parallelStream().anyMatch(subProc -> {
                subProc.setInputStream(copyQuery(query));
                return result != subProc.getOutputStream().findFirst().isPresent();});}

        for(int i = 1; i < subProcessors.size(); ++i) {
            subProcessor = subProcessors.get(i);
            subProcessor.setInputStream(copyQuery(query));
            if(result != subProcessor.getOutputStream().findFirst().isPresent()) {return false;}}
        return true;}

    @Override
    public AbstractProcessor setMonitor(boolean monitor) {
        for(AbstractProcessor subProcessor : subProcessors) {subProcessor.setMonitor(monitor);}
        super.setMonitor(monitor);
        return this;}


}
