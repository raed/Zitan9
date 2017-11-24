package Attributes;

import AbstractObjects.DataObject;
import AbstractObjects.Operators;
import Concepts.Concept;
import ConcreteDomain.ConcreteObject;
import MISC.Context;
import Utils.Utilities;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


/** This is a list of Attribute-DataObject-Operator triples.
 * In particular, one can use this list to check for two lists
 * whether the first list implies the other list.
 */
public class AttributeValueList implements Serializable {
    /** the keys */
    private ArrayList<Attribute> attributes = new ArrayList<>();
    /** the values */
    private ArrayList<DataObject> dataObjects = new ArrayList<>();
    /** the operators */
    private ArrayList<Operators> operators = new ArrayList<>();

    /** indicates that the only operator is equality */
    private boolean equalityOnly = true;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(Utilities.join(attributes,",",(a-> a.getName())));
        out.writeObject(dataObjects);
        out.writeObject(operators);}



    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        attributes = new ArrayList<Attribute>();
        for(String name : ((String)in.readObject()).split(",")) {
            attributes.add(Context.currentContext.getAttribute(name));}
        dataObjects = (ArrayList<DataObject>)in.readObject();
        operators = (ArrayList<Operators>)in.readObject();
        for(int i = 0; i < dataObjects.size(); ++i) {
            DataObject object = dataObjects.get(i);
            if(object instanceof Concept) {
                Concept concept = Context.currentContext.getConcept(((Concept) object).getName());
                if(concept == null) {Context.currentContext.putConcept((Concept)object);}
                else {dataObjects.set(i,concept);}}}}

    /** creates an empty list */
    public AttributeValueList() {}

    /** constructs a list with a single attribute-value pair.
     * The operator is EQUALS
     *
     * @param attribute the attribute
     * @param dataObject the value
     */
    public AttributeValueList(Attribute attribute, DataObject dataObject) {
        attributes.add(attribute);
        dataObjects.add(dataObject);
        operators.add(Operators.EQUALS);}

    /** constructs a list with a single attribute-value-operator triple.
     *
     * @param attribute the attribute
     * @param dataObject the value
     * @param operator the operator
     */
    public AttributeValueList(Attribute attribute,  Operators operator, DataObject dataObject) {
        attributes.add(attribute);
        dataObjects.add(dataObject);
        operators.add(operator);}

    /** clears the list.*/
    public void clear() {
        attributes.clear();
        dataObjects.clear();
        operators.clear();}

    /** adds a new attribute-value pair.
     * The operator is EQUALS
     *
     * @param attribute   the attribute
     * @param dataObject the value
     * @return  the list itself
     */
    public AttributeValueList add(Attribute attribute, DataObject dataObject) {
        attributes.add(attribute);
        dataObjects.add(dataObject);
        operators.add(Operators.EQUALS);
        return this;}

    /** adds a new attribute-value-operator triple.
     *
     * @param attribute   the attribute
     * @param dataObject the value
     * @param operator the operator
     * @return  the list itself
     */
    public AttributeValueList add(Attribute attribute, Operators operator, DataObject dataObject) {
        attributes.add(attribute);
        dataObjects.add(dataObject);
        operators.add(operator);
        equalityOnly &= (operator == Operators.EQUALS);
        return this;}

    /** gets the value for the attribute.
     *
     * @param attribute a attribute for accessing the corresponding value.
     * @return the value for the given attribute.
     * */
    public DataObject getValue(Attribute attribute) {
        int index = attributes.indexOf(attribute);
        if(index >= 0) {return dataObjects.get(index);}
        return null;
    }
    /** gets the operator for the attribute.
     *
     * @param attribute a attribute for accessing the corresponding value.
     * @return the operator for the given attribute.
     * */
    public Operators getOperator(Attribute attribute) {
        int index = attributes.indexOf(attribute);
        if(index >= 0) {return operators.get(index);}
        return null;}

    /** removes the attribute-value-operator triple
     *
     * @param attribute the attribute of the attribute-value pair to be removed.
     * @return the list itself.
     */
    public AttributeValueList remove(Attribute attribute) {
        int index = attributes.indexOf(attribute);
        if(index >= 0) {attributes.remove(index); dataObjects.remove(index); operators.remove(index);}
        return this;}

    /** @return true if the list is empty. */
    public boolean isEmpty() {return attributes.isEmpty();}

    /** compares the two lists.
     * attributes and values are compared using "equals".
     *
     * @param object the other list
     * @return true if the two lists are semantically equal.
     */
    public boolean equals(Object object) {
        if(object == null || !(object instanceof AttributeValueList)) {return false;}
        AttributeValueList other = (AttributeValueList)object;
        if(attributes.size() != other.attributes.size()) {return false;}
        int size = attributes.size();
        for(int i = 0; i < size; ++i) {
            int index = other.attributes.indexOf(attributes.get(i));
            if(index < 0) {return false;}
            if(!dataObjects.get(i).equals(other.dataObjects.get(index))) {return false;}
            if(!operators.get(i).equals(other.operators.get(index))) {return false;}}
        return true;}

    /** checks for every attribute-value in 'this' and 'other', whether 'value operator otherValue' holds,
     * where the operator is taken from 'other'.
     * <br>
     * Example:<br>
     * this = age 20<br>
     * other = age 30,LESS<br>
     * yields true because 20 LESS 30 is true.
     * <br>
     * Attributes occurring only in 'other' are ignored.
     *
     * @param other another list
     * @return the result of the comparison.
     */
    public boolean implies(AttributeValueList other, Context context) {
        if(!equalityOnly) {return false;}  // this would require real constraint handling, we cannot do yet.
        ArrayList<Attribute> otherAttributes = other.attributes;
        ArrayList<Operators> otherOperators = other.operators;
        ArrayList<DataObject> otherDataObjects = other.dataObjects;
        int size = otherAttributes.size();
        for(int i = 0; i < size; ++i) {
            Attribute attribute = otherAttributes.get(i);
            int index = attributes.indexOf(attribute);
            if(index < 0) {continue;}  // absent attribute means no constraint: everything is allowed.
            DataObject value      = dataObjects.get(index);
            DataObject otherValue = otherDataObjects.get(i);
            Operators operator    = otherOperators.get(i);
            Boolean result = (value instanceof ConcreteObject) ?
                    ((ConcreteObject)value).compare(operator,(ConcreteObject)otherValue) :
                    ((Concept)value).compare(operator,(Concept)otherValue, context);
            if(result == null) {
                System.out.println("Operator not applicable: " + value.toString() + " " + operator.toString() + " " +
                        otherValue.toString());
                return false;}
            if(!result) {return false;}}
        return true;}

    /**
     * @return the list as 'attribute operator value' string
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int thisSize = attributes.size();
        for(int i = 0; i < thisSize; ++i) {
            s.append(attributes.get(i).toString()).append(" ").
                    append(operators.get(i).toString()).append(" ").
                    append(dataObjects.get(i).toString()).
                    append(", ");}
        s.deleteCharAt(s.length()-1);s.deleteCharAt(s.length()-1);
        return s.toString();}



}
