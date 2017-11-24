package ConcreteDomain;

import AbstractObjects.DataObject;
import AbstractObjects.DataType;
import AbstractObjects.Operators;
import ConcreteDomain.AtomicTypes.BooleanType;
import ConcreteDomain.AtomicTypes.EnumerationType;
import MISC.Context;
import TableStructures.TableObject;

import java.lang.reflect.Method;

/** This is the superclass for all concrete data types (IntegerType, FloatType etc.)
 *
 */
public class ConcreteType extends DataType {
    protected Method parseObject1 = null;
    protected Method parseObject2 = null;
    protected Method parseCheck1 = null;
    protected Method parseCheck2 = null;

    public ConcreteType() { super();}

    /** constructs a ConcreteType by calling DataType's constructor
     * The new object is inserted into  context.dataTypePool.
     * 
     * @param name     the identifier for the data type
     * @param typename e.g. "Boolean" or "Integer" etc.
     * @param internalType the type of the wrapped object
     * @param context the context into which the data type is to be integrated.
     */
    public ConcreteType(String name, String typename, Class typeClass, Class objectClass, String internalType, Context context) {
        super(name,typename,typeClass,objectClass,internalType,context);
        setParsers();}

    /** constructs a ConcreteType by calling DataType's constructor
     * The new object is inserted into  context.dataTypePool.
     *
     * @param applicationName     the identifier for the data type
     * @param typename e.g. "Boolean" or "Integer" etc.
     * @param internalType the type of the wrapped object
     * @param context the context into which the data type is to be integrated.
     */
    public ConcreteType(String applicationName, String typename, Class clazz, String internalType, Context context) {
        super(applicationName,typename,clazz,clazz,internalType,context);
        setParsers();}


    private void setParsers() {
        try{parseObject1 = typeClass.getDeclaredMethod("parseString",String.class);}
        catch(Exception ex){}
        try{parseObject2 = typeClass.getDeclaredMethod("parseString",String.class, Context.class);}
        catch(Exception ex){}
        try{parseCheck1  = typeClass.getDeclaredMethod("parseCheck",String.class);}
        catch(Exception ex){}
        try{parseCheck2  = typeClass.getDeclaredMethod("parsecheck",String.class, Context.class);}
        catch(Exception ex){}
    }

    public static void initializeDatatypes(Context context) {
        new BooleanType("Boolean", new String[]{"true","wahr"}, new String[]{"false","falsch"}, context);
        new ConcreteType("Constants", "Constant",ConcreteDomain.AtomicTypes.ConstantObject.class,"String",context);
        new ConcreteType("Duration", "Duration",ConcreteDomain.AtomicTypes.Duration.class,null,context);
        new ConcreteType("Float", "Float",ConcreteDomain.AtomicTypes.FloatObject.class,"float",context);
        new ConcreteType("Integer", "Integer",ConcreteDomain.AtomicTypes.IntegerObject.class,"int",context);
        new ConcreteType("String", "String",ConcreteDomain.AtomicTypes.StringObject.class,"string",context);
        new ConcreteType("Table", "Table", TableObject.class,null,context);

        new ConcreteType("FloatInterval", "FloatInterval",ConcreteDomain.SetTypes.FloatInterval.class,"Float[]",context);
        new ConcreteType("FloatList", "FloatList",ConcreteDomain.SetTypes.FloatList.class,"ArrayList<Float>",context);
        new ConcreteType("IntegerInterval", "IntegerInterval",ConcreteDomain.SetTypes.IntegerInterval.class,"Integer[]",context);
        new ConcreteType("IntegerList", "IntegerList",ConcreteDomain.SetTypes.IntegerList.class,"ArrayList<Integer>",context);
        new ConcreteType("StringList", "StringList",ConcreteDomain.SetTypes.StringList.class,"ArrayList<String>",context);
    }

    public boolean subtypeOf(ConcreteType type) {
        if(this.equals(type)) {return true;}
        try{
            Class class1 = Class.forName(applicationName);
            Class class2 = Class.forName(type.applicationName);
            class1.asSubclass(class2);
        }
        catch(Exception ex){return false;}
        return true;
    }

    public boolean subtypeOf(Class class2) {
        try{
            Class class1 = Class.forName(applicationName);
            class1.asSubclass(class2);
        }
        catch(Exception ex){return false;}
        return true;
    }


    public static ConcreteType defineType(String typeSpecification, Context context, StringBuilder errors) {
        ConcreteType type = (ConcreteType)context.getDataType(typeSpecification);
        if(type != null) {return type;}
        if(typeSpecification.startsWith("Boolean")) {return BooleanType.defineType(typeSpecification,context,errors);}
        if(typeSpecification.startsWith("Enum"))    {return EnumerationType.defineType(typeSpecification,context,errors);}
        errors.append("Unknown DataType " + typeSpecification);
        return null;
    }

    /** parses a String into the concrete data object.
     * Syntax errors should be obtained by first calling parse Check.
     *
     * @param string the string to be parsed
     * @param context the context for the data
     * @return the parsed DataObject
     */
    public DataObject parseObject(String string, Context context) {
        if(parseObject1 != null) {
            try{return (DataObject) parseObject1.invoke(null,string);}
            catch(Exception ex) {return null;}}
        if(parseObject2 != null) {
            try{return (DataObject) parseObject2.invoke(null,string,context);}
            catch(Exception ex) {return null;}}
        return null;}

    /** checks a given string if it can be parsed into the DataObject
     *
     * @param string the string to be checked
     * @param context the context for the objects
     * @return an error string or null.
     */
    public String parseCheck(String string, Context context) {
        if(parseCheck1 != null) {
            try{return (String)parseCheck1.invoke(null,string);}
            catch(Exception ex) {return null;}}
        if(parseCheck2 != null) {
            try{return (String)parseCheck2.invoke(null,string,context);}
            catch(Exception ex) {return null;}}
        return "parseCheck: method applications failed for ConcreteType " + applicationName;}


    /** This method delivers for the given ConcreteType and an operator all the other DataTypes which can be compared by the operator.
     * This method is suitable for ConcreteTypes with a single instance, e.g. Integer, Float etc.
     * For all the others, like BooleanType, EnumerationType etc. there must be corresponding overwritten methods.
     *
     * @param operator an operator like "EQUALS" etc.
     * @param context the context where the objects live in
     * @return  the list of DataTypes which are comparable by the operator.
     */
    public DataType[] comparable(Operators operator, Context context) {
        try{Method compare = typeClass.getDeclaredMethod("comparable", Operators.class);
            Class[] comparables = (Class[])compare.invoke(null,operator);
            if(comparables == null) {return null;}
            DataType[] types = new DataType[comparables.length];
            for(int i = 0; i < types.length; ++i) {
                Class clazz = comparables[i];
                String classname = clazz.getSimpleName();
                if(classname.endsWith("Object")) {classname = classname.substring(0,classname.length()-6);}
                ConcreteType type = (ConcreteType)context.getDataType(classname);
                if(type == null) {return null;}
                types[i] = type;}
            return types;}
        catch(Exception ex){}
        return null;}


}
