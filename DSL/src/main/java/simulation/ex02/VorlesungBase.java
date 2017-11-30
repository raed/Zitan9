package simulation.ex02;

public class VorlesungBase {

    private String name;

    public VorlesungBase(){

    }

    public VorlesungBase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


   /*   Von Concept Framework: Concpet und DAG<Concept>  */
// ===================================================

// 1) create a Concept with Id = VorlesungBase,
// 2) add the VorlesungBase to the DAG<Concept>
// 3) create an Edge : PersonBasic -> StudentBasic


   /*   Von Concept Framework: Attribute und DAG<Attribute>  */
// ===================================================

// 1) create a Data-attribute node "name"
// 2) but we have already such an attribute node from Person in DAG<Attribute>
// 3) how should we solve this?
