package MISC;

import Utils.Messanger;

import java.util.HashMap;

public class Commons {
    public static HashMap<Messanger.MessangerType,Messanger> messangers = new HashMap();

    public static Messanger getMessanger(Messanger.MessangerType type) {
        synchronized(messangers) {
            Messanger messanger = messangers.get(type);
            if(messanger == null) {
                messanger = new Messanger(type);
                messangers.put(type,messanger);}
            return messanger;}}

    public static void insertMessage(Messanger.MessangerType type, String messageType, String message) {
        Messanger messanger = getMessanger(type);
        messanger.insert(messageType,message);
    }

}
