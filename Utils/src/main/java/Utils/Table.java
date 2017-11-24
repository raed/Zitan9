package Utils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class implements a matrix data structure, i.e. an ArrayList of T[] arrays.
 *  It provides in particular various kinds of streams for iterating over the matrix.
 *  The rows in the matrix are always kept at equal length, if necessary by inserting null-elements.
 */

public class Table<T> implements Serializable {
    /** the actual matrix */
    private ArrayList<T[]> table;
    /** the number of columns in the matrix */
    private int columns = 0;

    /** constructs an empty matrix */
    public Table() {
        table = new ArrayList<T[]>();}

    /** constructs an empty matrix with a default number of columns
     *
     * @param columns the initial number of columns
     */
    public Table(int columns) {
        assert columns > 0;
        this.columns = columns;
        table = new ArrayList<T[]>();}

    /** @return the current number of columns.*/
    public int nColumns() {return columns;}

    /** @return the current number of rows. */
    public int nRows() {return table.size();}


    /** adds a new row at the end of the matrix.
     *
     * The rows of the matrix are kept at equal length by inserting null elements if necessary.
     *
     * @param row the row to be added.
     */
    public void add(T[] row) {
        if(columns != 0) {
            if(row.length < columns) {row = Arrays.copyOf(row,columns);}
            else {
                if(row.length > columns) {
                    columns = row.length;
                    for(int i = 0; i < table.size(); ++i) {table.set(i,Arrays.copyOf(table.get(i),columns));}}}}
        else {columns = row.length;}
        table.add(row);}

    /** access the row at the given index
     *
     * @param rowIndex the row position
     * @return the row at this position
     */
    public T[] getRow(int rowIndex) {
        assert rowIndex >= 0 && rowIndex < table.size();
        return table.get(rowIndex);}

    /** yields the item at the given position
     *
     * @param rowIndex    the row's position
     * @param columnIndex the column's position
     * @return the item at the given position.
     */
    public T getItem(int rowIndex, int columnIndex) {
        assert columnIndex >= 0 && columnIndex < columns;
        assert rowIndex    >= 0 && rowIndex    < table.size();
        return table.get(rowIndex)[columnIndex];}

