package IO;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;

/** This class reads and XML-Document from a stream (file or url) and parses it into a XML-Document object.
 */
public class XMLFileReader {

    static {FileReader.addReader(XMLFileReader.class);}

    /** returns the filename extension 'xml'
     *
     * @return the filename extension 'xml'
     */
    public static String getFilenameExtension() {return "xml";}

    /** return the file type 'xml'
     *
     * @return the file type 'xml'
     */
    public static String getFileType() {return "xml";}

    /** reads the entire stream contents and parses it into a Document object.
     *
     * @param meta the contents of the meta-file (not needed here).
     * @param name the name of the source file (not needed here).
     * @param stream  the input stream.
     * @param length if greater equal 0, then it is the number of bytes in the stream (not needed here).
     * @param errors for inserting error messages.
     * @return the stream contents as a Document object.
     */
    public Document readStream(HashMap<String,String> meta, String name, InputStream stream, long length, StringBuilder errors) {
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            return builder.parse(stream);}
        catch(Exception ex) {errors.append(ex.toString());}
        return null;}

    /** returns a description of the FileReader.
     *
     * @return a description of the FileReader.
     */
    @Override
    public String toString() {return "XML-File Reader";}
}
