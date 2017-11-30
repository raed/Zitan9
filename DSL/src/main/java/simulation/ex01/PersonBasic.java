package simulation.ex01;

import java.util.HashMap;

public class PersonBasic {

    private String name;
    private HashMap<Class,Object> aspects = new HashMap<Class,Object>();


    public PersonBasic(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    public PersonBasic setName(String name) {this.name = name; return this;}

    public void addAspect(Class clazz, Object object) {aspects.put(clazz,object);}

    public Object getAspect(Class clazz) {return aspects.get(clazz);}


    /*   Von Concept Framework: Concpet und DAG<Concept>  */
    // ===================================================

    // 1) create a Concept with Id = PersonBasic,
    // 2) add the PersonBasic to the DAG<Concept>


   /*   Von Concept Framework: Attribute und DAG<Attribute>  */
    // ===================================================

    // 1) create a Data-attribute "name"
    // 2) add the DataAttribute to the DAG<Attribute>
    // 3) Was ist mit den Properties??


    /*  Questions */
    // ==============
    // Q1) Concept is abstract. Which class to use SetConcept or DerivedConcept?!  Or Refactoring Concept Package!
    // Q2) How should be the relation between DAG<Concept> and DAG<Attribute>
    //     cause there is no relation between PersonBasic Node in DAG<Concept> and
    //     the Attribute Node "Name" in DAG<Attribute>


}
