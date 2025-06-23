package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.Destination;
import hr.java.corporatetravelriskassessmenttool.model.Risk;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.DestinationRepository;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller class responsible for managing and searching travel destinations.
 * <p>
 *     This controller allows users to:
 *     <ul>
 *         <li>View a table of all destinations</li>
 *         <li>Filter destinations by country and city</li>
 *         <li>Delete selected destinations</li>
 *         <li>Open a window to update a selected destination by right-clicking it in the table</li>
 *     </ul>
 * </p>
 * Implements the {@link RoleAware} interface to track the currently logged-in user.
 */
public class DestinationSearchController implements RoleAware {
    @FXML
    private TextField countryTextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private TableView<Destination> destinationTableView;
    @FXML
    private TableColumn<Destination, String> idTableColumn;
    @FXML
    private TableColumn<Destination, String> countryTableColumn;
    @FXML
    private TableColumn<Destination, String> cityTableColumn;
    @FXML
    private TableColumn<Destination, String> risksTableColumn;
    private User loggedUser;
    AbstractRepository<Destination> destinationRepository = new DestinationRepository<>();

    /**
     * Initializes the controller, loads all destinations from the repository
     * and sets up the table view and context menu.
     */
    public void initialize()  {
        ObservableList<Destination> destinations = FXCollections.observableArrayList();
        try{
            destinations = FXCollections.observableArrayList(destinationRepository.findAll());
        }catch(RepositoryAccessException e){
            log.error("Error while fetching destinations from DB {}", e.getMessage(), e);
            ValidationUtils.showError("Error while fetching destinations", e.getMessage());
        }
        destinationTableView.setItems(destinations);
        destinationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        countryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCountry()));
        cityTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCity()));

        risksTableColumn.setCellValueFactory(cellData -> {
            StringBuilder riskString = new StringBuilder();
            Set<Risk> risks = cellData.getValue().getRisks();
            if(risks.isEmpty()) {
                return new SimpleStringProperty("No risks");
            }else {
                Integer riskCount = risks.size();
                BigDecimal avgRiskScore = risks.stream().map(Risk::calculateRisk).reduce(BigDecimal::add).get()
                        .divide(BigDecimal.valueOf(riskCount));
                riskString.append("Number of risks: ").append(riskCount).append("\nAverage risk score: ").append(avgRiskScore);
                List<String> descriptions = risks.stream().map(Risk::getDescription).toList();
                riskString.append("\nDescriptions: ");
                descriptions.forEach(description -> riskString.append(description).append("\n"));
                return new SimpleStringProperty(riskString.toString());
            }
        });
        destinationTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setupContextMenu();
    }

    /**
     * Filters the displayed destinations based on the entered country and city.
     */
    public void filterDestinations()  {
        String city = cityTextField.getText();
        List<Destination> destinations = destinationRepository.findAll();
        if(!city.isEmpty()){
            destinations = destinations.stream()
                    .filter(destination -> destination.getCity().toLowerCase().contains(city.toLowerCase()))
                    .toList();
        }
        String country = countryTextField.getText();
        if(!country.isEmpty()){
            destinations = destinations.stream()
                    .filter(destination -> destination.getCountry().toLowerCase().contains(country.toLowerCase()))
                    .toList();
        }
        destinationTableView.setItems(FXCollections.observableArrayList(destinations));
    }

    /**
     * Deletes the selected destination from the database.
     */
    public void deleteDestination(){
        Optional<Destination> destination = Optional.ofNullable(destinationTableView.getSelectionModel().getSelectedItem());
        if(destination.isPresent()){
            Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Delete destination",
                    "Are you sure you want to delete this destination");
            if(confirm.isPresent() && confirm.get() == ButtonType.OK){
                try {
                    destinationRepository.delete(destination.get().getId(), loggedUser);
                    destinationTableView.getItems().remove(destination.get());
                }catch(RepositoryAccessException e){
                    log.error("Error while deleting destination {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while deleting destination",
                            e.getMessage());
                }
            }
        }else{
            ValidationUtils.showError("No destination selected", "Please select a destination to delete");
        }
    }

    /**
     * Configures the right-click context menu for the table rows and enables access to the edit functionality.
     */
    private void setupContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");

        editItem.setOnAction(event -> {
            Destination selectedDestination = destinationTableView.getSelectionModel().getSelectedItem();
            if(selectedDestination != null){
                openUpdateWindow(selectedDestination);
            }
        });
        contextMenu.getItems().add(editItem);
        destinationTableView.setRowFactory(tableView -> {
            TableRow<Destination> destinationTableRow = new TableRow<>();
            destinationTableRow.setOnContextMenuRequested(event -> {
                if(!destinationTableRow.isEmpty()){
                    destinationTableView.getSelectionModel().select(destinationTableRow.getItem());
                    contextMenu.show(destinationTableRow, event.getScreenX(), event.getScreenY());
                }
            });
            destinationTableRow.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown() && !destinationTableRow.isEmpty()) {
                    destinationTableView.getSelectionModel().select(destinationTableRow.getItem());
                }
            });
            return destinationTableRow;
        });
    }

    /**
     * Opens a new window for updating the selected destination.
     * @param destination the destination to edit
     */
    private void openUpdateWindow(Destination destination){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/destination-update.fxml"));
            Parent root = loader.load();

            DestinationUpdateController controller = loader.getController();
            controller.setUser(loggedUser);
            controller.setDestination(destination);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Update Destination");
            stage.setScene(new Scene(root));
            stage.show();
        }catch(IOException e){
            log.error("Error while opening destination update window", e);
            ValidationUtils.showError("Opening update window failed!",
                    "An unexpected error occurred while opening update window.\n Please try again.");
        } catch (RepositoryAccessException e) {
            log.error("Error while updating destination {}", e.getMessage(), e);
            ValidationUtils.showError("Error while updating destination", e.getMessage());
        }
    }

    /**
     * Reloads all destinations and refreshes the table view after an update.
     */
    public void reloadDestinationTable(){
        try{
            List<Destination> destinations = destinationRepository.findAll();
            destinationTableView.setItems(FXCollections.observableArrayList(destinations));

        }catch(RepositoryAccessException e){
            log.error("Failed to reload destinations {}", e.getMessage(), e);
            ValidationUtils.showError("Failed to reload destinations", e.getMessage());
        }
        destinationTableView.refresh();
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
