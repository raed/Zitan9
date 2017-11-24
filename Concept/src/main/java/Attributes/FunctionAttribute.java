package Attributes;

import AbstractObjects.Operators;
import Concepts.Concept;
import Concepts.Scope;
import Concepts.SetConcept;
import ConcreteDomain.ConcreteType;
import Data.DataBlock;
import MISC.Context;
import AbstractObjects.DataObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import Utils.Utilities;

/** This class represents attributes which are computed by combining the attribute values of other attributes.
 * The choices for the lambda-expression are: <br>
 * 1. it is a unary function depending on one further attribute <br>
 * 2. it is a binary function depending on two further attributes <br>
 * 3. it is a unary function depending on an array of further attributes. <br>
 *
 * The functions ar applied to the ConcreteDomain Values (e.g. Integer) or to the Concepts, not to the DataObjects
 *
 * @author ohlbach
 */
public class FunctionAttribute extends Attribute implements Serializable {
    private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    private Function<Object,DataObject>          oneFunction = null;
    private BiFunction<Object,Object,DataObject> twoFunction = null;
    private Function<Object[],DataObject>      multiFunction = null;
    private String scriptText = null;

    private ConcreteType rangeType;


    /** constructs a FunctionAttribute
     *
     * @param name  the applicationName of the attribute
     * @param context where the objects live in.
     * @param domain the applicationName of the attribute's domain.
     * @param range the applicationName of the attribute's range type.
     */
    public FunctionAttribute(String name, Context context, SetConcept domain, ConcreteType range) {
        super(name,context);
        functional = true;
        this.domain = domain;
        this.rangeType = range;}

    /** adds a unary function for processing the attribute values of a single attribute.
     * The scriptText is only necessary if the attribute is to be serialized and the function itself is not serializable.
     *
     * @param scriptText  a textual representation for the function.
     * @param oneFunction the function itself
     * @param attribute the attribute whose values are to be processed.
     */
    public void setUnaryFunction(String scriptText, Function<Object,DataObject> oneFunction,Attribute attribute) {
        attributes.add(attribute);
        this.scriptText = scriptText;
        this.oneFunction = oneFunction;}

    /** adds a binary function for processing the attribute values of two attributes.
     * For both attributes a stream is generated and the values of the two streams are one-by-one combined with the function.
     * The scriptText is only necessary if the attribute is to be serialized and the function itself is not serializable.
     *
     * @param scriptText   a textual representation for the function.
     * @param twoFunction the function itself
     * @param attribute1 the first attribute whose values are to be processed.
     * @param attribute2 the second attribute whose values are to be processed.
     */
    public void setBinaryFunction(String scriptText, BiFunction<Object,Object,DataObject> twoFunction,Attribute attribute1,Attribute attribute2) {
        attributes.add(attribute1); attributes.add(attribute2);
        this.scriptText = scriptText;
        this.twoFunction = twoFunction;}

    /** adds a function for processing an array of attribute values for several attributes.
     * For all attributes a stream is generated and the values of the streams are one-by-one combined with the function.
     * The scriptText is only necessary if the attribute is to be serialized and the function itself is not serializable.
     *
     * @param scriptText     a textual representation for the function.
     * @param multiFunction  the function itself
     * @param attributes     the attributes whose values are to be combined.
     */
    public void setMultiFunction(String scriptText, Function<Object[],DataObject> multiFunction, Attribute... attributes) {
        for(Attribute attribute : attributes) {this.attributes.add(attribute);}
        this.scriptText = scriptText;
        this.multiFunction = multiFunction;}

    /** adds a function for processing an array of attribute values for several attributes.
     * For all attributes a stream is generated and the values of the streams are one-by-one combined with the function.
     * The scriptText is only necessary if the attribute is to be serialized and the function itself is not serializable.
     *
     * @param scriptText     a textual representation for the function.
     * @param multiFunction  the function itself
     * @param attributes     the attributes whose values are to be combined.
     */
    public void setMultiFunction(String scriptText, Function<Object[],DataObject> multiFunction, ArrayList<Attribute> attributes) {
        this.attributes = attributes;
        this.scriptText = scriptText;
        this.multiFunction = multiFunction;}


    @Override
    public ConcreteType getRangeType() {return rangeType;}

    /** returns the array of parameters (attributes)
     *
     * @return the list of parameters (attributes) or null if there was an error.
     */
    public ArrayList<Attribute> getAttributes()  {return attributes;}

