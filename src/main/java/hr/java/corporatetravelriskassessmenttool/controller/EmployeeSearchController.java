package hr.java.corporatetravelriskassessmenttool.controller;


import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.Employee;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.EmployeeRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller class responsible for managing and searching employees.
 * <p>
 *     This controller allows users to:
 *     <ul>
 *         <li>View a table of all employees</li>
 *         <li>Filter employees by values</li>
 *         <li>Delete selected employee</li>
 *         <li>Open a window to update a selected employee by right-clicking it in the table</li>
 *     </ul>
 * </p>
 * Implements the {@link RoleAware} interface to track the currently logged-in user.
 */
public class EmployeeSearchController implements RoleAware {

    private static final Logger log = LoggerFactory.getLogger(EmployeeSearchController.class);
    private User loggedUser;
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    private TableColumn<Employee, String> idTableColumn;
    @FXML
    private TableColumn<Employee, String> nameTableColumn;
    @FXML
    private TableColumn<Employee, String> jobTitleTableColumn;
    @FXML
    private TableColumn<Employee, String> departmentTableColumn;
    @FXML
    private TableColumn<Employee, String> salaryTableColumn;
    @FXML
    private TableColumn<Employee, String> birthDateTableColumn;
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

    private AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();

    /**
     * Initializes the controller, loads all employees from the repository
     * and sets up the table view and context menu.
     */
    public void initialize() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        try{
            employees = FXCollections.observableArrayList(employeeRepository.findAll());
        }catch(RepositoryAccessException e){
            log.error("Error while fetching employees {}", e.getMessage(), e);
            ValidationUtils.showError("Error while fetching employees", e.getMessage());
        }
        employeeTableView.setItems(employees);
        employeeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        jobTitleTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJobTitle()));
        departmentTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));
        salaryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSalary())));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateOfBirth().format(format)));
        employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setupContextMenu();
    }

    /**
     * Filters employees by entered search criteria.
     */
    public void filterEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();
        String employeeName = nameTextField.getText();
        if(!employeeName.isEmpty()){
            employeeList = employeeList.stream().filter(employee ->
                    employee.getName().toLowerCase().contains(employeeName.toLowerCase())).toList();
        }
        String jobTitle = jobTitleTextField.getText();
        if(!jobTitle.isEmpty()){
            employeeList = employeeList.stream().filter(employee -> employee.getJobTitle().toLowerCase()
                    .contains(jobTitle.toLowerCase())).toList();
        }
        String department = departmentTextField.getText();
        if(!department.isEmpty()){
            employeeList = employeeList.stream().filter(employee ->
                employee.getDepartment().toLowerCase().contains(department.toLowerCase())).toList();
        }
        if(!salaryTextField.getText().isEmpty()){
            try {
                BigDecimal salary = new BigDecimal(salaryTextField.getText());
                employeeList = employeeList.stream().filter(employee -> employee.getSalary().equals(salary)).toList();
            }catch(NumberFormatException e){
                log.warn("Invalid salary input when trying to filter employees", e);
                ValidationUtils.showError("Invalid salary format", "Please enter a valid salary (e.g., 5000.00)");
            }
        }
        Optional<LocalDate> dateOfBirth = Optional.ofNullable(birthDatePicker.getValue());
        if(dateOfBirth.isPresent() && !dateOfBirth.get().toString().isEmpty()) {
            employeeList = employeeList.stream()
                    .filter(employee -> employee.getDateOfBirth().equals(dateOfBirth.get()))
                    .toList();
        }
        ObservableList<Employee> employeeObservableList = FXCollections.observableList(employeeList);
        employeeTableView.setItems(employeeObservableList);
    }

    /**
     * Deletes the selected employee from the database and table.
     */
    public void deleteEmployee(){
        Optional<Employee> employee = Optional.ofNullable(employeeTableView.getSelectionModel().getSelectedItem());
        if(employee.isPresent()) {
            Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Delete employee",
                    "Are you sure you want to delete this employee?");
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                try {
                    employeeRepository.delete(employee.get().getId(), loggedUser);
                    employeeTableView.getItems().remove(employee.get());
                } catch (RepositoryAccessException e) {
                    log.error("Error while deleting employee {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while deleting employee",
                            e.getMessage());

                }
            } else {
                ValidationUtils.showError("No employee selected", "Please select an employee to delete");
            }
        }
    }

    /**
     * Configures the right-click context menu for the table rows and enables access to the edit functionality.
     */
    private void setupContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");

        editItem.setOnAction(event -> {
            Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
            if(selectedEmployee != null){
                openUpdateWindow(selectedEmployee);
            }
        });
        contextMenu.getItems().add(editItem);
        employeeTableView.setRowFactory(tableView -> {
            TableRow<Employee> employeeTableRow = new TableRow<>();
            employeeTableRow.setOnContextMenuRequested(event -> {
                if(!employeeTableRow.isEmpty()){
                    employeeTableView.getSelectionModel().select(employeeTableRow.getItem());
                    contextMenu.show(employeeTableRow, event.getScreenX(), event.getScreenY());
                }
            });
            employeeTableRow.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown() && !employeeTableRow.isEmpty()) {
                    employeeTableView.getSelectionModel().select(employeeTableRow.getItem());
                }
            });
            return employeeTableRow;
        });
    }

    /**
     * Opens a new update window for updating the selected employee.
     * @param employee the employee selected to be updated
     */
    private void openUpdateWindow(Employee employee){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/employee-update.fxml"));
            Parent root = loader.load();

            EmployeeUpdateController controller = loader.getController();
            controller.setUser(loggedUser);
            controller.setEmployee(employee);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Update Employee");
            stage.setScene(new Scene(root));
            stage.show();
        }catch(IOException e){
            log.error("Error while opening employee update window", e);
            ValidationUtils.showError("Opening update window failed!",
                    "An unexpected error occurred while opening update window.\n Please try again.");

        } catch (RepositoryAccessException e) {
            log.error("Error while updating employee {}", e.getMessage(), e);
            ValidationUtils.showError("Error while updating employee",
                    e.getMessage());
        }
    }

    /**
     * Reloads the employee table after an update.
     */
    public void reloadEmployeeTable() {
        try{
            List<Employee> employee = employeeRepository.findAll();
            employeeTableView.setItems(FXCollections.observableArrayList(employee));

        }catch(RepositoryAccessException e){
            log.error("Failed to reload employees {}", e.getMessage(), e);
            ValidationUtils.showError("Failed to reload employees", e.getMessage());
        }
        employeeTableView.refresh();
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
