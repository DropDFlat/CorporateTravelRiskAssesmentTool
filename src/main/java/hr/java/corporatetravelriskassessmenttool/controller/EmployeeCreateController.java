package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.Employee;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.EmployeeRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller class for creating new employees.
 * Provides a user interface for entering the name, salary, job title,
 * department, date of birth, and saving the employee to the database.
 * Implements {@link RoleAware} to receive the currently logged-in user.
 */
public class EmployeeCreateController implements RoleAware {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField salaryTextField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField departmentTextField;
    @FXML
    private TextField jobTitleTextField;
    private User loggedUser;

    /**
     * Saves a new employee based on user input.
     * Validates the input, asks for confirmation, and attempts to save the employee to the database.
     */
    public void saveEmployee(){
        StringBuilder errorMessage = new StringBuilder();
        String name = ValidationUtils.validateString(errorMessage, nameTextField, "Name");
        String department = ValidationUtils.validateString(errorMessage, departmentTextField, "Department");
        String jobTitle = ValidationUtils.validateString(errorMessage, jobTitleTextField, "Job title");
        LocalDate dateOfBirth = ValidationUtils.validateBirthDate(errorMessage, birthDatePicker, "Date of Birth");
        BigDecimal salary = ValidationUtils.validateBigDecimalValue(errorMessage, salaryTextField, "Salary");

        if(!errorMessage.isEmpty()) {
            ValidationUtils.showError("Employee not saved", errorMessage.toString());
        }else{
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Confirm saving employee");
            confirmationAlert.setContentText("Are you sure you want to save this employee?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                Employee employee = new Employee.Builder().setName(name).setDateOfBirth(dateOfBirth)
                        .setDepartment(department).setJobTitle(jobTitle).setSalary(salary).createEmployee();
                try {
                    employeeRepository.save(employee, loggedUser);
                    showSuccess(employee);
                } catch (RepositoryAccessException e) {
                    log.error("Error while saving employee {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while saving employee", e.getMessage());
                }
            }
        }
    }

    /**
     * Displays a success dialog when an employee is saved.
     * @param employee the employee that was successfully saved
     */
    private void showSuccess(Employee employee){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Employee " + employee.getName() + " saved");
        StringBuilder alertInfo = new StringBuilder();
        alertInfo.append("Name: " + employee.getName() + "\n")
                .append("Date of Birth: " + employee.getDateOfBirth() + "\n")
                .append("Department: " + employee.getDepartment() + "\n")
                .append("Job Title: " + employee.getJobTitle() + "\n")
                .append("Salary: " + employee.getSalary() + "\n");
        alert.setContentText(alertInfo.toString());
        alert.showAndWait();
    }
    /**
     * Sets the logged-in user.
     * @param user the user associated with this session
     */
    @Override
    public void setUser(User user) {
        this.loggedUser = user;
    }
}
