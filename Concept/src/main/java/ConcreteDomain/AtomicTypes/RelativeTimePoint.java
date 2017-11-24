package ConcreteDomain.AtomicTypes;

import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import MISC.Commons;
import MISC.Context;
import UnitsOfMeasurement.GregorianTimeUnit;
import Utils.Utilities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class represents relative time points like
     * 10:15  or 10:15:6  (hour minute second)<br>
     * week 3 10:15  (every third day in a week, hour minute second)<br>
     * month 3 10:15 (every third month in a week, hour minute second)<br>
     * 3 w 10:15  (every third week in a year, hour minute second)<br>
     * 2 m 10:15 (every third month in a yead, hour minute second)<br>
     * 3 m 2 d 10:15 (every third month, every second day in this month, hour minute second)<br>
     * and similar. 
     * 
 *
 */
public class RelativeTimePoint extends AtomicObject implements Serializable {
    private  int[] values = null;
    private  GregorianTimeUnit[] units = null;
    /**if true then the days are interpreted as days in a week, otherwise as days in a month*/
    private final boolean weekly;
    private final boolean monthly;
    
    /** contructs a RelativeTimePointObject.
     * The dates are normalized, e.g. 8 days becomes 1 week, 1 day.
     * 
     * @param values the values 
     * @param units  the corresponding time units.
     * @param weekly if true then the days are interpreted as days in a week
     * @param monthly if true then the days are interpreted as days in a month
     */
    public RelativeTimePoint(int[] values, GregorianTimeUnit[] units, boolean weekly, boolean monthly) {
        assert(values.length == units.length);
        assert(values.length > 0);
        this.values = values;
        this.units = units;
        this.weekly = weekly;
        this.monthly = monthly;}
    
    /** contructs a RelativeTimePointObject.
     * The dates are normalized, e.g. 8 days becomes 1 week, 1 day.
     * 
     * @param values the values 
     * @param units  the corresponding time units.
     * @param weekly if true then the days are interpreted as days in a week
     * @param monthly if true then the days are interpreted as days in a month
     */
    public RelativeTimePoint(ArrayList<Integer> values, ArrayList<GregorianTimeUnit> units, boolean weekly, boolean monthly) {
        int size = values.size();
        assert(size == units.size());
        assert(size > 0);
        this.values = new int[size]; 
        this.units  = new GregorianTimeUnit[size];
        for(int i = 0; i < size; ++i) {this.values[i]= values.get(i); this.units[i] = units.get(i);}
        this.weekly = weekly;
        this.monthly = monthly;}
    
   
   
  
    
