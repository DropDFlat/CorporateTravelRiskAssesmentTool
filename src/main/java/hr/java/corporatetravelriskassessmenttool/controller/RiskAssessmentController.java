package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.EmployeeRepository;
import hr.java.corporatetravelriskassessmenttool.repository.RiskAssessmentRepository;
import hr.java.corporatetravelriskassessmenttool.repository.TripRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller responsible for managing the risk assessment process.
 * <p>
 *     This controller handles:
 *     <ul>
 *         <li>Loading available trips into a combo box</li>
 *         <li>Displaying assessments for a selected trip</li>
 *         <li>Generating new assessments based on trip risks and employees</li>
 *         <li>Deleting and filtering assessments in table</li>
 *     </ul>
 * </p>
 * Implements {@link RoleAware} to receive the currently logged-in user.
 */
public class RiskAssessmentController implements RoleAware{
    @FXML
    private ComboBox<Trip<Person>> tripComboBox;
    @FXML
    private TableView<RiskAssessment<Person, Risk>> assessmentTableView;
    @FXML
    private TableColumn<RiskAssessment<Person, Risk>, String> employeeTableColumn;
    @FXML
    private TableColumn<RiskAssessment<Person, Risk>, String> riskTypeTableColumn;
    @FXML
    private TableColumn<RiskAssessment<Person, Risk>, String> riskScoreTableColumn;
    @FXML
    private TableColumn<RiskAssessment<Person, Risk>, String> assessmentDateTableColumn;
    @FXML
    private TableColumn<RiskAssessment<Person, Risk>, String> descriptionTableColumn;
    @FXML
    private Label statusLabel;
    @FXML
    private ComboBox<Person> employeeComboBox;
    @FXML
    private ComboBox<String> riskTypeComboBox;
    @FXML
    private TextField riskScoreTextField;
    @FXML
    private DatePicker assessmentDatePicker;
    private User loggedUser;
    private AbstractRepository<Trip<Person>> tripRepository = new TripRepository<>();
    private AbstractRepository<RiskAssessment<Person, Risk>> assessmentRepository = new RiskAssessmentRepository<>();
    private AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();
    private Optional<List<RiskAssessment<Person, Risk>>> filteredRiskAssessments;
    private static final String HEALTH_TYPE = "Health";
    private static final String POLITICAL_TYPE = "Political";
    private static final String ENVIRONMENTAL_TYPE = "Environmental";
    /**
     * Initializes the controller,
     * binds the table columns to appropriate properties, and loads all trips into the combo box.
     */
    public void initialize() {
        List<Person> employees = new ArrayList<>();
        employees.addAll(employeeRepository.findAll());
        employeeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPerson().getName()));
        riskTypeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRisk().getClass().getSimpleName()));
        riskScoreTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRisk().calculateRisk().toString()));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        assessmentDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAssessmentDate().format(format)));
        descriptionTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().generateReport()));
        List<String> types = new ArrayList<>();
        types.add(ENVIRONMENTAL_TYPE);
        types.add(HEALTH_TYPE);
        types.add(POLITICAL_TYPE);
        riskTypeComboBox.setItems(FXCollections.observableList(types));
        riskTypeComboBox.getSelectionModel().selectFirst();
        employeeComboBox.setItems(FXCollections.observableList(employees));
        assessmentTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tripComboBox.setItems(FXCollections.observableArrayList(tripRepository.findAll()));
    }

    /**
     * Generates or updates risk assessments for the selected trip.
     * For each employee-destination-risk combination in the trip, a new assessment is created,
     * or if an assessment already exists it is updated instead.
     */
    public void generateAssessments() {
        Trip<Person> selectedTrip = tripComboBox.getSelectionModel().getSelectedItem();
        if (selectedTrip == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No trip selected");
            alert.setContentText("Please select a trip");
            alert.showAndWait();
            return;
        }
        Set<Person> employees = selectedTrip.getEmployees();
        Set<Destination> destinations= selectedTrip.getDestinations();
        Set<Risk> risks = new HashSet<>();
        destinations.forEach(destination ->
            risks.addAll(destination.getRisks()));
        List<RiskAssessment<Person, Risk>> assessments = new ArrayList<>();
        Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Confirm generating assessments",
                "Are you sure you want to generate new Assessments?");
        if(confirm.isPresent() && confirm.get() == ButtonType.OK) {
            List<RiskAssessment<Person, Risk>> existing = assessmentRepository.findAll().stream()
                    .filter(a -> a.getTrip().equals(selectedTrip)).toList();
            for (Person e : employees) {
                for (Risk r : risks) {
                    RiskAssessment<Person, Risk> assessment = new RiskAssessment.Builder<>().setRisk(r).setPerson(e)
                            .setTrip(selectedTrip).setAssessmentDate(LocalDate.now()).build();
                    boolean exists = existing.stream().anyMatch(existingAssessment ->
                            existingAssessment.getRisk().getId().equals(r.getId()) &&
                                    existingAssessment.getPerson().getId().equals(e.getId()) &&
                                    existingAssessment.getTrip().getId().equals(selectedTrip.getId())
                    );
                    if(Boolean.TRUE.equals(exists)) {
                        assessment.setId(
                                existing.stream().filter(existingAssessment ->
                                        existingAssessment.getPerson().getId().equals(e.getId())
                                        && existingAssessment.getRisk().getId().equals(r.getId())
                                        && existingAssessment.getTrip().getId().equals(selectedTrip.getId()))
                                        .findFirst().map(RiskAssessment::getId).orElse(null)
                        );
                        assessmentRepository.update(assessment, loggedUser);
                    }else {
                        assessmentRepository.save(assessment, loggedUser);
                    }
                    assessments.add(assessment);
                }
            }
        }

        assessmentTableView.setItems(FXCollections
                .observableArrayList(assessments));
    }

    /**
     * Loads existing assessments for the selected trip if they exist.
     */
    public void tripSelected() {
        Trip<Person> selectedTrip = tripComboBox.getSelectionModel().getSelectedItem();
        if (selectedTrip == null) return;
        List<RiskAssessment<Person, Risk>> existing = assessmentRepository.findAll();
        existing = existing.stream().filter(assessment -> assessment.getTrip().getId().equals(selectedTrip.getId())).toList();
        filteredRiskAssessments = Optional.of(existing);
        if(!existing.isEmpty()) {
            assessmentTableView.setItems(FXCollections.observableArrayList(existing));
            statusLabel.setText("Loaded previously saved assessments.");
        }else{
            assessmentTableView.setItems(FXCollections.observableArrayList());
            statusLabel.setText("No assessments found, select 'generate assessments' to generate new ones.");
        }
    }

    /**
     * Filters the displayed risk assessments.
     * Displays error messages if inputs are invalid.
     */
    public void filterAssessments(){
        if(filteredRiskAssessments.isEmpty()) return;
        List<RiskAssessment<Person, Risk>> assessments = filteredRiskAssessments.get();
        String type = riskTypeComboBox.getValue();
        switch(type){
            case ENVIRONMENTAL_TYPE:
                assessments = assessments.stream()
                        .filter(riskAssessment -> riskAssessment.getRisk() instanceof EnvironmentalRisk)
                        .toList();
                break;
            case HEALTH_TYPE:
                assessments = assessments.stream()
                        .filter(riskAssessment -> riskAssessment.getRisk() instanceof HealthRisk)
                        .toList();
                break;
            case POLITICAL_TYPE:
                assessments = assessments.stream()
                        .filter(riskAssessment -> riskAssessment.getRisk() instanceof PoliticalRisk)
                        .toList();
                break;
            default:
        }
        if(!riskScoreTextField.getText().isEmpty()){
            try {
                BigDecimal riskScore = new BigDecimal(riskScoreTextField.getText());
                assessments = assessments.stream().filter(assessment ->
                    assessment.getRisk().calculateRisk().compareTo(riskScore) < 0
                ).toList();
            }catch(NumberFormatException e){
                log.warn("Invalid score input when trying to filter employees", e);
                ValidationUtils.showError("Invalid salary format", "Please enter a valid risk score (e.g., 20.00)");
            }
        }
        Optional<LocalDate> assessmentDate = Optional.ofNullable(assessmentDatePicker.getValue());
        if(assessmentDate.isPresent() && !assessmentDate.get().toString().isEmpty()) {
            assessments = assessments.stream()
                    .filter(riskAssessment -> riskAssessment.getAssessmentDate().equals(assessmentDate.get()))
                    .toList();
        }
        ObservableList<RiskAssessment<Person, Risk>> assessmentObservableList = FXCollections.observableList(assessments);
        assessmentTableView.setItems(assessmentObservableList);

    }

    /**
     * Deletes the selected risk assessment from the database.
     */
    public void deleteAssessment() {
        Optional<RiskAssessment<Person, Risk>> riskAssessment = Optional.ofNullable(assessmentTableView.getSelectionModel().getSelectedItem());
        if(riskAssessment.isPresent()){
            Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Delete risk assessment",
                    "Are you sure you want to delete this risk assessment?");
            if(confirm.isPresent() && confirm.get() == ButtonType.OK){
                try {
                    assessmentRepository.delete(riskAssessment.get().getId(), loggedUser);
                    assessmentTableView.getItems().remove(riskAssessment.get());
                }catch(RepositoryAccessException e){
                    log.error("Error while deleting risk assessment {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while deleting risk assessment", e.getMessage());
                }
            }
        }else{
            ValidationUtils.showError("No assessment selected", "Select a risk assessment to delete.");
        }

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
