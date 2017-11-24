package ConcreteDomain.AtomicTypes;

import AbstractObjects.DataType;
import AbstractObjects.Operators;
import ConcreteDomain.ConcreteObject;
import ConcreteDomain.ConcreteType;
import MISC.Context;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/** The instances of this class are Boolean types with different applications.
 * For example, there can be a Boolean type sex(male,female), or a Boolean type size(large,small) etc.
 * The truth values can even have different names for the same truth value, e.g. true,wahr.
 * The class provides a standard Boolean Type "Boolean" with truth value names: true,wahr and false,falsch.
 */
public class BooleanType extends ConcreteType implements Serializable {
    /** the set of names for the truth value true */
    private String[] truths = null;
    /** the set of names for the truth value false */
    private String[] falses = null;

    /** the standard Boolean Type with truth value names: true,wahr and false,falsch */
    public static BooleanType standardType = null;

    private static String booleanDeclaration =
            "Boolean:<applicationName>(<true>,<false>) or Boolean(<[true_1,...,true_n]>,<[false_1,...,false_m]>)\n";

    public BooleanType() {
        super(null,"Boolean",ConcreteDomain.AtomicTypes.BooleanType.class, ConcreteDomain.AtomicTypes.BooleanObject.class,"boolean", Context.currentContext);

    }

