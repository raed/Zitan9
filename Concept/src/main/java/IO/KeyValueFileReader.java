package IO;

import Utils.Utilities;

import java.io.InputStream;
import java.util.HashMap;

/** This class provides a FileReader for reading files with key-value pairs from files with filename extension 'kv'.
 *  These are text files where each line contains a key-value pair, for example 'name = John".
 *  <br>
 *  The file can contain two kinds of comments:<br>
 *      - if a line contains // then the rest of the line is skipped.<br>
 *      - text between &frasl;* .. *&frasl; which may span over several lines, is ignored.
 *      <br>
 *  Line breaks are ignored if the last character of a line is \<br>
 *  The key-value pairs are separated by =.
 *  <br>
 *  These special signs, //, &frasl;, \, = are the default.<br>
 *  If the filename extension contains an additional character, for example 'kv:' then
 *  this character becomes the separator sign for the key-value pairs.
 *  <br>
 *  The defaults can be changed in a second way. <br>
 *  If the file is accompanied by a second key-value file with the same name, but extension .meta,
 *  then other versions of the special symbols can be defined.
 *  <br>
 *  As an example, this file may contain: <br>
 *  oneLineComment    = %;<br>
 *  openComment       = [;<br>
 *  closeComment      = ]<br>
 *  lineContinuer     = $;<br>
 *  keyValueSeparator = :<br>
 *
 *  In this case these special symbols are used to parse the file.
 *  <br>
 *  The key-value file may contain an entry 'meta = key_1,...,key_n'.
 *  In this case the keys are taken to be part of the .meta file.
 *  The method saves to write a separate .meta file.
 *  The above mentioned keywords, however, cannot be in the file itself.
 *  <br>
 *  The file is parsed and turned into a HashMap&lt;String,String&gt;
 */
public class KeyValueFileReader {
    private String oneLineComment    = "//";
    private String openComment       = "/*";
    private String closeComment      = "*/";
    private char   lineContinuer     = '\\';
    private String keyValueSeparator = "=";
    private boolean trim = true;

    static {FileReader.addReader(KeyValueFileReader.class);}

    /** yields the allowed filename extensions
     *
     * @return the allowed filename extensions as a regular expression.
     */
    public static String getFilenameExtension() {return "kv.?";}

    /** return the file type key-value
     *
     * @return the file type 'key-value'
     */
    public static String getFileType() {return "key-value";}


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
    public HashMap<String,String> readStream(HashMap<String,String> meta, String name, InputStream stream, long length, StringBuilder errors) {
       if(meta != null) {
            if(meta.containsKey("oneLineComment"))    {oneLineComment    = meta.get("oneLineComment");}
            if(meta.containsKey("openComment"))       {openComment       = meta.get("openComment");}
            if(meta.containsKey("closeComment"))      {closeComment      = meta.get("closeComment");}
            if(meta.containsKey("lineContinuer"))     {lineContinuer     = meta.get("lineContinuer").charAt(0);}
            if(meta.containsKey("keyValueSeparator")) {keyValueSeparator = meta.get("keyValueSeparator");}
            if(meta.containsKey("trim"))              {trim = !meta.get("trim").equals("false");};}

        String extension = name.substring(name.lastIndexOf(".")+1,name.length());
        if(extension.length() > 2) {keyValueSeparator = extension.substring(2,extension.length());}
        HashMap<String,String> map = Utilities.parseKeyValueLines(Utilities.getLines(stream,oneLineComment,openComment,closeComment,lineContinuer,trim),keyValueSeparator,errors);
        if(map == null) {return map;}
        assert meta != null;
        if(map.containsKey("meta")) {
            String[] metaKeys = meta.get("meta").split("\\s*,\\s*");
            map.remove("meta");
            for(String key : metaKeys) {
                if(map.containsKey(key)) {meta.put(key,map.get(key)); map.remove(key);}}}
        return map;}

    /** returns a description of the FileReader.
     *
     * @return a description of the FileReader.
     */
    @Override
    public String toString() {
        return "Key-Value File: " + oneLineComment + "," + openComment + "," + closeComment+","+lineContinuer+ "," +keyValueSeparator + "," + trim;}

}
