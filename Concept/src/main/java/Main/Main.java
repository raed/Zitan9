package Main;

import IO.*;

import java.io.File;

/**
 */
public class Main {
    private static File configDirectory = null;

    public static void mainn(String[] args) {
        if(args.length != 1)               {System.err.println("You must provide the config directory.\nThe programm is terminated."); return;}
        configDirectory = new File(args[0]);
        if(!configDirectory.isDirectory()) {System.err.println("'"+args[0] +"' is not a config directory.\nThe programm is terminated."); return;}
    }


    /** This method causes the Reader-class files to be loaded and the readers to be registered in the FileReader class.
     */
    public static void loadReaders() {
        KeyValueFileReader.getFilenameExtension();
        TextFileReader.getFilenameExtension();
        CSVFileReader.getFilenameExtension();
        XMLFileReader.getFilenameExtension();
        JsonFileReader.getFilenameExtension();
    }

     public static void main(String[] args) {
         loadReaders();
         String dir = "/home/proj/www-all/pms/lehre/betriebssys/16ws17/Folien/a";
         mainn(new String[]{dir});
         System.out.println(System.getProperty("user.dir"));
         String s = System.getProperties().toString().replaceAll(",","\n");
         System.out.println(s);


     }

}
