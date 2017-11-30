package simulation.ex05;

import simulation.ex04.UniversityBase;

public class InstitutBase {

    private String name;
    private UniversityBase partOf;

    public InstitutBase(String name, UniversityBase partOf) {
        this.name = name;
        this.partOf = partOf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UniversityBase getPartOf() {
        return partOf;
    }

    public void setPartOf(UniversityBase partOf) {
        this.partOf = partOf;
    }


}
