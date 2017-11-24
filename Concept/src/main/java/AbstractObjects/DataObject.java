package AbstractObjects;

/** This interface comprises IndividualConcept and ConcreteObject.
 * It provides a common type for both of them.
 *
 */
public interface DataObject {

    /** Accesses wrapped objects.
     *
     * Since most classes which implement DataObject are actually wrapper classes for
     * elementary types like integers, intervals etc.
     * this method can be used to expose the wrapped object.
     *
     * @return this.
     */
    public default Object get() {return this;}


}
