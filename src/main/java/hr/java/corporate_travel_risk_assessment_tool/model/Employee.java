package hr.java.corporate_travel_risk_assessment_tool.model;

public class Employee extends Person{
    private String position;

    public Employee(Long id, String name, int age, String position) {
        super(id, name, age);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
