package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.Employee;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.EmployeeRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller class responsible for handling updating employees.
 * Allows users to change the selected employee data.
 * Implements {@link RoleAware} interface to track the currently logged-in user.
 */
public class EmployeeUpdateController implements RoleAware {
    private static final Logger log = LoggerFactory.getLogger(EmployeeUpdateController.class);
    @FXML
    private Label updateLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField jobTitleTextField;
    @FXML
    private TextField departmentTextField;
    @FXML
    private TextField salaryTextField;
    @FXML
    private DatePicker birthDatePicker;
    private Employee selectedEmployee;
    private User loggedUser;
    private EmployeeSearchController parentController;
    private AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();

    /**
     * Sets the employee to be updated and loads its data from the repository.
     * @param employee the employee selected for update.
     */
    public void setEmployee(Employee employee) {
        try {
            this.selectedEmployee = employeeRepository.findById(employee.getId());
            updateLabel.setText("Update Employee " + employee.getName());
            populateFields();
        }catch (EmptyRepositoryException e) {
            log.error(e.getMessage());
            ValidationUtils.showError("Employee not found!", e.getMessage());
        }
    }

    /**
     * Populates the UI fields with current data from the selected employee.
     */
    public void populateFields() {
        try{
        this.selectedEmployee = employeeRepository.findById(selectedEmployee.getId());
        nameTextField.setText(selectedEmployee.getName());
        jobTitleTextField.setText(selectedEmployee.getJobTitle());
        departmentTextField.setText(selectedEmployee.getDepartment());
        salaryTextField.setText(String.valueOf(selectedEmployee.getSalary()));
        birthDatePicker.setValue(selectedEmployee.getDateOfBirth());
        }catch(EmptyRepositoryException e) {
            log.error(e.getMessage());
            ValidationUtils.showError("Employee not found!", e.getMessage());
        }
    }

    /**
     * Updates the employee using current field values.
     * Validates input and asks for confirmation from user before saving to database.
     * Shows success message if update is successful.
     */
    public void updateEmployee() {
        StringBuilder errors = new StringBuilder();
        String name = ValidationUtils.validateString(errors, nameTextField, "Name");
        String jobTitle = ValidationUtils.validateString(errors, jobTitleTextField, "Job Title");
        String department = ValidationUtils.validateString(errors, departmentTextField, "Department");
        LocalDate dateOfBirth = ValidationUtils.validateBirthDate(errors, birthDatePicker, "Date of birth");
        BigDecimal salary = ValidationUtils.validateBigDecimalValue(errors, salaryTextField, "Salary");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Update failed", errors.toString());
        }else {
            Employee employee = new Employee.Builder().setId(selectedEmployee.getId()).setName(name).setDateOfBirth(dateOfBirth)
                    .setDepartment(department).setJobTitle(jobTitle)
                    .setSalary(salary).createEmployee();
            try {
                Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Update employee",
                        "Are you sure you want to update this employee?");
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    employeeRepository.update(employee, loggedUser);
                    if(parentController != null) {
                        parentController.reloadEmployeeTable();
                    }
                    showSuccess(employee);
                }
            } catch (RepositoryAccessException e) {
                log.error("Error while updating employee {}", e.getMessage(), e);
                ValidationUtils.showError("Update failed",
                        "An unexpected error occurred while updating employee.\n Please try again.");
            }
        }
    }

    /**
     * Sets a reference to the parent controller in order to refresh the employee table
     * in the search controller after an update is made.
     * @param controller the controller that launched this screen.
     */
    public void setParentController(EmployeeSearchController controller){
        this.parentController = controller;
    }

    /**
     * Shows a success dialog when an employee is updated.
     * @param employee the employee that was updated.
     */
    private void showSuccess(Employee employee){
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Success");
        success.setHeaderText("Employee updated");
        StringBuilder alertText = new StringBuilder();
        alertText.append("Name: ").append(employee.getName())
                .append("\nJob title: ").append(employee.getJobTitle())
                .append("\nDepartment: ").append(employee.getDepartment())
                .append("\nSalary: ").append(employee.getSalary())
                .append("\nDate of birth: ").append(employee.getDateOfBirth());
        success.setContentText(alertText.toString());
        success.showAndWait();
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
