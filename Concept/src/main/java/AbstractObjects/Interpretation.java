package AbstractObjects;

/** An interpretation maps syntactic elements to semantic elements.
 *  The main purpose of this interface is to enable writing algorithms
 *  independently of the concrete representation of the objects.
 *  <br>
 *  The default interface just maps objects to themselves.
 * Created by ohlbach on 04.03.16.
 */
public interface Interpretation<ConstantName,Constant,ConceptName,Concept,AttributeName,Attribute, DataTypeName, DataType> {

    /** maps constant names to constants
     *
     * @param constantName the applicationName to be mapped to a constant.
     * @return the default value (the applicationName itself)*/
    default Constant getConstant(ConstantName constantName) {return (Constant) constantName;}

    /** put the applicationName-constant tuple into the interpretation.
     *
     * @param constantName the applicationName of the concept
     * @param constant the actual concept
     */
    default void putConstant(ConstantName constantName, Constant constant) {};

    /** maps concept names to concepts
     *
     * @param conceptName the applicationName to be mapped to a concept
     * @return the default value (the applicationName itself)*/
    default Concept getConcept(ConceptName conceptName) {return (Concept) conceptName;}

    /** put the applicationName-concept tuple into the interpretation.
     *
     * @param name the applicationName of the concept
     * @param concept the actual concept
     */
    default void putConcept(ConceptName name, Concept concept) {};

    /** maps attribute names to attributes.
     *
     * @param attributeName the applicationName of an attribute
     * @return the default value (the applicationName itself)*/
    default Attribute getAttribute(AttributeName attributeName) {return (Attribute) attributeName;}

    /** inserts the (identifier, attribute)-pair into the interpretation.
     *
     * @param name the identifier of the attribute
     * @param attribute the attribute.
     */
    default void putAttribute(ConceptName name, Attribute attribute) {};

    /** maps data type names to data types
     *
     * @param id the identifier for a data type
     * @return the default value (the applicationName itself)*/
    default DataType getDataType(DataTypeName id) {return (DataType)this;}

    /** inserts a data type into the interpretation
     *
     * @param id   the identifier for the data type
     * @param dataType the datatype itself.
     */
    default void putDataType(DataTypeName id, DataType dataType) {}



}
