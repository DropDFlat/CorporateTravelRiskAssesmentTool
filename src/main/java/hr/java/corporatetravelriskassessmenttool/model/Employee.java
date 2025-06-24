package hr.java.corporatetravelriskassessmenttool.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
/**
 * Represents an employee, which is a specialized type of {@link Person}.
 * <p>
 * In addition to basic person attributes like name and date of birth, an employee has job-related
 * properties such as job title, department, and salary.
 * </p>
 */
public class Employee extends Person{
    private String jobTitle;
    private String department;
    private BigDecimal salary;
    /**
     * Constructs an {@code Employee} using the provided {@link Builder}.
     *
     * @param builder the builder used to construct the employee
     */
    public Employee(Builder builder) {
        super(builder.id, builder.name, builder.dateOfBirth);
        this.jobTitle = builder.jobTitle;
        this.department = builder.department;
        this.salary = builder.salary;
    }
    /**
     * @return the job title of the employee
     */
    public String getJobTitle() {
        return jobTitle;
    }
    /**
     * Sets the employee's job title.
     *
     * @param jobTitle the job title to set
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    /**
     * @return the department of the employee
     */
    public String getDepartment() {
        return department;
    }
    /**
     * Sets the employee's department.
     *
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Sets the employee's salary.
     *
     * @param salary the salary to set
     */
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    /**
     * @return the salary of the employee
     */
    public BigDecimal getSalary() {
        return salary;
    }
    /**
     * @return the employee's name
     */
    @Override
    public String toString() {
        return super.getName();
    }
    /**
     * Compares this employee to another based on their ID.
     *
     * @param o the object to compare
     * @return {@code true} if the other object is an employee with the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId());
    }

    /**
     * @return a hash code based on job title, department, and salary
     */
    @Override
    public int hashCode() {
        return Objects.hash(jobTitle, department, salary);
    }
    /**
     * Builder class for constructing {@link Employee} instances.
     */
    public static class Builder {
        private Long id;
        private String name;
        private String jobTitle;
        private String department;
        private BigDecimal salary;
        private LocalDate dateOfBirth;
        /**
         * Sets the employee ID.
         *
         * @param id the ID to set
         * @return the builder instance
         */
        public Employee.Builder setId(Long id) {
            this.id = id;
            return this;
        }
        /**
         * Sets the employee name.
         *
         * @param name the name to set
         * @return the builder instance
         */
        public Employee.Builder setName(String name) {
            this.name = name;
            return this;
        }
        /**
         * Sets the employee's job title.
         *
         * @param jobTitle the job title to set
         * @return the builder instance
         */
        public Employee.Builder setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }
        /**
         * Sets the employee's department.
         *
         * @param department the department to set
         * @return the builder instance
         */
        public Employee.Builder setDepartment(String department) {
            this.department = department;
            return this;
        }
        /**
         * Sets the employee's date of birth.
         *
         * @param dateOfBirth the date of birth to set
         * @return the builder instance
         */
        public Employee.Builder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }
        /**
         * Sets the employee's salary.
         *
         * @param salary the salary to set
         * @return the builder instance
         */
        public Employee.Builder setSalary(BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        /**
         * Builds and returns a new {@link Employee} instance.
         *
         * @return a new {@code Employee}
         */
        public Employee createEmployee() {
            return new Employee(this);
        }
    }
}
