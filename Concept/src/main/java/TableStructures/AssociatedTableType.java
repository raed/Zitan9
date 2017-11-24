package TableStructures;

import AbstractObjects.DataType;
import MISC.Context;

import java.io.Serializable;
import java.util.ArrayList;

/** This is best explained by an example.
 *
 * Consider a table type with columns "student, semester, mark",
 * for example to be used for exam results.<br>
 * If we want to collect each student's exam results, we could use another table
 * with columns "exam, semester, mark" to be used as attribute value for an attribute "exam-results".
 * This is a table type which is derived from the original table by replacing a particular column with a new column.
 */
public class AssociatedTableType extends TableType {
    private TableType originalTableType;
    protected int columnIndex;


    /** generates for a table type an associated table type.
     *
     * @param applicationName   the name of the table type (e.g. "exam-results")
     * @param originalTableType the original table type
     * @param oldColumnName     the column name of the original table to be replaced by the new column
     * @param newColumnName     the new column name.
     * @param newColumnType     the new column type
     * @param context           the context.
     */
    public AssociatedTableType(String applicationName, TableType originalTableType, String oldColumnName, String newColumnName, DataType newColumnType, Context context) {
        super(applicationName, (ArrayList<DataType>)originalTableType.columnTypes.clone(), (ArrayList<String>)originalTableType.columnNames.clone(), context);
        Integer colIndex = originalTableType.getColumnIndex(oldColumnName);
        assert colIndex != null;
        columnIndex = colIndex;
        this.originalTableType = originalTableType;
        columnTypes.set(colIndex,newColumnType);
        columnNames.set(colIndex,newColumnName);
    }






}
