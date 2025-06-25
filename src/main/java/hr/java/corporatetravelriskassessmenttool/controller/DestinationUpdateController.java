package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.DestinationRepository;
import hr.java.corporatetravelriskassessmenttool.repository.RiskRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller class responsible for handling updating destinations.
 * Allows users to update the city, country and associated risks of a destination.
 * Implements {@link RoleAware} interface to track the currently logged-in user.
 */
public class DestinationUpdateController implements RoleAware {
    private static final Logger log = LoggerFactory.getLogger(DestinationUpdateController.class);
    @FXML
    private Label updateLabel;
    @FXML
    private TextField countryTextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private ListView<Risk> addRiskListView;
    @FXML
    private ListView<Risk> removeRiskListView;

    private Destination selectedDestination;
    private User loggedUser;
    private DestinationSearchController parentController;
    private AbstractRepository<Destination> destinationRepository = new DestinationRepository<>();
    private AbstractRepository<Risk> riskRepository = new RiskRepository<>();

    /**
     * Initializes the controller and sets selection modes for list views.
     */
    public void initialize(){
        addRiskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        removeRiskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Sets the destination to be updated and loads its data from the repository
     * @param destination the destination selected for update
     */
    public void setDestination(Destination destination) {
        try {
            this.selectedDestination = destinationRepository.findById(destination.getId());
            updateLabel.setText("Update Destination " + destination.getCountry() + " " + destination.getCity());
            populateFields();
        }catch(EmptyRepositoryException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Destination not found!", e.getMessage());
        }
    }

    /**
     * Populates the UI fields with current data from the selected destination.
     * Fills the risk selection lists for adding and removing risks.
     */
    public void populateFields() {
        try {
            this.selectedDestination = destinationRepository.findById(selectedDestination.getId());
            countryTextField.setText(selectedDestination.getCountry());
            cityTextField.setText(selectedDestination.getCity());
            ObservableList<Risk> removeRisks = FXCollections.observableArrayList(selectedDestination.getRisks());
            List<Risk> addRisks = riskRepository.findAll();
            removeRisks.forEach(addRisks::remove);
            removeRiskListView.setItems(removeRisks);
            addRiskListView.setItems(FXCollections.observableArrayList(addRisks));
        }catch(EmptyRepositoryException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Destination not found!", e.getMessage());
        }
    }

    /**
     * Updates the destination using current field values.
     * Validates input and asks for confirmation from user before saving to database.
     * Shows success message if update is successful.
     */
    public void updateDestination() {
        StringBuilder errors = new StringBuilder();
        String country = ValidationUtils.validateString(errors, countryTextField, "Country cannot be empty\n");
        String city = ValidationUtils.validateString(errors, cityTextField, "City cannot be empty\n");
        List<Risk> removeRisks = removeRiskListView.getSelectionModel().getSelectedItems();
        List<Risk> addRisks = addRiskListView.getSelectionModel().getSelectedItems();
        Set<Risk> existingRisks = selectedDestination.getRisks();
        removeRisks.forEach(existingRisks::remove);
        existingRisks.addAll(addRisks);
        if(!errors.isEmpty()){
            ValidationUtils.showError("Update failed", String.valueOf(errors));
        }else {
            Destination destination = new Destination.Builder().setId(selectedDestination.getId()).setCountry(country)
                    .setCity(city).setRisks(existingRisks).createDestination();
            try {
                Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Change destination",
                        "Are you sure you want to save this destination?");
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    destinationRepository.update(destination, loggedUser);
                    showSuccess(destination);
                }
                if(parentController != null){
                    parentController.reloadDestinationTable();
                }
            } catch (RepositoryAccessException e) {
                log.error("Error while updating destination {}", e.getMessage(), e);
                ValidationUtils.showError("Error while updating destination",
                        e.getMessage());
            }
        }
        populateFields();
    }

    /**
     * Sets a reference to the parent controller in order to refresh the destination table
     * in the search controller after an update is made.
     * @param controller the controller that launched this screen.
     */
    public void setParentController(DestinationSearchController controller) {
        this.parentController = controller;
    }

    /**
     * Displays a success dialog when a destination is updated.
     * @param destination the destination that was updated.
     */
    private void showSuccess(Destination destination){
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Success");
        success.setHeaderText("Destination updated");
        StringBuilder alertText = new StringBuilder();
        alertText.append("Country: ").append(destination.getCountry())
                .append("\nCity: ").append(destination.getCity())
                .append("\nRisks: ").append(destination.getRisks());
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
