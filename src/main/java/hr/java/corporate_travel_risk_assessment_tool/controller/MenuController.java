package hr.java.corporate_travel_risk_assessment_tool.controller;

import hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporate_travel_risk_assessment_tool.model.RoleAware;
import hr.java.corporate_travel_risk_assessment_tool.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication.log;

public class MenuController {
    @FXML
    private MenuItem userSearch;
    @FXML
    private MenuItem userCreate;
    @FXML
    private MenuItem destinationCreate;
    @FXML
    private MenuItem riskCreate;
    @FXML
    private MenuItem tripPlan;

    private DashboardController dashboardController;
    private User loggedUser;
    public void hideAdminOptions(){
        userCreate.setVisible(false);
        destinationCreate.setVisible(false);
        riskCreate.setVisible(false);

    }
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    public void setUser(User user){
        this.loggedUser = user;
        if(user.role().equals("User")){
            hideAdminOptions();
        }
    }

    public void showUserSearch() {
        if(dashboardController != null){
            dashboardController.loadScreen("file:./src/main/resources/hr/java/RiskAssessmentTool/employee-search.fxml");
        }
    }

    public void showDestinationSearch(){
        if(dashboardController != null){
            dashboardController.loadScreen("file:./src/main/resources/hr/java/RiskAssessmentTool/destination-search.fxml");
        }
    }
}
