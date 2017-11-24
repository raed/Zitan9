package ConcreteDomain.SetTypes;

import ConcreteDomain.AtomicTypes.PhysicalHost;
import ConcreteDomain.ConcreteObject;

import java.io.Serializable;
import java.util.ArrayList;

/** This class implements a list of PhysicalHost data.
 */
public class PhysicalHostList extends ListObject<PhysicalHost> implements Serializable {
    /** constructs a new PhysicalHostList
     *
     * @param values a list of integers.
     */
    public PhysicalHostList(ArrayList<PhysicalHost> values) {
        super(values);}

    public PhysicalHostList(PhysicalHost... values){
        super(values);}

    /** parses a string "hostname1:part1 ; ... ; hostname_n:port_n" to a PhysicalHostList
     *
     * @param string the string to be parsed.
     * @return the parsed PhysicalHostList or null if a syntax error occurred.
     */
    public static ConcreteObject parseString(String string)  {
        String[] parts = string.split("\\s*(,|;|\\n)\\s*");
        ArrayList<PhysicalHost> hosts = new ArrayList<>();
        for(String part : parts) {
            PhysicalHost host = (PhysicalHost) PhysicalHost.parseString(part);
            if(host != null) {hosts.add(host);}}
        return hosts.isEmpty() ? null : new PhysicalHostList(hosts);}



}
