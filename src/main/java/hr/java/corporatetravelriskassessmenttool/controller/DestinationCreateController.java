package hr.java.corporatetravelriskassessmenttool.controller;

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

import java.util.Optional;
import java.util.Set;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;


/**
 * Controller class for creating new destinations.
 * Provides a user interface for entering the city and country names,
 * selecting associated risks, and saving the destination to the database.
 * Implements {@link RoleAware} to receive the currently logged-in user.
 */
public class DestinationCreateController implements RoleAware {
    @FXML
    private TextField cityTextField;
    @FXML
    private TextField countryTextField;
    @FXML
    private ListView<Risk> riskListView;
    private User loggedUser;

    /**
     * Initializes the controller and loads all available risks into the list view.
     */
    public void initialize() {
        AbstractRepository<Risk> riskRepository = new RiskRepository<>();
        ObservableList<Risk> risks = FXCollections.observableArrayList(riskRepository.findAll());

        riskListView.setItems(risks);
        riskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Saves a new destinations based on user input.
     * Validates the input, asks for confirmation, and attempts to save the destination to the database.
     */
    public void saveDestination() {
        StringBuilder errors = new StringBuilder();
        String city = ValidationUtils.validateString( errors, cityTextField, "City");
        String country = ValidationUtils.validateString( errors, countryTextField,"Country");
        Set<Risk> risks = ValidationUtils.validateRisks(errors, riskListView, "Risks");

        if(!errors.isEmpty()){
            ValidationUtils.showError("Destination not saved!", errors.toString());
        }else{
            Optional<ButtonType> result = ValidationUtils.showConfirmation("Confirm saving destination",
                    "Are you sure you want to save this destination?");
            if(result.isPresent() && result.get() == ButtonType.OK){
                Destination destination = new Destination.Builder().setCity(city).setCountry(country).setRisks(risks).createDestination();
                try{
                    AbstractRepository<Destination> destinationRepository = new DestinationRepository<>();
                    destinationRepository.save(destination, loggedUser);
                    showSuccess(destination);
                }catch(RepositoryAccessException e){
                    log.error("Error while saving destination {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while saving destination", e.getMessage());
                }
            }
        }
    }

    /**
     * Displays a success dialog when a destination is saved.
     * @param destination the destination that was successfully saved
     */
    private void showSuccess(Destination destination){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Destination " + destination.getCountry() + " " + destination.getCity() + " saved");
        StringBuilder alertInfo = new StringBuilder();
        alertInfo.append("Risks: " + destination.getRisks() + "\n");
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
