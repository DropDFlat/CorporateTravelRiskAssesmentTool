package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;
import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.RiskRepository;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.Optional;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller responsible for updating various types of risks
 * Allows users to change the selected risk data.
 * Implements {@link RoleAware} interface to track the currently logged-in user.
 */
public class RiskUpdateController implements RoleAware {
    @FXML
    private TextField disasterProbabilityTextField;
    @FXML
    private TextField unrestIndexTextField;
    @FXML
    private TextField stabilityIndexTextField;
    @FXML
    private GridPane healthGridPane;
    @FXML
    private TextField severityTextField;
    @FXML
    private GridPane politicalGridPane;
    @FXML
    private GridPane environmentalGridPane;
    @FXML
    private TextField damageIndexTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private ComboBox<RiskLevel> riskLevelComboBox;
    private User loggedUser;
    private Risk selectedRisk;
    private RiskSearchController parentController;

    private AbstractRepository<Risk> riskRepository = new RiskRepository<>();

    private static final String UPDATE_CONFIRM_STRING = "Update risk.";
    private static final String UPDATE_FAILED_STRING = "Update failed.";

    /**
     * Initializes the controller and populates the risk level combo box.
     */
    public void initialize(){
        riskLevelComboBox.getItems().addAll(RiskLevel.values());
        riskLevelComboBox.getSelectionModel().select(0);
    }

    /**
     * loads the selected risk from the repository by ID and populates the form field.
     * @param risk the risk object selected for updating
     */
    public void setRisk(Risk risk) {
        try {
            this.selectedRisk = riskRepository.findById(risk.getId());
            populateFields();
        }catch(EmptyRepositoryException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Risk not found!", e.getMessage());
        }
    }

    /**
     * Fill the form fields with data based on the selected risk and type of risk.
     * Makes specific section visible based on risk type.
     */
    public void populateFields() {
        try {
            this.selectedRisk = riskRepository.findById(selectedRisk.getId());
            descriptionTextField.setText(selectedRisk.getDescription());
            riskLevelComboBox.getSelectionModel().select(selectedRisk.getRiskLevel());
            if (selectedRisk instanceof EnvironmentalRisk environmentalRisk) {
                environmentalGridPane.setVisible(true);
                environmentalGridPane.setManaged(true);
                damageIndexTextField.setText(environmentalRisk.getDamageIndex().toString());
                disasterProbabilityTextField.setText(String.valueOf(environmentalRisk.getDisasterProbability()
                        .multiply(BigDecimal.valueOf(100))));
            }
            if (selectedRisk instanceof PoliticalRisk politicalRisk) {
                politicalGridPane.setVisible(true);
                politicalGridPane.setManaged(true);
                unrestIndexTextField.setText(String.valueOf(politicalRisk.getUnrestIndex()));
                stabilityIndexTextField.setText(String.valueOf(politicalRisk.getStabilityIndex()));
            }
            if (selectedRisk instanceof HealthRisk healthRisk) {
                healthGridPane.setVisible(true);
                healthGridPane.setManaged(true);
                severityTextField.setText(String.valueOf(healthRisk.getSeverity().multiply(BigDecimal.valueOf(100))));
            }
        }catch(EmptyRepositoryException e){
            log.error(e.getMessage(), e);
            ValidationUtils.showError("Risk not found!", e.getMessage());
        }
    }

    /**
     * Validates input and updates the selected risk object based on its type.
     * Delegates to specific update methods and reloads the parent risk table.
     */
    public void updateRisk() {
        StringBuilder errors = new StringBuilder();
        String description = ValidationUtils.validateString(errors, descriptionTextField, "description");
        RiskLevel riskLevel = riskLevelComboBox.getSelectionModel().getSelectedItem();
        if(selectedRisk instanceof EnvironmentalRisk){
            updateEnvironmentalRisk(errors, description, riskLevel);
        }
        if(selectedRisk instanceof PoliticalRisk){
            updatePoliticalRisk(errors, description, riskLevel);
        }
        if(selectedRisk instanceof HealthRisk){
            updateHealthRisk(errors, description, riskLevel);
        }
        if(parentController != null){
            parentController.reloadRiskTable();
        }
    }

