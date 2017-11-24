package Data;

import AbstractObjects.Interpretation;
import AbstractObjects.ItemWithId;
import Concepts.Concept;
import Concepts.IndividualConcept;
import Concepts.SetConcept;
import DAGs.DAG;
import MISC.Context;
import MISC.Namespace;
import Main.Symbols;
import Utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 */
public class DataParsers {
    static {addDataParsers();}



    private static void addDataParsers() {
        DataBlock.addParser("rel", "Concepts",
                (dataBlock,context,errors) -> parseConceptsAndRelations(dataBlock,context,errors));

        DataBlock.addChecker("rel", "Concepts",
                (dataBlock,context,errors) -> checkConceptsAndRelations(dataBlock,context,errors));

        DataBlock.addParser("rel", "Attributes",
                (dataBlock,context,errors) -> parseAttributes(dataBlock,context,errors));

        DataBlock.addChecker("rel", "Attributes",
                (dataBlock,context,errors) -> checkAttributes(dataBlock,context,errors));

    }

    private static boolean checkConceptsAndRelations(DataBlock dataBlock, Context context, StringBuilder errors)  {
        HashMap<String,String> meta = dataBlock.getMetaData();
        ArrayList<Object[]> rawData = (ArrayList<Object[]>)dataBlock.getRawData();
        Namespace namespace = dataBlock.getNamespace();
        String isSubconceptOf = Symbols.isSubconceptOf;
        String isInstanceOf = Symbols.isInstanceOf;
        String isSuperconceptOf = Symbols.isSuperconceptOf;
        String isPartOf = Symbols.isPartOf;
        String contains = Symbols.contains;
        if(meta.containsKey(isSubconceptOf))   {isSubconceptOf   = meta.get(isSubconceptOf);}
        if(meta.containsKey(isInstanceOf))     {isInstanceOf     = meta.get(isInstanceOf);}
        if(meta.containsKey(isSuperconceptOf)) {isSuperconceptOf = meta.get(isSuperconceptOf);}
        if(meta.containsKey(isPartOf))         {isPartOf         = meta.get(isPartOf);}
        if(meta.containsKey(contains))         {contains         = meta.get(contains);}
        boolean okay = true;
        for(Object[] data : rawData) {
            String relation = (String)data[0];
            ArrayList<String> data1 =  (ArrayList<String>)data[1];
            ArrayList<String> data2 =  (ArrayList<String>)data[2];
            if(relation.equals(isSubconceptOf))   {okay &= checkSubconceptOf(data1,data2,namespace, context,errors); continue;}
            if(relation.equals(isSuperconceptOf)) {okay &= checkSubconceptOf(data2,data1,namespace, context,errors); continue;}
            if(relation.equals(isInstanceOf))     {okay &= checkIndividualOf(data1,data2,namespace, context,errors); continue;}
            if(relation.equals(contains))         {okay &= checkIndividualOf(data2,data1,namespace, context,errors); continue;}
            okay &= checkRelation(relation,data1,data2,namespace, context,errors);}
        return okay;}

    private static boolean parseConceptsAndRelations(DataBlock dataBlock, Context context, StringBuilder errors)  {
        HashMap<String,String> meta = dataBlock.getMetaData();
        ArrayList<Object[]> rawData = (ArrayList<Object[]>)dataBlock.getRawData();
        Namespace namespace = dataBlock.getNamespace();
        String isSubconceptOf = Symbols.isSubconceptOf;
        String isInstanceOf = Symbols.isInstanceOf;
        String isSuperconceptOf = Symbols.isSuperconceptOf;
        String isPartOf = Symbols.isPartOf;
        String contains = Symbols.contains;
        if(meta.containsKey(isSubconceptOf))   {isSubconceptOf   = meta.get(isSubconceptOf);}
        if(meta.containsKey(isInstanceOf))     {isInstanceOf     = meta.get(isInstanceOf);}
        if(meta.containsKey(isSuperconceptOf)) {isSuperconceptOf = meta.get(isSuperconceptOf);}
        if(meta.containsKey(isPartOf))         {isPartOf         = meta.get(isPartOf);}
        if(meta.containsKey(contains))         {contains         = meta.get(contains);}
        boolean okay = true;
        for(Object[] data : rawData) {
            String relation = (String)data[0];
            ArrayList<String> data1 =  (ArrayList<String>)data[1];
            ArrayList<String> data2 =  (ArrayList<String>)data[2];
            if(relation.equals(isSubconceptOf))   {parseSubconceptOf(data1,data2,namespace,dataBlock, context); continue;}
            if(relation.equals(isSuperconceptOf)) {parseSubconceptOf(data2,data1,namespace,dataBlock, context); continue;}
            if(relation.equals(isInstanceOf))     {parseIndividualOf(data1,data2,namespace,dataBlock, context); continue;}
            if(relation.equals(contains))         {parseIndividualOf(data2,data1,namespace,dataBlock, context); continue;}
            parseRelation(relation,data1,data2,namespace,dataBlock,context);}
        return okay;}



