package IO;

import Data.DataBlock;
import Utils.FileClassLoader;
import Utils.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/** This class provides general access to different kinds of files, e.g. xml-files, json-files key-value files etc.
 * For each file type there must be a particular reader, which can read the file and turn it into an object of a specific kind.
 * For example, a key-value file is turned into a HashMap&lt;String,String&gt;.
 * For each file type there must be a particular class with the following public methods:
 * <br>
 * - static String getFilenameExtension() which yields a regular expression for the filename extension it can read.<br>
 * - static String getFileType() which yields string that characterises the file type (maybe the same as the filename extension).<br>
 * - Object readStream(HashMap&lt;String,String&gt;, String name, InputStream stream, long length, StringBuilder errors). <br>
 *
 * These classes can either be part of the project,
 * or they can be stored in a separate directory from where they are loaded at run time.
 * Newer versions of such .class files can be loaded at any time by calling readFileReaderClasses.
 *
 * All File-Reader classes which are part of the project must have a code snippet<br>
 * static {FileReader.addReader([myName].class);}<br>
 * which registers itself at the FileReader class.
 * In addition the main-method must call [myName].getFilenameExtension();
 * which causes the class to be loaded and registered.
 */
public class FileReader {
    /** for loading FileReader classes. When it becomes redefined, the old version together with its classes become garbage.*/
    private static FileClassLoader classLoader;
    /** the list of all FileReaders */
    private static ArrayList<FileReader> fileReaders = new ArrayList<>();
    /** the list of system-internal FileReaders */
    private static ArrayList<FileReader> predefinedFileReaders = new ArrayList<>();

    /** the filename extension of the files the FileReader can read. */
    private Pattern filenameExtension;
    /** e.g. 'text', 'csv', 'key-value',... */
    private String fileType;
    /** the method for reading a stream */
    private Method readStream;
    /** the FileReader's class */
    private Class readerClass;

    public static void clear() {fileReaders.clear();}

    public static void reset() {
        fileReaders.clear();
        fileReaders.addAll(predefinedFileReaders);}

    /** This method reads all .class files from the given directory and registers them as File-Reader classes.
     * The directory should contain only FileReader classes as .class files.
     * The method can be called at any to read newer versions of the FileReader classes.
     *
     * @param directory with the File-Reader classes
     * @param ignore all files whose names matches this regular expression are ignored.
     * @param errors    for inserting error messages.
     * @return          true if there was no error.
     */
    public static boolean readFileReaderClasses(File directory, String ignore, StringBuilder errors)  {
        classLoader = new FileClassLoader(); // the previous class loader becomes garbage, together with the older versions for the FileReaser classes.
        reset();
        boolean okay = true;
        for(File file : directory.listFiles(classfile -> classfile.getName().endsWith(".class"))) {
            if(ignore != null && file.getName().matches(ignore)) {continue;}
            try{Class clazz = classLoader.loadClass(file);
                okay &= addReader(clazz,errors);}
            catch(Exception ex) {okay = false; errors.append(ex.toString());}}
        return okay;}


    /** This method should be called from the system-internal File-Reader classes,
     * e.g. static {FileReader.addReader(KeyValueFileReader.class);}
     * It registers the class as FileReader.
     * If the class does not conform to the conventions then a stack trace is printed and the system exits.
     *
     * @param clazz the file reader class.
     */
     public static void addReader(Class clazz)  {
         try{
            String name = clazz.getName();
            FileReader reader = new FileReader();
            Method fnExtension = clazz.getDeclaredMethod("getFilenameExtension");
            reader.filenameExtension = Pattern.compile((String)fnExtension.invoke(null));
             Method fileType = clazz.getDeclaredMethod("getFileType");
            reader.fileType = (String)fileType.invoke(null);
            reader.readStream = clazz.getDeclaredMethod("readStream",HashMap.class,String.class,InputStream.class,long.class,StringBuilder.class);
            reader.readerClass = clazz;
            predefinedFileReaders.add(reader);
            fileReaders.add(reader);}
         catch(Exception ex) {
                ex.printStackTrace();
                System.exit(1);}}


    /** This method adds an externally read FileReader class.
     * It checks whether all required methods are there and then registers the class.
     *
     * @param clazz  a class file of a FileReader class.
     * @param errors for inserting error messages.
     * @return true if there were no errors.
     */
    public static boolean addReader(Class clazz, StringBuilder errors) {
        String name = clazz.getName();
        boolean okay = true;
        FileReader reader = new FileReader();
        Method fnExtension = null;
        try {fnExtension = clazz.getDeclaredMethod("getFilenameExtension");}
        catch(Exception ex) {errors.append("Class '"+name+"' has no method 'getFilenameExtension'\n"); okay = false;}
        if(okay){try {reader.filenameExtension = Pattern.compile((String)fnExtension.invoke(null));}
                catch(Exception ex) {errors.append(ex.toString()); okay = false;}}
        try {reader.readStream = clazz.getDeclaredMethod("readStream",HashMap.class,String.class,InputStream.class,long.class,StringBuilder.class);}
        catch(Exception ex) {errors.append("Class '"+name+"' has no method 'readStream(HashMap,InputStream,long,StringBuilder)'\n"); okay = false;}
        if(okay) {
            reader.readerClass = clazz;
            fileReaders.add(reader);}
        return okay;}

    /** The method searches for a FileReader which can read files with the given filename extension.
     *
     * @param extension a filename extension, e.g. 'json'
     * @return the FileReader for these file classes, or null if none was found.
     */
    public static FileReader findReader(String extension) {
        for(FileReader fileReader : fileReaders) {
            if(fileReader.filenameExtension.matcher(extension).matches()) {return fileReader;}}
        return null;}


