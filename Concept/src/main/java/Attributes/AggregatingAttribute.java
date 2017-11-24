package Attributes;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Concepts.Concept;
import Concepts.Scope;
import Concepts.SetConcept;
import ConcreteDomain.ConcreteType;
import Data.DataBlock;
import MISC.Context;
import Utils.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;


/** This attribute can aggregate the values of another attribute.
 * Example: the concept peter has an attribute marks, and he has several marks.
 * Now one can define an AggregatingAttribute averageMarks which computes the average of peter's marks.
 * The AggregatingAttribute is specified by providing: <br>
 * - an initial object <br>
 * - an aggregating function that aggregates the values into the initial object<br>
 * - a finalizer that processes the aggregated value in some way.
 * <br>
 * The attribute is automatically functional.
 *
 */
public class AggregatingAttribute extends Attribute implements Serializable {
    private Attribute    attribute         = null;
    private Serializable startObject       = null;
    private String       startObjectString = null;
    private BiFunction<Object,DataObject,Object> aggregator = null;
    private String               aggregatorString = null;
    private Function<Object,DataObject> finalizer = null;
    private String                finalizerString = null;
    private ConcreteType                rangeType = null;


    /** constructs a AggregatingAttribute
     *
     * @param name  the applicationName of the attribute
     * @param context where the attribute is to be integrated
     * @param rangeType the range Type (may be null)
     * @param attribute whose values are to be aggregated
     */
    public AggregatingAttribute(String name, Context context, ConcreteType rangeType, Attribute attribute) {
        super(name,context);
        this.domain      = attribute.domain;
        this.rangeType   = rangeType;
        this.attribute   = attribute;
        functional = true;}

    /** sets the objects which are used to aggregate the values.
     * If the attribute is not to be serialized, no textual representations are necessary, They may be null.
     * If the attribute is to be serialized, there are two possibilities:<br>
     *     1. aggregator and finalizer are Serializable. Then no textual represetnation is necessary.<br>
     *     2. They are not Serializable. The a textual representation which can be compiled by the Groovy compiler need to be provided.
     *
     * @param startObject        the initial object for starting the aggregation
     * @param startObjectString  a textual representation of the initial object.
     * @param aggregator         the aggregator for adding new values to the initial object
     * @param aggregatorString   a textual representation of the aggregator
     * @param finalizer          a function for processing the aggregated value in some way
     * @param finalizerString    a textual representation of the finalizer
     */
    public void setAggregators(Serializable startObject, String startObjectString,
                               BiFunction<Object,DataObject,Object> aggregator, String aggregatorString,
                               Function<Object,DataObject> finalizer, String finalizerString) {
        this.startObject       = startObject;
        this.startObjectString = startObjectString;
        this.aggregator        = aggregator;
        this.aggregatorString  = aggregatorString;
        this.finalizer         = finalizer;
        this.finalizerString   = finalizerString;
    }

    /** @return the range type */
    @Override
    public ConcreteType getRangeType() {return rangeType;}


    /** returns the result of the aggregation (considering constraints if necessary).
     * otherConstraints is either null, an AttributeValueList, or an ArrayList&lt;AttributeValueList&gt;
     * (if the url-Attribute.rel is a ChainAttribute).
     *
     * @param concept     the concept for which the attribute value is accessed.
     * @param scope       the scope for the values to be returned.
     * @param operator    for comparing the url-attribute's values with 'otherValue'
     * @param otherValue  to be compared with the url-attribute's values
     * @param otherConstraints the url-attributes constraints must imply 'otherConstraints'
     * @param context     where the objects live in.
     * @return            result of the aggregation.
     */
    public DataObject getFirst(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context) {
        if(attribute == null) {return null;}
        Stream<DataObject> stream = attribute.stream(concept,scope,operator,otherValue,otherConstraints,context);
        if(stream == null) {return null;}
        Object[] item = new Object[]{startObject};
        stream.forEach(d-> item[0] = aggregator.apply(item[0], d));
        if(item[0] == null) {return null;}
        return finalizer.apply(item[0]);}



    /** returns the aggregated value as stream with one element.
     * otherConstraints is either null, an AttributeValueList, or an ArrayList&lt;AttributeValueList&gt;
     * (if the url-Attribute.rel is a ChainAttribute).
     *
     * @param concept     the concept for which the attribute value is accessed.
     * @param scope       the scope for the values to be returned.
     * @param operator    for comparing the url-attribute's values with 'otherValue'
     * @param otherValue  to be compared with the url-attribute's values
     * @param otherConstraints the url-attributes constraints must imply 'otherConstraints'
     * @param context     where the objects live in.
     * @return            the aggregated value as stream with one element.
     */
    public Stream<DataObject> stream(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context) {
        DataObject item = getFirst(concept,scope,operator,otherValue,otherConstraints,context);
        return (item == null) ? null :  Stream.of(item);}


    private static final String functionDeclaration =
            "AggregatingAttribute <applicationName> <attribute> range = <range> startObject = ... aggregator = ... finalizer = ... ;";

