package UnitsOfMeasurement;

import MISC.Commons;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class represents metric weight units (gramm, kilogramm etc.) 
 *
 */
public class WeightMetric extends Weight {
    public static WeightMetric gramm      = new WeightMetric("gramm", "g",1f);
    public static WeightMetric milligramm = new WeightMetric("milligramm", "mg",0.001f);
    public static WeightMetric kilogramm  = new WeightMetric("kilogramm", "kg",1000f);
    public static WeightMetric tons       = new WeightMetric("tons", "t",1000000f);
    

    /** constructs a weight unit of measurement. 
     * Only the predefined units can be created.
     * 
     * @param id an identifier for the unit
     * @param shortId an abbreviation 
     * @param conversionFactor converts to gramm.
     */
    private WeightMetric(String id, String shortId, float conversionFactor) {
        super(id, shortId, conversionFactor);}
    
    /** converts the given measure from 'this' to unit. 
     * Example: 3 kilogram become 300000 milligram
     * 
     * @param measure any float
     * @param unit one of the WeightMetric units
     * @return the converted measure
     */
    public float convert(float measure, LengthMetric unit) {
        return measure*conversionFactor/unit.conversionFactor;}
    
    /** maps a string to a weight unit of measurement.  
     * 
     * @param string one of the identifiers or short identifiers.
     * @return one of the predefined weight units, or null
     */
    public static Weight parseString(String string)  {
        string = string.toLowerCase();
        if(string.equals("g")  || string.startsWith("gramm"))      {return gramm;}
        if(string.equals("mg") || string.startsWith("milligram"))  {return milligramm;}
        if(string.equals("kg") || string.startsWith("kilogramm"))  {return kilogramm;}
        if(string.equals("t")  || string.startsWith("ton"))        {return tons;}
        Commons.getMessanger(DataErrors).insert("Typo","Unknown weight unit: " + string);
        return null;}
}
