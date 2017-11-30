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

}
