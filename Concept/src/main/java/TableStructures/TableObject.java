package TableStructures;

import AbstractObjects.DataObject;
import Attributes.Attribute;
import Concepts.Concept;
import Concepts.Scope;
import ConcreteDomain.AtomicObject;
import MISC.Context;
import TableStructures.TableType;
import Utils.Table;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 *
 */
public class TableObject implements DataObject,Serializable {
    protected Table<DataObject> table;
    public TableType tableType;


    public TableObject(TableType tableType, Table<DataObject> table) {
        this.tableType = tableType;
        this.table = table;}

    public TableObject(TableType tableType) {
        this.tableType = tableType;
        this.table = new Table<DataObject>();}

    @Override
    public Object get() {return table;}

    public void add(DataObject[] row) {table.add(row);}


    private void writeObject(ObjectOutputStream out)  throws IOException {
        out.writeObject(tableType.applicationName);
        out.writeObject(table);}


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Context context = Context.currentContext;
        String typeName = (String)in.readObject();
        tableType = (TableType)context.getDataType(typeName);
        if(tableType != null) {throw new ClassNotFoundException(typeName);}
        table = (Table<DataObject>)in.readObject();}



    @Override
    public String toString() {
        return table.toString();}

    /** generates a stream of rows as DataObject[]
     *
     * @return the stream of rows.
     */
    public Stream<DataObject[]> rowsStream() {return table.rowsStream();}



    /** generates a stream of items at the given row index.
     *
     * @param rowIndex  the row's position
     * @return the stream of items of this row.
     */
    public Stream<DataObject> rowStream(int rowIndex) {return table.rowStream(rowIndex);}

    /** generates a sub-stream of items at the given row index
     *
     * @param rowIndex       the row's position
     * @param startInclusive the start column index
     * @param endExclusive   the end column index
     * @return the stream of items of the row between start and end.
     */
    public Stream<DataObject> rowStream(int rowIndex, int startInclusive, int endExclusive) {
        return table.rowStream(rowIndex,startInclusive,endExclusive);}

    /** generates a stream of items at the given column index.
     *
     * @param colIndex  the columns's position
     * @return the stream of items of this column.
     */
    public Stream<DataObject> columnStream(int colIndex) {
        return table.columnStream(colIndex);}

    /** generates a stream of items at the given column index.
     *
     * @param colName  the columns's name
     * @return the stream of items of this column.
     */
    public Stream<DataObject> columnStream(String colName) {
        Integer colIndex = tableType.getColumnIndex(colName);
        assert colIndex != null;
        return table.columnStream(colIndex);}


    /** generates a sub-stream of items at the given column index.
     *
     * @param colIndex  the columns's position
     * @param startInclusive the start row index
     * @param endExclusive   the end row index
     * @return the stream of items of this column.
     */
    public Stream<DataObject> columnStream(int colIndex, int startInclusive, int endExclusive) {
        return table.columnStream(colIndex,startInclusive,endExclusive);}


    /** generates a sub-stream of items at the given column index.
     *
     * @param colName  the columns's name
     * @param startInclusive the start row index
     * @param endExclusive   the end row index
     * @return the stream of items of this column.
     */
    public Stream<DataObject> columnStream(String colName, int startInclusive, int endExclusive) {
        Integer colIndex = tableType.getColumnIndex(colName);
        assert colIndex != null;
        return table.columnStream(colIndex,startInclusive,endExclusive);}


    /** generates a stream of tuples which are extracted from different column positions in the rows
     * The tuples are stored in a single little array. The array is reused for each new tuple.
     *
     * @param colIndices the different column positions
     * @return the stream of tuples with values extracted from the column positions.
     */
    public Stream<DataObject[]> columnStream(int[] colIndices) {
        return table.columnStream(colIndices);}

    /** generates a stream of tuples which are extracted from different column positions in the rows
     * The tuples are stored in a single little array. The array is reused for each new tuple.
     *
     * @param colNames the different column names.
     * @return the stream of tuples with values extracted from the column positions.
     */
    public Stream<DataObject[]> columnStream(String[] colNames) {
        int[] colIndices = tableType.getColumnIndices(colNames);
        assert colIndices != null;
        return table.columnStream(colIndices);}



    /** generates a sub-stream of tuples which are extracted from different column positions in the rows
     * The tuples are stored in a single little array. The array is reused for each new tuple.
     *
     * @param colIndices the different column positions
     * @param startInclusive the start row index
     * @param endExclusive   the end row index
     * @return the stream of tuples with values extracted from the column positions.
     */
    public Stream<DataObject[]> columnStream(int[] colIndices, int startInclusive, int endExclusive) {
        return table.columnStream(colIndices,startInclusive,endExclusive);}

    /** generates a sub-stream of tuples which are extracted from different column positions in the rows
     * The tuples are stored in a single little array. The array is reused for each new tuple.
     *
     * @param colNames the different column names.
     * @param startInclusive the start row index
     * @param endExclusive   the end row index
     * @return the stream of tuples with values extracted from the column positions.
     */
    public Stream<DataObject[]> columnStream(String[] colNames, int startInclusive, int endExclusive) {
        int[] colIndices = tableType.getColumnIndices(colNames);
        assert colIndices != null;
        return table.columnStream(colIndices,startInclusive,endExclusive);}


    /** This method turns the column with the given index into an attribute value.
     *
     * @param columnIndex the column's index
     * @return  the column turned into a TableAttributeValue
     */
    public TableAttributeValue makeTableAttributeValue(int columnIndex) {
        assert columnIndex >= 0 && columnIndex < table.nColumns();
        return new TableAttributeValue(this,columnIndex);
    }

    /** This method turns the column with the given name into an attribute value.
     *
     * @param columnName the column name
     * @return the column turned into a TableAttributeValue
     */
    public TableAttributeValue makeTableAttributeValue(String columnName) {
        Integer columnIndex = tableType.getColumnIndex(columnName);
        assert columnIndex != null;
        return new TableAttributeValue(this,columnIndex);}

    /** This method inserts all items in the given column into corresponding associated tables.
     *
     * Example:<br>
     * Consider a table of type MarkList with columns "Participant, Semester, Mark" <br>
     * It may be the attribute value of an individual "exam44".<br>
     * Each participant may now have an associated table "marks" with columns "Exam, Semester, Mark"<br>
     * The purpose of this method is now to turn each row, like for example "Tom, 3, 2.7" into a
     * row "exam44, 3, 2.7" and add it to Tom's associated "marks"-table.
     *
     * @param concept             a concept (like "exam44" in the example)
     * @param attribute           an attribute (like "marks" in the example)
     * @param associatedTableType the corresponding associated table type ("Exam, Semester, Mark", in the example)
     * @param context             the corresponding context.
     */
    public void insertIntoAssociatedTable(Concept concept, Attribute attribute, AssociatedTableType associatedTableType, Context context) {
        int columnIndex = associatedTableType.columnIndex;
        int nRows = table.nRows();
        StringBuilder errors = new StringBuilder();
        for(int rowIndex = 0; rowIndex < nRows; ++rowIndex) {
            DataObject[] row = table.getRow(rowIndex).clone();
            Concept c = (Concept)row[columnIndex];
            TableObject associatedTable = (TableObject)c.getFirst(attribute,context);
            if(associatedTable == null) {
                associatedTable = new TableObject(associatedTableType);
                c.add(attribute,associatedTable, Scope.LOCAL,context,errors);
                System.out.println(errors.toString());
            }
            row[columnIndex] = concept;
            associatedTable.add(row);}
    }



}
