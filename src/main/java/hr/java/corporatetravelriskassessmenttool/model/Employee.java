package hr.java.corporatetravelriskassessmenttool.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Employee extends Person{
    private String jobTitle;
    private String department;
    private BigDecimal salary;

    public Employee(Builder builder) {
        super(builder.id, builder.name, builder.dateOfBirth);
        this.jobTitle = builder.jobTitle;
        this.department = builder.department;
        this.salary = builder.salary;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return super.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobTitle, department, salary);
    }

    public static class Builder {
        private Long id;
        private String name;
        private String jobTitle;
        private String department;
        private BigDecimal salary;
        private LocalDate dateOfBirth;

        public Employee.Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Employee.Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Employee.Builder setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }
        public Employee.Builder setDepartment(String department) {
            this.department = department;
            return this;
        }

        public Employee.Builder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
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
