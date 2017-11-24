package Attributes;


import AbstractObjects.*;
import Concepts.*;
import ConcreteDomain.ConcreteType;
import DAGs.Direction;
import Data.DataBlock;
import MISC.Context;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/** This is the top class of various attribute types.
 * An Attributes is a function or binary relation which maps concepts to ConcreteTypes or Concepts.
 *
 * Examples: <br>
 * Concept Person, attribute age (to Integer), <br>
 * attribute hasFriend (to Concepts), relational. <br>
 * <br>
 * Attributes may or may not have a domain type and a range type.<br>
 * The domain type is always a Concept (Person, Car, Student,...) etc.<br>
 * The range type may also be a Concept, or it may be a ConcreteType (Integer, String, ...)
 * <br>
 * An attribute is usually created before the domain and range types are created.
 * Therefore an attribute can be created just with an applicationName, and the further information can be added later on.
 * <br>
 * Notice that serialization serializes and deserializes domain and ranges
 */

public abstract class Attribute extends ItemWithId  {

    protected SetConcept domain = null;

    /** indicates that the attribute is functional */
    protected boolean functional = false;

    /** @return true if the attribute is reflexive */
    public boolean isReflexive() {return false;}

    /** @return true if the attribute is symmetric */
    public boolean isSymmetric() {return false;}

    /** @return true if the attribute is transitive */
    public boolean isTransitive() {return false;}

    /** @return true if the attribute is functional */
    public boolean isFunctional() {return functional;}

    public boolean isConceptAttribute() {return false;}


    /** constructs an attribute with a given applicationName.
     *
     * @param name      the applicationName of the attribute
     * @param context where to insert the attribute
     */
    public Attribute(String name, Context context) {
        super(name);
        if(context != null) {context.putAttribute(this);}}

    /** return the domain.
     *
     * @return the domain, or null if it has not been defined
     */
    public SetConcept getDomain() {return domain;}

    /** set the attribute's domain as identifier or concept
     *
     * @param domain the domain's identifier*/
    public void setDomain(SetConcept domain) {
        this.domain = domain;}



    /** return the range concept for those attributes mapping to SetConcepts
     *
     * @return the range, or null if it has not been defined*/
    public SetConcept getRangeConcept() {return null;}

    /** return the range type for those attributes mapping to ConcreteTypes
     *
     * @return the range, or null if it has not been defined*/
    public ConcreteType getRangeType() {return null;}


    /**   @return the inverse of the attribute, or null*/
    public ConceptAttribute getInverse()  {return null;}

    /** checks if the concept is in the attribute's domain
     *
     * @param concept the concept to be checked
     * @param context the context where the objects live in
     * @return true if the concept is in the domain.
     */
    public boolean isInDomain(Concept concept, Context context) {
        Concept domain = getDomain();
        if(domain == null || concept == domain) {return true;}
        return context.conceptHierarchy.isSubnodeOf(concept,domain);}

    /** checks if the value is in the attribute's range.
     * This method checks ConcreteDomain types, not concepts.
     * For concepts the method should be overwritten in corresponding subclasses.
     *
     * @param value   the value to be checked
     * @param context the context where the objects live in
     * @return true if the value is in the attribute's range.
     */
    public boolean isInRange(DataObject value, Context context) {
        ConcreteType type = getRangeType();
        if(type != null) {return ConcreteType.instanceOf(value,type);}
        return true;}

    /** @return the range's string representation, or null. */
    public String getRangeName() {
        ConcreteType type = getRangeType();
        if(type != null) {return type.toString();}
        Concept range = getRangeConcept();
        if(range != null) {return range.getName();}
        return null;}


    /** adds a value to the concept's attribute values
     *
     * @param concept the concept where the value is added.
     * @param value   the value to be added
     * @param constraints the constraints for the value
     * @param scope the scope for the value to be added.
     * @param context the current interpretation (usually the context).
     */
    public boolean addValue(Concept concept, DataObject value, AttributeValueList constraints,
                            Scope scope, Context context, StringBuilder errors) {
        if(!isInRange(value,context)) {
            errors.append("Concept " + concept.getName() + ", attribute " + getName() + ": value " + value.toString() +
                    " is not in its range " + getRangeName()+"\n");
            return false;}

        AttributeValue oldValue = concept.get(this);
        if(oldValue == null) {
            AVSingleton singleton = (constraints == null) ?
                    new AVSingletonUnconstrained(value):
                    new AVSingletonConstrained(value,constraints);
            concept.put(this,new AttributeValue(functional ? singleton : new AVList(singleton), scope));}
        else {
            AVObject avObject = oldValue.get();
            if(functional){((AVSingleton)avObject).setValue(value,constraints);}
            else {
                AVSingleton singleton = (constraints == null) ?
                        new AVSingletonUnconstrained(value):
                        new AVSingletonConstrained(value,constraints);
                ((AVList)avObject).addValue(singleton);}}
        return true;}

    /** returns the very first attribute value that satisfies the constraints (if there are any)
     *
     * @param concept the concept for which the attribute value is accessed.
     * @param scope the scope for the value to be returned
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return the very first attribute value satisfying the constraints, or null if there is none.
     */
    public DataObject getFirst(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context) {
        Stream<DataObject> stream = stream(concept,scope,operator,otherValue,otherConstraints,context);
        if(stream == null) {return null;}
        Optional optional = stream.limit(1).findAny();
        return optional.isPresent() ? (DataObject)optional.get() : null;}

