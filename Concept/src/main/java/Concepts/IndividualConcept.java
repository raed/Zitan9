package Concepts;

import Data.DataBlock;
import MISC.Commons;
import MISC.Context;
import Utils.Messanger;

import java.io.Serializable;


/** This class represents leaf nodes in the concept hierarchy.
 * Typical examples are concrete persons as sub-concept of the concept Person.
 *
 * @author ohlbach
 */
public class IndividualConcept extends Concept implements Serializable {

    /** creates a new individual concept and puts it into its context.
     *
     * @param id  the applicationName for the concept.
     * @param context  the context for the concept.
     */
    public IndividualConcept(String id, Context context) {
        super(id,context);}

    /** creates a new individual concept and puts it into its context.
     *
     * @param id            the applicationName for the concept.
     * @param context       the context for the concept.
     * @param superconcepts the new concept's superconcepts.
     */
    public IndividualConcept(String id, Context context, Concept... superconcepts) {
        super(id,context);
        addToSuperconcepts(context,superconcepts);}


    /** checks if the concept is an individual.
     *
     * @return true
     */
    public boolean isIndividual() {return true;}


    /** maps a string to the corresponding concept.
     * If a concept with this string as identifier is already in context.conceptPool then this object is returned.
     * Otherwise a new IndividualConcept object is generated.
     *
     * @param identifier the string to be parsed (actually just the identifier).
     * @param context the context for the concept.
     * @return either an existing object with this identifier, or a new IndividualConcept object.
     */
    public static IndividualConcept parseString(String identifier, Context context) {
        Concept concept = context.getConcept(identifier);
        if(concept == null) {return new IndividualConcept(identifier,context);}
        if(concept.getClass() != IndividualConcept.class) {
            Commons.getMessanger(Messanger.MessangerType.DataErrors).
                    insert("Wrong concept",
                            identifier + " is known, but not an individual");
            return null;}
        return (IndividualConcept)concept;}


    /** This method parses a single line with the information:
     * concept individual_1,...,individual_n
     * concept must be a known set-concept.
     * If the individuals are unknown they are newly generated.
     * The new concept/individual relation is inserted into the concept hierarchy.
     *
     * @param line      the line to be parsed
     * @param namespace the namespace prefix
     * @param context   the context for the concepts
     * @param dataBlock the origin of the data
     * @return          success or fail
     */
    public static boolean parseIndividual(String line, String namespace, Context context, StringBuilder errors, int lineNumber,  DataBlock dataBlock) {
        String[] parts = line.split("\\s*( |,|;|:)\\s*");
        Concept concept = context.getConcept(namespace+parts[0]);
        if(concept == null) {errors.append("Line " + lineNumber + " unknown concept " + parts[0] + "\n"); return false;}
        if(concept.isIndividual()) {errors.append("Line " + lineNumber + " concept " + parts[0] + " is an individual\n"); return false;}
        boolean okay = true;
        for(int i = 1; i < parts.length; ++i) {
            String name = parts[i];
            Concept individual = context.getConcept(namespace+name);
            if(individual == null) {individual = new IndividualConcept(namespace+name,context);}
            else{if(!individual.isIndividual()) {errors.append("Line " + lineNumber + " " + name + " is not an individual\n"); okay = false; continue;}}
            context.conceptHierarchy.addSubnode(concept,individual);}
        return okay;}


}
