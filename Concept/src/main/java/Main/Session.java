package Main;

import AbstractObjects.ItemWithId;
import Data.GlobalData;
import MISC.Namespace;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class Session extends Thread {
    private final HashMap<Class,HashMap<String, ItemWithId>> items = new HashMap<>();

    public Session(String name) {
        super(name);
        setName(name+getId());}


    public boolean exists(Class clazz, String pathname) {
        if(GlobalData.exists(clazz,pathname)) {return true;}
        HashMap<String, ItemWithId> map = items.get(clazz);
        return map != null && map.containsKey(pathname);}

    public boolean exists(Class clazz, String name, Namespace namespace) {
        return exists(clazz,(namespace == null) ? name : namespace.getPathname(name));}

    public ItemWithId getItem(Class clazz, String pathname) {
        ItemWithId item = GlobalData.getItem(clazz,pathname);
        if(item != null) {return item;}
        HashMap<String, ItemWithId> map = items.get(clazz);
        return (map == null) ? null :  map.get(pathname);}

    public ItemWithId getItem(Class clazz, String name, Namespace namespace) {
        return getItem(clazz,(namespace == null) ? name : namespace.getPathname(name));}

    public ItemWithId getItem(String pathname) {
        ItemWithId item = GlobalData.getItem(pathname);
        if(item != null) {return item;}
        for(Map.Entry entry : items.entrySet()) {
            Class clazz = (Class)entry.getKey();
            item = ((HashMap<String, ItemWithId>)entry.getValue()).get(pathname);
            if(item != null) {return item;}}
        return null;}

    public ItemWithId getItem(String name, Namespace namespace) {
        return getItem((namespace == null) ? name : namespace.getPathname(name));}


    public void addItem(ItemWithId item) {
        Class clazz = item.getClass();
        HashMap<String, ItemWithId> map = items.get(item.getClass());
        if(map == null) {map = new HashMap<>(); items.put(clazz,map);}
        map.put(item.getPathname(),item);}



    public void run() {
        System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getId());
    }


    public static void main(String[] args) {
        //new Main.Session("test").start();
    }

}
