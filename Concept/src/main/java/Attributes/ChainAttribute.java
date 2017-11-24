package Attributes;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Concepts.Concept;
import Concepts.Scope;
import Concepts.SetConcept;
import ConcreteDomain.ConcreteObject;
import ConcreteDomain.ConcreteType;
import Data.DataBlock;
import Graphs.StreamGenerators;
import MISC.Context;
import Utils.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;


/** This class represents chains of attributes.
 * Example: concept: Smith, attributes: friends parents age<br>
 * represents the ages of the parents of Smith's friends.
 *
 */
public class ChainAttribute extends Attribute {

    /** these are the attribute chains */
    private ArrayList<Attribute> attributeChain = null;

    /** the range of the last attribute if it is a concept. */
    private SetConcept rangeConcept = null;

    /** the range of the last attribute, if it is a ConcreteType */
    private ConcreteType rangeType = null;


    /** creates a new attribute chain
     *
     * @param name    the applicationName of the chain.
     * @param context where the objects live in.
     * @param attributeChain the list of attributes in the attribute chain.
     */
    public ChainAttribute(String name, Context context, ArrayList<Attribute> attributeChain) {
        super(name,context);
        this.attributeChain = attributeChain;
        Attribute last = attributeChain.get(attributeChain.size()-1);
        this.domain  = attributeChain.get(0).getDomain();
        rangeConcept = last.getRangeConcept();
        rangeType    = last.getRangeType();
        functional = true;
        for(Attribute attribute : attributeChain) {functional &= attribute.isFunctional();}
    }
    /** creates a new attribute chain
     *
     * @param name    the applicationName of the chain
     * @param context where the objects live in.
     * @param attributes the list of attributes in the attribute chain
     */
    public ChainAttribute(String name, Context context, Attribute... attributes) {
        super(name,context);
        attributeChain = new ArrayList<>();
        for(Attribute attribute : attributes) {attributeChain.add(attribute);}
        Attribute last = attributeChain.get(attributeChain.size()-1);
        this.domain  = attributeChain.get(0).getDomain();
        rangeConcept = last.getRangeConcept();
        rangeType    = last.getRangeType();
        functional = true;
        for(Attribute attribute : attributeChain) {functional &= attribute.isFunctional();}
    }

    /** return the range concept for those attributes mapping to SetConcepts
     *
     * @return the range, or null if it has not been defined*/
    public SetConcept getRangeConcept() {return rangeConcept;}

    /** return the range type for those attributes mapping to ConcreteTypes
     *
     * @return the range, or null if it has not been defined*/
    public ConcreteType getRangeType() {return rangeType;}

    /** returns the attribute chain.
     * All but the last attribute must not be DataAttributes.
     *
     * @return the actual attribute chain
     */
    public ArrayList<Attribute> getAttributeChain()  {return attributeChain;}


    /** gets the first value of the attribute chain for a given concept.
     * If all attributes are functional this is just the first value.
     * If some attributes have mor than one value, the first attribute in a depth-first search is returned.
     *
     * @param concept  the concept in the domain of the attribute chain.
     * @param scope the scope for the value to be returned.
     * @param operator for comparing the returned value with otherValue
     * @param otherValue the value for comparing 'value operator otherValue'
     * @param otherConstraints constraints for the selecting the attribute values, one for each level in the attribute chain.
     * @param context where the objects live in.
     * @return the first non-null value in the chain.
     */
    @Override
    public DataObject getFirst(Concept concept, Scope scope, Operators operator, DataObject otherValue,
                               Object otherConstraints, Context context)   {
        ArrayList<AttributeValueList> constraints = (ArrayList<AttributeValueList>)otherConstraints;
        if(functional) {
            DataObject value = concept;
            for(int level = 0; level < attributeChain.size(); ++level) {
                Attribute attribute = attributeChain.get(level);
                value = ((Attribute)attribute).getFirst((Concept)value,scope,null,null,
                        (constraints == null || level >= constraints.size()) ? null: constraints.get(level),
                        context);
                if(value == null) {return null;}}
            if(operator != null) {
                if(value instanceof Concept) {
                    return ((Concept)value).compare(operator,(Concept)otherValue,context) ? value : null;}
                else {return ((ConcreteObject)value).compare(operator,(ConcreteObject)otherValue) ? value : null;}}
            else {return value;}}
        Stream<DataObject> stream = stream(concept,scope,operator,otherValue,constraints,context);
        if(stream == null) {return null;}
        Optional optional = stream.limit(1).findAny();
        return optional.isPresent() ? (DataObject)optional.get() : null;}


    /** gets a stream of values of the last attribute in the attribute chain.
     *
     * @param concept  the concept in the domain of the attribute chain.
     * @param scope the scope for the value to be returned.
     * @param operator for comparing the returned value with otherValue
     * @param otherValue the value for comparing 'value operator otherValue'
     * @param otherConstraints constraints for the selecting the attribute values, one for each level in the attribute chain.
     * @param context where the objects live in.
     * @return the stream of values of the last attribute in the attribute chain.
     */
    public Stream<DataObject> stream(Concept concept, Scope scope, Operators operator, DataObject otherValue,
                                     Object otherConstraints, Context context) {
        ArrayList<AttributeValueList> constraints = (ArrayList<AttributeValueList>)otherConstraints;
        Stream<DataObject> stream = StreamGenerators.stream(concept, attributeChain.size()-1,
                (concep, level) -> attributeChain.get(level).stream((Concept)concep, scope, null, null,
                        (constraints == null || level >= constraints.size()) ? null: constraints.get(level), context));
        if(operator != null) {
            stream = stream.filter(c->
                    (c instanceof Concept) ?
                            ((Concept)c).compare(operator,(Concept)otherValue,context) :
                            ((ConcreteObject)c).compare(operator,(ConcreteObject)otherValue));}
        return stream;}

