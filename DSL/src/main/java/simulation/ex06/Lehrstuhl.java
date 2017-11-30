package simulation.ex06;

import simulation.ex04.UniversityBase;
import simulation.ex05.InstitutBase;

import java.util.ArrayList;
import java.util.List;

public class Lehrstuhl {

    private String name;
    private InstitutBase partOf;  // Insitut is part of University ?


    public Lehrstuhl(String name, InstitutBase partOf) {
        this.name = name;
        this.partOf = partOf;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setPartOf(InstitutBase partOf) {
        this.partOf = partOf;
    }

    //
    public InstitutBase getPartOf() {
        return partOf;
    }



    /*
    public List<UniversityBase> getPartOf(){
        List<UniversityBase> list = new ArrayList<UniversityBase>();
        list.add(partOf.getPartOf());
        return list;
        list.add(partOf);

    }
    */


}
