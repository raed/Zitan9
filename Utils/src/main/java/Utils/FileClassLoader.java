package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This implements a class loader which loads a class from a file.
 */
public class FileClassLoader  extends ClassLoader{


    /** loads a class from a file.
     *
     * @param file the .class file
     * @return the loaded class
     * @throws IOException if something goes wrong.
     */
    public Class loadClass(File file) throws IOException {
        InputStream stream = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        stream.read(bytes);
        Class clazz = defineClass(null,bytes,0,bytes.length);
        resolveClass(clazz);
        return clazz;}
}
