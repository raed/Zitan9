package Attributes;

import AbstractObjects.DataObject;
import AbstractObjects.ItemWithId;
import AbstractObjects.Operators;
import Concepts.AttributeValue;
import Concepts.Concept;
import Concepts.Scope;
import Concepts.SetConcept;
import Data.DataBlock;
import Graphs.Strategy;
import MISC.Context;
import Graphs.StreamGenerators;
import MISC.Namespace;
import Utils.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/** This attribute type models binary relations between concepts.
 * The relations may be from Concept types to Concept types (e.g. friends : Person to Person),
 * Various properties of relations are supported (reflexivity, symmetry, transitivity),
 * and a mapping to inverse relations.
 *
 */
public class ConceptAttribute extends Attribute implements Serializable {

    SetConcept range;
    private boolean transitive = false;
    private boolean symmetric = false;
    private boolean reflexive = false;

    /** the inverse relation applicationName or relation. */
    private ConceptAttribute inverse;

    public static String[] allowedProperties = new String[]{"reflexive","symmetric","transitive","functional"};

    public static boolean isAllowedProperty(String name) {
        for(String allowedProperty : allowedProperties) {if(name.equals(allowedProperty)) {return true;}}
        return false;}


    /** constructs a new concept attribute which is not reflexive, symmetric nor transitive.
     *
     * @param name   the applicationName of the attribute
     * @param functional declares the attribute functional
     * @param domain   the domain applicationName, domain , or null
     * @param range    the range applicationName, range, or null
     */
    public ConceptAttribute(String name, Context context, boolean functional, SetConcept domain, SetConcept range) {
        super(name,context);
        this.functional = functional;
        this.domain = domain;
        this.range = range;}

    /** constructs a new concept attribute which is not reflexive, symmetric or transitive, and has nor domain and range specification.
     *
     * @param id   the applicationName of the attribute
     * @param functional declares the attribute functional
     */
    public ConceptAttribute(String id,  Context context, boolean functional) {
        super(id,context);
        this.functional = functional;}

    public boolean isConceptAttribute() {return true;}

    /** return the range concept for those attributes mapping to SetConcepts
     *
     * @return the range, or null if it has not been defined*/
    public SetConcept getRangeConcept() {return range;}

    /** makes the relation reflexive and equalizes domain and range */
    public String setReflexive() {
        String error = equalizeDomainAndRange();
        if(error == null) {reflexive = true;}
        if(inverse != null) {inverse.reflexive = true;}
        return error;}

    /** makes the relation symmetric and equalizes domain and range */
    public String setSymmetric() {
        String error = equalizeDomainAndRange();
        if(error == null) {symmetric = true;}
        if(inverse != null) {inverse.symmetric = true;}
        return error;}


    /** makes the relation transitive and equalizes domain and range */
    public String setTransitive() {
        String error = equalizeDomainAndRange();
        if(error == null) {transitive = true;}
        if(inverse != null) {inverse.transitive = true;}
        return error;}

    /** equalizes domain and range */
    private String equalizeDomainAndRange() {
        if(domain != null) {
            if(range == null) {range = domain; return null;}
            if(domain.equals(range)) {return null;}
            return "Attribute.rel " + getName() + ": which is reflexive, symmetric or transitive must have equal domain and range.\n   domain "
                    + domain.toString() + " != range: " + range.toString();}
        if(range != null) {domain = range;}
        return null;}


    /** @return true if the attribute is transitive */
    @Override
    public boolean isTransitive(){return transitive;}

    /** @return true if the attribute is reflexive */
    @Override
    public boolean isReflexive() {return reflexive;}

    /** @return true if the attribute is symmetric */
    @Override
    public boolean isSymmetric() {return symmetric;}


    /** sets the inverse attribute.
     *
     * @param context for mapping names to objects.
     * @param inverse the inverse attribute
     */
    public ConceptAttribute setInverse(Object inverse, Context context, StringBuilder errors)  {
        assert (inverse instanceof String ) || (inverse instanceof ConceptAttribute);
        Attribute attribute = null;
        if(inverse instanceof String) {
            attribute = context.getAttribute((String)inverse);
            if(attribute == null) {attribute =  new ConceptAttribute((String)inverse,context,functional,range,domain);}}
        else {attribute = (Attribute)inverse;}

        if(!(attribute instanceof ConceptAttribute)) {
            errors.append("Inverse attribute: " + inverse + " of " + getName() + "is not a ConceptAttribute, but a " +
                    attribute.getClass().getName());
            return null;}
        this.inverse = (ConceptAttribute)attribute;
        adaptInverse(this.inverse);
        return this.inverse;}

