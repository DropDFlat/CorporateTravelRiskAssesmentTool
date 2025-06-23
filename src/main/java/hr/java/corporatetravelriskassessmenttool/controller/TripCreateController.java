package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.DestinationRepository;
import hr.java.corporatetravelriskassessmenttool.repository.EmployeeRepository;
import hr.java.corporatetravelriskassessmenttool.repository.TripRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Controller class responsible for handling the creation of business trips.
 * Provides a user interface for inputting trip details such as name, date range,
 * destinations, and employees, and saves the trip to database.
 * Implements {@link RoleAware} to receive the currently logged-in user.
 */
public class TripCreateController implements RoleAware {
    private static final Logger log = LoggerFactory.getLogger(TripCreateController.class);
    @FXML
    private TextField nameTextField;
    @FXML
    private ListView<Destination> destinationListView;
    @FXML
    private ListView<Employee> employeeListView;
    @FXML
    DatePicker startDatePicker;
    @FXML
    DatePicker endDatePicker;
    private User loggedUser;

    /**
     * Initializes the controller and populates the employee and destination list views.
     */
    public void initialize() {
        AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();
        AbstractRepository<Destination> destinationRepository = new DestinationRepository<>();

        destinationListView.setItems(FXCollections.observableList(destinationRepository.findAll()));
        destinationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        employeeListView.setItems(FXCollections.observableList(employeeRepository.findAll()));
        employeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Handles saving a trip to database after validating input fields and getting confirmation.
     */
    public void saveTrip(){
        StringBuilder errors = new StringBuilder();
        String name = ValidationUtils.validateString(errors, nameTextField, "name");
        Set<Destination> destinations = validateDestinationList(errors);
        Set<Person> employees = validateEmployeeList(errors);
        LocalDate startDate = ValidationUtils.validateDate(errors, startDatePicker, "start date");
        LocalDate endDate = ValidationUtils.validateDate(errors, endDatePicker, "end date");

        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not save trip", errors.toString());
        }else{
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Confirm saving trip");
            confirmation.setContentText("Are you sure you want to save the trip?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                Trip<Person> trip = new Trip.TripBuilder<Person>().setName(name).setStartDate(startDate).setEndDate(endDate)
                        .setDestinations(destinations).setEmployees(employees).build();
                try{
                    AbstractRepository<Trip<Person>> tripRepository = new TripRepository<>();
                    tripRepository.save(trip, loggedUser);
                    showSuccess(trip);
                }catch(RepositoryAccessException e){
                    String failed = "Error while saving trip";
                    ValidationUtils.showError(failed, e.getMessage());
                    log.error(failed, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Validates that at least one destination is selected in the list view.
     * @param errors a string builder that collects error messages
     * @return a set of selected {@link Destination} objects
     */
    private Set<Destination> validateDestinationList(StringBuilder errors){
        Set<Destination> destinations = new HashSet<>(destinationListView.getSelectionModel().getSelectedItems());
        if(destinations.isEmpty()){
            errors.append("No destinations selected\n");
        }
        return destinations;
    }

    /**
     * Validates that at least one employee is selected in the list view.
     * @param errors a string builder that collects error messages
     * @return a set of selected {@link Person} specifically {@link Employee} objects
     */
    private Set<Person> validateEmployeeList(StringBuilder errors){
        Set<Person> employees = new HashSet<>(employeeListView.getSelectionModel().getSelectedItems());
        if(employees.isEmpty()){
            errors.append("No employees selected\n");
        }
        return employees;
    }

    /**
     * Displays a success dialog when a trip is saved.
     * @param trip the trip that was saved
     */
    private void showSuccess(Trip<Person> trip){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Destination " + trip.getName() + " saved");
        StringBuilder alertInfo = new StringBuilder();
        alertInfo.append("Start date: " + trip.getStartDate() + "\n")
                .append("End date: " + trip.getEndDate() + "\n")
                .append("Employees: " + trip.getEmployees() + "\n")
                .append("Destinations: " + trip.getDestinations() + "\n");
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
