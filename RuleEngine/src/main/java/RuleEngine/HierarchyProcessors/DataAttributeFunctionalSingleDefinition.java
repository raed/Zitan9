package RuleEngine.HierarchyProcessors;

import AbstractObjects.DataObject;
import MISC.Context;
import RuleEngine.AbstractProcessor;

import java.util.function.Predicate;

/** This class adds a processor for accessing attribute values for functional attributes only.
 * <br>
 * It can work in different modes.<br>
 *     In all these modes it reads a concept from the 'conceptIndex' position of the query array.<br>
 *     - It accesses a predefined attribute for this concept.<br>
 *     - It reads an attribute from the 'attributeIndex' position in the query array.<br>
 * It accesses the first attribute value (possibly under a constraint).<br>
 * If the first attribute value is not null, it can yet be filtered by the supplied filter. <br>
 * If outputIndex greater 0 the resulting value is written to the query array.
 * <br>
 * The class applicationName contains 'FunctionalSingle' because only a single attribute value can be accessed.
 */
public class DataAttributeFunctionalSingleDefinition extends AttributeDefinition {
    /** a filter for filtering the attribute value */
    Predicate<DataObject> filter = null;

    public DataAttributeFunctionalSingleDefinition(String id) {
        super(id);}


    /** sets a filter for answer concepts in the query stream.
     *
     * @param filter the filter to be set.
     * @return this
     */
    public DataAttributeFunctionalSingleDefinition setFilter(Predicate<DataObject> filter) {
        this.filter = filter;
        return this;}


    /**
     * @return true if the processing unit acts only as filter.
     */
    public boolean isFilter() {return outputIndex < 1;}

    /** constructs a new DataAttributeFunctionalSingleProcess
     *
     * @param context     the context which contains all the objects
     * @param parentProcessor the rule process which contains this processing unit
     * @return the new DataAttributeFunctionalSingleProcess
     */

    @Override
    public AbstractProcessor makeProcessor(Context context, AbstractProcessor parentProcessor) {
        return new DataAttributeFunctionalSingleProcessor(this,context, parentProcessor);}

    /**
     * @return attribute(fs) attribute-applicationName or attribute index.
     */
    @Override
    public String toString() {
        String s = "attribute(fs) " + getName();
        if(attribute != null) {s += " " + attribute.getName();}
        else {s += " " + Integer.toString(attributeIndex);}
        return s;}
}
