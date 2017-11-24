package RuleEngine.HierarchyProcessors;

import Concepts.Concept;
import DAGs.DAG;
import DAGs.Direction;
import Graphs.Strategy;
import MISC.Context;
import RuleEngine.AbstractProcessor;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

/** The instances of this class can actually process concept queries.
 */
public class ConceptProcessor extends AbstractProcessor {
    /** the context for the concepts */
    private Context context;

    /** creates a new ConceptProcessor
     *
     * @param definition its definition
     * @param context    its context
     * @param parentProcessor can be null.
     */
    public ConceptProcessor(ConceptDefinition definition, Context context, AbstractProcessor parentProcessor) {
        super(definition, parentProcessor);
        this.context = context;}

    /** generates the output stream by adding a flatMap to the input stream.
     *
     * @return the new output stream.
     */
    @Override
    public Stream<Object[]> getOutputStream() {
            ConceptDefinition def = (ConceptDefinition)definition;
            Concept concept      = def.concept;
            int inputIndex       = def.inputIndex;
            int outputIndex      = def.outputIndex;
            Direction direction  = def.direction;
            Strategy strategy    = def.strategy;
            boolean individuals  = def.individuals;
            BiPredicate<Concept, Context> filter = def.filter;
            DAG<Concept> conceptHierarchy = context.conceptHierarchy;

        outputStream = inputStream.flatMap(query -> {
            Concept referenceConcept = (inputIndex >= 0) ? (Concept)query[inputIndex] : concept;
            Concept subconcept   = (Concept)query[outputIndex];
            if(subconcept != null) {
                if ((filter == null || filter.test(subconcept,context)) &&
                        conceptHierarchy.isSubnodeOf(subconcept,referenceConcept)) {
                    return queryStream(query);}
                else {return Stream.empty();}}

            if(outputIndex < 0) {return Stream.empty();}

            Stream<Concept> concepts;
            if(individuals) {concepts = conceptHierarchy.leafNodes(referenceConcept);}
            else            {concepts = conceptHierarchy.innerNodes(referenceConcept, true, direction, strategy);}

            if(concepts == null) {return Stream.empty();}

            if(filter != null) {concepts = concepts.filter(concep -> filter.test(concep,context));}
            saveQuery(query);
            return concepts.map(concep -> {
                 restoreQuery(query);
                 query[outputIndex] = concep;
                 return query;});
            });
        return super.getOutputStream();
    }
}