    /** generates a stream of attribute values for the concept.
     * If only for one attribute the values are to be processed then operator, otherValue and otherConstraints can be
     * used to filter the values of the url-attribute.
     * If two or more attributes are to be combined then 'operator, otherValue and otherConstraints'
     * is used in a uniform way to filter the attribute values of all attributes.
     * This may not always make sense and therefore should be used carefully.
     *
     * @param concept a concept with some attributes
     * @param scope the scope of the value to be returned.
     * @param operator for checking 'this.value operator otherValue'
     * @param otherValue the value to be compared with the operator.
     * @param otherConstraints a map of attribute - dataObject constraints
     * @return a stream of attribute values.
     */
    public Stream<DataObject> stream(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context)  {

        if(oneFunction != null) {
            return attributes.get(0).stream(concept,scope,operator,otherValue,otherConstraints,context).
                    map(item -> oneFunction.apply(item.get())).filter(item -> item != null);}

        if(twoFunction != null) {
            Stream<DataObject> stream1 = attributes.get(0).stream(concept,scope,operator,otherValue,otherConstraints,context);
            Stream<DataObject> stream2 = attributes.get(1).stream(concept,scope,operator,otherValue,otherConstraints,context);
            if(stream1 == null || stream2 == null) {return null;}
            Iterator<DataObject> iterator1 = stream1.iterator();
            Iterator<DataObject> iterator2 = stream2.iterator();
            ArrayList<DataObject> result = new ArrayList<DataObject>();
            while(iterator1.hasNext() && iterator2.hasNext()) {
                DataObject item = twoFunction.apply(iterator1.next().get(),iterator2.next().get());
                if(item != null) {result.add(item);}}
            return result.stream();}

        if(multiFunction != null) {
            int length = attributes.size();
            Iterator<DataObject>[] iterators = new Iterator[length];
            for(int i = 0; i < length; ++i) {
                Stream<DataObject> stream = attributes.get(i).stream(concept,scope,operator,otherValue,otherConstraints,context);
                if(stream == null) {return null;}
                iterators[i] = stream.iterator();}
            Object[] items = new Object[length];
            ArrayList<DataObject> result = new ArrayList<DataObject>();
            boolean again = true;
            do {again = true;
                for(int i = 0; i < length; ++i) {if(!iterators[i].hasNext()) {again = false;}}
                if(again) {
                    for(int i = 0; i < length; ++i) {items[i] = iterators[i].next().get();}
                    DataObject item = multiFunction.apply(items);
                    if(item != null) {result.add(item);}}}
            while(again);
            return result.stream();}
        return null;}


    private static final String functionDeclaration =
            "FunctionAttribute <applicationName> attribute_1 ... attribute_n domain = <domain> range = <range> function = ...;";


    /** parses an FunctionAttribute declaration  'attribute_1 ... attribute_n domain = [domain] range = [range] function = ...;'
     *
     * @param type must be 'FunctionAttribute'
     * @param name the applicationName of the attribute
     * @param declaration the declaration
     * @param namespace a applicationName prefix
     * @param context where the objects reside
     * @param errors for appending error messages
     * @param dataBlock url of the defintion
     * @return a new FunctionAttribute or null
     */
    public static FunctionAttribute parseString(String type, String name, String declaration, String namespace, Context context, StringBuilder errors, int lineNumber,  DataBlock dataBlock)  {
        boolean okay = true;
        if(!type.equals("FunctionAttribute") || declaration.isEmpty()) {
            errors.append("Line " + lineNumber + " wrong attribute type: " + type + " for attribute " +name +
                    "\n   The syntax is '" + functionDeclaration+ "\n");
            okay = false;}

        ArrayList<Attribute> attributes = new ArrayList<>();
        for(String part : declaration.split("\\s*( |,)\\s*")) {
            if(part.equals("domain") || part.equals("range") || part.equals("function")) {break;}
            Attribute attribute = context.getAttribute(namespace+part);
            if(attribute != null) {attributes.add(attribute);}
            else {errors.append("Line " + lineNumber + "FunctionAttribute " + name + ": unknown attribute " + part); okay = false;}
        }
        if(attributes.isEmpty()) {okay = false;}
        String fullname = namespace+name;
        SetConcept domain = null;
        ConcreteType rangeType = null;
        FunctionAttribute attribute = null;

        HashMap<String,String> parts = Utilities.split(declaration,",",
                (key -> key.equals("function") || key.equals("domain") || key.equals("range") || context.getAttribute(namespace+key) != null));
        for(String part : parts.keySet()) {
            String value = parts.get(part);
            if(value.startsWith("=")) {value = value.substring(1).trim();}
            switch(part) {
                case "domain":
                    if(value.isEmpty()) {errors.append("Line " + lineNumber + " attribute " + name + ":  no domain specified.\n"); okay = false;}
                    else {domain = SetConcept.parseString(namespace+value,context,errors);
                        okay &= domain != null;}
                    break;
                case "range":
                    if(value.isEmpty()) {errors.append("Line " + lineNumber + " attribute " + name + ":  no range specified.\n"); okay = false;}
                    else {rangeType = (ConcreteType)context.getDataType(value);
                        okay &= rangeType != null;}
                    break;}}

        String scriptText = parts.get("function");
        if(scriptText == null) {errors.append("Line " + lineNumber + " attribute " + name + " has no script text\n"); okay = false;}
        else {
            if(scriptText.startsWith("=")) {scriptText = scriptText.substring(1).trim();}
            switch(attributes.size()) {
                case 0: break;
                case 1:
                    Function function = Utilities.compileFunction("import AbstractObjects.DataObject;",scriptText,"Object", attributes.get(0).getName(),"DataObject",errors,lineNumber);
                    if(okay & function != null) {
                        attribute =  new FunctionAttribute(fullname,context,domain,rangeType);
                        attribute.setUnaryFunction(scriptText,function,attributes.get(0));}
                    break;
                case 2:
                    String parameter1 = attributes.get(0).getName();
                    String parameter2 = attributes.get(1).getName();
                    BiFunction bifunction = Utilities.compileFunction("import AbstractObjects.DataObject;",scriptText,"Object",parameter1,"Object",parameter2,"DataObject",errors,lineNumber);
                    if(okay && bifunction != null) {
                        attribute =  new FunctionAttribute(fullname,context,domain,rangeType);
                        attribute.setBinaryFunction(scriptText,bifunction,attributes.get(0),attributes.get(1));}
                    break;
                default:
                    Function<Object[],DataObject> lambda3;
                    String assignments = "";
                    String[] parameters = new String[attributes.size()];
                    for(int i = 0; i < attributes.size();++i) {
                        parameters[i] = attributes.get(i).getName();
                        assignments += "Object " + parameters[i] + " = parameters["+i+"];\n";}
                    function = Utilities.compileFunction("import AbstractObjects.DataObject;",assignments + scriptText,"Object[]","parameters","DataObject",errors,lineNumber);
                    if(okay & function != null) {
                        attribute =  new FunctionAttribute(fullname,context,domain,rangeType);
                        attribute.setMultiFunction(scriptText,function,attributes);}
                    break;}}
        return attribute;}

