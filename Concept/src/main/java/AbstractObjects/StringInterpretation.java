package AbstractObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/** This class allows one to map Strings/Identifiers to Constants, Concepts and Attributes and DataTypes.
 * The insertion and removal of constants, concepts and attributes can be watched by observers.
 * An observer is a BiConsumer with the objects as first argument and true/false as second argument.
 * true means insertion, false means removal.
 */
public class StringInterpretation<Constant,Concept,Attribute, DataType>
        implements Interpretation<String,Constant,String,Concept,String,Attribute,String, DataType> {
    /** for mapping identifiers to constants */
    private final HashMap<String,Constant>   constants  = new HashMap();
    /** observers for the insertion and removal of constants. */
    private final ArrayList<BiConsumer<Constant,Boolean>> constantObservers = new ArrayList<>();

    /** for mapping identifiers to concepts */
    private final HashMap<String,Concept>    concepts   = new HashMap();
    /** observers for the insertion and removal of constants. */
    private final ArrayList<BiConsumer<Concept,Boolean>> conceptObservers = new ArrayList<>();

    /** for mapping identifiers to attributes */
    private final HashMap<String,Attribute>  attributes = new HashMap();
    /** observers for the insertion and removal of constants. */
    private final ArrayList<BiConsumer<Attribute,Boolean>> attributeObservers = new ArrayList<>();

    /** for mapping identifiers to data types */
    private final HashMap<String, DataType>  dataTypes   = new HashMap();
    /** observers for the insertion and removal of constants. */
    private final ArrayList<BiConsumer<DataType,Boolean>> datatypeObservers = new ArrayList<>();

    /** clears the interpretation */
    public synchronized void clear() {
        constants.clear(); concepts.clear(); attributes.clear(); dataTypes.clear();
        constantObservers.clear();conceptObservers.clear();attributeObservers.clear();datatypeObservers.clear();
    }

    /**************************************** Observers ***************************************/

    /** adds a constant observer.
     * It is a BiConsumer&lt;Constant,Boolean&gt; where the second argument indicates insertion (true)
     * and removal(false).
     *
     * @param observer the observer to be added.
     */
    public void addConstantObserver(BiConsumer<Constant,Boolean> observer) {
        constantObservers.add(observer);}

    /** removes a constant observer.
     *
     * @param observer the observer to be added.
     */
    public void removeConstantObserver(BiConsumer<Constant,Boolean> observer) {
        constantObservers.remove(observer);}

    /** adds a concept observer.
     * It is a BiConsumer&lt;Concept,Boolean&gt; where the second argument indicates insertion (true)
     * and removal(false).
     *
     * @param observer the observer to be added.
     */
    public void addConceptObserver(BiConsumer<Concept,Boolean> observer) {
        conceptObservers.add(observer);}

    /** removes a concept observer.
     *
     * @param observer the observer to be added.
     */
    public void removeConceptObserver(BiConsumer<Concept,Boolean> observer) {
        conceptObservers.remove(observer);}

    /** adds an attribute observer.
     * It is a BiConsumer&lt;Attribute,Boolean&gt; where the second argument indicates insertion (true)
     * and removal(false).
     *
     * @param observer the observer to be added.
     */
    public void addAttributeObserver(BiConsumer<Attribute,Boolean> observer) {
        attributeObservers.add(observer);}

    /** removes an attribute observer.
     *
     * @param observer the observer to be added.
     */
    public void removeAttributeObserver(BiConsumer<Attribute,Boolean> observer) {
        attributeObservers.remove(observer);}

    /** adds a datatype observer.
     * It is a BiConsumer&lt;ConcreteType,Boolean&gt; where the second argument indicates insertion (true)
     * and removal(false).
     *
     * @param observer the observer to be added.
     */
    public void addDatatypeObserver(BiConsumer<DataType,Boolean> observer) {
        datatypeObservers.add(observer);}

    /** removes a datatype observer.
     *
     * @param observer the observer to be removed.
     */
    public void removeDatatypeObserver(BiConsumer<DataType,Boolean> observer) {
        datatypeObservers.remove(observer);}


    /**************************************** Constants ***************************************/

    /** return the constant for the constant applicationName
     *
     * @param id the constant applicationName
     * @return the constant, or null if there is none.
     */
    @Override
    public synchronized Constant getConstant(String id) {return constants.get(id);}

    /** adds a constant with a given applicationName to the interpretation.
     * The observers are activated before the constant is inserted.
     *
     * @param id   the identifier for the constant.
     * @param constant the constant itself.
     */
    @Override
    public synchronized void putConstant(String id, Constant constant) {
        for(BiConsumer<Constant,Boolean> observer : constantObservers) {observer.accept(constant,true);}
        constants.put(id,constant);}

    /** removes a constant with a given applicationName from the interpretation.
     * The observers are activated after the constant is removed.
     *
     * @param id   the identifier for the constant.
     */
    public synchronized void removeConstant(String id) {
        Constant constant = constants.get(id);
        if(constant != null) {
            constants.remove(id);
            for(BiConsumer<Constant,Boolean> observer : constantObservers) {observer.accept(constant,false);}}}

    /** checks if the given applicationName denotes a constant.
     *
     * @param id the applicationName to be checked
     * @return true if the applicationName denotes a constant.
     */
    public boolean isConstant(String id) {return constants.get(id) != null;}

    /** counts the number of constants.
     *
     * @return the number of constants.
     */
    public synchronized int nConstants() {return constants.size();}

    /**************************************** Concepts ***************************************/

    /** return the concept for the concept applicationName
     *
     * @param id the concept applicationName
     * @return the concept, or null if there is none.
     */
    @Override
    public synchronized Concept getConcept(String id) {return concepts.get(id);}

    /** adds a concept with a given applicationName to the interpretation.
     * The observers are activated before the concept is inserted.
     *
     * @param id   the identifier for the concept.
     * @param concept the concept itself.
     */
    @Override
    public synchronized void putConcept(String id, Concept concept) {
        for(BiConsumer<Concept,Boolean> observer : conceptObservers) {observer.accept(concept,true);}
        concepts.put(id,concept);}

    /** removes the concept with the given applicationName.
     * The observers are activated after the concept is removed.
     *
     * @param id the identifier for the concept to be removed.
     */
    public synchronized void removeConcept(String id) {
        Concept concept = concepts.get(id);
        if(concept != null) {
            concepts.remove(id);
            for(BiConsumer<Concept,Boolean> observer : conceptObservers) {observer.accept(concept,false);}}}

    /** checks if the given identifier denotes a concept
     *
     * @param id any string
     * @return true if the identifier denotes a concept.
     */
    public synchronized boolean isConcept(String id) {return concepts.get(id) != null;}

    /** counts the number of concepts.
     *
     * @return the number of concepts.
     */
    public synchronized int nConcepts() {return concepts.size();}

    /** applies the given function to each (identifier,concept)-pair
     *
     * @param consumer the function to be applied.
     */
    public synchronized void forEachConcept(BiConsumer<String,Concept> consumer) {
        concepts.forEach((id, concept) -> consumer.accept(id,concept));}

    /** searches through the concepts until the given function returns non-null.
     *
     * @param function the function to be applied to the concepts
     * @param <T> the result-type of the function.
     * @return the first non-null value
     */
    public synchronized <T> T findInConcepts(BiFunction<String,Concept,T> function) {
        for(Map.Entry<String,Concept> entry : concepts.entrySet()) {
            T value = function.apply(entry.getKey(),entry.getValue());
            if(value != null) {return value;}}
        return null;}

    /** searches through the concepts until the first non-null value is returned by the function.
     *
     * @param function a function to be applied to each concept.
     * @param <T> the return type of the function.
     * @return the first non-null value.
     */
    public synchronized <T> T findInConcepts(Function<Concept,T> function) {
        for(Concept concept : concepts.values()) {
            T value = function.apply(concept);
            if(value != null) {return value;}}
        return null;}

    /**************************************** Attributes ***************************************/

    /** return the attribute for the attribute applicationName
     *
     * @param id the attribute applicationName
     * @return the attribute, or null if there is none.
     */
    @Override
    public synchronized Attribute getAttribute(String id) {return attributes.get(id);}

    /** inserts the (identifier, attribute)-pair into the interpretation.
     * The observers are activated before the attribute is inserted.
     *
     * @param id the identifier
     * @param attribute the attribute.
     */
    @Override
    public synchronized void putAttribute(String id, Attribute attribute) {
        for(BiConsumer<Attribute,Boolean> observer : attributeObservers) {observer.accept(attribute,true);}
        attributes.put(id,attribute);}

    /** removes the attribute with the given identifier from the interpretation.
     * The observers are activated after the attribute is removed.
     *
     * @param id the identifier
     */
    public synchronized void removeAttribute(String id) {
        Attribute attribute = attributes.get(id);
        if(attribute != null) {
            attributes.remove(id,attribute);
            for(BiConsumer<Attribute,Boolean> observer : attributeObservers) {observer.accept(attribute,false);}}}


    /** checks if the given identifier denotes an attribute.
     *
     * @param id the string to be checked.
     * @return true if the string denotes an attribute.
     */
    public synchronized boolean isAttribute(String id) {return attributes.get(id) != null;}

    /** counts the number of attributes in the interpretation.
     *
     * @return the number of attributes.
     */
    public synchronized int nAttributes() {return attributes.size();}

    /** applies the consumer to each (identifier, attribute)-pair of the interpretation
     *
     * @param consumer the method to be applied to the (identifier, attribute)-pair
     */
    public synchronized void forEachAttribute(BiConsumer<String,Attribute> consumer) {
        attributes.forEach((id, attribute) -> consumer.accept(id,attribute));}

    /** applies the consumer to each attribute pair of the interpretation
     *
     * @param consumer the function to be applied to the attributes.
     */
    public synchronized void forEachAttribute(Consumer<Attribute> consumer) {
        attributes.forEach((id, attribute) -> consumer.accept(attribute));}

    /** searches through the attributes until the first non-null value is returned by the function.
     *
     * @param function a function to be applied to each (identifier, attribute)-pair
     * @param <T> the return type of the function.
     * @return the first non-null value.
     */
    public synchronized <T> T findInAttributes(BiFunction<String,Attribute,T> function) {
        for(Map.Entry<String,Attribute> entry : attributes.entrySet()) {
            T value = function.apply(entry.getKey(),entry.getValue());
            if(value != null) {return value;}}
        return null;}


    /** searches through the attributes until the first non-null value is returned by the function.
     *
     * @param function a function to be applied to each attribute
     * @param <T> the return type of the function.
     * @return the first non-null value.
     */
    public synchronized <T> T findInAttributes(Function<Attribute,T> function) {
        for(Attribute attribute : attributes.values()) {
            T value = function.apply(attribute);
            if(value != null) {return value;}}
        return null;}

    /**************************************** DataOld Types ***************************************/

    /** return the attribute for the attribute applicationName
     *
     * @param id the attribute applicationName
     * @return the attribute, or null if there is none.
     */
    public synchronized DataType getDataType(String id) {return dataTypes.get(id);}

    /** inserts the (identifier, datatype)-pair into the interpretation.
     * The observers are activated before the dataTape is inserted.
     *
     * @param id the identifier
     * @param dataType the datatype.
     */
    public synchronized void putDataType(String id, DataType dataType) {
        for(BiConsumer<DataType,Boolean> observer : datatypeObservers) {observer.accept(dataType,true);}
        dataTypes.put(id,dataType);}

    /** removes the datatype with the given identifier.
     * The observers are activated after the datatype is inserted.
     *
     * @param id the identifier of the datatype to be removed.
     */
    public synchronized void removeDataType(String id) {
        DataType dataType = dataTypes.get(id);
        if(dataType != null) {
            dataTypes.remove(id);
            for(BiConsumer<DataType,Boolean> observer : datatypeObservers) {observer.accept(dataType,false);}}}

    /** counts the number of datatypes in the interpretation.
     *
     * @return the number of datatypes.
     */
    public synchronized int nDataTypes() {return dataTypes.size();}

    /**************************************** General ***************************************/

    /** computes a string with all identifier: value pairs of the concepts and attributes.
     *
     * @return the computed string.
     */
    public synchronized String toString() {
        StringBuilder s = new StringBuilder();
        if(!concepts.isEmpty()) {
            s.append("Concepts:\n");
            concepts.forEach((id,concept) -> s.append(id).append(": ").append(concept.toString()).append("\n"));
            s.append("\n");}

        if(!attributes.isEmpty()) {
            s.append("Attributes:\n");
            attributes.forEach((id,attribute) -> s.append(id).append(": ").append(attribute.toString()).append("\n"));
            s.append("\n");}

        if(!dataTypes.isEmpty()) {
            s.append("DataTypes:\n");
            dataTypes.forEach((id,dataType) -> s.append(id).append(": ").append(dataType.toString()).append("\n"));
            s.append("\n");}


        return s.toString();}
}
