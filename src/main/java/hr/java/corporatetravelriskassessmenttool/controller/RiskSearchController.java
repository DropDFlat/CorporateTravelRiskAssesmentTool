package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.RiskRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;
/**
 * Controller class responsible for managing and searching risks.
 * <p>
 *     This controller allows users to:
 *     <ul>
 *         <li>View a table of all risks</li>
 *         <li>Filter risks by type, description and severity</li>
 *         <li>Delete selected risk</li>
 *         <li>Open a window to update a selected risk by right-clicking it in the table</li>
 *     </ul>
 * </p>
 * Implements the {@link RoleAware} interface to track the currently logged-in user.
 */
public class RiskSearchController implements RoleAware{
    @FXML
    ComboBox<String> riskTypeComboBox;
    @FXML
    TextField descriptionTextField;
    @FXML
    ComboBox<RiskLevel> riskLevelComboBox;
    @FXML
    TableView<Risk> riskTableView;
    @FXML
    private TableColumn<Risk, String> idTableColumn;
    @FXML
    TableColumn<Risk, String> riskTypeTableColumn;
    @FXML
    TableColumn<Risk, String> descriptionTableColumn;
    @FXML
    TableColumn<Risk, String> riskSeverityTableColumn;
    @FXML
    TableColumn<Risk, String> totalRiskTableColumn;
    private AbstractRepository<Risk> riskRepository = new RiskRepository<>();
    private User loggedUser;
    private static final String HEALTH_TYPE = "Health";
    private static final String POLITICAL_TYPE = "Political";
    private static final String ENVIRONMENTAL_TYPE = "Environmental";

    /**
     * Initializes the controller, loads all risks from the repository
     * and sets up the table view and context menu.
     */
    public void initialize() {
        ObservableList<Risk> risks = FXCollections.observableArrayList();
        try {
            risks = FXCollections.observableArrayList(riskRepository.findAll());
        }catch(RepositoryAccessException e){
            log.error("Error fetching risks from DB {}", e.getMessage(), e);
            ValidationUtils.showError("Error fetching risks from DB", e.getMessage());

        }
        riskTableView.setItems(risks);
        riskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        riskTypeTableColumn.setCellValueFactory(cellData -> {
            Risk risk = cellData.getValue();
            String type;
            switch(risk) {
                case EnvironmentalRisk _ ->
                    type = ENVIRONMENTAL_TYPE;
                case HealthRisk _ ->
                    type = HEALTH_TYPE;
                case PoliticalRisk _ ->
                    type = POLITICAL_TYPE;
                default -> type = "Unknown";
            }
            return new SimpleStringProperty(type);
        });
        idTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        riskSeverityTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRiskLevel().toString()));
        totalRiskTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().calculateRisk())));
        List<String> types = new ArrayList<>();
        types.add(ENVIRONMENTAL_TYPE);
        types.add(HEALTH_TYPE);
        types.add(POLITICAL_TYPE);
        riskTypeComboBox.setItems(FXCollections.observableList(types));
        riskTypeComboBox.getSelectionModel().selectFirst();
        riskLevelComboBox.setItems(FXCollections.observableArrayList(RiskLevel.values()));
        riskLevelComboBox.getSelectionModel().selectFirst();
        riskTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setupContextMenu();
    }
    /**
     * Filters risks by entered search criteria.
     */
    public void filterRisks() {
        List<Risk> risks = riskRepository.findAll();
        String description = descriptionTextField.getText();
        if(!description.isEmpty()) {
            risks = risks.stream()
                    .filter(risk -> risk.getDescription().toLowerCase().contains(description.toLowerCase())).toList();
        }
        String type = riskTypeComboBox.getValue();
        switch(type){
            case ENVIRONMENTAL_TYPE:
                risks = risks.stream()
                        .filter(EnvironmentalRisk.class::isInstance)
                        .toList();
                break;
            case HEALTH_TYPE:
                risks = risks.stream()
                        .filter(HealthRisk.class::isInstance)
                        .toList();
                break;
            case POLITICAL_TYPE:
                risks = risks.stream()
                        .filter(PoliticalRisk.class::isInstance)
                        .toList();
                break;
            default:
        }
        String level = riskLevelComboBox.getSelectionModel().getSelectedItem().toString();
        if(!level.isEmpty()) {
            risks = risks.stream()
                    .filter(risk -> risk.getRiskLevel().toString().toLowerCase().contains(level.toLowerCase()))
                    .toList();
        }
        riskTableView.setItems(FXCollections.observableList(risks));
    }

    /**
     * Deletes selected risk from the database and table
     */
    public void deleteRisk(){
        Optional<Risk> risk = Optional.ofNullable(riskTableView.getSelectionModel().getSelectedItem());
        if(risk.isPresent()){
            Optional<ButtonType> confirm = ValidationUtils.showConfirmation("Delete risk",
                    "Are you sure you want to delete this risk?");
            if(confirm.isPresent() && confirm.get() == ButtonType.OK){
                try {
                    riskRepository.delete(risk.get().getId(), loggedUser);
                    riskTableView.getItems().remove(risk.get());
                }catch(RepositoryAccessException e){
                    log.error("Error while deleting risk {}", e.getMessage(), e);
                    ValidationUtils.showError("Error while deleting riks", e.getMessage());
                }
            }
        }else{
            ValidationUtils.showError("No risk selected", "Select a risk to delete");
        }
    }
    /**
     * Configures the right-click context menu for the table rows and enables access to the edit functionality.
     */
    private void setupContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");

        editItem.setOnAction(event -> {
            Risk selectedRisk = riskTableView.getSelectionModel().getSelectedItem();
            if(selectedRisk != null){
                openUpdateWindow(selectedRisk);
            }
        });
        contextMenu.getItems().add(editItem);
        riskTableView.setRowFactory(tableView -> {
            TableRow<Risk> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if(!row.isEmpty()){
                    riskTableView.getSelectionModel().select(row.getItem());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            row.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown() && !row.isEmpty()) {
                    riskTableView.getSelectionModel().select(row.getItem());
                }
            });
            return row;
        });
    }
    /**
     * Opens a new update window for updating the selected risk.
     * @param risk the risk selected to be updated
     */
    private void openUpdateWindow(Risk risk){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/risk-update.fxml"));
            Parent root = loader.load();

            RiskUpdateController controller = loader.getController();
            controller.setUser(loggedUser);
            controller.setRisk(risk);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Update Risk");
            stage.setScene(new Scene(root));
            stage.show();
        }catch(IOException e){
            log.error("Error while opening risk update window", e);
            ValidationUtils.showError("Opening update window failed!",
                    "An unexpected error occurred while opening update window.\n Please try again.");
        } catch (RepositoryAccessException e) {
            log.error("Error while updating risk {}", e.getMessage(), e);
            ValidationUtils.showError("Error while updating risk", e.getMessage());

        }
    }
    /**
     * Reloads the risk table after an update.
     */
    public void reloadRiskTable() {
        try{
            List<Risk> risks = riskRepository.findAll();
            riskTableView.setItems(FXCollections.observableArrayList(risks));
        }catch(RepositoryAccessException e){
            log.error("Failed to reload risks {}", e.getMessage(), e);
            ValidationUtils.showError("Failed to reload risks", e.getMessage());
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
