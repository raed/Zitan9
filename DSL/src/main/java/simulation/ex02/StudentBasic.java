package simulation.ex02;

import simulation.ex01.PersonBasic;
import simulation.ex03.VorlesungBase;

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
