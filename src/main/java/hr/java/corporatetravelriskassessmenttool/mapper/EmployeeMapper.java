package hr.java.corporatetravelriskassessmenttool.mapper;

import hr.java.corporatetravelriskassessmenttool.model.Employee;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EmployeeMapper {
    private EmployeeMapper() {}
    public static Employee map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
        String jobTitle = rs.getString("job_title");
        String department = rs.getString("department");
        BigDecimal salary = rs.getBigDecimal("salary");
        return new Employee.Builder().setId(id).setName(name).setDateOfBirth(dateOfBirth).setDepartment(department).setJobTitle(jobTitle)
                .setSalary(salary).createEmployee();
    }
}
