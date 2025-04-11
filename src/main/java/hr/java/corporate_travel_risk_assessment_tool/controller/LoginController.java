package hr.java.corporate_travel_risk_assessment_tool.controller;

import hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporate_travel_risk_assessment_tool.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication.log;

public class LoginController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;

    private static final String USER_FILE = "dat/users.txt";
    private Map<String, User> users = new HashMap<>();

    public void initialize() {
        loadUserData();
    }
    public void login(){
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Login successful");
                alert.setContentText("Welcome " + username);
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Login failed");
                alert.setContentText("Invalid username or password");
            }

        }

    }

    public Boolean authenticate(String username, String password) {
        if(!users.containsKey(username)){
            return false;
        }
        String hashedInput = hashPassword(password);
        return hashedInput.equals(users.get(username).password());
    }
    private String hashPassword(String password) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            log.error("Error hashing password", e);
            throw new RuntimeException(e);
        }
    }
    private void loadUserData(){
        try(Stream<String> stream = Files.lines(Paths.get(USER_FILE))){
            stream.map(line -> line.split(":"))
                    .forEach(parts -> users.put(parts[0], new User(parts[0], parts[1], parts[2])));

        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
    public void showRegisterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registration-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Registration Screen");
        CorporateTravelRiskAssessmentApplication.getMainStage().setScene(scene);
        CorporateTravelRiskAssessmentApplication.getMainStage().show();
    }
}
