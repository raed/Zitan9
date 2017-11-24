package RuleEngine;

import AbstractObjects.ItemWithId;
import MISC.Context;

/** This is the top-class for all Processor definitions.
 * SO far is mainly used as type for the processor definition types.
 */
public abstract class AbstractDefinition extends ItemWithId {

    /** constructs a processor definition and gives it an identifier.
     * In the current version of the rule engine the parameter parentProcessor in the
     * makeProcessor method is not used. This may change in future versions.
     *
     * @param id the identifier for the definition.
     */
    public AbstractDefinition(String id) {
        super(id);}

    /** The method constructs a new processing unit.
     *  @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processor
     */
    public abstract AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor);

}