    /** overwrites the item at the given position
     *
     * @param rowIndex    the row's position
     * @param columnIndex the column's position
     * @param item        the item to be put at this position.
     * @return            the old item at this position.
     */
    public T setItem(int rowIndex, int columnIndex, T item) {
        assert columnIndex >= 0 && columnIndex < columns;
        assert rowIndex    >= 0 && rowIndex    < table.size();
        T[] row = table.get(rowIndex);
        T oldItem = row[columnIndex];
        row[columnIndex] = item;
        return oldItem;}

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        int size = table.size();
        for(int i = 0; i < size; ++i) {
            T[] row = table.get(i);
            s.append(Utilities.join(row,", ",(item ->  (item == null) ? "null" : item.toString())));
            if(i < size-1) {s.append("\n");}}
        return s.toString();}

    /** @return the sequential stream of rows */
    public Stream<T[]> rowsStream() {return table.stream();}

    /** @return a parallel stream of rows. */
    public Stream<T[]> rowsStreamParallel() {return table.parallelStream();}

    /** generates a stream of items at the given row index.
     *
     * @param rowIndex  the row's position
     * @return the stream of items of this row.
     */
    public Stream<T> rowStream(int rowIndex) {
        assert rowIndex >= 0 && rowIndex < table.size();
        return Arrays.stream(table.get(rowIndex));}

    /** generates a sub-stream of items at the given row index
     *
     * @param rowIndex       the row's position
     * @param startInclusive the start column index
     * @param endExclusive   the end column index
     * @return the stream of items of the row between start and end.
     */
    public Stream<T> rowStream(int rowIndex, int startInclusive, int endExclusive) {
        assert rowIndex >= 0 && rowIndex < table.size();
        assert startInclusive >= 0 && startInclusive < columns;
        assert endExclusive >= 0 && endExclusive <= columns;
        assert startInclusive < endExclusive;
        return Arrays.stream(table.get(rowIndex),startInclusive,endExclusive);}

    /** generates a stream of items at the given column index.
     *
     * @param colIndex  the columns's position
     * @return the stream of items of this column.
     */
    public Stream<T> columnStream(int colIndex) {
        assert colIndex >= 0 && colIndex < columns;
        int endExclusive = table.size();
        return StreamSupport.stream(Spliterators.spliterator(new SingleColumnIterator(colIndex,0,endExclusive),
                endExclusive, Spliterator.SIZED+Spliterator.CONCURRENT),false);}


    /** generates a sub-stream of items at the given column index.
     *
     * @param colIndex  the columns's position
     * @param startInclusive the start row index
     * @param endExclusive   the end row index
     * @return the stream of items of this column.
     */
    public Stream<T> columnStream(int colIndex, int startInclusive, int endExclusive) {
        assert colIndex >= 0 && colIndex < columns;
        assert startInclusive >= 0 && startInclusive <= table.size();
        assert endExclusive >= 0 && endExclusive <= table.size();
        assert startInclusive  < endExclusive;
        return StreamSupport.stream(Spliterators.spliterator(new SingleColumnIterator(colIndex,startInclusive,endExclusive),
                endExclusive-startInclusive,Spliterator.SIZED+Spliterator.CONCURRENT),false);}

    /** generates a stream of tuples which are extracted from different column positions in the rows
     * The tuples are stored in a single little array. The array is reused for each new tuple.
     *
     * @param colIndices the different column positions
     * @return the stream of tuples with values extracted from the column positions.
     */
    public Stream<T[]> columnStream(int[] colIndices) {
        assert checkColumnIndices(colIndices);
        int endExclusive = table.size();
        return StreamSupport.stream(Spliterators.spliterator(new MultipleColumnIterator(colIndices,0,endExclusive),
                endExclusive,Spliterator.SIZED),false);}

    /** generates a sub-stream of tuples which are extracted from different column positions in the rows
     * The tuples are stored in a single little array. The array is reused for each new tuple.
     *
     * @param colIndices the different column positions
     * @param startInclusive the start row index
     * @param endExclusive   the end row index
     * @return the stream of tuples with values extracted from the column positions.
     */
    public Stream<T[]> columnStream(int[] colIndices, int startInclusive, int endExclusive) {
        assert startInclusive >= 0 && startInclusive <= table.size();
        assert endExclusive >= 0 && endExclusive <= table.size();
        assert checkColumnIndices(colIndices);
        return StreamSupport.stream(Spliterators.spliterator(new MultipleColumnIterator(colIndices,startInclusive,endExclusive),
                endExclusive-startInclusive,Spliterator.SIZED),false);}

    private boolean checkColumnIndices(int[] colIndices) {
        for(int i : colIndices) {
            if(!(i >= 0 && i < columns)) {return false;}}
        return true;}


    /** This implements an iterator which iterates over a particular column in the table.
     */
    private class SingleColumnIterator implements Iterator {
        private int colIndex;
        private int rowIndex = 0;
        private int endExclusive;

        public SingleColumnIterator(int colIndex, int startInclusive, int endExclusive) {
            this.colIndex = colIndex;
            this.endExclusive = endExclusive;
            rowIndex = startInclusive;}

        @Override
        public boolean hasNext() {
            return rowIndex < endExclusive;}

        @Override
        public T next() {
            return table.get(rowIndex++)[colIndex];}
    }

    /** This implements an iterator which iterates over the rows and
     * collects items at given column positions in a little array.
     * The array is generated once and then changed in the iteration.
     *
     */
    private class MultipleColumnIterator implements Iterator {
        int[] colIndices;
        int size;
        int rowIndex = 0;
        int endExclusive;
        Object[] items;

        public MultipleColumnIterator(int[] colIndices, int startInclusive, int endExclusive) {
            this.colIndices = colIndices;
            this.endExclusive = endExclusive;
            size = colIndices.length;
            rowIndex = startInclusive;
            items = new Object[size];}

        @Override
        public boolean hasNext() {
            return rowIndex < endExclusive;}

        @Override
        public T[] next() {
            for(int i = 0; i< size; ++i) {
                items[i] = table.get(rowIndex)[colIndices[i]];}
            ++rowIndex;
            return (T[])items;}
    }

}
