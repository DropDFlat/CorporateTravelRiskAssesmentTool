package hr.java.corporate_travel_risk_assessment_tool.model;

import java.math.BigDecimal;
import java.util.Set;

public class Employee extends Person{
    private JobPosition position;
    private BigDecimal salary;

    public Employee(Builder builder) {
        super(builder.id, builder.name, builder.age);
        this.position = builder.position;
        this.salary = builder.salary;
    }

    public JobPosition getPosition() {
        return position;
    }

    public void setPosition(JobPosition position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public static class Builder {
        private Long id;
        private String name;
        private JobPosition position;
        private BigDecimal salary;
        private Integer age;

        public Employee.Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Employee.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Employee.Builder setPosition(JobPosition position) {
            this.position = position;
            return this;
        }

        public Employee.Builder setAge(Integer age) {
            this.age = age;
            return this;
        }
        public Employee.Builder setSalary(BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        public Employee createEmployee() {
            return new Employee(this);
        }
    }
}
