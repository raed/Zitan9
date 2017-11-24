package UnitsOfMeasurement;

import java.io.Serializable;

/** This is the abstract superclass for units of measurement (meter, gramm, months etc.)
 *
 */
public class UnitOfMeasurement implements Serializable{
    /** an applicationName, e.g. "meter", "gramm", "year" */
    public String id;
    /** a short applicationName, like "m" for meter or "min" for minutes */
    public String shortId;
    
    /** constructs a UnitOfMeasurement 
     * 
     * @param id the identifier
     * @param shortId the short identifier
     */
    public UnitOfMeasurement(String id, String shortId) {
        this.id = id;
        this.shortId = shortId;}
    
    /** returns the applicationName
     * 
     * @return the applicationName
     */
    @Override
    public String toString() {return id;}
    
    /** returns the short applicationName
     * 
     * @return the short applicationName.
     */
    public String shortString() {return shortId;}
}
