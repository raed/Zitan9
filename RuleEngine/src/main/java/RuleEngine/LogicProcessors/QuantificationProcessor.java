package RuleEngine.LogicProcessors;

import ConcreteDomain.AtomicTypes.IntegerObject;
import ConcreteDomain.SetTypes.IntegerInterval;
import MISC.Context;
import RuleEngine.AbstractDefinition;
import RuleEngine.AbstractProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;


/** This is the processor for handling quantifications
 */
public class QuantificationProcessor extends AbstractProcessor {
    private Context context;
    private Quantification quantification;
    private Quantifier quantifier;
    private int minIndex,maxIndex;
    private AbstractProcessor subProcessor;

    /** constructs the processor
     *
     * @param definition its definition
     * @param context its context
     * @param parentProcessor its parent processor
     */
    public QuantificationProcessor(AbstractDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;
        QuantificationDefinition def = (QuantificationDefinition)definition;
        quantification = def.quantification;
        quantifier = quantification.quantifier;
        minIndex = def.minIndex;
        maxIndex = def.maxIndex;
    }

    /** filters or extends the input stream by values defined by the quantified sub-processor
     *
     * @return the filtered or extended input stream.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
        QuantificationDefinition def = (QuantificationDefinition)definition;
        boolean testOnly = def.testOnly;
        boolean sendAll = def.sendAll;
        subProcessor = def.subProcessorDefinition.makeProcessor(context,this);

        if(testOnly) {
            switch(quantifier) {
                case SOME:    outputStream = inputStream.filter(query->testSome(query));    break;
                case ATLEAST: outputStream = inputStream.filter(query->testAtleast(query)); break;
                case ATMOST:  outputStream = inputStream.filter(query->testAtmost(query));  break;
                case EXACT:   outputStream = inputStream.filter(query->testExact(query));   break;
                case RANGE:   outputStream = inputStream.filter(query->testRange(query));   break;}}
        else {
            if(sendAll) {
                switch(quantifier) {
                    case SOME:    outputStream = inputStream.flatMap(query->doAllSome(query));    break;
                    case ATLEAST: outputStream = inputStream.flatMap(query->doAllAtleast(query)); break;
                    case ATMOST:  outputStream = inputStream.flatMap(query->doAllAtmost(query));  break;
                    case EXACT:   outputStream = inputStream.flatMap(query->doExact(query));      break;
                    case RANGE:   outputStream = inputStream.flatMap(query->doAllRange(query));   break;}}
            else {
                switch(quantifier) {
                    case SOME:    outputStream = inputStream.flatMap(query->doMinSome(query));    break;
                    case ATLEAST: outputStream = inputStream.flatMap(query->doMinAtleast(query)); break;
                    case ATMOST:  outputStream = inputStream.flatMap(query->doMinAtmost(query));  break;
                    case EXACT:   outputStream = inputStream.flatMap(query->doExact(query));      break;
                    case RANGE:   outputStream = inputStream.flatMap(query->doMinRange(query));   break;}}
            }
        return super.getOutputStream();
    }


    private boolean testSome(Object[] query) {
        subProcessor.setInputStream(copyQuery(query));
        return subProcessor.getOutputStream().findFirst().isPresent();}

    private Stream<Object[]> doAllSome(Object[] query) {
        subProcessor.setInputStream(queryStream(query));
        return subProcessor.getOutputStream();}

    private Stream<Object[]> doMinSome(Object[] query) {
        subProcessor.setInputStream(queryStream(query));
        return subProcessor.getOutputStream().limit(1);}

    private boolean testAtleast(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(copyQuery(query));
        return subProcessor.getOutputStream().limit(min).count() == min;}

    private Stream<Object[]> doAllAtleast(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(queryStream(query));
        int length = query.length;
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        subProcessor.getOutputStream().forEach(q->result.add(Arrays.copyOf(query,length)));
        if(result.size() >= min) {return result.stream();}
        else {return Stream.empty();}}

    private Stream<Object[]> doMinAtleast(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(queryStream(query));
        int length = query.length;
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        subProcessor.getOutputStream().limit(min).forEach(q->result.add(Arrays.copyOf(query,length)));
        if(result.size() >= min) {return result.stream();}
        else {return Stream.empty();}}

    private boolean testAtmost(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(copyQuery(query));
        return subProcessor.getOutputStream().limit(min+1).count() <= min;}

    private Stream<Object[]> doAllAtmost(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(queryStream(query));
        int length = query.length;
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        subProcessor.getOutputStream().limit(min+1).forEach(q->result.add(Arrays.copyOf(query,length)));
        if(result.size() <= min) {return result.stream();}
        else {return Stream.empty();}}

    private Stream<Object[]> doMinAtmost(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(queryStream(query));
        return subProcessor.getOutputStream().limit(1);}

    private boolean testExact(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(copyQuery(query));
        return subProcessor.getOutputStream().limit(min+1).count() == min;}

    private Stream<Object[]> doExact(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        subProcessor.setInputStream(queryStream(query));
        int length = query.length;
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        subProcessor.getOutputStream().limit(min+1).forEach(q->result.add(Arrays.copyOf(query,length)));
        if(result.size() == min) {return result.stream();}
        else {return Stream.empty();}}

    private boolean testRange(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        int max = getMinMax(query,maxIndex,quantification.max,false);
        subProcessor.setInputStream(copyQuery(query));
        long n = subProcessor.getOutputStream().limit(max+1).count();
        return min <= n && n <= max;}

    private Stream<Object[]> doAllRange(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        int max= getMinMax(query,maxIndex,quantification.max,false);
        subProcessor.setInputStream(queryStream(query));
        int length = query.length;
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        subProcessor.getOutputStream().limit(min+1).forEach(q->result.add(Arrays.copyOf(query,length)));
        int size = result.size();
        if(min <= size && size <= max) {return result.stream();}
        else {return Stream.empty();}}

    private Stream<Object[]> doMinRange(Object[] query) {
        int min = getMinMax(query,minIndex,quantification.min,true);
        int max= getMinMax(query,maxIndex,quantification.max,false);
        subProcessor.setInputStream(queryStream(query));
        int length = query.length;
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        subProcessor.getOutputStream().limit(min+1).forEach(q->result.add(Arrays.copyOf(query,length)));
        int size = result.size();
        if(min <= size && size <= max) {return result.stream().limit(min);}
        else {return Stream.empty();}}

    /** determines the actual min/max values for the numeric quantifiers.
     *
     * @param query the query-array
     * @param index the index where to find the min/max value in the query array (or -1)
     * @param defaultValue the default value taken from the quantification
     * @param min if true then the from value, otherwise the to value is taken from IntegerInterval
     * @return the actual value for the quantification
     */
    private int getMinMax(Object[] query, int index, int defaultValue, boolean min) {
        if(index > 0) {
            Object i = query[index];
            if(i == null) {return defaultValue;}
            if(i instanceof Integer) {return (Integer)i;}
            if(i instanceof IntegerObject) {
                return (Integer)((IntegerObject)i).get();}
            if(i instanceof  ConcreteDomain.SetTypes.IntegerInterval) {
                return min ? ((IntegerInterval)i).from : ((IntegerInterval)i).to;}}
        return defaultValue;}



    @Override
    public AbstractProcessor setMonitor(boolean monitor) {
        subProcessor.setMonitor(monitor);
        super.setMonitor(monitor);
        return this;}
}




    


