package hr.java.corporate_travel_risk_assessment_tool.model;

public abstract class Person extends Entity {
    private String name;
    private Integer age;

    protected Person(Long id, String name, Integer age) {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
