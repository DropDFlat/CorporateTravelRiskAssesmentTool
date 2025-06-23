package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.RiskRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.Optional;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;
/**
 * Controller class for creating different types of risks.
 * Displays appropriate UI fields based on the type selected,
 * and validates user input before saving to database.
 * Implements {@link RoleAware} to receive the currently logged-in user.
 */
public class RiskCreateController implements RoleAware {
    @FXML
    ComboBox<String> riskTypeComboBox;
    @FXML
    TextField descriptionTextField;
    @FXML
    ComboBox<RiskLevel> riskLevelComboBox;
    @FXML
    GridPane healthGridPane;
    @FXML
    GridPane politicalGridPane;
    @FXML
    GridPane environmentalGridPane;
    @FXML
    TextField unrestIndexTextField;
    @FXML
    TextField stabilityIndexTextField;
    @FXML
    TextField severityTextField;
    @FXML
    TextField damageIndexTextField;
    @FXML
    TextField disasterProbabilityTextField;
    private User loggedUser;
    private String health = "Health";
    private String political = "Political";
    private String environmental = "Environmental";
    private static final String CONFRIM_HEADER = "Confirm saving risk";
    private AbstractRepository<Risk> riskRepository = new RiskRepository<>();

    /**
     * Initializes the controller and populates combo boxes and configures field visibility.
     */
    public void initialize() {
        riskTypeComboBox.getItems().addAll(health, political, environmental);
        riskLevelComboBox.getItems().addAll(RiskLevel.values());
        riskTypeComboBox.getSelectionModel().selectFirst();
        setRiskFieldVisibility(riskTypeComboBox.getValue());
        riskLevelComboBox.getSelectionModel().selectFirst();
        riskTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> setRiskFieldVisibility(newValue));
    }

    /**
     * Updates the visible input fields based on selected risk type
     * @param riskType the selected risk type
     */
    private void setRiskFieldVisibility(String riskType){
        healthGridPane.setVisible(health.equals(riskType));
        healthGridPane.setManaged(health.equals(riskType));

        environmentalGridPane.setVisible(environmental.equals(riskType));
        environmentalGridPane.setManaged(environmental.equals(riskType));

        politicalGridPane.setVisible(political.equals(riskType));
        politicalGridPane.setManaged(political.equals(riskType));
    }

    /**
     * Initiates the process of validating input and saving the selected type of risk.
     */
    public void saveRisk()  {
        StringBuilder errors = new StringBuilder();
        String type  = riskTypeComboBox.getValue();
        String description = ValidationUtils.validateString(errors, descriptionTextField, "Description");
        if(description.isEmpty()){
            errors.append("Description cannot be empty\n");
        }
        RiskLevel level = riskLevelComboBox.getValue();

        if(type.equals("Health")) {
            saveHealthRisk(description, level, errors);
        }
        if(type.equals("Political")) {
            savePoliticalRisk(description, level, errors);
        }
        if(type.equals("Environmental")) {
            saveEnvironmentalRisk(description, level, errors);
        }
    }

    /**
     * Validates and saves a health risk to database.
     * @param description the risk description
     * @param level the risk level
     * @param errors a string builder to collect error messages
     */
    private void saveHealthRisk(String description, RiskLevel level, StringBuilder errors)  {
        BigDecimal severity = ValidationUtils.validateBigDecimalPercentageValue(errors, severityTextField, "Severity");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not save health risk", errors.toString());
        }else{
            Optional<ButtonType> result = ValidationUtils.showConfirmation(CONFRIM_HEADER,
                    "Are you sure you want to save this health risk?");
            if(result.isPresent() && result.get() == ButtonType.OK){
                HealthRisk healthRisk = new HealthRisk.HealthRiskBuilder().setDescription(description).setRiskLevel(level)
                        .setSeverity(severity).createHealthRisk();
                try{
                    riskRepository.save(healthRisk, loggedUser);
                    showSuccess(healthRisk);
                }catch (RepositoryAccessException e){
                    log.error("Error while saving health risk {}" , e.getMessage(), e);
                    ValidationUtils.showError("Error while saving health risk", e.getMessage());
                }
            }
        }
    }

    /**
     * Validates and save a political risk to database.
     * @param description the risk description
     * @param level the risk level
     * @param errors a string to collect error messages
     */
    private void savePoliticalRisk(String description, RiskLevel level, StringBuilder errors) {
        Integer stabilityIndex = ValidationUtils.validateIntegerValue(errors, stabilityIndexTextField, "Stability");
        Integer unrestIndex = ValidationUtils.validateIntegerValue(errors, unrestIndexTextField, "Unrest");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not save political risk", errors.toString());
        }else{
            Optional<ButtonType> result = ValidationUtils.showConfirmation(CONFRIM_HEADER,
                    "Are you sure you want to save this political risk?");
            if(result.isPresent() && result.get() == ButtonType.OK){
                PoliticalRisk politicalRisk = new PoliticalRisk.PoliticalRiskBuilder().setDescription(description)
                        .setUnrestIndex(unrestIndex).setStabilityIndex(stabilityIndex).setRiskLevel(level).createPoliticalRisk();
                try{
                    riskRepository.save(politicalRisk, loggedUser);
                    showSuccess(politicalRisk);
                } catch (RepositoryAccessException e) {
                    log.error("Error while saving political risk {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while saving political risk", e.getMessage());
                }
            }
        }
    }

    /**
     * Validates and saves an environmental risk to database.
     * @param description the risk description
     * @param level the risk level
     * @param errors a string to collect error messages
     */
    private void saveEnvironmentalRisk(String description, RiskLevel level, StringBuilder errors){
        Integer damageIndex = ValidationUtils.validateIntegerValue(errors, damageIndexTextField, "Damage");
        BigDecimal disasterProbability = ValidationUtils.validateBigDecimalPercentageValue(errors, disasterProbabilityTextField, "Disaster");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not save environmental risk", errors.toString());
        }else{
            Optional<ButtonType> result = ValidationUtils.showConfirmation(CONFRIM_HEADER,
                    "Are you sure you want to save this environmental risk?");
            if(result.isPresent() && result.get() == ButtonType.OK){
                EnvironmentalRisk environmentalRisk = new EnvironmentalRisk.EnvironmentalRiskBuilder().setRiskLevel(level).setDescription(description)
                        .setDamageIndex(damageIndex).setDisasterProbability(disasterProbability).createEnvironmentalRisk();
                try{
                    riskRepository.save(environmentalRisk, loggedUser);
                    showSuccess(environmentalRisk);
                }catch(RepositoryAccessException e){
                    log.error("Error while saving environmental risk {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while saving environmental risk", e.getMessage());
                }
            }
        }
    }

    /**
     * Displays a success dialog after a risk is saved.
     * @param risk the risk that was saved
     */
    private void showSuccess(Risk risk){
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Success");
        success.setHeaderText("Risk saved");
        StringBuilder alertText = new StringBuilder();
        alertText.append("Description: ").append(risk.getDescription())
                .append("\nLevel: ").append(risk.getRiskLevel())
                .append("\nTotal risk score: ").append(risk.calculateRisk());
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