    /** This method performs the serialization. It writes the applicationName, the truths and falses to the stream.
     *
     * @param out  The out-stream
     * @throws IOException for write errors.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(applicationName);
        out.writeObject(truths);
        out.writeObject(falses);
    }

    /** This method performs the deserialization. It reads  the applicationName, the truths and falses to the stream.
     *
     * @param in  The in-stream
     * @throws IOException for write errors.
     */
    private void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
        applicationName = (String)in.readObject();
        truths = (String[])in.readObject();
        falses = (String[])in.readObject();
        typeName = "Boolean";
        typeClass = ConcreteDomain.AtomicTypes.BooleanType.class;
        objectClass = ConcreteDomain.AtomicTypes.BooleanObject.class;
        internalType = "boolean";

    }


    /** constructs a Boolean type with corresponding representations for true and false
     *
     * @param context the context into which ths Boolean type is to be integrated.
     * @param truths   for example "male"
     * @param falses  for example "female"
     */
    public BooleanType(String applicationName, String[] truths, String[] falses, Context context) {
        super(applicationName, "Boolean",ConcreteDomain.AtomicTypes.BooleanType.class, ConcreteDomain.AtomicTypes.BooleanObject.class,"boolean",context);
        this.truths = truths;
        this.falses = falses;
    }

    /** This method generates a standard BooleanType with application name "Boolean" and truth values "true,wahr" and "false,falsch".
     *
     * @param context the context into which the data types are to be integrated.
     */
    public static void initializeDatatypes(Context context) {
        String[]truths = {"true","wahr"};
        String[]falses = {"false","falsch"};
        standardType = new BooleanType("Boolean",truths,falses,context);
    }

    /**Defines a new Boolean type declaration.
     *
     * Examples for a Boolean type declaration are:<br>
     * Boolean:sex(male,female) or <br>
     * Boolean:sex([male,maennlich],[female,weiblich])
     *
     * @param typeSpecification a type declaration
     * @param context the context for the data type
     * @param errors for appending error messages if it is an erraneous Boolean-declaration
     * @return null or a BooleanType
     */
    public static BooleanType defineType(String typeSpecification, Context context, StringBuilder errors) {
        String error = "Wrong BooleanType declaration '" + typeSpecification + "'\n  Should be like " + booleanDeclaration;
        String[] truths = null;
        String[] falses = null;
        String[] parts = typeSpecification.split(":\\s*",2);
        if(parts.length < 2) {errors.append(error); return null;}
        typeSpecification = parts[1];
        int firstIndex1 = typeSpecification.indexOf("(");
        if(firstIndex1 < 0) {errors.append(error); return null;}
        String applicationName = typeSpecification.substring(0,firstIndex1).trim();
        DataType type = context.getDataType(applicationName);
        if(type != null) {errors.append("DataType " + applicationName + " is already defined"); return null;}

        int lastIndex1 = typeSpecification.lastIndexOf(")");
        if(lastIndex1 < 0) {errors.append(error); return null;}
        int firstIndex2 = typeSpecification.indexOf("[",firstIndex1+1);
        if(firstIndex2 > 0) {
            int lastIndex2 = typeSpecification.indexOf("]",firstIndex2+1);
            if(lastIndex2 < 0) {errors.append(error); return null;}
            truths = typeSpecification.substring(firstIndex2+1,lastIndex2).trim().split("\\s*,\\s*");
            firstIndex2 = typeSpecification.indexOf("[",lastIndex2+1);
            if(firstIndex2 < 0) {errors.append(error); return null;}
            lastIndex2 = typeSpecification.indexOf("]",firstIndex2+1);
            if(lastIndex2 < 0) {errors.append(error); return null;}
            falses = typeSpecification.substring(firstIndex2+1,lastIndex2).trim().split("\\s*,\\s*");}
        else {
            parts = typeSpecification.substring(firstIndex1+1,lastIndex1).trim().split("\\s*,\\s*");
            if(parts.length != 2) { errors.append(error); return null;}
            truths = new String[]{parts[0]};
            falses = new String[]{parts[1]};}
        return new BooleanType(applicationName,truths,falses,context);}

    /**This method yields for a given operator an array of all those types the operator is able to compare with 'this'.
     * Actually the operator only EQUALS can be used to compare the same type.
     * An example which is not comparable is
     * sex(male,female) with hight(tall,small)  although both are Boolean.
     *
     * @param operator any operator
     * @return an array of all classes C such that 'this operator instance_of_C' is allowed.
     */
    @Override
    public ConcreteType[] comparable(Operators operator, Context context){
        switch(operator) {
            case EQUALS: return new ConcreteType[]{this};}
        return null;}


    /** comprises the data into a single string to be uses as applicationName for the Boolean Type
     *
     * @return a string with all information packed into it
     */
    @Override
    public String toString() {
        if(this == standardType) {return "Boolean";}
        if(truths.length == 1 && falses.length == 1) {return "Boolean:" + applicationName + "(" + truths[0]+","+falses[0]+")";}
        return "Boolean:" + applicationName + "("+ Arrays.toString(truths)+ ","+Arrays.toString(falses)+")";
    }

    /** checks if the string can be parsed as a Boolean value
     *
     * @param string a string to be tested
     * @param context not needed
     * @return null or an error string.
     */
    @Override
    public String parseCheck(String string, Context context)  {
        string = string.trim();
        for(String tr : truths){if(tr.equals(string))  {return null;}}
        for(String fa : falses){if(fa.equals(string)) {return null;}}
        return typeName + ": unknown truths value: " + string;
    }
    
    /** parses a string as a BooleanObject
     * 
     * @param string  the string to be parsed
     * @param context not needed
     * @return the BooleanObject, or null if it could not be parsed.
     */
    @Override
    public ConcreteObject parseObject(String string, Context context) {
        string = string.trim();
        for(String tr : truths){if(tr.equals(string))  {return BooleanObject.trueObject;}}
        for(String fa : falses){if(fa.equals(string))  {return BooleanObject.falseObject;}}
        return null;}


    /** Two Boolean Types are equal if their truth values are equal.
     * The application names may be different
     *
     * @param other the other object to be tested for equality.
     * @return true if the two BooleanTypes are equal up to the applicationName.
     */
   @Override
   public boolean equals(Object other) {
       if(other.getClass() != BooleanType.class) {return false;}
       BooleanType type2 = (BooleanType)other;
       if(!Arrays.equals(truths,type2.truths)) {return false;}
       if(!Arrays.equals(falses,type2.falses)) {return false;}
       return true;
   }

}
