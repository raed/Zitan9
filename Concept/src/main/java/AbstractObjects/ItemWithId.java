package AbstractObjects;

import MISC.Namespace;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/** This class can be used as top-class for all classes whose objects need to have a unique identifier.
 * The item is identified by a namespace and a name. Together this yields a pathname.
 *
 * New items are stored in a temporary HashMap. They are made permanent with the 'makePermanent' method.
 * temporary items can be removed by calling the undo method.
 *
 * The class is not yet synchronized.
 */

public class ItemWithId implements Serializable {
    /** the item's name */
    private String name = null;
    /** the item's full pathname */
    private String pathname = null;
    /** the item's namespace */
    private Namespace namespace = null;

    /** the pathname of the data block which introduced this item. */
    private String dataBlockName = null;

    /** the empty constructor */
    public ItemWithId(){}

    /** constructs an ItemWithId with a given name (and empty namespace)
     *
     * @param name a name for the item.
     */
    public ItemWithId(String name) {
        this.setName(name);
        this.pathname = name;}


    /** constructs an ItemWithId with name and namespace.
     *
     * @param name   a name for the item.
     */
    public ItemWithId(String name, Namespace namespace) {
        this.setName(name);
        this.namespace = namespace;
        this.pathname = namespace.getPathname(name);}


    /** This method reads the item, and integrates it into the internal data structures.
     *
     * @param in    where to read the item from.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }



    /** the applicationName for the object */
    public String getName() {return name;}

    /** returns the full pathname for the item
     *
     * @return  the full pathname for the item
     */
    public String getPathname() {return pathname;}

    /** returns the namespace for the item
     *
     * @return  the namespace for the item
     */
    public Namespace getNamespace() {return namespace;}


    /** gives the item a name (empty namespace)
     *
     * @param name the name for the item
     * @return this
     */
    public ItemWithId setName(String name) {
        this.name = name;
        this.pathname = name;
        return this;}

    /** gives the item a name and namespace
     *
     * @param name the name for the item
     * @param namespace the item's namespace.
     * @return this
     */
    public ItemWithId setName(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
        this.pathname = namespace.getPathname(name);
        return this;
    }

    /** sets the data block that introduced the item.
     *
     * @param dataBlockName the data block which introduce the item.
     */
    public void setDataBlock(String dataBlockName) {
        this.dataBlockName = dataBlockName;}

    /** returns the pathname of the data block which introduced the item.
     *
     * @return the pathname of the data block which introduced the item.
     */
    public String getDataBlockName() {return dataBlockName;}


    /** generates the hash code from the pathname
     *
     * @return the item's hash code
     */
    @Override
    public int hashCode() {return pathname.hashCode();}

    /** compares the objects by their pathnames.
     *
     * @param object any object
     * @return true if both have the same uuid.
     */
    @Override
    public boolean equals(Object object) {
        return object != null && this.getClass() == object.getClass() &&
                pathname.equals(((ItemWithId)object).pathname);}

    /** just returns the pathname for the object.
     *
     * @return the pathname for the object.
     */
    @Override
    public String toString() {return pathname;}



    /** This method should be overwritten in subclasses to generate a more expressive description of the object.
     *
     * @return again just the identifier for the object.
     */
    public String infoString() {return toString();}



}