    private static final String chainDeclaration = "AttributeChain <applicationName> attribute1,...";

    /** parses a chainAttribute declaration: "ChainAttribute [applicationName] attribute1,...
     *
     * @param type must be "ChainAttribute"
     * @param name the attribute's applicationName
     * @param declaration the attribute declaration
     * @param context the context where the objects live in.
     * @param namespace prefix for the attribute names
     * @param errors for appending error messages
     * @param dataBlock the origin of the data.
     * @return the new AttributeChain or null
     */
    public static ChainAttribute parseString(String type, String name, String declaration, String namespace, Context context, StringBuilder errors, int lineNumber, DataBlock dataBlock)  {
        boolean okay = true;
        if(!type.equals("ChainAttribute") || declaration.isEmpty()) {
            errors.append("Line " + lineNumber + " wrong attribute type: " + type + " for attribute " +name +
                    ", should be 'ChainAttribute'.\n  The syntax is '" + chainDeclaration+ "\n");
            okay = false;}
        String[] parts = declaration.split("\\s*( |,|;)\\s*");
        int length = parts.length;
        ArrayList<Attribute> attributes = new ArrayList();
        for(int i = 0; i < length; ++i) {
            String attributeName = parts[i];
            Attribute attribute = context.getAttribute(namespace+attributeName);
            if(attribute == null) {
                okay = false;
                errors.append("Line " + lineNumber +  " attribute declaration for ChainAttribute: "+ name + ": "+ attributeName+" is unknown.\n"); continue;}
            if(!((attribute instanceof ConceptAttribute) || (i == length-1 && (attribute instanceof DataAttribute)))) {
                okay = false;
                errors.append("Line " + lineNumber + " attribute declaration for ChainAttribute: " + name +
                        ": attribute " + attributeName + " is not a ConceptAttribute or DataAttribute.\n"); continue;}
            attributes.add(attribute);
        }
        if(!okay) {return null;}
        if(checkAttributeChain(name,attributes,errors)) {
            ChainAttribute attribute =  new ChainAttribute(namespace+name,context,attributes);
            return attribute;}
        return null;}

    /** checks the range/domain relations.
     * The range of the i'th attribute must be equal to the domain of the i+1'th attribute
     *
     * @param name the applicationName of the ChainAttrinute
     * @param attributes the list of attributes in the chain
     * @param errors for appending error messages
     * @return true if there is no error.
     */
    private static boolean checkAttributeChain(String name, ArrayList<Attribute> attributes, StringBuilder errors) {
        if(attributes.isEmpty()) {return false;}
        Attribute last = attributes.get(0);
        boolean okay = true;
        for(int i = 1; i < attributes.size(); ++i) {
            Attribute next = attributes.get(i);
            if(last.getRangeConcept() != next.getDomain()) {
                errors.append("Attribute.rel declaration for ChainAttribute: " + name +
                        "  range("+ last.getName() +") != domain("+ next.getName() +"): " + last.getRangeConcept().getName() + " != " + next.getDomain().getName() +".\n");
                okay = false;}
            last = next;}
        return okay;}

    /** This method is necessary because a domain and range can be a concept, and concepts need to be unique in the current context.
     * The attributes in the chain need be unique as well.
     *
     * @param out an ObjectOutputStream for writing the value.
     * @throws IOException if something goes wrong.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(domain);
        out.writeObject(rangeConcept);
        out.writeObject(rangeType == null ? null : rangeType.toString());
        out.writeObject(functional);
        out.writeObject(Utilities.join(attributeChain,",",(a-> a.getName())));
    }
    /** This method reconstructs the attribute.
     * If domain and range are concepts, it ensures that there are no duplicates in the current context.
     * The attributes in the chain are taken from Context.currentConcept.
     *
     * @param in an ObjectInputStream for reading the objects.
     * @throws IOException  if reading the object goes wrong
     * @throws ClassNotFoundException should never be thrown.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        domain     = (SetConcept)in.readObject();
        rangeConcept = (SetConcept)in.readObject();
        String rangeName = (String)in.readObject();
        if(rangeName != null) {rangeType = (ConcreteType)Context.currentContext.getDataType(rangeName);}
        functional = (boolean)in.readObject();
        attributeChain = new ArrayList<>();
        for(String name : ((String)in.readObject()).split(",")) {
            attributeChain.add(Context.currentContext.getAttribute(name));}
        if(domain != null) {
            SetConcept concept = (SetConcept)Context.currentContext.getConcept(domain.getName());
            if(concept == null) {Context.currentContext.putConcept(domain);}
            else {domain = concept;}}}

    /** returns a short description of the chain.
     *
     * @return a short description of the chain.
     */
    @Override
    public String infoString() {
        return getName() + " = " +Utils.Utilities.join(attributeChain, " o ", (a->a.toString()));
    }

}
