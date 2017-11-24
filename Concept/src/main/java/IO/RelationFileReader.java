package IO;

import Utils.Utilities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/** This class provides a FileReader for reading files with key-value pairs from files with filename extension 'rel'.
 *  These are text files where each line contains a 'subject relation object' information , for example 'name = John".
 *  'subject' and 'object' may be 'separator'-separated strings (without blanks).
 *  <br>
 *  The file can contain two kinds of comments:<br>
 *      - if a line contains // then the rest of the line is skipped.<br>
 *      - text between &frasl;* .. *&frasl; which may span over several lines, is ignored.
 *      <br>
 *  Line breaks are ignored if the last character of a line is \<br>
 *  <br>
 *  These special signs, //, &frasl;, \, are the default.<br>
 *  <br>
 *  The defaults can be changed in a second way. <br>
 *  If the file is accompanied by a key-value file with the same name, but extension .meta,
 *  then other versions of the special symbols can be defined.
 *  <br>
 *  As an example, this file may contain: <br>
 *  oneLineComment    = %;<br>
 *  openComment       = [;<br>
 *  closeComment      = ]<br>
 *  lineContinuer     = $;<br>
 *  separator         = ;<br>
 *  trim              = false<br>
 *
 *  In this case these special symbols are used to parse the file.
 *  <br>
 *  The relation file may contain an entry 'meta = key_1,...,key_n'.
 *  In this case the keys are taken to be part of the .meta file.
 *  The method saves to write a separate .meta file.
 *  The above mentioned keywords, however, cannot be in the file itself.
 *  <br>
 *  The file is parsed and turned into a ArrayList&lt;Object[]&gt;
 *  where the elements of the ArrayList are Arrays [relation, left, right].
 *  'relation' is the relation name, 'left' and 'right' are ArrayList&lt;String&gt;
 *  with the 'separator'-split subject and object parts.
 */
public class RelationFileReader {
    private String oneLineComment    = "//";
    private String openComment       = "/*";
    private String closeComment      = "*/";
    private char   lineContinuer     = '\\';
    private String separator = ",";
    private boolean trim = true;

    static {FileReader.addReader(RelationFileReader.class);}

    /** yields the allowed filename extensions
     *
     * @return the allowed filename extensions as a regular expression.
     */
    public static String getFilenameExtension() {return "rel";}

    /** return the file type key-value
     *
     * @return the file type 'key-value'
     */
    public static String getFileType() {return "relations";}


    /** This method reads a Key-Value file and parses it into a HashMap.
     * The HashMap may contain, for example <br>
     *  oneLineComment    = %;<br>
     *  openComment       = [;<br>
     *  closeComment      = ]<br>
     *  lineContinuer     = $;<br>
     *  keyValueSeparator = :<br>
     *
     * @param meta   the contents of the .meta-file. It may contain
     * @param name  the file name
     * @param stream the stream
     * @param length the length in bytes, if &lt; 0 the no length is unknown.
     * @param errors for inserting error messages
     * @return key-values as a HashMap.
     */
    public ArrayList<Object[]> readStream(HashMap<String,String> meta, String name, InputStream stream, long length, StringBuilder errors) {
        if(meta != null) {
            if(meta.containsKey("oneLineComment"))    {oneLineComment    = meta.get("oneLineComment");}
            if(meta.containsKey("openComment"))       {openComment       = meta.get("openComment");}
            if(meta.containsKey("closeComment"))      {closeComment      = meta.get("closeComment");}
            if(meta.containsKey("lineContinuer"))     {lineContinuer     = meta.get("lineContinuer").charAt(0);}
            if(meta.containsKey("keyValueSeparator")) {separator = meta.get("separator");}
            if(meta.containsKey("trim"))              {trim = !meta.get("trim").equals("false");}}
        ArrayList<Object[]> lines = Utilities.parseRelationLines(Utilities.getLines(stream,oneLineComment,openComment,closeComment,lineContinuer,trim),separator,errors);
        if(lines.isEmpty()) {return lines;}
        assert meta != null;
        ArrayList<String> metaKeys = null;
        for(Object[] line : lines) {
            if(line[0].equals("=")) {
                ArrayList<String> left = (ArrayList<String>)line[1];
                ArrayList<String> right = (ArrayList<String>)line[2];
                if(left.size() == 1 && left.get(0).equals("meta")) {metaKeys = right;}}}
        if(metaKeys == null) {return lines;}
        for(int i = 0; i < lines.size(); ++i) {
            Object[] line = lines.get(i);
            if(line[0].equals("=")) {
                ArrayList<String> left = (ArrayList<String>)line[1];
                ArrayList<String> right = (ArrayList<String>)line[2];
                if(left.size() == 1) {
                    String lft = (String)left.get(0);
                    if(lft.equals("meta")) {lines.remove(i--); continue;}
                    if(metaKeys.contains(lft)) {
                        meta.put(lft, Utilities.join(right,",",(l->l)));
                        lines.remove(i--);}}}}
        return lines;}

    /** returns a description of the FileReader.
     *
     * @return a description of the FileReader.
     */
    @Override
    public String toString() {
        return "Relation File: " + oneLineComment + "," + openComment + "," + closeComment+","+lineContinuer+ "," +separator + "," + trim;}

}