    private static boolean checkSubconceptOf(ArrayList<String> subconceptNames, ArrayList<String> superconceptNames, Namespace namespace,
                                             Context context,StringBuilder errors) {
        boolean okay = true;
        for(String subconceptName : subconceptNames) {okay &= asConcept(subconceptName,namespace,errors);}
        for(String superconceptName : superconceptNames) {okay &= asConcept(superconceptName,namespace,errors);}
        return okay;}

    private static void parseSubconceptOf(ArrayList<String> subconceptNames, ArrayList<String> superconceptNames,
                                          Namespace namespace, DataBlock dataBlock, Context context) {
        ArrayList<SetConcept> superConcepts = new ArrayList<>();
        for(String superconceptName : superconceptNames) {superConcepts.add(getConcept(superconceptName,namespace,dataBlock,context));}
        DAG<Concept> hierarchy = context.conceptHierarchy;
        for(SetConcept superconcept : superConcepts) {
            for(String subconceptName : subconceptNames) {
                hierarchy.addSubnode(superconcept, getConcept(subconceptName,namespace,dataBlock,context));}}}



    private static boolean checkIndividualOf(ArrayList<String> instanceNames, ArrayList<String> conceptNames, Namespace namespace, Context context, StringBuilder errors) {
        boolean okay = true;
        for(String individualName : instanceNames) {okay &= asIndividual(individualName,namespace,errors);}
        for(String conceptName : conceptNames) {okay &= asConcept(conceptName,namespace,errors);}
        return okay;}

    private static void parseIndividualOf(ArrayList<String> individualNames, ArrayList<String> conceptNames,
                                          Namespace namespace, DataBlock dataBlock, Context context) {
        ArrayList<SetConcept> concepts = new ArrayList<>();
        for(String conceptName : conceptNames) {concepts.add(getConcept(conceptName,namespace,dataBlock,context));}
        DAG<Concept> hierarchy = context.conceptHierarchy;
        for(SetConcept concept : concepts) {
            for(String individualName : individualNames) {
                hierarchy.addSubnode(concept, getIndividual(individualName,namespace,dataBlock,context));}}}




    private static boolean checkRelation(String relationName, ArrayList<String> fromNames, ArrayList<String> toNames, Namespace namespace,
                                         Context context,StringBuilder errors) {
        boolean okay = true;
        return okay;}

    private static void parseRelation(String relationName, ArrayList<String> fromNames, ArrayList<String> toNames, Namespace namespace,
                                      DataBlock dataBlock, Context context) {
    }



    private static boolean asConcept(String name, Namespace namespace, StringBuilder errors) {
        ItemWithId item = GlobalData.getItem(name,namespace);
        if(item == null) {return true;}
        if(item.getClass() != SetConcept.class) {
            errors.append("'"+name+"'  is not a Concept, but a '"+item.getClass().getName()+"'\n");
            return false;}
        return true;}

    private static boolean asAttribute(String name, Namespace namespace, String type, StringBuilder errors) {
        ItemWithId item = GlobalData.getItem(name,namespace);
        if(item == null) {return true;}
        try{
            if(item.getClass() != Class.forName("Attributes."+type)) {
                errors.append("'"+name+"'  is not a "+type+", but a '"+item.getClass().getName()+"'\n");
                return false;}}
        catch(Exception ex) {errors.append(ex.toString()); return false;}
        return true;}



