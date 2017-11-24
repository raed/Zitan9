package MISC;

import AbstractObjects.DataType;
import AbstractObjects.StringInterpretation;
import Attributes.Attribute;
import Concepts.Concept;
import Concepts.DerivedConcept;
import ConcreteDomain.AtomicTypes.ConstantObject;
import DAGs.DAG;
import DAGs.Direction;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.util.ArrayList;


/** A context is just a storage for all relevant objects,
 * in particular all concepts, attributes, hierarchies concrete domain types.
 *
 */
public class Context extends StringInterpretation<ConstantObject, Concept, Attribute, DataType> {
    public String id;
    public final DAG<Attribute> attributeHierarchy  = new DAG<Attribute>("RelationHierarchy",(attribute->false));
    public final DAG<Concept>   conceptHierarchy    = new DAG<Concept>("Concepts",(concept->concept.isIndividual()));
    public ArrayList<DerivedConcept> derivedConcepts = null;
    public static Context currentContext = null;
    public String keyspace = "RQLJ";
    public Cluster cluster = null;
    public Session session;
    private String database = "127.0.0.1";

    /** creates a context and initialises the concrete domain types.
     *
     * @param id an identifier for the context.
     */
    public Context(String id) {
        this.id = id;
        currentContext = this;
        createConcreteDomains();
        databaseConnect();
    }


    private boolean databaseConnect() {
        /*
        try{cluster = Cluster.builder().addContactPoint(database).build();}
        catch(Exception ex) {ex.printStackTrace(); return false;}
        session = cluster.connect();
        session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace + " WITH REPLICATION = {'class':'SimpleStrategy','replication_factor':3}");
        session.execute("USE " + keyspace);*/
        return true;
    }


    /** creates all concrete domain types.
     * This method should be updated if a new concrete domain type is added.
     */
    private void createConcreteDomains() {
        DataType.initializeDatatypes(this);}

    public void setCurrentContext(Context context) {
        currentContext = context;}

    /** adds a concept with a given applicationName to the interpretation.
     *
     * @param concept the concept itself.
     */
    public synchronized void putConcept(Concept concept) {
        putConcept(concept.getName(),concept);
        if(concept instanceof DerivedConcept) {
            if(derivedConcepts == null) {derivedConcepts = new ArrayList<>();}
            derivedConcepts.add((DerivedConcept)concept);}}


    /** puts the attribute into the context.
     *
     * @param attribute the attribute to be put into the context.
     */
    public void putAttribute(Attribute attribute) {
        putAttribute(attribute.getName(),attribute);}


    /** checks if the first attribute is below the second attribute in the attribute hierarchy.
     *
     * @param attribute1 the first attribute
     * @param attribute2 the second attribute
     * @return true if the first attribute is below the second attribute in the attribute hierarchy.
     */
    public boolean isSubset(Attribute attribute1, Attribute attribute2) {
        return attributeHierarchy.isSubnodeOf(attribute1,attribute2);}

    /** checks if the first concept is below the second concept in the concept hierarchy.
     *
     * @param concept1 the first concept
     * @param concept2 the second concept
     * @return true if the first concept is below the second concept in the concept hierarchy.
     */
    public boolean isSubset(Concept concept1, Concept concept2) {
        return conceptHierarchy.isSubnodeOf(concept1, concept2);}

    /** checks whether the two attributes have a common sub-attribute in the attribute hierarchy
     *
     * @param attribute1 the first attribute
     * @param attribute2 the second attribute
     * @return true if the two attributes have no common sub/attribute.
     */
    public boolean isDisjoint(Attribute attribute1, Attribute attribute2) {
        return !attributeHierarchy.hasCommonNode(attribute1,attribute2, Direction.DOWN);}

    /** checks if the two concepts have a common sub-concept
     *
     * @param concept1 the first concept
     * @param concept2 the second concept
     * @return true if the two concepts have no common sub-concept.
     */
    public boolean isDisjoint(Concept concept1, Concept concept2) {
        return !conceptHierarchy.hasCommonNode(concept1, concept2,Direction.DOWN);}


    /** just returns the applicationName.
     *
     * @return the applicationName.
     */
    @Override
    public String toString() {return "Context " + id;}


}
