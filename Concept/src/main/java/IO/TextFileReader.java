package IO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/** This class implements a reader which reads text files into a string.
 */
public class TextFileReader {
    static {
        FileReader.addReader(TextFileReader.class);}

    /** returns the filename extension pattern: txt or text.
     *
     * @return the filename extension pattern
     */
    public static String getFilenameExtension() {
        return "te?xt";}

    /** return the file type 'text'
     *
     * @return the file type 'text'
     */
     public static String getFileType() {
        return "text";}

    /** reads the entire text from the file.
     *
     * @param meta the contents of the meta-file (not needed here).
     * @param name the name of the source file.
     * @param stream  the input stream
     * @param length if greater equal 0, then it is the number of bytes in the stream.
     * @param errors for inserting error messages
     * @return the stream contents as a string.
     */
    public String readStream(HashMap<String,String> meta, String name, InputStream stream, long length, StringBuilder errors) {
        try {
            if(length > 0 && length < Integer.MAX_VALUE) {
                byte[] bytes = new byte[(int)length];
                stream.read(bytes);
                return new String(bytes);}
            else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder s = new StringBuilder();
                reader.lines().forEach(line->s.append(line).append("\n"));
                s.deleteCharAt(s.length()-1);
                return s.toString();}}
        catch(Exception ex) {errors.append(ex.toString());}
        return null;}

    /** returns a description of the FileReader.
     *
     * @return a description of the FileReader.
     */
    @Override
    public String toString() {return "Text-File Reader";}

}
