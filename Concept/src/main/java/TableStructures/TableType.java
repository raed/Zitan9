package TableStructures;

import AbstractObjects.DataObject;
import AbstractObjects.DataType;
import ConcreteDomain.ConcreteObject;
import ConcreteDomain.ConcreteType;
import MISC.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class TableType extends DataType implements Serializable {
    protected ArrayList<DataType> columnTypes;
    protected ArrayList<String> columnNames;
    protected HashMap<String,Integer> columnPositions;

    private static String tableDeclaration = "Table:<applicationName>(<dataType1>:<columnName1>,...)";

    public TableType(String applicationName, ArrayList<DataType> columnTypes, ArrayList<String> columnNames, Context context) {
        super(applicationName, "Table", TableType.class, TableObject.class, "Table<DataObject>",context);
        this.columnTypes = columnTypes;
        this.columnNames = columnNames;
        columnPositions = new HashMap<>();
        for(int i = 0; i< columnNames.size(); ++i) {columnPositions.put(columnNames.get(i),i);}
    }

    private void writeObject(ObjectOutputStream out)  throws IOException {
        out.writeObject(applicationName);
        out.writeObject(columnNames);
        for(DataType type : columnTypes) {out.writeObject(type.applicationName);}}


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Context context = Context.currentContext;
        initialize((String)in.readObject(), "Table", TableType.class, TableObject.class, "Table<DataObject>",context);
        columnNames = (ArrayList<String>)in.readObject();
        int size = columnNames.size();
        columnTypes = new ArrayList<>(size);
        columnPositions = new HashMap<>();
        for(int i = 0; i < size; ++i) {
            String applicationName = (String)in.readObject();
            DataType type = context.getDataType(applicationName);
            if(type == null) {throw new ClassCastException(applicationName);}
            columnTypes.add(type);
            columnPositions.put(columnNames.get(i),i);}
    }


    /** reconstructs the type declaration such that it can be parsed again.
     *
     * @return the type declaration in a form which can be parsed again.
     */
    public String infoString() {
        StringBuilder s = new StringBuilder();
        s.append("Table:").append(applicationName).append("(");
        int size = columnNames.size()-1;
        for(int i = 0; i < size; ++i) {s.append(columnNames.get(i)).append(":").append(columnTypes.get(i).applicationName).append(",");}
        s.append(columnNames.get(size)).append(":").append(columnTypes.get(size).applicationName).append(")");
        return s.toString();}

    /**Defines a new TableType.
     *
     * Examples for a Table type declaration are:<br>
     * Table:exam(Concept:student,Float:mark)
     *
     * @param typeSpecification a type declaration
     * @param context the context for the data type
     * @param errors for appending error messages if it is an erraneous Table-declaration
     * @return null or a TableType
     */
    public static TableType defineType(String typeSpecification, Context context, StringBuilder errors) {
        String error = "Wrong TypeType declaration '" + typeSpecification + "'\n  Should be like " + tableDeclaration;
        String[]parts = typeSpecification.split(":\\s*",2);
        if(parts.length < 2) {errors.append(error); return null;}
        parts = parts[1].split("\\s*\\(\\s*",2);
        if(parts.length < 2) {errors.append(error); return null;}
        String applicationName = parts[0];
        int index = parts[1].lastIndexOf(')');
        if(index < 0) {errors.append(error); return null;}
        boolean wrong = false;
        ArrayList<DataType> columnTypes = new ArrayList();
        ArrayList<String> columnNames = new ArrayList();;
        for(String part : parts[1].substring(0,index).split("\\s*,\\s*")) {
            String[] column = part.split("\\s*:\\s*");
            if(column.length < 2) {errors.append("Wrong column declaration " + part + " in " +typeSpecification + "\n"); wrong = true; continue;}
            DataType type = context.getDataType(column[0]);
            if(type == null) {errors.append("Unknown column type " + column[0] + " in " +typeSpecification + "\n"); wrong = true; continue;}
            columnTypes.add(type);
            columnNames.add(column[1]);}
        if(wrong) {return null;}
        return new TableType(applicationName,columnTypes,columnNames,context);
    }





    /** for a given column name it yields the column index.
     *
     * @param columnName a column name
     * @return the corresponding index, or null if the name is unknown.
     */
    public Integer getColumnIndex(String columnName) {
        return columnPositions.get(columnName);}

    /** maps an array of column names to the array of indices
     *
     * @param columnNames some column names
     * @return an array of indices, or null if there is an unknown column name.
     */
    public int[] getColumnIndices(String... columnNames) {
        int[] indices = new int[columnNames.length];
        for(int i = 0; i < columnNames.length; ++i) {
            Integer index = columnPositions.get(columnNames[i]);
            if(index == null) {return null;}
            indices[i] = index;}
        return indices;}


    /** checks if the string can be parsed as a Table value
     *
     * @param string a string to be tested
     * @param context not needed
     * @return null or an error string.
     */
    @Override
    public String parseCheck(String string, Context context)  {
        return null;
    }

    /** parses a string as a TableObject
     *
     * @param string  the string to be parsed
     * @param context not needed
     * @return the TableObject, or null if it could not be parsed.
     */
    @Override
    public ConcreteObject parseObject(String string, Context context) {
        return null;
    }

    /** This method compiles code for boolean tests of row elements.
     * Example: The table contains columns with names mark1 and mark2.
     * The method can then be used to compile a string "return mark1 &lt; mark2" into a
     * Predicate&lt;DataObject[]&gt; whose code accesses the corresponding column entries of
     * the given row and evaluates the boolean expression.
     * The predicate can be used as a filter for a stream of rows.
     *
     * @param code  a string with a code fragment that returns a boolean value.
     * @param errors for inserting messages about compilation errors
     * @return a Predicate&lt;DataObject[]&gt; that can be applied to a row of the given table.
     */
    public Predicate<DataObject[]> compileRowFilter(String code, StringBuilder errors) {
        String cd = "new java.util.function.Predicate<AbstractObjects.DataObject[]>() {\n" +
                "public boolean test(AbstractObjects.DataObject[] row) {\n" +
                rowAssignments("row") + code + "}}";
        return  (Predicate<DataObject[]>)Utils.Utilities.evaluate(cd,errors);
    }

    /** This method compiles application code for row elements.
     * Example: The table contains columns with names mark1 and mark2.
     * The method can then be used to compile a string "return (Float)(mark1 + mark2)" into a
     * Function&lt;DataObject[],Float&gt; whose code accesses the corresponding column entries of
     * the given row and evaluates the expression.
     *
     * @param code  a string with a code fragment
     * @param errors for inserting messages about compilation errors
     * @return a Function&lt;DataObject[],T&gt; that can be applied to a row of the given table.
     */
    public <T> Function<DataObject[],T> compileRowFunction(String code, StringBuilder errors) {
        String cd = "new java.util.function.Function<AbstractObjects.DataObject[],Object>() {\n" +
                "public Object apply(AbstractObjects.DataObject[] row) {\n" +
                rowAssignments("row") + code + "}}";
        return  (Function<DataObject[],T>)Utils.Utilities.evaluate(cd,errors);
    }



    private String rowAssignments(String rowVariable) {
        String assignments = "";
        for(int i = 0; i < columnNames.size(); ++i) {
            assignments += "    "+columnTypes.get(i).internalType + " " + columnNames.get(i) + " = ("+columnTypes.get(i).internalType+")"+rowVariable+
                    "["+i+"].get()\n";   // Groovy Array Access
        }
        return assignments;
    }

    // Parsing tables from Stream<String[]>

    /** This method allows one to parse a stream of String-arrays into DataObject items and to put them into a TableObject
     * The stream of String-arrays may, for example, come from a csv-file.
     * Either a single token in a String-array is parsed into a DataObject item,
     * or several tokens are mapped to a single DataObject item.
     *
     *
     * @param stream  the stream of String-arrays
     * @param context the context where the objects reside
     * @param errors  for insering parsing errors
     * @param parsers one parser for each column in the TableType. A "null" entry generates a default parser for the column type.
     * @return the parsed TableObject.
     */
    public TableObject parseFromStream(Stream<String[]> stream, Context context, StringBuilder errors, StringParser... parsers) {
        int size = parsers.length;
        assert size == columnNames.size();
        for(int i = 0; i < size; ++i) {
            if(parsers[i] == null) {parsers[i] = new SingletonStringParser(i,columnTypes.get(i));}}
        TableObject table = new TableObject(this);
        stream.forEach(tokens -> {
            DataObject[] row = new DataObject[size];
            for(int i = 0; i < size; ++i) {
                StringParser parser = parsers[i];
                row[i] = parser.parseString(tokens,context,errors);}
            table.add(row);});
        return table;}


    /** This interface has two implementing classes,
     * one parsing a single token, and one for parsing several tokens into a single DataObject.
     */
    public interface StringParser {
        /** parses certain tokens of the token array into a DataObject
         *
         * @param tokens  the token-array, maybe an entire line of a csv file.
         * @param context the context where the objects reside.
         * @param errors  for inserting parsing errors
         * @return the parsed DataObject, or null if a parsing error occurred.
         */
        DataObject parseString(String[] tokens,  Context context,  StringBuilder errors);}

    /** Instances of this class parse several tokens into a single DataObject
     *
     */
    private static class MultipleStringParser implements StringParser {
        private BiFunction<String[], Context, DataObject> parser;
        private int[] indices;
        private String[] tokens;
        private int size;
        private DataType resultType;

        /** generates a parser for mapping several tokens into a single DataObject.
         *
         * @param parser   a function which accepts the token array and the context, and returns a DataObject, or null
         * @param indices  the indices in the original token array from where the tokens are to be extracted for the parser
         * @param resultType the DataType of the result.
         */
        public MultipleStringParser(BiFunction<String[], Context, DataObject> parser, int[] indices, DataType resultType) {
            this.parser = parser;
            this.indices = indices;
            size = indices.length;
            tokens = new String[size];
        }

        /** parses certain tokens of the token array into a DataObject
         *
         * @param tokens  the token-array, maybe an entire line of a csv file.
         * @param context the context where the objects reside.
         * @param errors  for inserting parsing errors
         * @return the parsed DataObject, or null if a parsing error occurred.
         */
        public DataObject parseString(String[] tokens, Context context, StringBuilder errors) {
            for(int i = 0; i < size; ++i) {
                this.tokens[i] = tokens[indices[i]];}
            DataObject item = parser.apply(this.tokens,context);
            if(item == null) {errors.append(Arrays.toString(this.tokens) + " cannot be parsed into " + resultType.applicationName + ".\n");}
            return item;
        }
    }

    /** The instances of this class can map a single token to a DataObject.
     *
     */
    private static class SingletonStringParser implements StringParser {
        private DataType dataType;
        private int index;

        /** creates a parser
         *
         * @param index    the index in the original token array, from where the token is to be extracted
         * @param dataType the DataType of the result
         */
        public SingletonStringParser(int index, DataType dataType) {
            this.dataType = dataType;
            this.index = index;}

        /** extracts a single token from the token array and maps it to a DataObject
         *
         * @param tokens  the token-array, maybe an entire line of a csv file.
         * @param context the context where the objects reside.
         * @param errors  for inserting parsing errors
         * @return the parsed DataObject, or null if a parsing error occurred.
         */
        public DataObject parseString(String[] tokens, Context context, StringBuilder errors) {
            String token = tokens[index];
            String error = dataType.parseCheck(token,context);
            if(error == null) {return dataType.parseObject(token,context);}
            else {errors.append("parse error: " + token + " is not of type " + dataType.applicationName+ "\n");
                return null;}}
    }

    /** This method generates a parser for mapping a single token to a DataObject
     *
     * @param index      the index in the original token array from where to extract the token
     * @param columnName the name of the column in the resulting table
     * @param errors     for inserting parsing errors
     * @return           the parser
     */
    public SingletonStringParser makeParser(int index, String columnName, StringBuilder errors) {
        Integer position = columnPositions.get(columnName);
        if(position == null) {errors.append("Unknown column name: " + columnName + "\n"); return null;}
        else {return new SingletonStringParser(index,columnTypes.get(position));}}

    /** This method generates a parser for mapping a single token to a DataObject
     *  The token is extracted from exactly the same position as the column name in the resulting array.
     *
     * @param columnName the name of the column in the resulting table
     * @param errors     for inserting parsing errors
     * @return           the parser
     */
    public SingletonStringParser makeParser(String columnName, StringBuilder errors) {
        Integer position = columnPositions.get(columnName);
        if(position == null) {errors.append("Unknown column name: " + columnName + "\n"); return null;}
        else {return new SingletonStringParser(position,columnTypes.get(position));}}


    /** This method generates a parser for mapping several tokens to a DataObject
     *
     * @param indices    The list of indices for extracting the tokens
     * @param columnName the name of the column in the resulting table
     * @param errors     for inserting parsing errors
     * @param parser     a function for mapping the tokens to a DataObject.
     * @return           the parser
     */
    public MultipleStringParser makeParser(int[] indices, String columnName, StringBuilder errors, BiFunction<String[],Context,DataObject> parser) {
        Integer position = columnPositions.get(columnName);
        if(position == null) {errors.append("Unknown column name: " + columnName + "\n"); return null;}
        return new MultipleStringParser(parser,indices,columnTypes.get(position));}



}