    /** makes the properties of the inverse attribute the same as the properties of 'this'.
     *
     * @param inverse the inverse attribute.
     */
    private void adaptInverse(ConceptAttribute inverse) {
        inverse.domain = range;
        inverse.range = domain;
        inverse.functional = functional;
        inverse.transitive = transitive;
        inverse.reflexive = reflexive;
        inverse.symmetric = symmetric;
        inverse.inverse = this;}

    /** @return the inverse of the attribute, or null
     * */
    @Override
    public ConceptAttribute getInverse() {return inverse;}


    /** checks if the concept is in the attribute's range.
     *
     * @param concept the concept to be checked.
     * @param context the context where the objects live in.
     * @return true if the concept is in the range.
     */
    public boolean isInRange(DataObject concept, Context context) {
        if(!(concept instanceof Concept)) {return false;}
        if(range == null) {return true;}
        return context.conceptHierarchy.isSubnodeOf((Concept)concept,range);}


    /** adds a value to the concept's attribute values
     *
     * @param concept the concept where the value is added.
     * @param value   the value to be added
     * @param constraints the constraints for the value
     * @param scope the scope for the value to be added.
     * @param context the current interpretation (usually the context).
     */
    @Override
    public boolean addValue(Concept concept, DataObject value, AttributeValueList constraints, Scope scope, Context context, StringBuilder errors) {
        if(!super.addValue(concept,value,constraints,scope,context,errors)) {return false;}
        if(symmetric) {if(!super.addValue((Concept)value, concept,constraints,scope,context,errors)) {return false;}}
        if(inverse != null) {
            try{inverse.inverse = null;
                return inverse.addValue((Concept)value, concept,constraints,scope,context,errors);}
            finally{inverse.inverse = this;}}
        return true;}

    /** returns the very first attribute value that satisfies the constraints (if there are any)
     *
     * @param concept the concept for which the attribute value is accessed.
     * @param scope the scope of the value to be returned.
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue'
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return the very first attribute value satisfying the constraints, or null if there is none.
     */
    @Override
    public DataObject getFirst(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context) {
        assert (operator == null) == (otherValue == null);
        if (reflexive && (operator == null || concept.compare(operator, (Concept)otherValue, context))) {return concept;}

        AttributeValue value = concept.get(this);
        if (value == null || value.scope != scope) {return null;}

        if(transitive) {
            Stream<DataObject> stream = stream(concept,scope,operator,otherValue,otherConstraints,context);
            if(stream == null) {return null;}
            Object[] result = stream.limit(1).toArray();
            return (result.length == 0) ? null : (DataObject)result[0];}
        else {return value.getFirst(operator,otherValue,(AttributeValueList)otherConstraints,context);}}


    /** generates a stream of values.
     * <br>
     * Example: <br>
     * Concept Smith, attribute friends<br>
     * friends.stream(Smith,true) would generate a stream of Smith's friends.
     * <br>
     * Only transitivity is handled in this method.
     * Symmetry and Reflexivity must be handled somewhere else.
     *
     * @param concept the concept for which the stream is to be generated.
     * @param scope the scope of the value to be returned.
     * @param operator an operator
     * @param otherValue for comparing: 'this operator otherValue
     * @param otherConstraints for comparing this.constraints implies otherConstraints
     * @param context the context
     * @return a stream of attribute values.
     */
    public Stream<DataObject> stream(Concept concept, Scope scope, Operators operator, DataObject otherValue, Object otherConstraints, Context context) {
        assert (operator == null) == (otherValue == null);
        AttributeValue value = concept.get(this);
        if(value == null || value.scope != scope) {return null;}
        if(transitive) {
            Stream<DataObject> stream =  StreamGenerators.streamForGraph(concept,reflexive, Strategy.BREADTH_FIRST, // anpassen
                    c -> {AttributeValue val = ((Concept)c).get(this);
                        if(val == null || val.scope != scope) {return null;}
                        return val.stream(null,null,(AttributeValueList)otherConstraints,context);});
            if(stream == null) {return null;}
            if(operator != null) {stream = stream.filter(c -> ((Concept)c).compare(operator,(Concept)otherValue,context));}
            return stream;
        }
        return Utilities.streamConcat(
                (reflexive && (operator == null || concept.compare(operator, (Concept)otherValue, context))) ? Stream.of(concept) : null,
                value.stream(operator,otherValue,(AttributeValueList)otherConstraints,context));}


