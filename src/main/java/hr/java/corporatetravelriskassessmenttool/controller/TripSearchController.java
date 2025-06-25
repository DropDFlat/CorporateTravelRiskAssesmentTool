package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.*;
import hr.java.corporatetravelriskassessmenttool.threads.FindHighestRiskTripThread;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;
/**
 * Controller class responsible for managing and searching trips.
 * <p>
 *     This controller allows users to:
 *     <ul>
 *         <li>View a table of all trips</li>
 *         <li>Filter trips by name,employees, destinations, start and end date.</li>
 *         <li>Delete selected trips</li>
 *         <li>Open a window to update a selected trip by right-clicking it in the table</li>
 *         <li>Highlights the riskiest trip in the table in red using timeline, and any risks with warnings in yellow</li>
 *     </ul>
 * </p>
 * Implements the {@link RoleAware} interface to track the currently logged-in user.
 */
public class TripSearchController implements RoleAware {
    @FXML
    private ListView<Employee> employeeListView;
    @FXML
    private ListView<Destination> destinationListView;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField nameTextField;
    @FXML
    private TableView<Trip<Person>> tripTableView;
    @FXML
    private TableColumn<Trip<Person>, String> idTableColumn;
    @FXML
    private TableColumn<Trip<Person>, String> startDateTableColumn;
    @FXML
    private TableColumn<Trip<Person>, String> nameTableColumn;
    @FXML
    private TableColumn<Trip<Person>, String> endDateTableColumn;
    @FXML
    private TableColumn<Trip<Person>, String> employeeTableColumn;
    @FXML
    private TableColumn<Trip<Person>, String> destinationTableColumn;
    @FXML
    private Label testLabel;
    private User loggedUser;
    AbstractRepository<Trip<Person>> tripRepository = new TripRepository<>();
    AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();
    AbstractRepository<Destination> destinationRepository = new DestinationRepository<>();
    private Optional<Trip<Person>> riskiestTrip = Optional.empty();
    private List<Trip<Person>> allTrips = new ArrayList<>();
    private ObservableList<Trip<Person>> filteredTrips = FXCollections.observableArrayList();

