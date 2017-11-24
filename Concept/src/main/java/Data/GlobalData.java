package Data;

import AbstractObjects.ItemWithId;
import Attributes.Attribute;
import Concepts.Concept;
import DAGs.DAG;
import MISC.Namespace;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class GlobalData {

    private final static HashMap<Class,HashMap<String, ItemWithId>> items = new HashMap<>();

    public static void clear() {items.clear();}

    public static boolean exists(Class clazz, String name, Namespace namespace) {
        return exists(clazz,(namespace == null) ? name : namespace.getPathname(name));}

    public static boolean exists(Class clazz, String pathname) {
        HashMap<String, ItemWithId> map = items.get(clazz);
        if(map == null) {return false;}
        return map.containsKey(pathname);}

    public static ItemWithId getItem(Class clazz, String name, Namespace namespace) {
        return getItem(clazz,(namespace != null) ? name : namespace.getPathname(name));}

    public static ItemWithId getItem(Class clazz, String pathname) {
        HashMap<String, ItemWithId> map = items.get(clazz);
        return (map == null)  ? null : map.get(pathname);}


    public static ItemWithId getItem(String name, Namespace namespace) {
        return getItem((namespace != null) ? name : namespace.getPathname(name));}

    public static ItemWithId getItem(String pathname) {
        for(Map.Entry entry : items.entrySet()) {
            Class clazz = (Class)entry.getKey();
            ItemWithId item = ((HashMap<String, ItemWithId>)entry.getValue()).get(pathname);
            if(item != null) {return item;}}
        return null;}



    public static void addItem(ItemWithId item) {
        Class clazz = item.getClass();
        HashMap<String, ItemWithId> map = items.get(item.getClass());
        if(map == null) {map = new HashMap<>(); items.put(clazz,map);}
        map.put(item.getPathname(),item);}

    public static void removeItem(ItemWithId item) {
        Class clazz = item.getClass();
        HashMap<String, ItemWithId> map = items.get(clazz);
        if(map == null) {return;}
        map.remove(item.getPathname());
        if(map.isEmpty()) {items.remove(clazz);}}

    private final static DAG<Concept>   conceptHierarchy    = new DAG<Concept>("Concepts",(concept->concept.isIndividual()));
    private final static DAG<Attribute> attributeHierarchy  = new DAG<Attribute>("RelationHierarchy",(attribute->false));

    public static void addSubConcept(Concept superconcept, Concept subconcept) {
        conceptHierarchy.addSubnode(superconcept,subconcept);}

    public static void addSubAttribute(Attribute superAttribute, Attribute subAttribute) {
        attributeHierarchy.addSubnode(superAttribute,subAttribute);
    }

}
