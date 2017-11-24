package RuleEngine.HierarchyProcessors;

import AbstractObjects.DataObject;
import MISC.Context;
import RuleEngine.AbstractProcessor;

import java.util.function.Predicate;

/** This class provides a processor which can access relational DataAttributes,
 * i.e. attributes which may have several ConcreteDomain values.
 * The processing unit fetches a concept from the query array,
 * computes the attribute values, filters them and puts the
 *  resulting attribute values into the query stream.
 *    <br>
 *    The following pieces of information can be taken from the query stream: <br>
 *    - the concept for which the attribute values are to be obtained is taken from query[conceptIndex]<br>
 *    - the attribute itself is either predefined, or taken from query[attributeIndex]<br>
 */
public class DataAttributeRelationalDefinition extends AttributeDefinition {
    /** a filter for the attribute values */
    Predicate<DataObject> filter = null;


    /** creates a definition with the only mandatory parameter: the conceptIndex
     *
     * @param id the index in the query array where to get the concept from.
     */
    public DataAttributeRelationalDefinition(String id) {
        super(id);}


    /** sets the filter for the attribute values.
     *
     * @param filter a filter for the attribute values.
     * @return this
     */
    public DataAttributeRelationalDefinition setFilter(Predicate<DataObject> filter) {
        this.filter = filter;
        return this;}


    /** constructs the processor.
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the processor which contains this processing unit
     * @return the processor
     */
    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new DataAttributeRelationalProcessor(this,context,parentProcessor);
    }

    /**
     * @return attribute(r) + applicationName
     */
    @Override
    public String toString() {
        return "attribute(r)" + getName();}
}
