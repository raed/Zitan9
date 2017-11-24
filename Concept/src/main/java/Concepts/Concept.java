package Concepts;

import AbstractObjects.*;
import Attributes.Attribute;
import Attributes.AttributeValueList;
import ConcreteDomain.ConcreteType;
import DAGs.Direction;
import DAGs.InnerNode;
import DAGs.Node;
import Data.DataBlock;
import Graphs.Strategy;
import MISC.Context;
import DAGs.DAG;
import Utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/** This is an abstract superclass of IndividualConcept and SetConcept.
 * It contains almost all necessary methods for manipulating the concepts.
 *
 */
public abstract class Concept extends ItemWithId implements DataObject, Serializable {

    /** the concept's attributes */
    private final HashMap<Attribute,AttributeValue> attributeValues = new HashMap();

    /** creates a new concept and puts it into its context.
     *
     * @param name  the applicationName for the concept.
     * @param context  the context for the concept.
     */
    public Concept(String name, Context context) {
        super(name);
        if(context != null) {context.putConcept(this);}}

    /** creates a new concept and puts it into its context.
     *
     * @param context  the context for the concept.
     * @param superconcepts a number of superconcepts for this concept.
     */
    public void addToSuperconcepts(Context context, Concept[] superconcepts) {
        for(Concept superconcept : superconcepts) {
            context.conceptHierarchy.addSubnode(superconcept,this);}}

    /** just sets the attribute value, without any checks or inferences
     *
     * @param attribute the attribute
     * @param value     its value
     */
    public void put(Attribute attribute, AttributeValue value) {attributeValues.put(attribute,value);}

    /** returns the AttributeValue object for the given attribute
     *
     * @param attribute an attribute
     * @return the corresponding AttributeValue, or null if there is none.
     */
    public AttributeValue get(Attribute attribute) {return attributeValues.get(attribute);}

    /** adds an unconstrained attribute-value pair to the concept.
     * If the attribute is symmetric then the symmetry partner is added.
     * If there is an inverse attribute then the inverse is also added.
     * Reflexivity and transitivity are not treated here.
     *
     * @param attribute the attribute to be added
     * @param value the value to be added (as DataObject)
     * @param context the context where the objects live in.
     * @param scope the scope of the value to be returned.
     * @return true if there was no error.
     */
    public boolean add(Attribute attribute, DataObject value,  Scope scope, Context context, StringBuilder errors) {
        return attribute.addValue(this,value,null,scope,context,errors);}

    /** adds an constrained attribute-value pair to the concept.
     * If the attribute is symmetric then the symmetry partner is added.
     * If there is an inverse attribute then the inverse is also added.
     * Reflexivity and transitivity are not treated here.
     *
     * @param attribute the attribute to be added
     * @param value the value to be added (as DataObject)
     * @param context the context where the objects live in.
     * @param constraints the contraints on the attribute value
     * @param scope the scope of the value to be added.
     * @return true if there was no error.
     */
    public boolean add(Attribute attribute, DataObject value,  Scope scope, Context context, AttributeValueList constraints, StringBuilder errors) {
        return attribute.addValue(this,value,constraints,scope,context,errors);}

    /** checks if the concept is an individual.
     *
     * @return true if the concept is an individual.
     */
    public boolean isIndividual() {return false;}


    /** compares: 'this operator concept'.
     *
     * @param operator SUBSET, SUPERSET, DISJOINT
     * @param concept the concept to be compared with 'this'
     * @param context where the objects live in
     * @return  teh result of 'this operator concept'
     */
    public Boolean compare(Operators operator, Concept concept, Context context) {
        switch(operator) {
            case SUBSET:   return context.conceptHierarchy.isSubnodeOf(this,concept);
            case SUPERSET: return context.conceptHierarchy.isSubnodeOf(concept,this);
            case DISJOINT: return !context.conceptHierarchy.hasCommonNode(this,concept,Direction.DOWN);
            default: return null;
        }
    }

    /** generates a stream of all individuals below the given concept.
     *
     * @param context the context where the objects live in.
     * @return a stream of all individuals below the given concept, or null if there are none.
     */
    public Stream<Concept> individuals(Context context) {return context.conceptHierarchy.leafNodes(this);}



    /** checks whether this is a subset of other.
     * The method checks whether this is a subnode of 'other' in the concept hierarchy.
     *
     * @param other another concept
     * @param context for mapping names to objects.
     * @return true if this is a subset of the other concept.
     */
    public boolean isSubset(Concept other, Context context) {
        if(other == null) {return false;}
        if(this == other) {return true;}
        return context.conceptHierarchy.isSubnodeOf(this,other);}