    private static SetConcept getConcept(String name, Namespace namespace, DataBlock dataBlock, Context context) {
        ItemWithId item = GlobalData.getItem(name,namespace);
        if(item != null) {return (SetConcept)item;}
        else {
            String pathname = (namespace != null) ? namespace.getPathname(name) : name;
            SetConcept concept = new SetConcept(pathname, context);
            concept.setDataBlock(dataBlock.getPathname());
            return concept;}}

    private static boolean asIndividual(String name, Namespace namespace, StringBuilder errors) {
        ItemWithId item = GlobalData.getItem(name,namespace);
        if(item == null) {return true;}
        if(item.getClass() != IndividualConcept.class) {
            errors.append("'"+name+"'  is not an individual, but a '"+item.getClass().getName()+"'\n");
            return false;}
        return true;}


    private static IndividualConcept getIndividual(String name, Namespace namespace, DataBlock dataBlock, Context context) {
        ItemWithId item = GlobalData.getItem(name,namespace);
        if(item != null) {return (IndividualConcept)item;}
        else {
            String pathname = (namespace != null) ? namespace.getPathname(name) : name;
            IndividualConcept individual = new IndividualConcept(pathname, context);
            individual.setDataBlock(dataBlock.getPathname());
            return individual;}}


    /* ************************  Attributes ******************************* */

    private static boolean checkAttributes(DataBlock dataBlock, Context context, StringBuilder errors) {
        HashMap<String, String> meta = dataBlock.getMetaData();
        ArrayList<Object[]> lines = (ArrayList<Object[]>)dataBlock.getRawData();
        Namespace namespace = dataBlock.getNamespace();
        int end = 0;
        boolean okay = true;
       /* for(int i = 0; i < lines.size(); i = end) {
            Object[] line = lines.get(i);
            end = findNextAttributeDefinition(lines,i+1);
            String type = (String)line[1];
            String name = ((ArrayList<String>)line[2]).get(0);
            okay &= asAttribute(name,namespace,type,errors);
            HashMap<String,ArrayList<String>> map = specifications(lines,i+1,end, type,name, errors);
            if(map == null) {okay = false; continue;}
            switch(type) {
                case "ConceptAttribute" :    okay &= checkConceptAttribute(map,type,name,namespace, context, errors);     break;
                case "DataAttribute":        okay &= checkDatatAttribute(map, context, errors);       break;
                case "ChainAttribute":       okay &= checkChaintAttribute(map, context, errors);      break;
                case "FunctionAttribute":    okay &= checkFunctionAttribute(map, context, errors);    break;
                case "AggregatingAttribute": okay &= checkAggregatingAttribute(map, context, errors); break;
            }}*/
        return okay;}



    private static int findNextAttributeDefinition(ArrayList<Object[]> lines, int start) {
        for(int i = start; i < lines.size(); ++i) {
            Object[] line = lines.get(i);
            int j = i-1;
            switch ((String)line[0]) {
                case "ConceptAttribute":     return j;
                case "DataAttribute":        return j;
                case "ChainAttribute":       return j;
                case "FunctionAttribute":    return j;
                case "AggregatingAttribute": return j;
            }}
        return Integer.MAX_VALUE;}

    private static HashMap<String,ArrayList<String>> specifications(ArrayList<Object[]> lines, int start, int end, String type, String name, StringBuilder errors) {
        HashMap<String,ArrayList<String>> map = new HashMap();
        for(int i = start; i < lines.size(); ++i) {
            Object[] line = lines.get(i);
            ArrayList<String> left = (ArrayList<String>) line[1];
            ArrayList<String> right = (ArrayList<String>) line[2];
            if(left.size() != 1) {errors.append(type + " '" + name + "' has illegal left side of the specification: '"+
                    Utilities.join(left,",",(l->l))); return null;}
            else {map.put(left.get(0),right);}}
        return map;}

    private static boolean parseAttributes(DataBlock dataBlock, Context context, StringBuilder errors) {
        HashMap<String, String> meta = dataBlock.getMetaData();
        return true;
    }



}
