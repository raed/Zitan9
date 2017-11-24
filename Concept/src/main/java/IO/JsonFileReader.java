package IO;


import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;
import java.util.HashMap;

/**
 *  This class reads and Json-Document from a stream (file or url) and parses it into a JsonObject.
 */
public class JsonFileReader {
    static {
        FileReader.addReader(JsonFileReader.class);}

    /** returns the filename extension 'json'.
     *
     * @return the filename extension 'json'
     */
    public static String getFilenameExtension() {return "json";}

    /** returns the file type 'json'.
     *
     * @return the file type 'json'
     */
    public static String getFileType() {return "json";}

    /** reads the entire stream and parses it into a JsonObject.
     *
     * @param meta the contents of the meta-file (not needed here).
     * @param name the name of the source file (not needed here).
     * @param stream  the input stream
     * @param length if greater equal 0, then it is the number of bytes in the stream (not needed here).
     * @param errors for inserting error messages
     * @return the stream contents as a JSonObject
     */
    public JsonObject readStream(HashMap<String,String> meta, String name, InputStream stream, long length, StringBuilder errors) {
        try{
            javax.json.JsonReader rdr = Json.createReader(stream);
            return rdr.readObject();}
        catch(Exception ex) {errors.append(ex.toString());}
        return null;}

    /** returns a description of the FileReader.
     *
     * @return a description of the FileReader.
     */
    @Override
    public String toString() {return "Json-File Reader";}

}
