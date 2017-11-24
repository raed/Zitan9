package Attributes;

import AbstractObjects.Operators;
import Concepts.AttributeValue;
import Concepts.Concept;
import Concepts.Scope;
import Concepts.SetConcept;
import ConcreteDomain.ConcreteType;
import Data.DataBlock;
import MISC.Context;
import AbstractObjects.DataObject;
import Utils.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *  This class represents attributes which map concepts to concrete data types.
 */
public class DataAttribute extends Attribute implements Serializable {

    private ConcreteType range;


    /** constructs a data attribute.
     *
     * @param id      The applicationName of the attribute
     * @param functional declares the attribute functional
     * @param domain   the domain (identifier or concept, or null)
     * @param range    the type of the range (identifier, concept, ConcreteType or null)
     */
    public DataAttribute(String id, Context context, boolean functional, SetConcept domain, ConcreteType range) {
        super(id,context);
        this.functional = functional;
        this.domain = domain;
        this.range = range;}


    /** set the attribute's range as concept, identifier of ConcreteType
     *
     * @param range the concept*/
    public void setRange(ConcreteType range) {this.range = range;}


    public ConcreteType getRangeType(){return range;}

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
    @Override
    public DataObject getFirst(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context)  {
        AttributeValue value = concept.get(this);
        return (value == null || value.scope != scope) ? null : value.getFirst(operator,otherValue,(AttributeValueList)otherConstraints,context);}


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
    public Stream<DataObject> stream(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context){
        AttributeValue value = concept.get(this);
        return (value == null || value.scope != scope) ? null : value.stream(operator,otherValue,(AttributeValueList)otherConstraints,context);}


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
    @Override
    public <T> T find(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context, Function<DataObject,T> function) {
        AttributeValue value = concept.get(this);
        return (value == null || value.scope != scope) ? null : value.find(operator,otherValue,(AttributeValueList)otherConstraints,context,function);}



    private static final String dataAttributeDeclaration =
            "DataAttribute <applicationName> <functional> domain = <domain> range = <range>;";

    /** parses a dataAttribute declaration: DataAttribute [applicationName] [functional] domain = [domainId] range = [range];
     * All components except the applicationName are optional.
     *
     * @param type must be "DataAttribute"
     * @param name the applicationName of the attribute.
     * @param declaration the attribute declaration
     * @param namespace the namespace for the attribute applicationName.
     * @param context the context where the objects live in
     * @param errors for appending error messages.
     * @return a new DataAttribute or null
     */
    public static DataAttribute parseString(String type, String name, String declaration, String namespace, Context context, StringBuilder errors, int lineNumber, DataBlock dataBlock)  {
        boolean okay = true;
        if(!type.equals("DataAttribute")) {
            errors.append("Line " + lineNumber + " wrong attribute type: " + type + " for attribute " +name +
                    ", should be 'ConceptAttribute'.\nThe syntax is" + dataAttributeDeclaration+ "\n");
            okay = false;}
        boolean functional = false;
        SetConcept domain = null;
        ConcreteType range = null;
        HashMap<String,String> parts = Utilities.split(declaration,",",
                (key -> key.equals("functional") || key.equals("domain") || key.equals("range")));
        for(String part : parts.keySet()) {
            String value = parts.get(part);
            if(value.startsWith("=")) {value = value.substring(1).trim();}
            switch(part) {
                case "functional": functional = true; break;
                case "domain":
                    if(value.isEmpty()) {errors.append("Line " + lineNumber + " attribute " + name + ":  no domain specified.\n"); okay = false;}
                    else {domain = SetConcept.parseString(namespace+value,context,errors);
                        okay &= domain != null;}
                    break;
                case "range":
                    if(value.isEmpty()) {errors.append("Line " + lineNumber + " attribute " + name + ":  no range specified.\n"); okay = false;}
                    else {range = (ConcreteType)context.getDataType(value);
                        okay &= range != null;}
                    break;}}
        return okay ? new DataAttribute(name,context,functional,domain,range) : null;}



    /** This method is necessary because a domain and range can be a concept, and concepts need to be unique in the current context.
     *
     * @param out an ObjectOutputStream for writing the value.
     * @throws IOException if something goes wrong.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(domain);
        out.writeObject(range == null ? null : range.toString());
        out.writeObject(functional);}

    /** This method reconstructs the attribute.
     * If domain and range are concepts, it ensures that there are no duplicates in the current context.
     *
     * @param in an ObjectInputStream for reading the objects.
     * @throws IOException  if reading the object goes wrong
     * @throws ClassNotFoundException should never be thrown.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        domain     = (SetConcept)in.readObject();
        String rangeName = (String)in.readObject();
        if(rangeName != null) {range = (ConcreteType)Context.currentContext.getDataType(rangeName);}

        functional = (boolean)in.readObject();
        if(domain != null) {
            SetConcept concept = (SetConcept)Context.currentContext.getConcept(domain.getName());
            if(concept == null) {Context.currentContext.putConcept(domain);}
            else {domain = concept;}}}


    /** @return a short description of the attribute */
    @Override
    public String infoString() {
        String s = super.infoString();
        String f = "";
        if(functional) {f+= "f";}
        if(!f.isEmpty()) {s += " ("+f+")";}
        return s;}
}
