package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.*;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller class responsible for handling updating trips.
 * Allows user to change the selected risk data.
 * Implements {@link RoleAware} interface to track the currently logged-in user.
 */
public class TripUpdateController implements RoleAware {
    private static final Logger log = LoggerFactory.getLogger(TripUpdateController.class);
    @FXML
    private Label updateLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private ListView<Destination> addDestinationListView;
    @FXML
    private ListView<Destination> removeDestinationListView;
    @FXML
    private ListView<Person> addEmployeeListView;
    @FXML
    private ListView<Person> removeEmployeeListView;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    private Trip<Person> selectedTrip;
    private User loggedUser;
    private AbstractRepository<Trip<Person>> tripRepository = new TripRepository<>();
    private AbstractRepository<Destination> destinationRepository = new DestinationRepository<>();
    private AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();

    /**
     * Initializes list view selection modes for multiple selection.
     */
    public void initialize(){
        addDestinationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        removeDestinationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        addEmployeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        removeEmployeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Loads the selected trip's details using its ID and populates UI fields.
     * @param trip the trip to be updated
     */
    public void setTrip(Trip<Person> trip) {
        try {
            this.selectedTrip = tripRepository.findById(trip.getId());
            updateLabel.setText("Update Trip " + trip.getName());
            populateFields();
        }catch(EmptyRepositoryException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Trip not found!", e.getMessage());
        }
    }

    /**
     * Populates all input fields with current trip data.
     */
    public void populateFields() {
        try {
            this.selectedTrip = tripRepository.findById(selectedTrip.getId());
            nameTextField.setText(selectedTrip.getName());
            startDatePicker.setValue(selectedTrip.getStartDate());
            endDatePicker.setValue(selectedTrip.getEndDate());
            ObservableList<Destination> removeDestination = FXCollections.observableArrayList(selectedTrip.getDestinations());
            List<Destination> addDestination = destinationRepository.findAll();
            removeDestination.forEach(addDestination::remove);
            removeDestinationListView.setItems(removeDestination);
            addDestinationListView.setItems(FXCollections.observableArrayList(addDestination));
            ObservableList<Person> removeEmployee = FXCollections.observableArrayList(selectedTrip.getEmployees());
            List<Employee> addEmployee = employeeRepository.findAll();
            removeEmployee.forEach(addEmployee::remove);
            removeEmployeeListView.setItems(removeEmployee);
            addEmployeeListView.setItems(FXCollections.observableArrayList(addEmployee));
        }catch(EmptyRepositoryException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Employee not found!", e.getMessage());
        }
    }

    /**
     * Validates input fields, applies employee and destination modifications,
     * Builds the trip and saves it to database after asking for user confirmation.
     */
    public void updateTrip() {
        StringBuilder errors = new StringBuilder();
        String name = ValidationUtils.validateString(errors, nameTextField, "Name cannot be empty\n");
        LocalDate startDate = ValidationUtils.validateDate(errors, startDatePicker, "Start date cannot be empty\n");
        LocalDate endDate = ValidationUtils.validateDate(errors, endDatePicker, "End date cannot be empty\n");
        List<Destination> removeDestination = removeDestinationListView.getSelectionModel().getSelectedItems();
        List<Destination> addDestination = addDestinationListView.getSelectionModel().getSelectedItems();
        Set<Destination> existingDestinations = selectedTrip.getDestinations();
        removeDestination.forEach(existingDestinations::remove);
        existingDestinations.addAll(addDestination);
        List<Person> removeEmployee = removeEmployeeListView.getSelectionModel().getSelectedItems();
        List<Person> addEmployee = addEmployeeListView.getSelectionModel().getSelectedItems();
        Set<Person> existingEmployees = selectedTrip.getEmployees();
        removeEmployee.forEach(existingEmployees::remove);
        existingEmployees.addAll(addEmployee);
        if(!errors.isEmpty()){
            ValidationUtils.showError("Error updating trip", String.valueOf(errors));
        }else {
            Trip<Person> trip = new Trip.TripBuilder<Person>().setId(selectedTrip.getId()).setName(name)
                    .setStartDate(startDate).setEndDate(endDate).setDestinations(existingDestinations).setEmployees(existingEmployees)
                    .build();
            try {
                Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Update trip",
                        "Are you sure you want to update this trip?");
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    tripRepository.update(trip, loggedUser);
                    showSuccess(trip);
                }
            } catch (RepositoryAccessException e) {
                log.error(e.getMessage(), e);
                ValidationUtils.showError("Update failed", e.getMessage());
            }
        }
    }

    /**
     * Shows a success dialog when a trip is updated.
     * @param trip the trip that was updated.
     */
    private void showSuccess(Trip<Person> trip){
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Success");
        success.setHeaderText("Trip updated");
        StringBuilder alertText = new StringBuilder();
        alertText.append("Name: ").append(trip.getName())
                .append("\nStart date: ").append(trip.getStartDate())
                .append("\nEnd date: ").append(trip.getEndDate());
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
