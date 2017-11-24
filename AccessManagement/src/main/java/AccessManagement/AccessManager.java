package AccessManagement;

import Attributes.Attribute;
import Attributes.AttributeValueList;
import Concepts.Concept;
import Concepts.IndividualConcept;
import DAGs.Direction;
import Graphs.Strategy;
import MISC.Context;
import DAGs.DAGMap;
import Utils.Utilities;
import static AccessManagement.AccessSequence.*;

import java.util.ArrayList;

/**
 * Created by ohlbach on 08.04.2016.
 */
public class AccessManager {
    Context context;
    private int[][] accessSequence = new int[AccessMode.size()][];
    private Object[] permissions = new Object[AccessMode.size()];

    public AccessManager(Context context) {
        this.context = context;
        for(AccessMode mode : AccessMode.values()) {
            setAccessSequence(mode,CONCEPT_ATTRIBUTE_CONSTRAINT,CONCEPT_ATTRIBUTE,ATTRIBUTE_CONSTRAINT,CONCEPT,ATTRIBUTE,CONSTRAINT);}}

    public void setAccessSequence(AccessMode mode, AccessSequence... sequence) {
        int[] positions = new int[AccessSequence.size()];
        accessSequence[mode.ordinal()] = positions;
        for(int i = 0; i < sequence.length; ++i) {positions[i] = sequence[i].ordinal();}
        for(int i = sequence.length; i < positions.length; ++i) {positions[i] = -1;}}

    public void addAllowance(AccessMode mode, Concept role, Concept concept) {
        addAllowance(mode,CONCEPT.ordinal(), concept,new AccessConstraint(role,null,null));}

    public void addAllowance(AccessMode mode, Concept role, Concept concept, Attribute attribute) {
        addAllowance(mode,CONCEPT_ATTRIBUTE.ordinal(), concept,new AccessConstraint(role,attribute,null));}

    public void addAllowance(AccessMode mode, Concept role, Concept concept, Attribute attribute, AttributeValueList avConstraint) {
        addAllowance(mode,CONCEPT_ATTRIBUTE_CONSTRAINT.ordinal(), concept,new AccessConstraint(role,attribute,avConstraint));}

    public void addAllowance(AccessMode mode, Concept role, Attribute attribute) {
        addAllowance(mode,ATTRIBUTE.ordinal(),attribute,new AccessConstraint(role,null,null));}

    public void addAllowance(AccessMode mode, Concept role, Attribute attribute, AttributeValueList avConstraint) {
        addAllowance(mode,ATTRIBUTE_CONSTRAINT.ordinal(),attribute,new AccessConstraint(role,null,avConstraint));}

    public void addAllowance(AccessMode mode, Concept role, AttributeValueList avConstraint) {
        AccessConstraint ac = new AccessConstraint(role,null,avConstraint);
        int position = CONSTRAINT.ordinal();
        int index = mode.ordinal();
        Object[] allowanceList = (Object[]) permissions[index];
        if(allowanceList == null) {
            allowanceList = new Object[AccessSequence.size()];
            permissions[index] = allowanceList;}
        ArrayList<AccessConstraint> allowance = (ArrayList<AccessConstraint>)allowanceList[position];
        if(allowance == null) {
            allowance = new ArrayList<>();
            allowanceList[position]= allowance;}
        allowance.add(ac);}

    private void addAllowance(AccessMode mode, int position, Concept concept, AccessConstraint ac) {
        int index = mode.ordinal();
        Object[] allowanceList = (Object[]) permissions[index];
        if(allowanceList == null) {
            allowanceList = new Object[AccessSequence.size()];
            permissions[index] = allowanceList;}
        DAGMap<Concept,ArrayList<AccessConstraint>> allowance = (DAGMap<Concept,ArrayList<AccessConstraint>>)allowanceList[position];
        if(allowance == null) {
            allowance = new DAGMap<>(context.conceptHierarchy);
            allowanceList[position]= allowance;}
        ArrayList<AccessConstraint> constraints = allowance.get(concept);
        if(constraints == null) {
            constraints = new ArrayList<>();
            allowance.put(concept,constraints);}
        constraints.add(ac);}

    private void addAllowance(AccessMode mode, int position, Attribute attribute, AccessConstraint ac) {
        int index = mode.ordinal();
        Object[] allowanceList = (Object[]) permissions[index];
        if(allowanceList == null) {
            allowanceList = new Object[AccessSequence.size()];
            permissions[index] = allowanceList;}
        DAGMap<Attribute,ArrayList<AccessConstraint>> allowance = (DAGMap<Attribute,ArrayList<AccessConstraint>>)allowanceList[position];
        if(allowance == null) {
            allowance = new DAGMap<>(context.attributeHierarchy);
            allowanceList[position]= allowance;}
        ArrayList<AccessConstraint> constraints = allowance.get(attribute);
        if(constraints == null) {
            constraints = new ArrayList<>();
            allowance.put(attribute,constraints);}
        constraints.add(ac);}



