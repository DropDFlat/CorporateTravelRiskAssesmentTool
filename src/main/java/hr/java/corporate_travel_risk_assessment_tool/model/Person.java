package hr.java.corporate_travel_risk_assessment_tool.model;

public abstract class Person extends Entity {
    private String name;
    private int age;

    public Person(Long id, String name, int age) {
        super(id);
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