    /**
     * Sets the currently identified riskiest trip to be highlighted in the table view.
     *
     * @param trip an {@code Optional} containing the riskiest trip, or empty if none is set
     */
    public void setRiskiestTrip(Optional<Trip<Person>> trip){
        this.riskiestTrip = trip;
    }
    /**
     * Updates the internal list of all trips and re-applies filters to refresh the table view.
     *
     * @param newTrips a list of updated {@code Trip} objects
     */
    public void updateTrips(List<Trip<Person>> newTrips){
        this.allTrips.clear();
        this.allTrips.addAll(newTrips);
        filterTrips();
    }
    /**
     * Initializes the controller, loads all trips from the repository
     * and sets up the table view and context menu.
     * Starts a background thread to periodically determine and highlight the riskiest trip.
     */
    public void initialize() {
        ObservableList<Trip<Person>> trips = FXCollections.observableArrayList();
        try{
            trips = FXCollections.observableArrayList(tripRepository.findAll());
        }catch(RepositoryAccessException e){
            log.error("Error while fetching trips from DB {}", e.getMessage(), e);
            ValidationUtils.showError("Error while fetching trips from DB", e.getMessage());

        }
        List<Employee> employees = employeeRepository.findAll();
        List<Destination> destinations = destinationRepository.findAll();
        idTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        employeeListView.setItems(FXCollections.observableArrayList(employees));
        employeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        destinationListView.setItems(FXCollections.observableList(destinations));
        destinationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        startDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate().format(format)));
        endDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate().format(format)));
        employeeTableColumn.setCellValueFactory(cellData -> {
            StringBuilder employeeString = new StringBuilder();
            cellData.getValue().getEmployees().forEach(employee -> employeeString.append(employee.getName()).append("\n"));
            return new SimpleStringProperty(employeeString.toString());
        });
        destinationTableColumn.setCellValueFactory(cellData ->{
            StringBuilder destinationString = new StringBuilder();
            cellData.getValue().getDestinations().forEach(destination ->
                    destinationString.append(destination.getCity()).append(" ").append(destination.getCountry()).append("\n")
            );
            return new SimpleStringProperty(destinationString.toString());
        });
        allTrips.addAll(trips);
        filteredTrips.setAll(trips);
        tripTableView.setItems(filteredTrips);
        tripTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tripTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tripTableView.setRowFactory(tv -> {
            TableRow<Trip<Person>> row = new TableRow<>() {
                @Override
                protected void updateItem(Trip trip, boolean empty) {
                    super.updateItem(trip, empty);
                    updateRowStyle(this, trip);
                    if (trip == null || empty) {
                        setContextMenu(null);
                    } else {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem editItem = new MenuItem("Edit");
                        editItem.setOnAction(event -> openUpdateWindow(trip));
                        contextMenu.getItems().add(editItem);
                        setContextMenu(contextMenu);
                    }
                }
            };
            row.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown() && !row.isEmpty()) {
                    tripTableView.getSelectionModel().select(row.getItem());
                }
            });

            return row;
        });
        startHighestRiskTimeline();
    }
    /**
     * Filter trips by entered search criteria
     */
    public void filterTrips() {
        try {
            List<Trip<Person>> result = tripRepository.findAll();
            Set<Employee> selectedEmployees = new HashSet<>(employeeListView.getSelectionModel().getSelectedItems());
            if(!selectedEmployees.isEmpty()) result = result.stream()
                        .filter(trip -> trip.getEmployees().stream().anyMatch(selectedEmployees::contains))
                        .toList();
            Set<Destination> selectedDestinations = new HashSet<>(destinationListView.getSelectionModel().getSelectedItems());
            if(!selectedDestinations.isEmpty()) result = result.stream()
                        .filter(trip -> trip.getDestinations().stream().anyMatch(selectedDestinations::contains))
                        .toList();
            String name = nameTextField.getText();
            if(!name.isEmpty()) result = result.stream()
                        .filter(trip -> trip.getName().toLowerCase().contains(name.toLowerCase()))
                        .toList();
            Optional<LocalDate> startDate = Optional.ofNullable(startDatePicker.getValue());
            if(startDate.isPresent()) result = result.stream()
                        .filter(trip -> trip.getStartDate().isAfter(startDate.get()))
                        .toList();
            Optional<LocalDate> endDate = Optional.ofNullable(endDatePicker.getValue());
            if(endDate.isPresent()) result = result.stream()
                        .filter(trip -> trip.getEndDate().isBefore(endDate.get()))
                        .toList();
            tripTableView.setItems(FXCollections.observableList(result));
        }catch(RepositoryAccessException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Filtering failed", e.getMessage());
        }
    }
    /**
     * Deletes the selected trip from the database and table.
     */
    public void deleteTrip(){
        Optional<Trip<Person>> trip = Optional.ofNullable(tripTableView.getSelectionModel().getSelectedItem());
        if(trip.isPresent()){
            Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Delete trip",
                    "Are you sure you want to delete this trip?");
            if(confirm.isPresent() && confirm.get() == ButtonType.OK){
                try {
                    tripRepository.delete(trip.get().getId(), loggedUser);
                    tripTableView.getItems().remove(trip.get());
                }catch(RepositoryAccessException e){
                    log.error("Error while deleting trip {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while deleting trip", e.getMessage());
                }
            }
        }else{
            ValidationUtils.showError("No trip selected", "Select a trip to delete.");
        }
    }
    /**
     * Opens the trip update window for editing the selected trip.
     * @param trip the trip to update
     */
    private void openUpdateWindow(Trip<Person> trip){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/trip-update.fxml"));
            Parent root = loader.load();
            TripUpdateController controller = loader.getController();
            controller.setUser(loggedUser);
            controller.setTrip(trip);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Update Trip");
            stage.setScene(new Scene(root));
            stage.show();
        }catch(IOException e){
            log.error("Error while opening trip update window {}", e.getMessage(), e);
            ValidationUtils.showError("Opening update window failed!",
                    "An unexpected error occurred while opening update window.\n Please try again.");
        } catch (RepositoryAccessException e) {
            log.error("Error while updating trip {}", e.getMessage(), e);
            ValidationUtils.showError("Error while updating trip", e.getMessage());
        }
    }
    /**
     * Starts a background timeline that continuously evaluates and highlights
     * the riskiest trip in the table every 5 seconds.
     */
    private void startHighestRiskTimeline(){
        FindHighestRiskTripThread thread = new FindHighestRiskTripThread(tripTableView, this);
        Timeline riskiestTripTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Thread runner = new Thread(thread);
            runner.setDaemon(true);
            runner.start();
        }),
                new KeyFrame(Duration.seconds(5)));
        riskiestTripTimeline.setCycleCount(Animation.INDEFINITE);
        riskiestTripTimeline.play();
    }
    /**
     * Updates the row style for a given trip in the table view.
     * <ul>
     *     <li>Trips with warnings(e.g. the end date is before the start date) are highlighted with a yellow background</li>
     *     <li>The currently riskiest trip is highlighted with a red background</li>
     *     <li>Rows are reset if the trip is null</li>
     * </ul>
     * @param row the table row to style
     * @param trip the trip associated with the row
     */
    private void updateRowStyle(TableRow<Trip<Person>> row, Trip<Person> trip){
        if(trip == null){
            row.setStyle("");
            return;
        }
        String style = "";
        if(trip.hasWarning()){
            style = "-fx-background-color: #fff3cd;";
        }
        if (riskiestTrip.isPresent() && trip.getId().equals(riskiestTrip.get().getId())) {
            style = "-fx-background-color: #ffcccc;";
        }
        row.setStyle(style);
    }

    /**
     * Reloads the trip table after an update.
     */
    public void reloadTripTable() {
        try{
            List<Trip<Person>> trips = tripRepository.findAll();
            allTrips.clear();
            allTrips.addAll(trips);
            filterTrips();
        }catch(RepositoryAccessException e){
            log.error("Failed to reload trips {}", e.getMessage(), e);
            ValidationUtils.showError("Failed to reload trips", e.getMessage());
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