    /**
     * updates a {@link HealthRisk} instance after validation and confirmation.
     * @param errors a string builder to collect error messages
     * @param description risk description
     * @param riskLevel the selected risk level
     */
    private void updateHealthRisk(StringBuilder errors, String description, RiskLevel riskLevel) {
        BigDecimal severity = ValidationUtils.validateBigDecimalPercentageValue(errors, severityTextField, "Severity");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not update health risk", errors.toString());
        }else {
            HealthRisk healthRisk = new HealthRisk.HealthRiskBuilder().setId(selectedRisk.getId()).setDescription(description)
                    .setRiskLevel(riskLevel).setSeverity(severity).createHealthRisk();
            try {
                Optional<ButtonType> confirm = ValidationUtils.showConfirmation(UPDATE_CONFIRM_STRING,
                        "Are you sure you want to update this health risk?");
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    riskRepository.update(healthRisk, loggedUser);
                    showSuccess(healthRisk);
                }
            } catch (RepositoryAccessException e) {
                log.error("Error while updating health risk {}", e.getMessage(), e);
                ValidationUtils.showError(UPDATE_FAILED_STRING,
                        "An unexpected error occurred while updating health risk.\n Please try again.");
            }
        }
    }
    /**
     * updates a {@link EnvironmentalRisk} instance after validation and confirmation.
     * @param errors a string builder to collect error messages
     * @param description risk description
     * @param riskLevel the selected risk level
     */
    private void updateEnvironmentalRisk(StringBuilder errors, String description, RiskLevel riskLevel)  {
        Integer damageIndex = ValidationUtils.validateIntegerValue(errors, damageIndexTextField, "Damage Index");
        BigDecimal disasterProbability = ValidationUtils.validateBigDecimalPercentageValue(errors,
                disasterProbabilityTextField, "Disaster Probability");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not update environmental risk", errors.toString());
        }else {
            EnvironmentalRisk environmentalRisk = new EnvironmentalRisk.EnvironmentalRiskBuilder().setId(selectedRisk.getId()).setDescription(description)
                    .setRiskLevel(riskLevel).setDamageIndex(damageIndex).setDisasterProbability(disasterProbability).createEnvironmentalRisk();
            try {
                Optional<ButtonType> confirm = ValidationUtils.showConfirmation(UPDATE_CONFIRM_STRING,
                        "Are you sure you want to update this environmental risk?");
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    riskRepository.update(environmentalRisk, loggedUser);
                    showSuccess(environmentalRisk);
                }
            } catch (RepositoryAccessException e) {
                log.error("Error while updating environmental risk {}", e.getMessage(), e);
                ValidationUtils.showError(UPDATE_FAILED_STRING,
                        "An unexpected error occurred while updating environmental risk.\n Please try again.");
            }
        }
    }
    /**
     * updates a {@link PoliticalRisk} instance after validation and confirmation.
     * @param errors a string builder to collect error messages
     * @param description risk description
     * @param riskLevel the selected risk level
     */
    private void updatePoliticalRisk(StringBuilder errors, String description, RiskLevel riskLevel)  {
        Integer unrestIndex = ValidationUtils.validateIntegerValue(errors, unrestIndexTextField, "Damage Index");
        Integer stabilityIndex = ValidationUtils.validateIntegerValue(errors, stabilityIndexTextField, "Disaster Probability");
        if(!errors.isEmpty()){
            ValidationUtils.showError("Could not update political risk", errors.toString());
        }else {
            PoliticalRisk politicalRisk = new PoliticalRisk.PoliticalRiskBuilder().setId(selectedRisk.getId()).setDescription(description)
                    .setRiskLevel(riskLevel).setUnrestIndex(unrestIndex).setStabilityIndex(stabilityIndex).createPoliticalRisk();
            try {
                Optional<ButtonType> confirm = ValidationUtils.showConfirmation(UPDATE_CONFIRM_STRING,
                        "Are you sure you want to update this political risk?");
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    riskRepository.update(politicalRisk, loggedUser);
                    showSuccess(politicalRisk);
                }
            } catch (RepositoryAccessException e) {
                log.error("Error while updating political risk {}", e.getMessage(), e);
                ValidationUtils.showError(UPDATE_FAILED_STRING,
                        "An unexpected error occurred while updating political risk.\n Please try again.");
            }
        }
    }

    /**
     * Displays a success dialog after a risk is updated.
     * @param risk the risk that was saved
     */
    private void showSuccess(Risk risk){
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Success");
        success.setHeaderText("Risk updated");
        StringBuilder alertText = new StringBuilder();
        alertText.append("Description: ").append(risk.getDescription())
                .append("\nLevel: ").append(risk.getRiskLevel())
                .append("\nTotal risk score: ").append(risk.calculateRisk());
        success.setContentText(alertText.toString());
        success.showAndWait();
    }

    /**
     * Sets a reference to the parent controller in order to refresh the risk table
     * in the search controller after an update is made.
     * @param controller the controller that launched this screen.
     */
    public void setParentController(RiskSearchController controller){
        this.parentController = controller;
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
