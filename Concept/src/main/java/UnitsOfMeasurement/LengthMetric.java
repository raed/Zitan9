package UnitsOfMeasurement;

import MISC.Commons;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class contains the metric units of measurement.
 *
 */
public class LengthMetric extends Length {
    public static LengthMetric meter      = new LengthMetric("meter","m",1);
    public static LengthMetric decimeter  = new LengthMetric("decimeter","dm",0.1f);
    public static LengthMetric centimeter = new LengthMetric("centimeter","cm",0.01f);
    public static LengthMetric millimeter = new LengthMetric("millimeter","mm",0.001f);
    public static LengthMetric micrometer = new LengthMetric("micrometer","mcm",0.000001f);
    public static LengthMetric kilometer  = new LengthMetric("kilometer","km",1000f);
    
    /** constructs the metric units of measurement.
     * Only the predefined units can be constructed.
     * 
     * @param id the applicationName for the unit
     * @param shortId an abbreviations.
     * @param conversionFactor a conversion factor for converting to meter.
     */
    private LengthMetric(String id, String shortId, float conversionFactor) {
        super(id,shortId,conversionFactor);}
    
    /** converts the given measure from 'this' to unit. 
     * Example: 3 kiometer become 300000 centimeter
     * 
     * @param measure any float
     * @param unit one of the LengthMetric units
     * @return the converted measure
     */
    public float convert(float measure, Length unit) {
        assert(unit instanceof LengthMetric);
        return measure*conversionFactor/unit.conversionFactor;}
    
    /** parses a string to one of the predefined length units.
     * 
     * @param string the string to be parsed.
     * @return one of the length units, or null
     */
    public static Length parseString(String string) {
        string = string.toLowerCase();
        if(string.equals("m")   || string.startsWith("meter"))      {return meter;}
        if(string.equals("dm")  || string.startsWith("decimeter"))  {return decimeter;}
        if(string.equals("cm")  || string.startsWith("centimeter")) {return centimeter;}
        if(string.equals("mm")  || string.startsWith("millimeter")) {return millimeter;}        
        if(string.equals("mcm") || string.startsWith("micrometer")) {return micrometer;}
        if(string.equals("km")  || string.startsWith("kilometer"))  {return kilometer;}
        Commons.getMessanger(DataErrors).insert("Typo","Unknown length unit: " + string);
        return null;}
}
