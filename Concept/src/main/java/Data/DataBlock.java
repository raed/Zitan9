package Data;

import AbstractObjects.ItemWithId;
import MISC.Context;
import MISC.Namespace;
import Utils.TriFunction;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 */
public class DataBlock extends ItemWithId{
    private HashMap<String,String> metaData;
    private Object rawData;
    private File file;
    private URL url;
    private LocalDateTime lastChanged = null;
    private String fileType;
    private static int counter = 0;

    private static HashMap<String,DataBlock> permanentDataBlocks = new HashMap<>();
    private static HashMap<String,DataBlock> temporaryDataBlocks = new HashMap<>();

    private void initialize() {
        if(metaData == null) {return;}
        String pathname;
        if(metaData.containsKey("name")) {
            pathname = metaData.get("name");
            Object[] nsp = Namespace.splitPathname(pathname);
            setName((String)nsp[1],(Namespace)nsp[0]);}
        else {pathname = "datablock"+(counter++);
            setName(pathname);}
        temporaryDataBlocks.put(pathname,this);}


    public DataBlock(Object rawData, HashMap<String,String> metaData) {
        this.rawData  = rawData;
        this.metaData = metaData;
        initialize();}

    public DataBlock(String fileType, HashMap<String,String> metaData,Object rawData) {
        this.fileType = fileType;
        this.metaData = metaData;
        this.rawData = rawData;
        initialize();}

    public static void clear() {
        permanentDataBlocks.clear();
        temporaryDataBlocks.clear();
        counter = 0;}

    /** clears the temporary items list */
    public static void undo() {
        temporaryDataBlocks.clear();}

    /** makes all temporary items permanent.
     */
    public static void makePermanent() {
        permanentDataBlocks.putAll(temporaryDataBlocks);
        temporaryDataBlocks.clear();}

    public void setFile(File file) {
        this.file = file;
        long modified = file.lastModified();
        lastChanged = LocalDateTime.ofEpochSecond(modified/1000,(int)((modified % 1000)*1000), ZoneOffset.ofHours(1));}

    public void setURL(URL url) {
        this.url = url;}

    public Object getRawData() {return rawData;}

    public HashMap<String,String> getMetaData() {return metaData;}

    public Namespace getNamespace() {
        if(metaData == null) {return null;}
        String pathname = metaData.get("namespace");
        if(pathname == null) {return null;}
        return Namespace.getNamespace(pathname);}

    private static HashMap<String,HashMap<String,TriFunction<DataBlock,Context,StringBuilder,Boolean>>> checkers = new HashMap<>();

    public static void addChecker(String fileType, String dataType, TriFunction<DataBlock,Context,StringBuilder,Boolean> function) {
        HashMap<String, TriFunction<DataBlock,Context,StringBuilder,Boolean>> map = checkers.get(fileType);
        if(map == null) {
            map = new HashMap<String, TriFunction<DataBlock,Context,StringBuilder,Boolean>>();
            checkers.put(fileType, map);}
        map.put(dataType,function);}

    private static HashMap<String,HashMap<String,TriFunction<DataBlock,Context,StringBuilder,Boolean>>> parsers = new HashMap<>();

    public static void addParser(String fileType, String dataType, TriFunction<DataBlock,Context,StringBuilder,Boolean> function) {
        HashMap<String, TriFunction<DataBlock,Context,StringBuilder,Boolean>> map = parsers.get(fileType);
        if(map == null) {
            map = new HashMap<String, TriFunction<DataBlock,Context,StringBuilder,Boolean>>();
            parsers.put(fileType, map);}
        map.put(dataType,function);}



    public boolean internalize(Context context, StringBuilder errors) {
        String dataType = metaData.get("datatype");
        if(dataType == null) {errors.append("No datatype specified in " + file.toString()+".\n"); return false;}
        boolean okay = true;
        HashMap<String, TriFunction<DataBlock,Context,StringBuilder,Boolean>> map = parsers.get(fileType);
        if(map == null) {errors.append("No parser for file type: '" + fileType + "' and data type '"+dataType+"'.\n"); return false;}
        TriFunction<DataBlock,Context,StringBuilder,Boolean> function = map.get(dataType);
        if(function == null) {errors.append("No parser for file type: '" + fileType + "' and data type '"+dataType+"'.\n"); return false;}
        return function.apply(this,context,errors);
    }



}