    /** checks whether 'this' is disjoint with 'other'
     *
     * @param other the other semantic set
     * @param context for mapping names to objects.
     * @return true if 'this' and 'other' are disjoint.
     */
    public boolean isDisjoint(Concept other, Context context) {
        if(other == null) {return true;}
        if(this == other) {return false;}
        return !context.conceptHierarchy.hasCommonNode(this,other, Direction.DOWN);}


    /** computes a stream of the attribute values without considering any constraints.
     * The stream is a concatenation of the following streams: <br>
     *  1. the concept itself if the attribute is reflexive
     * 2. the concept's LOCAL attribute values of all attributes down the attribute hierarchy <br>
     * 3. the superconcepts ALL attribute values up the concept hierarchy.<br>
     * If all this yields nothing then the search goes up the concept hierarchy.<br>
     * to find the first non-null default values.
     *<br>
     * Notice that the result maybe non-null even if the stream is empty.
     *
     * @param attribute the corresponding attribute.
     * @param context the context where the objects live in.
     * @return  a stream of DataObjects, or null if there are none.
     */
    public Stream<DataObject> stream(Attribute attribute, Context context) {return stream(attribute,null,null,null,context);}

    /** computes a stream of the attribute values while considering the constraints.
     * The stream is a concatenation of the following streams: <br>
     *  1. the concept itself if the attribute is reflexive
     * 2. the concept's LOCAL attribute values of all attributes down the attribute hierarchy <br>
     * 3. the superconcepts ALL attribute values up the concept hierarchy.<br>
     * If all this yields nothing then the search goes up the concept hierarchy.<br>
     * to find the first non-null default values.
     *<br>
     * Notice that the result maybe non-null even if the stream is empty.
     *
     * @param attribute the corresponding attribute.
     * @param context the context where the objects live in.
     * @param otherConstraints a map of attribute - dataObject constraints.
     * @return  a stream of DataObjects, or null if there are none.
     */
    public Stream<DataObject> stream(Attribute attribute, Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context)  {
        Stream<DataObject> stream = streamAttHier(attribute,Scope.LOCAL,operator,otherValue,otherConstraints,context);

        Stream<DataObject>[] dummy = new Stream[]{stream};
        context.conceptHierarchy.applyToInnerLabels(this, Direction.UP, Strategy.BREADTH_FIRST,
                (superconcept -> {dummy[0] =  Utilities.streamsConcat(dummy[0],
                        superconcept.streamAttHier(attribute,Scope.ALL,operator,otherValue,otherConstraints,context));}));
        stream = dummy[0];
        if(stream != null) {return stream;}
        return context.conceptHierarchy.findInInnerLabels(this,Direction.UP,Strategy.BREADTH_FIRST,
                (superconcept -> (superconcept == this) ? null :
                        superconcept.streamAttHier(attribute,Scope.DEFAULT,operator,otherValue,otherConstraints,context)));}

    /** This method goes down the attribute hierarchy and concatenates the streams of
     * all attribute values with the given scope.
     * If the scope is DEFAULT, then only the first non-null stream is returned.
     *
     * @param attribute the start attribute in the attribute hierarchy
     * @param context the context where the objects live in.
     * @param scope     the scope of the attributes
     * @param otherConstraints constraints for the search.
     * @return the concatenated streams of the given scope.
     */
    private Stream<DataObject> streamAttHier(Attribute attribute,Scope scope, Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context)  {
        Stream<DataObject>[] dummy = new Stream[]{attribute.stream(this,scope,operator,otherValue,otherConstraints,context)};
        context.attributeHierarchy.applyToInnerLabels(attribute, Direction.DOWN, Strategy.BREADTH_FIRST,
                (subrelation -> {
                    if(subrelation == attribute) {return;}
                    Stream stream = subrelation.stream(this,scope,operator,otherValue,otherConstraints,context);
                    if(stream != null) {dummy[0] = Utilities.streamsConcat(dummy[0],stream);
                        if(scope == Scope.DEFAULT) {return;}}}));
        return dummy[0];}


    /** returns the first attribute value without considering constraints.
     * The search proceeds as follows:<br>
     *     1. If the attribute is reflexive then just 'this' is returned.<br>
     *     2. the first LOCAL attribute value down the attribute hierarchy is searched.<br>
     *     3. the first ALL attribute value up the concept hierarchy is searched. <br>
     *     4. the first DEFAULT attribute value up the concept hierarchy is searched.
     *
     * @param attribute the attribute for which the value is to be returned.
     * @param context the context where the objects live in.
     * @return the first attribute value.
     */
    public DataObject getFirst(Attribute attribute, Context context)  {return getFirst(attribute,null,null,null,context);}

