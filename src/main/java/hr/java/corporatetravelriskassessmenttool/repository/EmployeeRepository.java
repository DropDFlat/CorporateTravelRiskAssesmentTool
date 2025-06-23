package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.mapper.EmployeeMapper;
import hr.java.corporatetravelriskassessmenttool.model.Employee;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.changelogRepository;

public class EmployeeRepository<T extends Employee> extends AbstractRepository<T> {
    private static final String DATABASE_ERROR_STRING = "Database config failed";

    @Override
    public synchronized T findById(Long id) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try(PreparedStatement ps = con.prepareStatement("SELECT id, name, " +
                    "job_title, department, date_of_birth, salary FROM employees WHERE id = ?");) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EmptyRepositoryException("Employee with id " + id + " not found");
                }
                return (T) EmployeeMapper.map(rs);

            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    @Override
    public synchronized List<T> findAll() {
        waitForDbAccess();
        List<T> employees = new ArrayList<>();

        try(Connection con = connectToDb()){
            try(Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT ID, NAME, DATE_OF_BIRTH, JOB_TITLE, DEPARTMENT, SALARY FROM employees");
                while (rs.next()) {
                    employees.add((T) EmployeeMapper.map(rs));
                }
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }
        catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
        return employees;
    }

    @Override
    public synchronized void save(T entity, User user){
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try(PreparedStatement ps =con.prepareStatement("INSERT INTO EMPLOYEES(NAME, JOB_TITLE, DEPARTMENT, DATE_OF_BIRTH, SALARY)"
             + "VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getJobTitle());
                ps.setString(3, entity.getDepartment());
                ps.setDate(4, Date.valueOf(entity.getDateOfBirth()));
                ps.setBigDecimal(5, entity.getSalary());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    ChangelogUtil.logCreation(user, "Created new employee ",
                            "Id: " + id + " Name: " + entity.getName());
                }
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    @Override
    public synchronized void update(T entity, User user) {
        Employee existingEmployee = findById(entity.getId());
        waitForDbAccess();
        try(Connection con = connectToDb()) {
            try (PreparedStatement employeeStmt = con.prepareStatement("UPDATE employees SET name = ?, job_title = ?" +
                    ", department = ?, salary = ?, date_of_birth = ? WHERE id = ?");
            ) {
                employeeStmt.setString(1, entity.getName());
                employeeStmt.setString(2, entity.getJobTitle());
                employeeStmt.setString(3, entity.getDepartment());
                employeeStmt.setBigDecimal(4, entity.getSalary());
                employeeStmt.setDate(5, Date.valueOf(entity.getDateOfBirth()));
                employeeStmt.setLong(6, entity.getId());
                employeeStmt.executeUpdate();
                ChangelogUtil.logEmployeeUpdate(user, existingEmployee, entity);
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    @Override
    public synchronized void delete(Long id, User user) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try(PreparedStatement ps = con.prepareStatement("DELETE FROM EMPLOYEES WHERE id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
                changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(),
                        "Deleted employee", "Id: " + id, LocalDateTime.now()));
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally {
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

}
