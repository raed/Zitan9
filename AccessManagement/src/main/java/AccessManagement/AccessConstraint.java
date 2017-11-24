package AccessManagement;

import Attributes.Attribute;
import Attributes.AttributeValueList;
import Concepts.Concept;
import MISC.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/** This class implements access constraints for concept-attribute-constraints triples.
 * The concept itself,however, is not stored here, but in the access manager.<br>
 * Examples:<br>
 *     reading (student,marks) is allowed only be the dean of studies.<br>
 *     The AccessConstraint would be 'attribute' = 'marks', 'role' = 'dean_of_studies'.<br>
 *     This would be attached to the class 'student' in the access manager.<br>
 *<br>
 *     writing (*,*,years = 1900-2000) is allowed only by the 'admin'<br>
 *     (all historical data can only be changed by the administrator)<br>
 *     The AccessConstraint would be 'attribute' = null, 'role' = 'admin'.<br>
 *
 * Created by ohlbach on 08.04.2016.
 */
public class AccessConstraint implements Serializable {
    /** the one who has access */
    Concept role = null;
    /** the attribute for which access is granted */
    Attribute attribute = null;
    /** the constraints for which access is granted */
    AttributeValueList constraints = null;
    /** for internal use during serialization: roleId, attributeId, constraints*/
    private Object[] transferred = null;

    public String toString() {
        StringBuilder s = new StringBuilder();
        if(transferred != null) {
            if (transferred[1] == null && transferred[2] == null) {s.append(transferred[0]);}
            else {s.append(transferred[0]).append(" has access for ");
                if(transferred[1] != null) {s.append("attribute '").append(transferred[1]).append("' ");}
                if(transferred[2] != null) {s.append("constraint '").append(transferred[2].toString()).append("'");}}}
        else {
            if(attribute == null && constraints == null) {s.append(role.getName());}
            else {s.append(role.getName()).append(" has access for ");
                if(attribute != null) {s.append("attribute '").append(attribute.getName()).append("' ");}
                if(constraints != null) {s.append("constraint '").append(constraints.toString()).append("'");}}}
        return s.toString();}

    /** constructs a new AccessConstraint
     *
     * @param role      the one who has access
     * @param attribute the attribute for which access is granted
     * @param constraints the constraints for which access is granted.
     */
    public AccessConstraint(Concept role, Attribute attribute, AttributeValueList constraints) {
        assert role != null;
        this.role = role;
        this.attribute = attribute;
        this.constraints = constraints;}


    /** checks whether access is allowed for the given parameters.
     *
     * @param attribute   the attribute for which access is asked for
     * @param constraints the constraints for which access is asked for
     * @param role        the concept asking for access
     * @param context     the context of the query.
     * @return true if access is allowed, otherwise false
     */
    public boolean allowed(Attribute attribute, AttributeValueList constraints, Concept role, Context context) {
      /*  if(!reconstruct(context)) {return false;}
        if(!context.conceptHierarchy.isSubnodeOf(role,this.role)) {return false;}
        if(this.attribute != null) {
            if(attribute == null) {return false;}
            if(!context.attributeHierarchy.isSubnodeOf(attribute,this.attribute)) {return false;}}
        if(this.constraints != null) {
            if(constraints == null) {return false;}
            if(!constraints.isSubset(this.constraints,context)) {return false;}}
       */ return true;

    }

    /** checks whether access is allowed for the given parameters.
     *
     * @param constraints the constraints for which access is asked for
     * @param role        the concept asking for access
     * @param context     the context of the query.
     * @return true if access is allowed, otherwise false
     */
    public boolean allowed(AttributeValueList constraints, Concept role, Context context) {
       /* assert constraints!= null;
        if(!reconstruct(context)) {return false;}
        if(!context.conceptHierarchy.isSubnodeOf(role,this.role)) {return false;}
        if(this.constraints != null) {
            if(constraints == null) {return false;}
            if(!constraints.isSubset(this.constraints,context)) {return false;}}
        */return true;
    }

    /** writes the AccessConstraint to the output stream.
     * For the concepts and attributes only their identifiers are written.
     *
     * @param out  the output stream.
     * @throws IOException if there is a communication error
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        /*
        if(role == null) {return;}
        String attributeId = (attribute == null) ? null : attribute.applicationName;
        AttributeValueList constraintsId = (constraints == null) ? null : constraints.transform(Attribute.rel.Attribute2Id(),DataObject.Value2Id());
        out.writeObject(role.applicationName);
        out.writeObject(attributeId);
        out.writeObject(constraintsId);
        */}

    /** reads the AccessConstraint from the input stream.
     * The concept and attribute's identifiers are not yet mapped to their objects.
     *
     * @param in the input stream.
     * @throws Exception if there is a communication error
     */
    private void readObject(ObjectInputStream in) throws Exception {
        transferred = new Object[]{in.readObject(),in.readObject(),in.readObject()};}

    /** reconstructs the objects from their identifiers after deserialization
     *
     * @param context to be used for the reconstruction.
     * @return true if the reconstruction was successful.
     */
    private boolean reconstruct(Context context) {
        /*
        if(transferred == null) {return true;}
        boolean error = false;
        role = context.getConcept((String)transferred[0]);
        if(role == null) {
            Commons.getMessanger(Messanger.MessangerType.DataErrors).insert("Unknown concept",(String)transferred[0]);
            error = true;}
        String attributeId = (String)transferred[1];
        if(attributeId != null) {
            attribute = context.getAttribute(attributeId);
            if(attribute == null) {
                Commons.getMessanger(Messanger.MessangerType.DataErrors).insert("Unknown attribute",attributeId);
                error = true;}}
        AttributeValueList constraintsId = (AttributeValueList)transferred[2];
        if(constraintsId != null) {
            StringBuilder errors = new StringBuilder();
            constraints = constraintsId.transform(Attribute.rel.Id2Attribute(context,errors),DataObject.Id2Value(context,errors));
            if(errors.length() != 0) {
                Commons.getMessanger(Messanger.MessangerType.DataErrors).insert("constraints",errors.toString());
                error = true;}}
        transferred = null;
        return !error;}

*/
        return true;
    }
}
