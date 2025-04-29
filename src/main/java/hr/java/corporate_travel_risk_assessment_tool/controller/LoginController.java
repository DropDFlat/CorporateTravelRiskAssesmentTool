package hr.java.corporate_travel_risk_assessment_tool.controller;

import hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporate_travel_risk_assessment_tool.model.User;
import hr.java.corporate_travel_risk_assessment_tool.utils.UserManagerUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;

    private static final String USER_FILE = "dat/users.txt";
    private Map<String, User> users = new HashMap<>();

    public void initialize() {
        users = UserManagerUtil.loadUserData();
    }
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage.toString());
        }else{
            if(authenticate(username, password)){
                User user = users.get(username);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Login successful");
                alert.setContentText("Welcome " + user.username());
                alert.show();
                FXMLLoader loader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/dashboard.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();
                controller.setUser(user);
                CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Corporate Travel Risk Assessment");
                CorporateTravelRiskAssessmentApplication.getMainStage().setScene(new Scene(root));
                CorporateTravelRiskAssessmentApplication.getMainStage().show();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Login failed");
                alert.setContentText("Invalid username or password");
                alert.showAndWait();
            }

        }

    }

    public Boolean authenticate(String username, String password) {
        if(!users.containsKey(username)){
            return false;
        }
        String hashedInput = UserManagerUtil.hashPassword(password);
        return hashedInput.equals(users.get(username).password());
    }

    public void showRegisterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/registration-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Registration Screen");
        CorporateTravelRiskAssessmentApplication.getMainStage().setScene(scene);
        CorporateTravelRiskAssessmentApplication.getMainStage().show();
    }
}