    /** This method reads at first the .meta file (if there is one) and then the file itself from the URL.
     * It uses the filename extension to determine the appropriate Reader.
     *
     * @param url    for the file
     * @param errors for inserting error messages.
     * @return  the contents of the file parsed into an object that depends on the file type.
     */
    public static DataBlock readFile(URL url, StringBuilder errors)  {
        String filename = url.getFile();
        String metaFilename = getMetaFilename(filename,errors);
        if(metaFilename == null) {return null;}
        HashMap<String,String> meta = null;
        InputStream metaStream = null;
        if(metaFilename != null) {
            try{
                URL metaURL = new URL(url.getProtocol(),url.getHost(),url.getPort(),metaFilename);
                try{metaStream = metaURL.openStream();} catch(Exception ex) {}
                if(metaStream != null) {meta = Utilities.parseKeyValueLines(Utilities.getLines(metaStream,"//","/*","*/",'\\',true),"=",errors);}}
            catch(Exception ex) {errors.append(ex.toString());}
            finally{try{metaStream.close();}catch(Exception ex) {}}}
        InputStream stream = null;
        DataBlock block = null;
        try{stream = url.openStream();
            block = readStream(meta,stream,filename,-1L,errors);
            if(block == null) {return null;}
            block.setURL(url);}
        catch(Exception ex) {errors.append(ex.toString());}
        finally {try{stream.close();}catch(Exception ex) {}}
        return block;}

    /** This method reads at first the .meta file (if there is one) and then the file itself.
     * It uses the filename extension to determine the appropriate Reader.
     *
     * @param file   the file
     * @param errors for inserting error messages.
     * @return  the contents of the file parsed into an object that depends on the file type.
     */
     public static DataBlock readFile(File file, StringBuilder errors)  {
         String filename = file.getName();
         String metaFilename = getMetaFilename(filename,errors);
         if(metaFilename == null) {return null;}
         HashMap<String,String> meta =  new HashMap<String,String>();
         InputStream metaStream = null;
         if(metaFilename != null) {
            try{
                File metaFile = new File(file.getParent(),metaFilename);
                if(metaFile.exists()) {
                    metaStream = new FileInputStream(metaFile);
                    meta = Utilities.parseKeyValueLines(Utilities.getLines(metaStream,"//","/*","*/",'\\',true),"=",errors);}}
            catch(Exception ex) {errors.append(ex.toString());}
            finally{try{metaStream.close();}catch(Exception ex) {}}}
         InputStream stream = null;
         DataBlock block = null;
         try{stream = new FileInputStream(file);
             block = readStream(meta,stream,filename,file.length(),errors);
             if(block == null) {return null;}
             block.setFile(file);}
         catch(Exception ex) {errors.append(ex.toString());}
         finally {try{stream.close();}catch(Exception ex) {}}
         return block;}


    /** determines the name of the .meta file
     *
     * @param filename the original filename
     * @param errors for inserting error messages
     * @return  the name of the .meta file, or null
     */
    private static String getMetaFilename(String filename, StringBuilder errors) {
        int index = filename.lastIndexOf(".");
        if(index < 0) {errors.append("File '" + filename + "' has no extension. Its type cannot be determined.\n"); return null;}
        return filename.substring(0,index)+".meta";}


    /** This method reads the file from the input stream.
     *
     * @param meta    the contents of the .meta file, or null
     * @param stream  for reading the file
     * @param filename the name of the file.
     * @param length   the length of the file in bytes, if this is known.
     * @param errors   for inserting error messages
     * @return         the contents of the file packed into a DataBlock.
     */
    private static DataBlock readStream(HashMap<String,String> meta, InputStream stream, String filename, long length, StringBuilder errors) {
        if(length == 0) {errors.append("'" + filename + "'  is emtpy.\n"); return null;}
        int index = filename.lastIndexOf(".");
        FileReader reader = findReader(filename.substring(index+1,filename.length()));
        if(reader == null) {errors.append("FileReader.readFile: unknown filename extension in '"+filename+"'\n");return null;}
        DataBlock block = null;
        try{
            Object data =  reader.readStream.invoke(reader.readerClass.newInstance(),meta,filename,stream,length,errors);
            if(meta.isEmpty()) {meta = null;}
            block = new DataBlock(reader.fileType,meta,data);}
        catch(Exception ex) {
            errors.append(ex.toString()).append("\n");}
        finally{try{stream.close();}catch(Exception ex) {}}
        return block;}


    /** returns a short description of the reader.
     *
     * @return a short description of the reader.
     */
    @Override
    public String toString() {
        String s = "FileReader " + readerClass.getName() + " for files with filename extensions '" + filenameExtension.pattern() +"'";
        try {Method toString = readerClass.getMethod("toString");
            s += "\n   default: "+toString.invoke(readerClass.newInstance());}
        catch(Exception ex) {}
        return s;}

    /** returns the descriptions of all registered file readers.
     *
     * @return the descriptions of all registered file readers.
     */
    public static String allToString() {
        StringBuilder s = new StringBuilder();
        for (FileReader reader : fileReaders) {s.append(reader.toString()).append("\n");}
        return s.toString();
    }



   public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

       String name = "/home/o/ohlbach/Lehre/InfoIII/Programme/inselThread.class";
       KeyValueFileReader.getFilenameExtension();
       StringBuilder errors = new StringBuilder();
       System.out.println(errors.toString());
       System.out.println(allToString());
       System.out.println(findReader("kv:"));
       System.out.println(findReader("kv"));

   }


}
