package MISC;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class implements namespaces.
 * A namespace is a hierarchically structured sequence of strings,
 * very much like the package system in Java.<br>
 * Example: "LMU.fac16.ifi.pms"
 */

public class Namespace implements Serializable {
    /** the full applicationName from the root to the current namespace, separated by ".". */
    private String pathname;
    /** the upper namespace in the tree, or null */
    private Namespace superNamespace = null;
    /** the list of sub-namespaces */
    private ArrayList<Namespace> subNamespaces = null;
    /** maps full pathnames to namespace objects */
    private static HashMap<String,Namespace> namespaceMap = new HashMap<>();
    /** separates the components of a pathname */
    private static String separator = ".";

    /** This method just serializes the pathname.
     *
     * @param out  where to send the pathname
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out)  throws IOException {
        out.writeObject(pathname);}


    /** This method reads the pathname, and integrates it into the internal data structures.
     *
     * @param in    where to read the pathname from.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        pathname = (String)in.readObject();
        Namespace.setNamespace(pathname);
        Namespace namespace = namespaceMap.get(pathname);
        superNamespace = namespace.superNamespace;
        subNamespaces = namespace.subNamespaces;
        namespaceMap.put(pathname,this);
    }

    /** creates a new namespace.
     *
     * @param pathname  the local applicationName of the namespace
     * @param superNamespace the upper namespace in the tree, or null
     */
    private Namespace(String pathname, Namespace superNamespace) {
        this.pathname = pathname;
        this.superNamespace = superNamespace;
        namespaceMap.put(pathname,this);
        if(superNamespace == null) {return;}
        if(superNamespace.subNamespaces == null) {superNamespace.subNamespaces = new ArrayList<>();}
        superNamespace.subNamespaces.add(this);
    }

    /** clears the internal hash map */
    public static void clear() {namespaceMap.clear();}


    /** @return the pathname of the namespace */
    public String getPathname()  {return pathname;}

    /** extends the pathname with the given name.
     *
     * @param name any string.
     * @return pathname.name
     */
    public String getPathname(String name)  {return pathname+separator+name;}



    /** @return the namespace for a given pathname, or null if there is none */
    public static Namespace getNamespace(String pathname) {return namespaceMap.get(pathname);}


    /** splits a string into the namespace-part and the name.
     * The namespace-part is turned into a Namespace object.
     * All compoments of the namespace-part are also turned into Namespace objects.
     *
     * @param pathname  a '.'-separated string which ends with a name.
     * @return [Namespace, name]
     */
    public static Object[] splitPathname(String pathname) {
        int index = pathname.lastIndexOf(separator);
        if(index < 0) {return new Object[]{null,pathname};}
        return new Object[]{setNamespace(pathname.substring(0,index)),pathname.substring(index+1,pathname.length())};}

    /** All components of the pathname are turned into Namespace objects.
     *
     * @param pathname a '.'-separated pathname string
     * @return the corresponding Namespace object.
     */
    public static Namespace setNamespace(String pathname) {
        Namespace namespace = namespaceMap.get(pathname);
        if(namespace != null) {return namespace;}
        String[] parts = pathname.split("\\"+separator);
        int length = parts.length;
        namespace = null;
        Namespace superNamespace = null;
        pathname = "";
        for(int i = 0; i < length; ++i) {
            pathname += parts[i];
            namespace = namespaceMap.get(pathname);
            if(namespace == null) {namespace = new Namespace(pathname,superNamespace);}
            pathname += separator;
            superNamespace = namespace;}
        return namespace;}


    /** returns all root namespaces
     *
     * @return all root namespaces
     */
    public static ArrayList<Namespace> getTopNamespaces() {
        ArrayList<Namespace> topNamespaces = new ArrayList<>();
        namespaceMap.forEach((pathname,namespace) -> {if(namespace.superNamespace == null) {topNamespaces.add(namespace);}});
        return topNamespaces;}

    /** turns all namespaces into lines of a string.
     * If pathname is given, but unknown as namespace then the empty string is returned.
     *
     * @param pathname either null, or a pathname.
     * @return either all namespaces, or all namespaces below the pathname as a string.
     */
    public static String allToString(String pathname) {
        StringBuilder s = new StringBuilder();
        if(pathname == null || pathname.isEmpty()) {
            for(Namespace namespace : getTopNamespaces()) {allToStringRec(namespace,s);}}
        else {Namespace namespace = namespaceMap.get(pathname);
            if(namespace == null) {return "";}
            else {allToStringRec(namespace,s);}}
        s.deleteCharAt(s.length()-1);
        return s.toString();}

    private static void allToStringRec(Namespace namespace, StringBuilder strings) {
        strings.append(namespace.pathname).append("\n");
        if(namespace.subNamespaces != null) {
            for(Namespace nspace :namespace.subNamespaces) {allToStringRec(nspace,strings);}}}




    /** compares two namespaces by their pathnames
     *
     * @param object any object another namespace
     * @return true if the pathnames are equal.
     */
    @Override
    public boolean equals(Object object) {
        return object != null && object.getClass() == Namespace.class && pathname.equals(((Namespace)object).pathname);}



    /**
     * @return the pathname.
     */
    @Override
    public String toString() {return pathname;}



}
