package AbstractObjects;

import Concepts.Concept;
import ConcreteDomain.ConcreteType;
import MISC.Context;
import org.apache.commons.lang3.StringUtils;

/** This is the top-class for all data types.
 * Instances of the class DataType represent either object classes, for example the class Concept,
 * or instances of subclasses for DataType, for example EnumerationType instances.
 *
 * There are two main branches of DataTypes: the class Concept (with its own hierarchy),
 * and the DataTypes represented by the ConcreteType class.
 * The only direct instance of DataType represents the Concept type.
 * All other instances are instances of the subclass ConcreteType.
 *
 * The main purpose of this class is to allow a common treatment of Concept instances and ConcreteDomain types.
 */

public class DataType {
    /** an application applicationName, for example "sex" for a Boolean type */
    public String applicationName;

    /** the actual type applicationName, for example "Concept", "Boolean", "Enumeration" etc. */
    public String typeName;

    /** The type's class, e.g. Concept.class, BooleanType.class*/
    public Class typeClass;

    /** The object's class, e.g. again Concept.class, but BooleanObject.class*/
    public Class objectClass;

    /** This is the concrete return type of the objectClasse's get() method, usually the type of the wrapped object */
    public String internalType;

    public void initialize(String applicationName, String typename, Class typeClass, Class objectClass, String internalType, Context context) {
        this.applicationName = applicationName;
        this.typeName = typename;
        this.typeClass = typeClass;
        this.objectClass = objectClass;
        this.internalType = internalType;
        context.putDataType(applicationName,this);}


    /** constructs a ConcreteType
     * The new object is inserted into  context.dataTypePool.
     *
     * @param applicationName  the basic applicationName for the data type, e.g. "sex".
     * @param typename    the typeName, e.g. "Boolean" (where an instance can be Boolean(male,female))
     * @param typeClass   the  type's class, e.g. Concept.class, BooleanType.class
     * @param objectClass the object's class, e.g. again Concept.class, but BooleanObject.class
     * @param internalType the type of the wrapped object
     * @param context     the context into which the data type is to be integrated.
     */
    public DataType(String applicationName, String typename, Class typeClass, Class objectClass, String internalType, Context context) {
        this.applicationName = applicationName;
        this.typeName = typename;
        this.typeClass = typeClass;
        this.objectClass = objectClass;
        this.internalType = internalType;
        context.putDataType(applicationName,this);}

    public DataType() {}

    /** This method initializes the built-in DataTypes.
     *
     * It creates the Concept type and initializes the Concrete Domain types.
     *
     * @param context the context into which the data types are to be integrated.
     */
    public static void initializeDatatypes(Context context) {
        Class clazz = Concepts.Concept.class;
        new DataType("Concept","Concept",clazz,clazz,"Concepts.Concept", context);
        clazz = Concepts.IndividualConcept.class;
        new DataType("Individual","IndividualConcept",clazz,clazz,"Concepts.IndividualConcept", context);
        ConcreteType.initializeDatatypes(context);
    }

    /** checks if there is only a single intance oc the type.
     *
     * Examples: "Concept" is singleton, but "Boolean" and "Enumeration" are not.
     *
     * @return true if there is only a single instance of this type.
     * */
    public boolean isSingleton() {
        return typeClass == objectClass;}

    /** This method maps the type applicationName to the corresponding DataType instance.
     * The applicationName must exactly match the definition's applicationName.
     *
     * @param applicationName the DataType's applicationName
     * @param context  the context where the datatype lives in
     * @return the corresponding DataType instance, or null if the applicationName is unknown.
     */
    public static DataType getDataType(String applicationName, Context context) {
        return context.getDataType(StringUtils.capitalize(applicationName.trim()));}


    /** This method parses a typeSpecification and generates a new DataType instance
     *
     * Example: typeSpecification = "Boolean:sex(male,female) yields a new BooleanType with application name "sex".
     *
     * @param typeSpecification  the string to be parsed
     * @param context     the context for all the data
     * @param errors      for inserting error messages.
     * @return            the corresponding DataType object.
     */
    public static DataType defineType(String typeSpecification, Context context, StringBuilder errors) {
        return ConcreteType.defineType(typeSpecification,context,errors);
    }


    /** checks if the data object is an instance of the data type.
     *
     * Examples: an instance of the Concept class is also an instance of the DataType "Concept" <br>
     * The instance "red" of the EnumerationObject of the EnumerationType "Color" would be an instance of the DataType Color.
     *
     * @param value  any DatObject instance
     * @param type   any DataType instance.
     * @return true if the DataObject is an instance of the DataType.
     */
    public static boolean instanceOf(AbstractObjects.DataObject value, DataType type) {
        return value.getClass() == type.objectClass;}


    /** parses a String into the concrete data object.
     *
     * This method is called only for Concept types.
     * In all other cases the parseString method of ConcreteType or its subtypes is called.
     * Syntax errors should be obtained by first calling parse Check.
     *
     * @param string the string to be parsed
     * @param context the context for the data
     * @return the parsed DataObject or null.
     */
    public AbstractObjects.DataObject parseObject(String string, Context context) {
        return context.getConcept(string);}

    /** checks a given string if it can be parsed into the DataObject
     *
     * This method is called only for Concept types.
     * In all other cases the parseString method of ConcreteType or its subtypes is called.
     *
     * @param string the string to be checked
     * @param context the context for the objects
     * @return an error string or null.
     */
    public String parseCheck(String string, Context context) {
        Concept concept = context.getConcept(string);
        return (concept != null) ? null : "Unknown concept " + string;}

    /**
     * @return the full applicationName of the data type.
     */
    @Override
    public String toString() {
        return isSingleton() ? typeName : applicationName + ":" + typeName;}


}
