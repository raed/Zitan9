package IO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/** The instances of this class can read csv-files and parse them into an ArrayList&lt;String[]&gt;
 */
public class CSVFileReader {
    private String separator = ";";

    /** returns the filename extension: csv.
     *
     * @return the filename extension: csv
     */
    public static String getFilenameExtension() {return "csv";}

    /** returns the file type: csv.
     *
     * @return the file type : csv
     */
    public static String getFileType() {return "csv";}

    /** This method reads a csv-file and parses it into an ArrayList&lt;String[]&gt;
     * By default the separator is ';'. It can be changed in the .meta file, e.g. 'separator = ,'.
     *
     *
     * @param meta     a Hash-Map with meta-information, for example 'separator'
     * @param filename the original file name
     * @param stream   the InputStream
     * @param length   the length of the file, if known, otherwise &lt; 0.
     * @param errors   for inserting error messages
     * @return         the file contents as ArrayList&lt;String[]&gt; or null.
     */
    ArrayList<String[]> readStream(HashMap<String,String> meta, String filename, InputStream stream, long length, StringBuilder errors) {
        if(meta != null && meta.containsKey("separator")) {separator = meta.get("separator");}
        String sep = "\\s*"+separator+"\\s*";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ArrayList<String[]> lines = new ArrayList<>();
        reader.lines().forEach(line->lines.add(line.trim().split(sep)));
        return lines;}

    /** returns a description of the FileReader.
     *
     * @return a description of the FileReader.
     */
    @Override
    public String toString() {
        return "CSV-File Reader with separator: '" + separator + "'";}

}
