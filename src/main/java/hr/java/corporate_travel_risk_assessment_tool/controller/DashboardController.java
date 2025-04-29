package hr.java.corporate_travel_risk_assessment_tool.controller;

import hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporate_travel_risk_assessment_tool.model.RoleAware;
import hr.java.corporate_travel_risk_assessment_tool.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;

import static hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication.log;

public class DashboardController {

    @FXML
    private BorderPane dashboardPane;
    @FXML
    private Label welcomeLabel;

    private MenuController menuController;
    private User loggedUser;
    public void initialize() {

    }

    public void setUser(User user) {
        this.loggedUser = user;
        welcomeLabel.setText("Welcome " + user.username());
        try {
            FXMLLoader menuLoader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/menu.fxml"));
            Parent menu = menuLoader.load();
            dashboardPane.setTop(menu);
            MenuController menuController = menuLoader.getController();
            menuController.setUser(user);
            menuController.setDashboardController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadScreen(String fxmlPath){
        try{
            FXMLLoader loader = new FXMLLoader(new URL(fxmlPath));
            Parent root = loader.load();
            if(loader.getController() instanceof RoleAware){
                ((RoleAware)loader.getController()).setUser(loggedUser);
            }
            dashboardPane.setCenter(root);
        }catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