    private static final String conceptAttributeDeclaration =
            "ConceptAttribute <applicationName> reflexive,symmetric,transitive,functional domain = <domain> range = <range> inverse = <inverse>";

    /** Parses a conceptAttribute declaration: reflexive,symmetric,transitive,functional [domain = domain] [range = range] [inverse = inverse];
     * All parameters are optional.
     * Notice that attributes with inverse which are either reflexive, symmetric or transitive must have equal domain and range.
     *
     * @param type must be 'ConceptAttribute'
     * @param name the applicationName of the attribute
     * @param declaration the attribute declaration
     * @param context the context where the objects live in.
     * @param errors for appending error messages
     * @param dataBlock the origin of the definition.
     * @return a new ConceptAttribute or null if an error occurred.
     */
    public static ConceptAttribute parseString(String type, String name, String declaration, String namespace, Context context, StringBuilder errors, int lineNumber, DataBlock dataBlock) {
        boolean okay = true;
        if(!type.equals("ConceptAttribute")) {
            errors.append("Line "+ lineNumber + " wrong attribute type: " + type + " for attribute " +name +
                    ", should be 'ConceptAttribute'.\nThe syntax is" + conceptAttributeDeclaration+ "\n");
            okay = false;}
        String fullname = namespace+name;
        Attribute attr = context.getAttribute(fullname);
        if(attr != null) {errors.append("Line "+ lineNumber + " attribute "+fullname+ " has already been defined.\n"); return null;}

        boolean functional = false;
        boolean reflexive = false;
        boolean symmetric = false;
        boolean transitive = false;
        SetConcept domain = null;
        SetConcept range = null;
        String inverse = null;
        String[] parts = declaration.split("\\s*(,|=| )\\s*");
        int length = parts.length;
        for(int i = 0; i < length; ++i) {
            switch(parts[i]) {
                case "functional": functional = true; break;
                case "reflexive":  reflexive  = true; break;
                case "symmetric":  symmetric  = true; break;
                case "transitive": transitive = true; break;
                case "domain":
                    if(i == length - 1) {errors.append("Line "+ lineNumber + " attribute " + name + ":  no domain specified.\n"); okay = false;}
                    else {domain = SetConcept.parseString(namespace+parts[++i],context,errors);
                        okay &= domain != null;}
                    break;
                case "range":
                    if(i == length - 1) {errors.append("Line "+ lineNumber + " attribute " + name + ":  no range specified.\n"); okay = false;}
                    else {range = SetConcept.parseString(namespace+parts[++i],context,errors);
                        okay &= range != null;}
                    break;
                case "inverse":
                    if(i == length - 1) {errors.append("Line "+ lineNumber + " attribute " + name + ":  no inverse specified.\n"); okay = false;}
                    else {inverse = parts[++i];}
                    break;
                default: errors.append("Line "+ lineNumber + " attribute " + name + ": unknown parameter: "+parts[i]+"\n"); okay = false;}}

        if(reflexive || symmetric || transitive) {
            if(domain != null && range != null && !domain.equals(range)) {
                errors.append("Line "+ lineNumber + " attribute '" + name + "': unequal domain and range: " + domain.getName() + " != " + range.getName());
                okay = false;}}
        if(!okay) {return null;}

        ConceptAttribute attribute = new ConceptAttribute(fullname,context,functional,domain,range);
        if(reflexive) {attribute.setReflexive();}
        if(symmetric) {attribute.setSymmetric();}
        if(transitive) {attribute.setTransitive();}
        if(inverse != null) {attribute.setInverse(inverse,context,errors);}
        return attribute;}