    public String toString() {
        StringBuilder s = new StringBuilder();
        for(AccessMode mode : AccessMode.values()) {
            s.append(mode.toString()).append("-access:\nAccess Sequence: ");
            int[] sequence = accessSequence[mode.ordinal()];
            for(int i = 0; i < sequence.length; ++i) {
                int position = sequence[i];
                if(position < 0) {break;}
                s.append(AccessSequence.values()[position].toString()).append(", ");}
            s.append("\n");
            Object allowances = this.permissions[mode.ordinal()];
            if(allowances == null) {continue;}
            for(int i = 0; i < AccessSequence.size(); ++i) {
                Object permission = ((Object[]) allowances)[i];
                if (permission == null) {continue;}
                AccessSequence access = AccessSequence.values()[i];
                s.append(access.toString()).append(" permissions:\n");
                switch (access) {
                    case CONCEPT:
                    case CONCEPT_ATTRIBUTE:
                    case CONCEPT_ATTRIBUTE_CONSTRAINT:
                        DAGMap<Concept, ArrayList<AccessConstraint>> conceptPermission = (DAGMap<Concept, ArrayList<AccessConstraint>>) permission;
                        s.append(conceptPermission.toString(((Concept concept)-> concept.getName())," can be accessed by:\n",
                                ((ArrayList<AccessConstraint> constraints) -> acListToString(constraints)))).append("\n");
                        break;
                    case ATTRIBUTE:
                    case ATTRIBUTE_CONSTRAINT:
                        DAGMap<Attribute, ArrayList<AccessConstraint>> attributePermission = (DAGMap<Attribute, ArrayList<AccessConstraint>>) permission;
                        s.append(attributePermission.toString(((Attribute attribute)-> attribute.getName())," can be accessed by:\n",
                                ((ArrayList<AccessConstraint> constraints) ->acListToString(constraints)))).append("\n");
                        break;
                    case CONSTRAINT:
                        s.append(acListToString((ArrayList<AccessConstraint>) permission)).append("\n");}}}
        return s.toString();}

    private static String acListToString(ArrayList<AccessConstraint> constraints) {
        return Utilities.join(constraints,"\n",(AccessConstraint constraint) -> "   "+ constraint.toString()+"\n");}



    public boolean allowed(AccessMode mode, IndividualConcept individualConcept, Attribute attribute, AttributeValueList avConstraint, IndividualConcept role, Context context) {
        Object[] allowances = (Object[])this.permissions[mode.ordinal()];
        if(allowances == null) {return true;}
        int[] positions = accessSequence[mode.ordinal()];
        Boolean allowed;
        Boolean noConstraint = true;
        for(int i = 0; i < positions.length; ++i) {
            int position = positions[i];
            if(position < 0) {break;}
            Object allowance = allowances[position];
            if(allowance == null) {continue;}
            switch(AccessSequence.values()[position]) {
                case CONCEPT:
                case CONCEPT_ATTRIBUTE:
                case CONCEPT_ATTRIBUTE_CONSTRAINT:
                    DAGMap<IndividualConcept,ArrayList<AccessConstraint>> conceptConstraint = (DAGMap<IndividualConcept,ArrayList<AccessConstraint>>)allowance;
                    allowed = conceptConstraint.find(individualConcept, Direction.UP, Strategy.BREADTH_FIRST,(constraints -> checkConcept(constraints,attribute,avConstraint,role,context)));
                    if(allowed != null) {
                        if(allowed) {return true;}
                        noConstraint = false;}
                    break;
                case ATTRIBUTE:
                case ATTRIBUTE_CONSTRAINT:
                    DAGMap<Attribute,ArrayList<AccessConstraint>> attributeConstraint = (DAGMap<Attribute,ArrayList<AccessConstraint>>)allowance;
                    allowed = attributeConstraint.find(attribute,Direction.UP,Strategy.BREADTH_FIRST,(constraints -> checkConcept(constraints,avConstraint,role,context)));
                    if(allowed != null) {
                        if(allowed) {return true;}
                        noConstraint = false;}
                    break;
                case CONSTRAINT:
                    for(AccessConstraint constraint : (ArrayList<AccessConstraint>)allowance) {
                        allowed = constraint.allowed(avConstraint,role,context);
                        if(allowed) {return true;}
                        noConstraint = false;}}}
        return noConstraint;}

    private static Boolean checkConcept(ArrayList<AccessConstraint> constraints, Attribute attribute, AttributeValueList valueConstraint, IndividualConcept role, Context context) {
        for(AccessConstraint constraint : constraints) {
            boolean result = constraint.allowed(attribute,valueConstraint,role,context);
            if(result){return result;}}
        return false;}

    private static Boolean checkConcept(ArrayList<AccessConstraint> constraints, AttributeValueList valueConstraint, IndividualConcept role, Context context) {
        for(AccessConstraint constraint : constraints) {
            boolean result = constraint.allowed(valueConstraint,role,context);
            if(result){return result;}}
        return false;}




}
