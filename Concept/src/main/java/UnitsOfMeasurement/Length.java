package UnitsOfMeasurement;

import java.io.Serializable;

/** This is the abstract superclass for length units of measurement.
 *
 */
public abstract class Length extends UnitOfMeasurement implements Serializable {
    /** a factor for converting the units to a standard unit */
    public float conversionFactor;
    
    /** constructs a length unit of measurement.
     * 
     * @param id an identifier (e.g. meter)
     * @param shortId an abbreviation (e.g. m)
     * @param conversionFactor for converting to a standard unit.
     */
    public Length(String id, String shortId, float conversionFactor) {
        super(id,shortId);
        this.conversionFactor = conversionFactor;}
    
    /** converts the given measure from 'this' to unit. 
     * Example: 3 kilometers become 300000 centimeters
     * 
     * @param measure any float
     * @param unit one of the LengthMetric units
     * @return the converted measure
     */
    public abstract float convert(float measure, Length unit);
    
     /** parses a string to one of the predefined length units.
      * If other than metric length units are implemented, this must be changed.
     * 
     * @param string the string to be parsed.
     * @return one of the length units or null
     */
    public static Length parseString(String string) {
        return LengthMetric.parseString(string);}
}
