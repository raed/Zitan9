package ConcreteDomain.AtomicTypes;

import AbstractObjects.Interpretation;
import AbstractObjects.SemanticSet;
import ConcreteDomain.AtomicObject;
import ConcreteDomain.ConcreteObject;
import MISC.Commons;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

import static Utils.Messanger.MessangerType.DataErrors;

/** This class enables using the access data (hostname and port) about physical hosts as attribute values.
 *
 */
public class PhysicalHost extends AtomicObject implements Serializable,Comparable {
    private String hostname;
    private int port;

    public PhysicalHost() {}

    /** constructs the data about a physical host
     *
     * @param hostname the applicationName of the host
     * @param port     the access port
     */
    public PhysicalHost(String hostname, int port) {
        this.hostname = hostname.trim();
        this.port = port;}

    public boolean isAlive() {
        try{
            InetAddress address = InetAddress.getByName(hostname);
            Socket socket = new Socket(address,port);
            socket.close();}
        catch(Exception ex){
            System.out.println(ex);
            return false;}
        return true;}

    /** @return hostname:port
     */
    @Override
    public Object get() {
        return hostname+":"+port;}

    /** checks equality with ==
     *
     * @param object the object to be compared
     * @return true if this = object
     */
    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof PhysicalHost)) {return false;}
        return hostname.equals(((PhysicalHost)object).hostname) && port == (((PhysicalHost)object).port);}


    /** returns the string's hashCode
     *
     * @return the string's hashCode
     */
    @Override
    public int hashCode() {return hostname.hashCode()+port;}

    /** checks for subset-relationship
     *
     * @param other the other semantic set
     * @param interpretation the interpretation where the objects live in
     * @return true if 'this' is subset of 'other'
     */
    public boolean isSubset(SemanticSet other, Interpretation interpretation) {
        return this.equals(other);}

    /** checks for disjointenss-relationship
     *
     * @param other the other semantic set
     * @param interpretation the interpretation where the objects live in
     * @return true if 'this' is disjoint with 'other'
     */
    public boolean isDisjoint(SemanticSet other, Interpretation interpretation){
        return !this.equals(other);}

    @Override
    public String toString() {
        return hostname+":"+port;}

    /** parses a string as PhysicalHost
     *
     * @param s hostname:port
     * @return the corresponding PhysicalHost.
     */

    public static ConcreteObject parseString(String s) {
        String[] parts = s.split("\\s*:\\s*");
        if(parts.length != 2) {
            Commons.getMessanger(DataErrors).insert("Typo","Phyical consist of 'hostname:part', and not " + s); return null;}
        int port = 0;
        try{port = Integer.parseInt(parts[1]);}
        catch(Exception ex) {
            Commons.getMessanger(DataErrors).insert("Typo",ex.getMessage());
                            return null;}
        return new PhysicalHost(parts[0],port);}


    /** compares (hostname:port) lexicographically.
     *
     * @param object any object to be compared with 'this'
     * @return -1,0,1 depending on the comparison
     */

    @Override
    public int compareTo(Object object) {
        if(object == null || !(object instanceof PhysicalHost)) {return 1;}
        PhysicalHost host = (PhysicalHost)object;
        if(hostname.equals(host.hostname)) {
            if(port == host.port) {return 0;}
            return (port < host.port) ? -1 : 1;}
        return hostname.compareTo(host.hostname);}

}