    /** parses an AggregatingAttribute declaration of the form<br>
     *  [attribute] range = [range] startObject = ... aggregator = ... finalizer = ... ";
     *
     * Note that a Groovy parser is used. therefore the code must be Groovy code.
     * One must use the full package paths in the code.
     *
     * @param type must be "AggregatingAttribute"
     * @param name the applicationName of the attribute
     * @param declaration the textual declaration of the attribute.
     * @param context the context into which the string is to be parsed.
     * @param errors for appending error messages
     * @param dataBlock the origin of the declaration
     * @return the parsed attribute or null.
     */
    public static AggregatingAttribute parseString(String type, String name, String declaration, String namespace, Context context, StringBuilder errors, int lineNumber, DataBlock dataBlock)  {
        boolean okay = true;
        if(!type.equals("AggregatingAttribute") || declaration.isEmpty()) {
            errors.append("Line " + lineNumber + " wrong attribute type: " + type + " for attribute " +name +
                    ", should be 'FuncitonAttribute'.\nThe syntax is '" + functionDeclaration+ "\n");
            okay = false;}

        String error = "Line " + lineNumber + " wrong AggregatingAttribute declaration, \nshould be '" + functionDeclaration + "'\n but is: " + declaration;
        String imports = "import AbstractObjects.DataObject;";

        SetConcept domain = null;
        ConcreteType rangeType = null;
        Attribute attribute = null;
        Serializable startObject = null;
        BiFunction<Object,DataObject,Object> aggregator = null;
        Function<Object,DataObject> finalizer = null;
        String startObjectString = null, aggregatorString = null, finalizerString = null;

        HashMap<String,String> map = Utilities.split(declaration,",",
                (key -> key.equals("startObject") ||  key.equals("aggregator") || key.equals("range") ||
                        key.equals("finalizer") || key.equals("range") || context.getAttribute(namespace+key) != null));
        for(String key : map.keySet()) {
            String value = map.get(key);
            if(value != null && value.startsWith("=")) {value = value.substring(1).trim();}
            switch(key) {
                case "range":
                    if(value.isEmpty()) {errors.append("Line " + lineNumber + " attribute " + name + ":  no range specified.\n"); okay = false;}
                    else {rangeType = (ConcreteType)context.getDataType(value);
                        okay &= rangeType != null;}
                    break;
                case "startObject": startObject = (Serializable)Utilities.evaluate(value,errors);
                    startObjectString = value;
                    break;
                case "aggregator":  aggregator = Utilities.compileFunction(imports,value,"Object","aggregated","DataObject","value","Object",errors,lineNumber);
                    aggregatorString = value;
                    break;
                case "finalizer":   finalizer = Utilities.compileFunction(imports,value,"Object","aggregated","DataObject",errors,lineNumber);
                    finalizerString = value;
                    break;
                default: attribute = context.getAttribute(namespace+key);
                    break;}}
        if(startObject == null){errors.append("Line " + lineNumber + " aggregatingAttribute " + name + " no startObject defined"); return null;}
        if(aggregator == null) {errors.append("Line " + lineNumber + " aggregatingAttribute " + name + " no aggregator defined");  return null;}
        if(finalizer == null)  {errors.append("Line " + lineNumber + " aggregatingAttribute " + name + " no finalizer defined");   return null;}
        AggregatingAttribute agg = new AggregatingAttribute(name,context,rangeType,attribute);
        agg.setAggregators(startObject,startObjectString,aggregator,aggregatorString,finalizer,finalizerString);
        return agg;
    }


    /** This method is necessary because must of the instance variables cannot be serialized straightforwardly.
     *
     * @param out an ObjectOutputStream for writing the value.
     * @throws IOException if something goes wrong.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(domain);
        out.writeObject(rangeType == null ? null : rangeType.toString());
        out.writeObject(attribute.getName());
        out.writeObject(startObjectString);
        out.writeObject(startObjectString != null ? null : startObject);
        out.writeObject(aggregatorString);
        out.writeObject(aggregatorString != null ? null : aggregator);
        out.writeObject(finalizerString);
        out.writeObject(finalizerString != null ? null : finalizer);
    }
    /** This method reconstructs the attribute.
     * It ensures that the corresponding objects are properly integrated in Context.currentContext
     *
     * @param in an ObjectInputStream for reading the objects.
     * @throws IOException  if reading the object goes wrong
     * @throws ClassNotFoundException should never be thrown.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        functional = true;
        domain = (SetConcept) in.readObject();
        if (domain != null) {
            SetConcept concept = (SetConcept) Context.currentContext.getConcept(domain.getName());
            if (concept == null) {Context.currentContext.putConcept(domain);}
            else {domain = concept;}}

        String rangeName = (String) in.readObject();
        if (rangeName != null) {rangeType = (ConcreteType)Context.currentContext.getDataType(rangeName);}

        String attributeName = (String)in.readObject();
        attribute = Context.currentContext.getAttribute(attributeName);

        StringBuilder errors = new StringBuilder();

        startObjectString = (String)in.readObject();
        startObject = (Serializable)in.readObject();
        if(startObject == null && startObjectString != null) {
            startObject = (Serializable)Utilities.evaluate(startObjectString,errors);}

        String imports = "import AbstractObjects.DataObject;";
        aggregatorString = (String)in.readObject();
        aggregator = (BiFunction<Object,DataObject,Object>)in.readObject();
        if(aggregator == null && aggregatorString != null) {
            aggregator = Utilities.compileFunction(imports,aggregatorString,"Object","aggregated","DataObject","value","Object",errors,1);}

        finalizerString = (String)in.readObject();
        finalizer = (Function<Object,DataObject>)in.readObject();
        if(finalizer == null && finalizerString != null) {
            finalizer = Utilities.compileFunction(imports,finalizerString,"Object","aggregated","DataObject",errors,1);}

        if(errors.length() != 0) {System.out.println(errors.toString());}
    }

    /** returns a short info string.
     *
     * @return a short info string.
     */
    @Override
    public String infoString() {
        return getName() + ": aggregator for the attribute " + attribute.toString();}

}
