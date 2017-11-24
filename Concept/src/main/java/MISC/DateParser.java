package MISC;

import java.time.LocalDateTime;

import static Utils.Messanger.MessangerType.DataErrors;

public class DateParser {

    public static LocalDateTime parse(String string) {
        try{return LocalDateTime.parse(string);}
        catch(Exception ex) {
            Commons.getMessanger(DataErrors).insert("Typo",ex.getMessage());}
        return null;}
}
