package ConcreteDomain.AtomicTypes;

import AbstractObjects.DataType;
import AbstractObjects.Operators;
import ConcreteDomain.ConcreteObject;
import ConcreteDomain.ConcreteType;
import MISC.Context;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Function;

/**This class models finite lists of Strings as enumeration type.
 * Example: Enum:colors(red,green,blue)
 *
 */
public class EnumerationType extends ConcreteType implements Serializable {
    /** the list of enumeration items */
    EnumerationObject[] items;
    /** maps the names to the list of EnumerationObject. */
    HashMap<String,EnumerationObject> map = new HashMap<>();

    private static String enumDeclaration = "Enum:<applicationName>(<item_1>,...,<item_n>)\n";


    /** constructs a new enumeration type
     * 
     * @param applicationName the identifiers (e.g. colors).
     * @param context the context into which the enumeration type is to be included
     * @param items the list of items.
     */
    public EnumerationType(String applicationName, Context context, String... items) {
        super(applicationName,"Enumeration",EnumerationType.class,EnumerationObject.class,"String",context);
        this.items = new EnumerationObject[items.length];
        for(int i = 0; i < items.length; ++i) {
            EnumerationObject enumObject = new EnumerationObject(items[i],applicationName);
            map.put(items[i],enumObject);
            this.items[i] = enumObject;}}


    /** returns the EumerationObject for the string
     *
     * @param string any string
     * @return the EnumerationObject, or null.
     */
    public EnumerationObject get(String string) {return map.get(string);}


    /**This method yields for a given operator an array of all those classes the operator is able to compare with 'this'.
     *
     * @param operator any operator
     * @return an array of all classes C sich that 'this operator instance_of_C' is allowed.
     */
    @Override
    public DataType[] comparable(Operators operator, Context context){
        switch(operator) {
            case EQUALS: return new DataType[]{this,context.getDataType("String"),context.getDataType("Constants")};
        }
        return null;}


    /** parses a string into an EnumerationObject
     * 
     * @param string the string to be parsed
     * @param context not needed
     * @return an EnumerationObject, or null.
     */
    @Override
    public ConcreteObject parseObject(String string, Context context)  {
        return map.get(string.trim());}

    /** checks if the string can be parsed as EnumerationObject
     *
     * @param string the string to be parsed
     * @param context not needed
     * @return an error message, or null
     * */
    @Override
    public String parseCheck(String string, Context context)  {
        string = string.trim();
        if(map.get(string) == null) {return infoString() + ": " + " unknown item " + string + "\n";}
        return null;}


    /** parses an enumeration specification.
     * The syntax is  'Enum:applicationName(item_1,...,item_n)'
     *
     * @param string the string to be parsed
     * @param context the context for the objects
     * @param errors to append error messages
     * @return an EnumerationType, or null
     */
    public static EnumerationType defineType(String string, Context context, StringBuilder errors) {
        String error = "Wrong EnumerationType declaration " + string +
                "\n  Should be \""+enumDeclaration+"\"\n";
        String[] parts = string.split(":\\s*",2);
        if(parts.length < 2) {errors.append(error);return null;}
        string = parts[1];
        int firstIndex = string.indexOf("(");
        if(firstIndex < 0) {errors.append(error); return null;}
        int lastIndex = string.lastIndexOf(")");
        if(lastIndex < 0) {errors.append(error); return null;}
        String[] names = string.substring(firstIndex+1,lastIndex).trim().split("\\s*,\\s*");
        String applicationName = string.substring(0,firstIndex).trim();
        return new EnumerationType(applicationName,context,names);}


    /** Two Enumeration Types are equal if their truth values are equal.
     * The application names may be different
     *
     * @param other the other object to be tested for equality.
     * @return true if the two EnumerationTypes are equal up to the applicationName.
     */
    @Override
    public boolean equals(Object other) {
        if (other.getClass() != EnumerationType.class) {return false;}
        EnumerationType type2 = (EnumerationType) other;
        if(items.length != type2.items.length) {return false;}
        for(int i = 0; i < items.length; ++i) {
            if(!items[i].get().equals(type2.items[i].get())) {return false;}}
        return true;}


    /** generates a string that can be parsed again.
     *
     * @return the EnumerationType as a string that can be parsed again
     */
    public String infoString() {
        Function<Object,String> f = (item->(((EnumerationObject)item).value));
        return "Enum:"+applicationName+"(" + Utils.Utilities.join(items,",",f)+")";

    }

    /** This method performs the serialization. It writes the applicationName and the items to the stream.
     *
     * @param out  The out-stream
     * @throws IOException for write errors.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(applicationName);
        out.writeObject(items.length);
        for(EnumerationObject item : items) {
            out.writeObject(item.get());}}

    /** This method performs the deserialization. It reads the applicationName and the iterms from the stream.
     *
     * @param in  The in-stream
     * @throws IOException for write errors.
     */
    private void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
        applicationName = (String)in.readObject();
        int length = (Integer)in.readObject();
        items = new EnumerationObject[length];
        for(int i = 0; i < length; ++i) {
            String item = (String)in.readObject();
            items[i] = new EnumerationObject(item,applicationName);}
        typeName = "Enumeration";
        typeClass = ConcreteDomain.AtomicTypes.EnumerationType.class;
        objectClass = ConcreteDomain.AtomicTypes.EnumerationObject.class;
        internalType = "String";
    }

}
