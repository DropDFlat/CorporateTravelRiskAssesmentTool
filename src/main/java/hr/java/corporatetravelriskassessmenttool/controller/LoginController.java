package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.MalformedUserFileException;
import hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.UserManagerUtil;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.APP_CSS;
import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller responsible for handling user login functionality.
 * Validates input fields, authenticates users using stored credentials,
 * and navigates to dashboard on successful login.
 * Also provides navigation to the user registration screen.
 */
public class LoginController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;

    private Map<String, User> users = new HashMap<>();

    /**
     * Initializes the controller and loads user data from file.
     * Displays a error if user file is malformed.
     */
    public void initialize(){
        try {
            users = UserManagerUtil.loadUserData();
        }catch(MalformedUserFileException e) {
            ValidationUtils.showError("Failed to load user file",e.getMessage());
            log.error("Failed to load user file", e);
        }
    }

    /**
     * Handles the login action.
     * Validates that both fields are filled, authenticates the user against the stored user map.
     * On success loads the dashboard and passes the user to it.
     * On failure shows an error.
     * @throws IOException if FXML file loading fails
     */
    public void login() throws IOException {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();
        StringBuilder errorMessage = new StringBuilder();
        if(username.isEmpty()){
            errorMessage.append("Username is required\n");
        }
        if(password.isEmpty()){
            errorMessage.append("Password is required\n");
        }
        if(!errorMessage.isEmpty()){
            ValidationUtils.showError("Login failed", errorMessage.toString());
        }else{
            Boolean b = authenticate(username, password);
            if(Boolean.TRUE.equals(b)){
                User user = users.get(username);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Login successful");
                alert.setContentText("Welcome " + user.username());
                alert.show();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/dashboard.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();
                controller.setUser(user);
                Scene scene = new Scene(root, 650, 750);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(APP_CSS)).toExternalForm());
                CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Corporate Travel Risk Assessment");
                CorporateTravelRiskAssessmentApplication.getMainStage().setScene(scene);
                CorporateTravelRiskAssessmentApplication.getMainStage().show();
            }else{
                ValidationUtils.showError("Login failed", "Invalid username or password");
            }

        }

    }

    /**
     * Authenticates the user by hashing the password and comparing it with the stored hashed password.
     * @param username the entered username
     * @param password the entered password
     * @return {@code true} if credentials match, {@code false} otherwise
     */
    public Boolean authenticate(String username, String password) {
        if(!users.containsKey(username)){
            return false;
        }
        String hashedInput = UserManagerUtil.hashPassword(password);
        return hashedInput.equals(users.get(username).password());
    }

    /**
     * Loads the registration view.
     * @throws IOException if FXML file loading fails
     */
    public void showRegisterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/registration-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 750);
        CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Registration Screen");
        CorporateTravelRiskAssessmentApplication.getMainStage().setScene(scene);
        CorporateTravelRiskAssessmentApplication.getMainStage().show();
    }
}