    /** checks the two objects for equality.
     * 
     * @param object the other DurationObject 
     * @return true if the two are equal.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof RelativeTimePoint)) {return false;}
        RelativeTimePoint other = (RelativeTimePoint)object;
        if(values.length != other.values.length || weekly != other.weekly || monthly != other.monthly) {return false;}
        for(int i = 0; i < values.length; ++i) {
            if(values[i] != other.values[i]) {return false;}
            if(units[i]  != other.units[i])  {return false;}}
        return true;}
    
    /**
     * @return the hash code for the object 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Arrays.hashCode(this.values);
        hash = 89 * hash + Arrays.deepHashCode(this.units);
        return hash;
    }
    
     public static void main(String[] args) throws Exception {
        int[] values = new int[]{1,1};
        GregorianTimeUnit[] units = new GregorianTimeUnit[]{GregorianTimeUnit.week,GregorianTimeUnit.day};
        RelativeTimePoint rt = new RelativeTimePoint(values,units,true,false);
        LocalDateTime date = LocalDateTime.of(2016,1,4,0,1);
        //LocalDateTime date = LocalDateTime.now();
        System.out.println(date);
        System.out.println(rt.firstDateAfter(date));
    }
    
    /** This method computes for the relative date the first absolute date after the given date.
     * Example: the relative date ist the begin of a lecture, say 10:15<br>
     * The absolute date is the first day of the semester.
     * The result is then the absolute date-time of the first lecture in the semester. 
     * 
     * @param date an absolute date-time
     * @return the first instance of the relative date after 'date'.
     */
    public LocalDateTime firstDateAfter(LocalDateTime date) {
        System.out.println("REC " + date);
        int length = values.length;
        int year  = date.getYear();
        int millenium = year/1000;
        int century = (year-millenium*1000)/100;
        int decade = (year-millenium*1000 - century*100)/10;
        year -= millenium*1000 + century*100 + decade*10; 
        int month = date.getMonthValue();
        int day   = date.getDayOfMonth();
        int hour   = date.getHour();
        int minute = date.getMinute();
        int second = date.getSecond();
        
        int n_millenium = millenium;
        int n_century = century;
        int n_decade = decade;
        int n_year = year;
        int n_month = month;
        int n_day = day;
        int n_hour = hour;
        int n_minute = minute;
        int n_second = second;
        boolean week_encountered = false;
        List<GregorianTimeUnit> changableUnits = (ArrayList<GregorianTimeUnit>)GregorianTimeUnit.timeUnits.clone();
        changableUnits.remove(GregorianTimeUnit.microsecond);
        changableUnits.remove(GregorianTimeUnit.millisecond);
        if(monthly) {changableUnits.remove(GregorianTimeUnit.week);}
        if(weekly) {changableUnits.remove(GregorianTimeUnit.month);}
        
        for(int i = 0; i < length; ++i) {
            boolean last = i == length-1;
            int value = values[i]; 
            GregorianTimeUnit unit = units[i];
            if(last) {int index = changableUnits.indexOf(unit); 
                      changableUnits = changableUnits.subList(index+1, changableUnits.size());}
            else {changableUnits.remove(unit);}
            if(unit == GregorianTimeUnit.century) {
                n_century = value-1; 
                if(last) {n_decade = 0; n_year = 0; n_month = 1; n_day = 1; n_hour = 0; n_minute = 0; n_second = 0;}
                continue;}
            if(unit == GregorianTimeUnit.decade)  {
                n_decade  = value-1; 
                if(last) {n_year = 0; n_month = 1; n_day = 1; n_hour = 0; n_minute = 0; n_second = 0;}
                continue;}
            if(unit == GregorianTimeUnit.year)    {
                n_year    = value-1; 
                if(last) {n_month = 1; n_day = 1; n_hour = 0; n_minute = 0; n_second = 0;}
                continue;}
            if(unit == GregorianTimeUnit.month)   {
                n_month   = value; 
                if(last) {n_day = 1; n_hour = 0; n_minute = 0; n_second = 0;}
                continue;}
            if(unit == GregorianTimeUnit.week)   {
                int[] y_m_d = firstDayInWeek(n_millenium*1000+n_century*100+n_decade*10+n_year,value);
                n_year = y_m_d[0];
                n_month = y_m_d[1];
                n_day = y_m_d[2]; 
                n_millenium = n_year/1000;
                n_century = (n_year-n_millenium*1000)/100;
                n_decade = (n_year-n_millenium*1000 - n_century*100)/10;
                n_year -= n_millenium*1000+n_century*100+n_decade*10;
                if(last) {n_hour = 0; n_minute = 0; n_second = 0;}
                week_encountered = true;
                continue;}
            if(unit == GregorianTimeUnit.day)   {
                if(weekly) {
                    if(week_encountered) {n_day += value-1;}
                    else {
                        LocalDate ld = LocalDate.of(n_millenium*1000+n_century*100+n_decade*10+n_year,n_month,n_day);
                        ld = ld.plusDays(value-ld.getDayOfWeek().getValue());
                        n_year = ld.getYear();
                        n_millenium = n_year/1000;
                        n_century = (n_year-n_millenium*1000)/100;
                        n_decade = (n_year-n_millenium*1000 - n_century*100)/10;
                        n_year -= n_millenium*1000 + n_century*100 + n_decade*10; }}
                else {n_day = value;}
                if(last) {n_hour = 0; n_minute = 0; n_second = 0;}
                continue;}
             if(unit == GregorianTimeUnit.hour) {
                 n_hour = value; 
                 if(last) {n_minute = 0; n_second = 0;}
                 continue;}             
             if(unit == GregorianTimeUnit.minute) {
                 n_minute = value; 
                 if(last) {n_second = 0;}
                 continue;}
             if(unit == GregorianTimeUnit.second) {n_second = value;}}
        LocalDateTime n_date = LocalDateTime.of(n_millenium*1000 + n_century*100 + n_decade*10 + n_year,
                                                n_month,n_day,n_hour,n_minute,n_second);
        System.out.println("D " + n_date);
        if(n_date.isBefore(date)) {
            GregorianTimeUnit unit = changableUnits.get(0); System.out.println("U " + unit.id);
            while(true) {
                if(unit == GregorianTimeUnit.second)    {n_date = n_date.plusSeconds(1);  break;}
                if(unit == GregorianTimeUnit.minute)    {n_date = n_date.plusMinutes(1);  break;}
                if(unit == GregorianTimeUnit.hour)      {n_date = n_date.plusHours(1);    break;}
                if(unit == GregorianTimeUnit.day)       {n_date = n_date.plusDays(1);     break;}
                if(unit == GregorianTimeUnit.week)      {n_date = n_date.plusDays(7);     break;}
                if(unit == GregorianTimeUnit.month)     {n_date = n_date.plusMonths(1);   break;}
                if(unit == GregorianTimeUnit.year)      {n_date = n_date.plusYears(1);    break;}
                if(unit == GregorianTimeUnit.decade)    {n_date = n_date.plusYears(10);   break;}
                if(unit == GregorianTimeUnit.century)   {n_date = n_date.plusYears(100);  break;}
                if(unit == GregorianTimeUnit.millenium) {n_date = n_date.plusYears(1000); break;}
                break;}}
        if(!n_date.isBefore(date)) {return n_date;}
        return firstDateAfter(n_date);}
    
    
    /** The method computes for a given year and week first day in this week.
     * The convention is: the first week in a year has week number 1, and it is the 
     * week intervalContaining more days are in the new year than in the old year.
     * 
     * @param year a year in the Gregorian Calendar
     * @param week a week 
     * @return [year,month,day] for the first day (Monday) in the given week.
     */
    public static int[] firstDayInWeek(int year, int week) {
        LocalDate date = LocalDate.of(year,1,1);
        int day = date.getDayOfWeek().getValue();
        if(day > 1) {date = LocalDate.of(year-1, 12, 33-day);}
        if(day > 4) {++week;}
        date = date.plusWeeks(week-1);
        return new int[]{date.getYear(),date.getMonthValue(),date.getDayOfMonth()};
    }
    
