package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller class for managing the main Dashboard layout.
 * <p>
 *     Responsible for:
 *     <ul>
 *         <li>Loading and embedding the menu bar</li>
 *         <li>Loading content screens into the center of the Dashboard BorderPane.</li>
 *     </ul>
 * </p>
 */
public class DashboardController {

    @FXML
    private BorderPane dashboardPane;
    @FXML
    private Label welcomeLabel;

    private User loggedUser;

    /**
     * Returns the root dashboard BorderPane.
     * @return The {@link BorderPane} used in the dashboard.
     */
    public BorderPane getDashboardPane() {
        return dashboardPane;
    }

    /**
     * Sets the logged-in user and loads the menu bar.
     * @param user the {@link User} object representing the logged-in user.
     */
    public void setUser(User user) {
        this.loggedUser = user;
        welcomeLabel.setText("Welcome " + user.username());
        try {
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/menu.fxml"));
            Parent menu = menuLoader.load();
            dashboardPane.setTop(menu);
            MenuController menuController = menuLoader.getController();
            menuController.setUser(user);
            menuController.setDashboardController(this);

        } catch (IOException e) {
            log.error("Failed to load menu.fxml", e);
            ValidationUtils.showError("Failed to load menu bar", e.getMessage());
        }
    }

    /**
     * Loads a screen into the center of the dashboard borderpane.
     * The loaded controller must implement {@link RoleAware} so the user information can be passed on.
     * @param fxmlPath the path to the FXML file.
     */
    public void loadScreen(String fxmlPath){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            ((RoleAware)loader.getController()).setUser(loggedUser);

            dashboardPane.setCenter(root);
        }catch (IOException e) {
            log.error("Failed to load screen " + fxmlPath, e);
            ValidationUtils.showError("Failed to load screen " +fxmlPath, e.getMessage());
        }
    }

    /**
     * Loads the changelog view into the center of the dashboard borderpane.
     * The changelog view does not require user data.
     * @param fxmlPath the path to the changelog-view FXML file.
     */
    public void loadChangelog(String fxmlPath){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            dashboardPane.setCenter(root);
        }catch(IOException e) {
            log.error("Failed to load changelog " + fxmlPath, e);
            ValidationUtils.showError("Failed to load changelog " +fxmlPath, e.getMessage());

        }
    }
}