    /** returns the first attribute value while considering constraints.
     * The search proceeds as follows:<br>
     *     1. If the attribute is reflexive then just 'this' is returned.<br>
     *     2. the first LOCAL attribute value down the attribute hierarchy is searched.<br>
     *     3. the first ALL attribute value up the concept hierarchy is searched. <br>
     *     4. the first DEFAULT attribute value up the concept hierarchy is searched.
     *
     * @param attribute the attribute for which the value is to be returned.
     * @param context the context where the objects live in.
     * @param otherConstraints the constraints for the search.
     * @return the first attribute value.
     */
    public DataObject getFirst(Attribute attribute,  Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context)  {
        DataObject value = getFirstAttHier(attribute,Scope.LOCAL,operator, otherValue, otherConstraints,context);
        if(value != null) {return value;}
        value = context.conceptHierarchy.findInInnerLabels(this, Direction.UP, Strategy.BREADTH_FIRST,
                (superconcept -> superconcept.getFirstAttHier(attribute,Scope.ALL, operator, otherValue, otherConstraints,context)));
        if(value != null ) {return value;}
        value = context.conceptHierarchy.findInInnerLabels(this, Direction.UP, Strategy.BREADTH_FIRST,
                (superconcept -> (superconcept == this) ? null :
                        superconcept.getFirstAttHier(attribute, Scope.DEFAULT,operator, otherValue, otherConstraints,context)));
        return value;}

    /** searches the first attribute value with the given scope, down the attribute hierarchy
     *
     * @param attribute the start attribute in the hierarchy
     * @param context the context where the objects live in.
     * @param scope     the scope of the values to be searched
     * @param otherConstraints the constraints for the search.
     * @return the first non-null attribute value.
     */
    private DataObject getFirstAttHier(Attribute attribute, Scope scope, Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context)  {
        DataObject value = attribute.getFirst(this,scope,operator, otherValue, otherConstraints,context);
        if(value != null) {return value;}
        return context.attributeHierarchy.findInInnerLabels(attribute, Direction.DOWN, Strategy.BREADTH_FIRST,
                (subrelation -> {
                    return (subrelation == attribute) ? null :
                            subrelation.getFirst(this,scope,operator, otherValue, otherConstraints,context);}));}




    /** searches through the concept's attribute values without considering constraints until the first function application returns non-null.
     * The method searches for the non-null function value in the following order:<br>
     * 1. if the attribute is reflexive then the function is applied to 'this'.<br>
     * 2. it goes down the attribute hierarchy and checks the LOCAL attribute values. <br>
     * 3. it goes up the concept Hierarchy and checks the ALL attribute values. <br>
     * 4. it goes up the concept hierarchy and checks the DEFAULT attribute values.
     *
     * @param <T>  the result type of the function
     * @param attribute the attribute whose values are to be checked.
     * @param context the context where the objects live in.
     * @param function a function to be applied to the attribute values.
     * @return the first non-null function application.
     */
    public <T> T find(Attribute attribute,Context context, Function<DataObject,T> function) {
        return find(attribute,null,null,null,context,function);}

    /** searches through the concept's attribute values satisfying the constraints until the first function application returns non-null.
     * The method searches for the non-null function value in the following order:<br>
     * 1. if the attribute is reflexive then the function is applied to 'this'.<br>
     * 2. it goes down the attribute hierarchy and checks the LOCAL attribute values. <br>
     * 3. it goes up the concept Hierarchy and checks the ALL attribute values. <br>
     * 4. it goes up the concept hierarchy and checks the DEFAULT attribute values.
     *
     * @param <T>  the result type of the function
     * @param attribute the attribute whose values are to be checked.
     * @param context the context where the objects live in.
     * @param otherConstraints the filter for the attribute values
     * @param function a function to be applied to the attribute values.
     * @return the first non-null function application.
     */
    public <T> T find(Attribute attribute, Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject,T> function)  {
        T result = findAttHier(attribute,Scope.LOCAL,operator,otherValue,otherConstraints,context,function);
        if(result != null) {return result;}
        result = context.conceptHierarchy.findInInnerLabels(this, Direction.UP, Strategy.BREADTH_FIRST,
                (superconcept -> superconcept.findAttHier(attribute,Scope.ALL,operator,otherValue,otherConstraints,context,function)));
        if(result != null) {return result;}
        return context.conceptHierarchy.findInInnerLabels(this, Direction.UP, Strategy.BREADTH_FIRST,
                (superconcept -> (superconcept == this) ? null :
                        superconcept.findAttHier(attribute,Scope.DEFAULT,operator,otherValue,otherConstraints,context,function)));}

