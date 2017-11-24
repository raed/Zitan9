package Concepts;

import MISC.Context;

import java.io.Serializable;

/** This concept type represents sets of individuals.
 * It is subclassed by DerivedConcept which represents filtered sets of individuals.
 * Created by ohlbach on 24.04.2016.
 */
public class SetConcept extends Concept implements Serializable {
    /** creates a new set concept and puts it into its context.
     *
     * @param id  the applicationName for the concept.
     * @param context  the context for the concept.
     */
    public SetConcept(String id, Context context) {
        super(id,context);}

    /** creates a new set concept and puts it into its context.
     *
     * @param id            the applicationName for the concept.
     * @param context       the context for the concept.
     * @param superconcepts the new concept's superconcepts.
     */
    public SetConcept(String id, Context context, Concept... superconcepts) {
        super(id,context);
        addToSuperconcepts(context,superconcepts);}

    /** maps a string to the corresponding concept.
     * If a concept with this string as identifier is already in context.conceptPool then this object is returned.
     * Otherwise a new SetConcept object is generated.
     * *
     * @param identifier the string to be parsed (actually just the identifier).
     * @param context the context for the concept.
     * @param errors for inserting error messages.
     * @return either an existing object with this identifier, or a new SetConcept object.
     */
    public static SetConcept parseString(String identifier, Context context, StringBuilder errors) {
        Concept concept = context.getConcept(identifier);
        if(concept == null) {return new SetConcept(identifier,context);}
        if(concept.getClass() != SetConcept.class) {
            errors.append("Wrong concept: "+ identifier + " is known, but not a a set concept");
            return null;}
        return (SetConcept)concept;}


}