    /** This method is necessary because a domain and range can be a concept, and concepts need to be unique in the current context.
     * The attributes in the chain need be unique as well.
     *
     * @param out an ObjectOutputStream for writing the value.
     * @throws IOException if something goes wrong.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(domain);
        out.writeObject(rangeType == null ? null : rangeType.toString());
        out.writeObject(functional);
        out.writeObject(Utilities.join(attributes,",",(a-> a.getName())));
        boolean written = false;
        if(oneFunction != null)   {out.writeObject(1); if(oneFunction   instanceof Serializable) {out.writeObject(oneFunction);  written = true;}}
        if(twoFunction != null)   {out.writeObject(2); if(twoFunction   instanceof Serializable) {out.writeObject(twoFunction);  written = true;}}
        if(multiFunction != null) {out.writeObject(3); if(multiFunction instanceof Serializable) {out.writeObject(multiFunction);written = true;}}
        if(!written){out.writeObject(scriptText);}}


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
        String rangeName = (String)in.readObject();
        if(rangeName != null) {rangeType = (ConcreteType)Context.currentContext.getDataType(rangeName);}
        functional = (boolean)in.readObject();
        attributes = new ArrayList<>();
        for(String name : ((String)in.readObject()).split(",")) {
            attributes.add(Context.currentContext.getAttribute(name));}
        if(domain != null) {
            SetConcept concept = (SetConcept)Context.currentContext.getConcept(domain.getName());
            if(concept == null) {Context.currentContext.putConcept(domain);}
            else {domain = concept;}}
        int n = (int)in.readObject();
        Object item = in.readObject();
        if(item == null) {return;}
        if(!(item instanceof String)) {
            switch(n) {
                case 1: oneFunction   = (Function<Object,DataObject> & Serializable)item;          break;
                case 2: twoFunction   = (BiFunction<Object,Object,DataObject> & Serializable)item; break;
                case 3: multiFunction = (Function<Object[],DataObject>& Serializable)item;         break;
            }}
        else {
            scriptText = (String)item;
            StringBuilder errors = new StringBuilder();
            switch(n) {
                case 1: oneFunction = Utilities.compileFunction("import AbstractObjects.DataObject;",scriptText,"Object", attributes.get(0).getName(),"DataObject",errors,1);
                    break;
                case 2:
                    String parameter1 = attributes.get(0).getName();
                    String parameter2 = attributes.get(1).getName();
                    twoFunction = Utilities.compileFunction("import AbstractObjects.DataObject;",scriptText,"Object",parameter1,"Object",parameter2,"DataObject",errors,1);
                    break;
                case 3:
                    String assignments = "";
                    String[] parameters = new String[attributes.size()];
                    for(int i = 0; i < attributes.size();++i) {
                        parameters[i] = attributes.get(i).getName();
                        assignments += "Object " + parameters[i] + " = parameters["+i+"];\n";}
                    multiFunction = Utilities.compileFunction("import AbstractObjects.DataObject;",assignments + scriptText,"Object[]","parameters","DataObject",errors,1);
            }}}



    @Override
    public String infoString() {
        StringBuilder s = new StringBuilder();
        s.append(getName()).append(" function(");
        if(attributes != null) {
            for(int i = 0; i < attributes.size(); ++i) {
                s.append(attributes.get(i).toString()).append(",");}
            s.deleteCharAt(s.length()-1);}
        s.append(")");
        if(scriptText != null){s.append("\n").append(scriptText);}
        return s.toString();}



}
