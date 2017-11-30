package simulation.ex03;

import simulation.ex01.PersonBasic;
import simulation.ex02.VorlesungBase;

import java.util.List;

public class StudentBasic {


    private int matrikelnummer;


    // Generated from Include
    private PersonBasic person;


    public StudentBasic(int matrikelnummer, PersonBasic person) {
        this.matrikelnummer = matrikelnummer;
        this.person = person;
        person.addAspect(StudentBasic.class,this);
    }

    public PersonBasic getPerson() {return person;}

    public String getName() {return person.getName();}

    public StudentBasic setName(String name) {person.setName(name); return this;}

    public int getMatrikelnummer() {return matrikelnummer;}

    public StudentBasic setMatrikelnummer(int matrikelnummer) {this.matrikelnummer = matrikelnummer; return this;


    }



    // Erweiterung um ConceptAttribute
    private List<VorlesungBase> belegteVorlesungList;


    public StudentBasic(int matrikelnummer, PersonBasic person, List<VorlesungBase> belegteVorlesungList) {
        this(matrikelnummer, person);
        this.belegteVorlesungList = belegteVorlesungList;
    }

    // add and remove methods
    public boolean add(VorlesungBase vorlesungBase){
        return belegteVorlesungList.add(vorlesungBase);
    }

    public boolean remove(VorlesungBase vorlesungBase){
        return belegteVorlesungList.remove(vorlesungBase);
    }


    public List<VorlesungBase> getBelegteVorlesungList() {
        return belegteVorlesungList;
    }

    public void setBelegteVorlesungList(List<VorlesungBase> belegteVorlesungList) {
        this.belegteVorlesungList = belegteVorlesungList;

    }




    public static void main(String[] args) {
        PersonBasic paulPerson = new PersonBasic("Paul");
        StudentBasic paulStudent = new StudentBasic(12345, paulPerson);

        System.out.println(paulPerson.getName());
        System.out.println(paulStudent.getName());

        paulStudent.setName("Paulchen");
        System.out.println(paulPerson.getName());

        StudentBasic s = (StudentBasic)paulPerson.getAspect(StudentBasic.class);
        System.out.println(s.getName());


    }
}


    /*   Von Concept Framework: Concpet und DAG<Concept>  */
// ===================================================

// 1) create a Concept with Id = StudentBasic,
// 2) add the StudentBasic to the DAG<Concept>
// 3) create an Edge : PersonBasic -> StudentBasic
// 3) create an Edge : StudentBasic -> VorlesungBasic


   /*   Von Concept Framework: Attribute und DAG<Attribute>  */
// ===================================================

// 1) create a Data-attribute "matrikelnummer"
// 2) add the DataAttribute to the DAG<Attribute>
// 3) Was ist mit den Properties??

// What is now with the VorlesungBasic??
// It is an attribute of StudentBasic !!
// Should it be added to DAG<Attribute> ???







