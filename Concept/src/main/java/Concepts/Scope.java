package Concepts;

/** A SCOPE specifies the scope of attribute values.
 */
public enum Scope {
    /** Scope.LOCAL means that an attribute value holds only for the concept itself.<br>
     * Example: A person's applicationName in only valid for the person itself.<br>
     * Example: the average mark of a group of students (e.g. exam participants)
     * holds for the entire group, but not for its members.
     */
    LOCAL,
    /** Scope.DEFAULT specifies that attribute value is the default for the sub-concepts of a concept.<br>
     * Example: number of wheels for cars = 4. would be a default for cars.
     * Each individual car may overwrite this default value.
     */
    DEFAULT,
    /** Scope.ALL means that the attribute value holds for the concept itself and all its subconcepts.
     * Example: "Dean fo Studies" for Computer Science Programes = Mayer  is then inherited to all
     * Bachelor and masters programmes in Computer Science.<br>
     * A different attribute value is set for a subconcept of "Computer Science Programmes" causes an
     * inconsistency.
     */
    ALL
}