    /** generates a string representation of the time object
     * 
     * @return the time object as a string.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        boolean hourLevel = false;
        for(int i = 0; i < values.length; ++i) {
            if(hourLevel) {
                s.append(Integer.toString(values[i]));
                 if(i < values.length-1) {s.append(":");}
                 continue;}
            GregorianTimeUnit unit = units[i];
            if(unit == GregorianTimeUnit.hour || unit == GregorianTimeUnit.minute ||  unit == GregorianTimeUnit.second){
                hourLevel = true;
                s.append(Integer.toString(values[i]));
                if(i < values.length-1){s.append(":");}
                else {s.append(" h");}
                continue;}

            s.append(Integer.toString(values[i])).append(" ");
            if(unit == GregorianTimeUnit.day) {
                s.append("d");
                if(weekly) {s.append("w");}
                else {if(monthly) {s.append("m");}}}
            else{s.append(unit.shortString());}
            if(i < values.length-1) {s.append(", ");}}
        return s.toString();}
    
    /** parses a string:
     * The syntax can be like <br>
     * 10:15  or 10:15:6  (hour minute second)<br>
     * 3 dw 10:15  (every third day in a week, hour minute second)<br>
     * 3 dm 10:15 (every third month in a month, hour minute second)<br>
     * 3 w 10:15  (every third week in a year, hour minute second)<br>
     * 2 m 10:15 (every third month in a yead, hour minute second)<br>
     * 3 m 2 d 10:15 (every third month, every second day in this month, hour minute second)<br>
     * and similar. 
     * <br>
     * proper syntax checks have still to be implemented. 
     * 
     * @param string the string to be parsed.
     * @param context not needed here
     * @return the parsed RelativeTimePoint.
     */
    public static ConcreteObject parseString(String string, Context context)  {
        String[] parts = string.split("\\s*(,|\\s+)\\s*");
        int length = parts.length;
        if(length > 1 && parts[1].startsWith("mil")) {
            Commons.getMessanger(DataErrors).insert("Typo", "a relative time cannot start with the millenium:\n" + string);
            return null;}
        ArrayList<Integer> values = new ArrayList();
        ArrayList<GregorianTimeUnit> units = new ArrayList();
        boolean weekly = false;
        boolean monthly = false;
        boolean error = false;
        for(int i = 0; i < length; ++i) {
            String part = parts[i];
            if(part.contains(":")) {
                parts = part.split(":");
                switch(parts.length) {
                    case 1: units.add(GregorianTimeUnit.hour); values.add(Integer.parseInt(parts[0])); break;
                    case 2: 
                        units.add(GregorianTimeUnit.hour); values.add(Integer.parseInt(parts[0])); 
                        units.add(GregorianTimeUnit.minute); values.add(Integer.parseInt(parts[1])); break;
                    case 3: 
                        units.add(GregorianTimeUnit.hour); values.add(Integer.parseInt(parts[0])); 
                        units.add(GregorianTimeUnit.minute); values.add(Integer.parseInt(parts[1])); 
                        units.add(GregorianTimeUnit.second); values.add(Integer.parseInt(parts[2])); break;}
                break;}
             if(i == length-1) {
                 Integer value = Utilities.parseInt(part);
                 if(value == null) {error = true; continue;}
                 else{values.add(value);units.add(GregorianTimeUnit.hour);}
                 break; }
             
            String unitName = parts[i+1];
            if(unitName.equals("dm") || unitName.equals("day-in-month")) {
                monthly = true;
                Integer value = Utilities.parseInt(part);
                if(value == null) {error = true; continue;}
                else {values.add(value);units.add(GregorianTimeUnit.day);}
                ++i;
                continue;}
            if(unitName.equals("dw") || unitName.equals("day-in-week")) {
                weekly = true;
                Integer value = Utilities.parseInt(part);
                if(value == null) {error = true; continue;}
                else {values.add(value); units.add(GregorianTimeUnit.day);}
                ++i;
                continue;}
               
            GregorianTimeUnit unit = GregorianTimeUnit.parseString(parts[++i]);
            if(unit == null) {
                Commons.getMessanger(DataErrors).insert("Typo","unknown time unit "+parts[i]);
                continue;}
            Integer value = Utilities.parseInt(part);
            if(value == null) {error = true; continue;}
            else {values.add(value);units.add(unit);}
            if(unit == GregorianTimeUnit.week) {weekly = true;}
            else {if(unit == GregorianTimeUnit.month) {monthly = true;}}}
         if(monthly && weekly) {
             Commons.getMessanger(DataErrors).insert("Typo", "one cannot specify month and week together in\n "+string);
             return null;}
        return error ? null : new RelativeTimePoint(values,units,weekly,monthly);}
    
    
}