    /** This method goes down the attribute hierarchy to find the first non-null function application.
     *
     * @param attribute the start attribute in the attribute hierarchy
     * @param context the context where the objects live in.
     * @param scope     the scope of the values to be searched
     * @param otherConstraints the constraints for the attribute values
     * @param function the function to be applied to the attribute values.
     * @param <T> the result type of the function.
     * @return the first non-null function application.
     */

    private <T> T findAttHier(Attribute attribute, Scope scope, Operators operator, DataObject otherValue, AttributeValueList otherConstraints, Context context, Function<DataObject,T> function)  {
        T value = attribute.find(this,scope,operator,otherValue,otherConstraints,context,function);
        if(value != null) {return value;}
        return context.attributeHierarchy.findInInnerLabels(attribute, Direction.DOWN, Strategy.BREADTH_FIRST,
                (subrelation ->  (subrelation == attribute) ? null :
                        subrelation.find(this,scope,operator,otherValue,otherConstraints,context,function)));}


    /** repositions the concept if it is affected by one or more of the derived concepts.
     * All derived concepts are taken into account to reposition the concept.
     * It may happen that the concept becomes a sub-concept of one or several derived concepts.
     * It may also happen that a concept which is a sub-concept of a derived concept, but where the
     * corresponding filter no longer returns true, is moved away form the derived concept.
     *
     * @param context the context where the concept lives in.
     * @return true if the concept has actually been repositioned.
     */
    public boolean repositionConcept(Context context) {
        if(context.derivedConcepts == null) {return false;}
        DAG<Concept> hierarchy = context.conceptHierarchy;
        Node node = hierarchy.getNode(this);
        boolean again = true;
        boolean repositioned = false;

        while(again) {
            again = false;
            for(DerivedConcept derivedConcept : context.derivedConcepts) {
                ArrayList<InnerNode<Concept>> subnodes = hierarchy.getInnerNodes(derivedConcept);
                if(subnodes != null && subnodes.contains(node)) {
                    if(derivedConcept.moveUpwards(this,context)) {
                        again = true; repositioned = true;}}}}

        ArrayList<DerivedConcept> checked = new ArrayList<>();
        again = true;

        while(again) {
            again = false;
            for(DerivedConcept derivedConcept : context.derivedConcepts) {
                if(checked.contains(derivedConcept)) {continue;}
                if(derivedConcept.isAffected(this,context)) {
                    derivedConcept.repositionConcept(this,context);
                    checked.add(derivedConcept);
                    repositioned = true;
                    again = true;}}}
        return repositioned;}

    /** generates an info-String with applicationName and attribute-value pairs (without inheritance)
     *
     * @return an info-String with applicationName and attribute-value pairs (without inheritance)
     */
    public String infoString(){
        StringBuilder s = new StringBuilder();
        s.append(getName()).append(":\n");
        attributeValues.forEach((key,value) -> s.append("   ").append(key.getName()).append(" = ").append(value.toString()).append("\n"));
        return s.toString();}


    /** This method parses a single line with the information:
     * superconcept subconcept1, subconcept2,...
     * All concept must be either unknown, or SetConcepts.
     * The new super/subconcept relation is inserted into the concept hierarchy.
     * Nothing is changed if some of the concepts are Individuals.
     *
     * @param line      the line to be parsed
     * @param namespace the namespace prefix
     * @param context   the context for the concepts
     * @param errors for appending error messages.
     * @param dataBlock the origin of the data
     * @return          success or fail
     */
    public static boolean parseConceptHierarchy(String line, String namespace, Context context, StringBuilder errors, int lineNumber, DataBlock dataBlock) {
        String[] parts = line.split("\\s*(:|,|;| )\\s*");
        boolean okay = true;
        Concept superconcept = context.getConcept(namespace+parts[0]);
        if(superconcept != null && superconcept.isIndividual()) {
            errors.append("Line " + lineNumber + " concept " + parts[0] + " is an individual.\n");
            okay = false;}

        int length = parts.length;
        for(int i = 0; i < length; ++i) {
            String name = parts[i];
            Concept subconcept = context.getConcept(namespace+name);
            if(subconcept != null && subconcept.isIndividual()) {
                errors.append("Line " + lineNumber + " subconcept " + name + " of concept " + parts[0] + " is an individual\n");
                okay = false;}}
        if(!okay) {return false;}

        if(superconcept == null) {superconcept = new SetConcept(namespace+parts[0],context);}
        for(int i = 1; i < length; ++i) {
            String name = parts[i];
            Concept subconcept = context.getConcept(namespace+name);
            if(subconcept == null) {subconcept = new SetConcept(namespace+name,context);}
            context.conceptHierarchy.addSubnode(superconcept,subconcept);}
        return okay;
    }


