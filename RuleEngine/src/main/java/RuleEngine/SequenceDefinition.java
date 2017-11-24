package RuleEngine;

import MISC.Context;
import Utils.Utilities;

import java.util.ArrayList;

/** This class allows one to comprise a sequence of processors into a singel processor
 *
 */
public class SequenceDefinition extends AbstractDefinition {
    /** the sequence of processor definitions */
    ArrayList<AbstractDefinition> definitions = new ArrayList<>();

    /** creates a sequence processor
     *
     * @param id an identifier for it
     */
    public SequenceDefinition(String id) {
        super(id);}

    /** adds a processor definition to the sequence
     *
     * @param definition a processor definition to be added
     * @return this
     */
    public SequenceDefinition addProcessorDefinition(AbstractDefinition... definition) {
        for(AbstractDefinition def : definition) {definitions.add(def);}
        return this;}

    /** creates a sequence processor
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processing unit
     * @return the new processor
     */
    @Override
    public SequenceProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new SequenceProcessor(this, context, parentProcessor);
    }

    /**
     * @return id_1,.., id_n - applicationName
     */
    @Override
    public String toString() {
        return Utilities.join(definitions,",",(def-> def.getName())) + " -> " + getName();
    }
}