    /** generates a stream of the attribute values for the concept which satisfy the constraints (if there are any).
     *
     * @param concept a concept with some attributes
     * @param scope the scope for the value to be returned
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return a stream of attribute values.
     */
    public abstract Stream<DataObject> stream(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context);


    /** This is the default implementation for the find method.
     * The first non-null function application is returned.
     *
     * @param <T> the return type
     * @param concept the concept for which the value is to be found
     * @param scope the scope for the value to be returned.
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @param function the function to be applied to the attribute values
     * @return         the first non-null function value
     */
    public <T> T find(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints,
                      Context context, Function<DataObject,T> function) {
        Stream<DataObject> stream = stream(concept,scope,operator,otherValue,otherConstraints,context);
        if(stream == null) {return null;}
        Optional optional = stream.map(d->function.apply(d)).filter(d->d != null).limit(1).findAny();
        return optional.isPresent() ? (T)optional.get() : null;}

    /** checks whether 'this' is below 'other' in the attribute hierarchy
     *
     * @param other the other attribute
     * @param context the context where the objects live in
     * @return true if 'this' is below 'other' in the attribute hierarchy
     */
    public boolean isSubset(Attribute other, Context context) {
        return context.attributeHierarchy.isSubnodeOf(this,other);}

    /** checks whether 'this' and 'other' don't have a common subnode in the attribute hierarchy.
     *
     * @param other the other attribute
     * @param context the interpretation where the objects live in
     * @return true if 'this' and 'other' don't have a common subnode in the attribute hierarchy.
     */
    public boolean isDisjoint(Attribute other, Context context) {
        if(this == other) {return false;}
        return !context.attributeHierarchy.hasCommonNode(this,other, Direction.DOWN);}


    /** just returns the applicationName.
     * It should be overwritten in subclasses.
     *
     * @return the applicationName
     */
    public String infoString() {
        String s = getName();
        String range = null;
        SetConcept c = getRangeConcept();
        if(c != null) {range = c.toString();}
        else {ConcreteType t = getRangeType(); if(t != null) {range = t.toString();}}
        if(domain != null && range != null) {s += ": " + domain.toString() + " -> " + range;}
        if(domain != null && range == null) {s += ": " + domain.toString() + " -> top";}
        if(domain == null && range != null) {s += ":" + range;}
        if(domain == null && range != null) {s += ":" + range;}
        return s;}

    /** returns the attribute class with the given applicationName
     *
     * @param name the applicationName of the attribute class
     * @return the corresponding class.
     */
    public static Class getAttributeClass(String name) {
        try{return Class.forName("Attributes."+name);}
        catch(Exception ex) {}
        return null;
    }


    public static Attribute parseAttribute(String string, String namespace, Context context,  StringBuilder errors, int lineNumber, DataBlock dataBlock) {
        String[] parts = string.split("\\s+",3);
        if(parts.length < 3) {errors.append("Line " + lineNumber + " attribute declaration does nut have the form 'attribute type specifification', but " +string+"\n"); return null;}
        String type = parts[0];
        String name = parts[1];
        String declaration = parts[2];
        switch(type.substring(0,2)) {
            case "Da": return DataAttribute.parseString(type,name,declaration,namespace,context,errors,lineNumber,dataBlock);
            case "Co": return ConceptAttribute.parseString(type,name,declaration,namespace,context,errors,lineNumber,dataBlock);
            case "Ch": return ChainAttribute.parseString(type,name,declaration,namespace,context,errors,lineNumber,dataBlock);
            case "Fu": return FunctionAttribute.parseString(type,name,declaration,namespace,context,errors,lineNumber,dataBlock);
            case "Ag": return AggregatingAttribute.parseString(type,name,declaration,namespace,context,errors,lineNumber,dataBlock);
        }
        errors.append("Line " + lineNumber + " unknown attribute type: " + type +
                ",\nshould be one of 'DataAttribute, ConceptAttribute, ChainAttribute, FunctionAttribute, AggregatingAttribute'.\n");
        return null;
    }
    /** This method parses a single line with the information:
     * superattribute subattribute_1, subattribute_2,...
     * All attributes must be known
     * The new super/subattribute relation is inserted into the concept hierarchy.
     * Nothing is changed if some of the concepts are unknown.
     *
     * @param line      the line to be parsed
     * @param namespace the namespace prefix
     * @param context   the context for the concepts
     * @param errors for appending error messages.
     * @param lineNumber in the url file
     * @param dataBlock the origin of the data
     * @return          success or fail
     */
    public static boolean parseAttributeHierarchy(String line, String namespace, Context context,  StringBuilder errors, int lineNumber, DataBlock dataBlock) {
        boolean okay = true;
        String[] parts = line.split("\\s*(:|,|;| )\\s*");
        ArrayList<Attribute> attributes = new ArrayList<>();
        for(String name : parts) {
            Attribute attribute = context.getAttribute(namespace+name);
            if(attribute == null) {errors.append("Line " + lineNumber + " unknown attribute " + name); okay = false;}
            else {attributes.add(attribute);}}
        if(!okay) {return false;}
        if(attributes.size() < 2) {errors.append("Line " + lineNumber +" no hierarchy specified: " + line); return false;}
        Attribute superattribute = attributes.get(0);
        for (int i = 1; i < attributes.size(); ++i) {
            context.attributeHierarchy.addSubnode(superattribute,attributes.get(i));}
        return true;}

}