    /** This method parses a single line with the information:
     * 'concept attribute = value, scope = scope if attribute_1 = value_1, attribute_2 = value_2,  ... '
     * The concept and attributes must be a known.
     * The attributes must have a range type.
     *
     * @param line      the line to be parsed
     * @param namespace the namespace prefix
     * @param context   the context for the concepts
     * @param dataBlock the origin of the data
     * @return          success or fail
     */
    public static boolean parseValue(String line, String namespace, Context context, StringBuilder errors, int lineNumber, DataBlock dataBlock) {
        boolean okay =  true;
        Attribute attribute =  null;
        DataObject value = null;
        Scope scope = Scope.LOCAL;
        AttributeValueList avl = null;
        // first we extract the concept applicationName.
        String[] parts = line.split("\\s+",2);
        Concept concept = context.getConcept(namespace+parts[0]);
        if(concept == null)   {okay = false; errors.append("Line " + lineNumber + " unknown concept " + parts[0] + "\n");}
        if(parts.length == 1) {okay = false; errors.append("Line " + lineNumber + " no value specified for concept " + parts[0]+"\n");}
        else {
            parts = parts[1].split("\\s+if\\s+",2); // parts[0] is now the value part, parts[1] is the constraint part.
            HashMap<String,String> assignment = Utilities.split(parts[0],",",(key->(key.equals("scope") || context.getAttribute(key) != null)));
            if(assignment == null || assignment.isEmpty()) {okay = false; errors.append("Line " + lineNumber + " value part '"+ parts[0] + "' does not start with an attribute.\n");}
            else {
                for(String key : assignment.keySet()) {
                    String val = assignment.get(key);
                    if(val.startsWith("=")) {val = val.substring(1).trim();
                        if(key.equals("scope")) {
                            try{scope = Scope.valueOf(val);}
                            catch(Exception ex) {okay = false; errors.append("Line " + lineNumber + " unknown scope " + val+"\n");}}
                        else {attribute = context.getAttribute(key);
                            value = parseAttributeValue(attribute,val,context,errors,lineNumber);
                            okay &= value != null;}}}
                if(attribute == null) {errors.append("Line " + lineNumber + " unknown attribute in " + parts[0]); okay = false;}}

            if(parts.length > 1) { // now we parse the constraint part.
                assignment = Utilities.split(parts[1],",",(key->context.getAttribute(key) != null));
                if(assignment == null) {okay = false; errors.append("Line " + lineNumber + " constraint part '"+ parts[1] + "' does not start with an attribute.\n");}
                else {
                    avl = new AttributeValueList();
                    for(String key : assignment.keySet()) {
                        Attribute att = context.getAttribute(key);
                        DataObject val = parseAttributeValue(att,assignment.get(key),context,errors,lineNumber);
                        if(val == null) {okay = false;}
                        else{avl.add(att,value);}}};}}

        if(okay) {concept.add(attribute,value,scope,context,errors);}
        return okay;}

    /** This method parses a single value. It can be a concept applicationName, or a string representation of a ConcreteDomain value
     *
     * @param attribute the attribute for which the value is to be parsed
     * @param item      the string to be parsed
     * @param context   the context for the objects
     * @param errors    for appending error messages
     * @return          the parsed DataObject, or null.
     */
    private static DataObject parseAttributeValue(Attribute attribute, String item, Context context, StringBuilder errors, int lineNumber) {
        if(item.startsWith("=")) {item = item.substring(1).trim();}
        if(attribute.isConceptAttribute()) {
            Concept value = context.getConcept(item);
            if(value == null) {errors.append("Line "  + lineNumber + " unknown concept " + item +"\n"); return null;}
            Concept range = (Concept)attribute.getRangeConcept();
            if(range != null) {
                if(!context.isSubset(value,range)) {
                    errors.append("Line " + lineNumber + " attribute value " + value.toString() + " is not a subset of the range " + range.toString() + " of the attribute " + attribute.toString()+".\n");
                    return null;}}
            return value;}
        else {
            ConcreteType range = (ConcreteType)attribute.getRangeType();
            if(range == null) {
                errors.append("Line " + lineNumber +  " the range of the attribute " + attribute.toString() + " is undefined.\n");
                return null;}
            String error = range.parseCheck(item,context);
            if(error == null) {return range.parseObject(item,context);}
            else{errors.append(error); return null;}}}
}



