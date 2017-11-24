package UnitsOfMeasurement;

import java.io.Serializable;

/** This is the superclass for weight unit of measurements
 *
 */
public class Weight extends UnitOfMeasurement implements Serializable {
    /** a factor for concerting units to a standard unit.*/
    public float conversionFactor;
    
    /** constructs a weight unit of measurement.
     * 
     * @param id an identifier, e.g. "gramm"
     * @param shortId a short identifier, e.g. "m"
     * @param conversionFactor the conversion factor, e.g. 1000 for kilogramm to gramm.
     */
    public Weight(String id, String shortId, float conversionFactor) {
        super(id,shortId);
        this.conversionFactor = conversionFactor;}
    
    
}
