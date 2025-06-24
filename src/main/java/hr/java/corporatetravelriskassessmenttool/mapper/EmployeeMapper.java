package hr.java.corporatetravelriskassessmenttool.mapper;

import hr.java.corporatetravelriskassessmenttool.model.Employee;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
/**
 * Utility class for mapping SQL {@link ResultSet} rows to {@link Employee} model objects.
 * <p>
 * This class provides a static method to convert a result set row into a fully constructed
 * {@link Employee} object using the builder pattern.
 * </p>
 */
public class EmployeeMapper {

    /**
     * Private constructor to prevent instantiation.
     */
    private EmployeeMapper() {}
    /**
     * Maps the current row of the given {@link ResultSet} to an {@link Employee} object.
     *
     * @param rs the {@link ResultSet} positioned at the row to map
     * @return a new {@link Employee} instance populated with the data from the result set
     * @throws SQLException if a database access error occurs or required columns are missing
     */
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
