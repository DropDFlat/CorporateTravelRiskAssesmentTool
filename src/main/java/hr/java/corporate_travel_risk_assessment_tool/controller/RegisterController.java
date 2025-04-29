package hr.java.corporate_travel_risk_assessment_tool.controller;

import hr.java.corporate_travel_risk_assessment_tool.exception.RegistrationException;
import hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporate_travel_risk_assessment_tool.model.User;
import hr.java.corporate_travel_risk_assessment_tool.utils.UserManagerUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication.log;

public class RegisterController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField passwordConfirmPasswordField;

    private static final String USER_FILE = "dat/users.txt";
    private Map<String, User> users = new HashMap<>();

    public void initialize() {
        users = UserManagerUtil.loadUserData();
    }

    public void register(){
        StringBuilder errorMessage = new StringBuilder();
        String username = usernameTextField.getText();
        if(username.isEmpty()){
            errorMessage.append("Username is required\n");
        }
        String password = passwordPasswordField.getText();
        if(password.isEmpty()){
            errorMessage.append("Password is required\n");
        }
        String confirmPassword = passwordConfirmPasswordField.getText();
        if(confirmPassword.isEmpty()){
            errorMessage.append("Repeat password input\n");
        }
        if(!errorMessage.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        }else {
            try {
                saveUser(username, password, confirmPassword);
            } catch (RegistrationException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    public void saveUser(String username, String password, String confirmPassword) throws IOException {
        if(users.containsKey(username)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Duplicate Username");
            alert.setContentText("Username already taken");
            throw new RegistrationException("Duplicate username found");
        }
        if(!password.equals(confirmPassword)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Passwords must match");
            alert.showAndWait();
            throw new RegistrationException("Passwords do not match");
        }
        String hashedPassword = UserManagerUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword, "User");
        users.put(username, newUser);
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(USER_FILE))){
            for(User user : users.values()) {
                writer.append(user.username()).append(":").append(user.password()).append(":").append(user.role()).append("\n");
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("User registered successfully");
        alert.show();
    }

    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Login Screen");
        CorporateTravelRiskAssessmentApplication.getMainStage().setScene(scene);
        CorporateTravelRiskAssessmentApplication.getMainStage().show();
    }
}