    /** This method is necessary because a domain and range can be a concept, and concepts need to be unique in the current context.
     *
     * @param out an ObjectOutputStream for writing the value.
     * @throws IOException if sonething goes wrong.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(domain);
        out.writeObject(range);
        out.writeObject(transitive);
        out.writeObject(symmetric);
        out.writeObject(reflexive);
        out.writeObject(functional);
        out.writeObject((inverse == null) ? "null" : inverse.getName());}

    /** This method reconstructs the attribute.
     * If domain and range are concepts, it ensures that there are no duplicates in the current context.
     *
     * @param in an ObjectInputStream for reading the objects.
     * @throws IOException  if reading the object goes wrong
     * @throws ClassNotFoundException should never be thrown.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        domain     = (SetConcept)in.readObject();
        range      = (SetConcept)in.readObject();
        transitive = (boolean)in.readObject();
        symmetric  = (boolean)in.readObject();
        reflexive  = (boolean)in.readObject();
        functional = (boolean)in.readObject();
        String inverseName = (String)in.readObject();
        if(domain != null) {
            SetConcept concept = (SetConcept)Context.currentContext.getConcept(domain.getName());
            if(concept == null) {Context.currentContext.putConcept(domain);}
            else {domain = concept;}}
        if(range != null) {
            SetConcept concept = (SetConcept)Context.currentContext.getConcept(range.getName());
            if(concept == null) {Context.currentContext.putConcept(range);}
            else {range = concept;}}
        if(!inverseName.equals("null")) {
            inverse = (ConceptAttribute)Context.currentContext.getAttribute(inverseName);
            if(inverse == null) {setInverse(inverseName,Context.currentContext,null);}}}

    /** Uebrarbearbeiten */
    public static boolean checkSpecification(HashMap<String,ArrayList<String>> specifications, String name, Namespace namespace, Context context, StringBuilder errors) {
        ArrayList<String> domains = specifications.get("domain");
        ArrayList<String> ranges = specifications.get("range");
        String domain = null;
        String range = null;
        boolean okay = true;
        String message = "ConceptAttribute '" + name + "' ";
        if(domains != null) {
            if(domains.size() > 1) {
                errors.append(message + "has more than one domain: " +Utilities.join(domains,",",(l->l))+"\n");
                okay = false;}
            else {domain = domains.get(0);
                //           okay &= ItemWithId.isOfClass(domain,namespace,SetConcept.class,errors);
            }}
        if(ranges != null) {
            if(ranges.size() > 1) {
                errors.append(message +"has more than one range: " +Utilities.join(ranges,",",(l->l))+"\n");
                okay = false;}
            else {range = ranges.get(0);
                //           okay &= ItemWithId.isOfClass(range,namespace,SetConcept.class,errors);
            }}
        ArrayList<String> properties = specifications.get("properties");
        if(properties != null) {
            for(String property : properties) {
                if(!isAllowedProperty(property)) {
                    errors.append(message + ": unknown property: '"+property+".\n");
                    okay = false;}}
            if(domain != null && domain != range) {
                errors.append(message + "domain and range must be equal, and not domain = '" + domain + "', range = '" +range + "'\n");
                okay =false;}}
        ArrayList<String>  inverses = specifications.get("inverse");
        if(inverses != null) {
            if(inverses.size() > 1) {
                errors.append(message + "only one inverse is allowed: " + Utilities.join(inverses,",",(l->l))+"\n");
                okay = false;}
            String inverse = inverses.get(0);
            ItemWithId inv = null; //ItemWithId.getItem(inverse,namespace);
            if(inv != null) {
                errors.append(message + "inverse :'" + inverse + "' is already defined in class '" + inv.getClass().getName()+"'\n");
                okay = false;}}
        return okay;}


    /** @return a short description of the attribute */
    @Override
    public String infoString() {
        String s = super.infoString();
        String f = "";
        if(functional) {f+= "f";}
        if(reflexive)  {f+= "r";}
        if(symmetric)  {f += "s";}
        if(transitive) {f += "t";}
        if(!f.isEmpty()) {s += " ("+f+")";}
        if(inverse != null){ s+= " " + getName() + "^-1 = " + inverse.toString();}
        return s;}
}
