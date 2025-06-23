package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller responsible for handling menu item actions and navigation.
 * The controller is tied to the menu bar.
 */
public class MenuController {
    @FXML
    private MenuItem userCreate;
    @FXML
    private MenuItem destinationCreate;
    @FXML
    private MenuItem riskCreate;

    private DashboardController dashboardController;

    /**
     * Hides all admin-only options.
     */
    public void hideAdminOptions(){
        userCreate.setVisible(false);
        destinationCreate.setVisible(false);
        riskCreate.setVisible(false);

    }

    /**
     * Sets the dashboard controller used to load different screens.
     * @param dashboardController the main dashboard controller
     */
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    /**
     * Sets the logged-in user.
     * @param user the user associated with this session
     */
    public void setUser(User user){
        if(user.role().equals("User")){
            hideAdminOptions();
        }
    }

    /**
     * Loads the employee search screen.
     */
    public void showEmployeeSearch() {
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/employee-search.fxml");
        }
    }

    /**
     * Loads the employee create screen.
     */
    public void showEmployeeCreate() {
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/employee-create.fxml");
        }
    }

    /**
     * Loads the destination search screen.
     */
    public void showDestinationSearch(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/destination-search.fxml");
        }
    }

    /**
     * Loads the destination create screen.
     */
    public void showDestinationCreate(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/destination-create.fxml");
        }
    }

    /**
     * Loads the risk search screen.
     */
    public void showRiskSearch(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/risk-search.fxml");
        }
    }

    /**
     * Loads the risk create screen.
     */
    public void showRiskCreate(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/risk-create.fxml");
        }
    }

    /**
     * Loads the trip search screen.
     */
    public void showTripSearch(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/trip-search.fxml");
        }
    }

    /**
     * Loads the trip create/plan screen.
     */
    public void showTripPlan(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/trip-create.fxml");
        }
    }

    /**
     * Loads the changelog screen.
     */
    public void showChangelog(){
        if(dashboardController != null){
            dashboardController.loadChangelog("/hr/java/RiskAssessmentTool/changelog.fxml");
        }
    }

    /**
     * Loads the risk assessment screen.
     */
    public void showRiskAssessment(){
        if(dashboardController != null){
            dashboardController.loadScreen("/hr/java/RiskAssessmentTool/risk-assessment.fxml");
        }
    }

    /**
     * Logs the current user out by returning to the login screen.
     */
    public void logout(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/login-view.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) dashboardController.getDashboardPane().getScene().getWindow();

            Scene loginScene = new Scene(loginRoot, 650, 750);
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            log.error("Logging out failed {}", e.getMessage(), e);
        }
    }

    /**
     * Exits the application
     */
    public void exit(){
        Platform.exit();
    }
}
