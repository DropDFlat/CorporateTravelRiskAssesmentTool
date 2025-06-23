package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.exception.MalformedUserFileException;
import hr.java.corporatetravelriskassessmenttool.exception.RegistrationException;
import hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.UserManagerUtil;
import hr.java.corporatetravelriskassessmenttool.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

/**
 * Controller responsible for handling user registration.
 * This class validates registration inputs and saves new users to the users file if successful.
 */
public class RegisterController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField passwordConfirmPasswordField;
    private String error = "Error";
    private static final String USER_FILE = "dat/users.txt";
    private Map<String, User> users = new HashMap<>();

    /**
     * Loads the user data from the users file during initialization.
     * Shows an error if the file is malformed.
     */
    public void initialize(){
        try {
            users = UserManagerUtil.loadUserData();
        }catch(MalformedUserFileException e) {
            log.error("Malformed user file", e);
            ValidationUtils.showError("Failed to load user data",e.getMessage());
        }
    }

    /**
     * Validates registration inputs and attempt to register the user.
     * Displays errors for missing input or failed validation.
     */
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
            alert.setTitle(error);
            alert.setHeaderText(null);
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        }else {
            try {
                saveUser(username, password, confirmPassword);
            } catch (RegistrationException e) {
                ValidationUtils.showError("User input validation failed",e.getMessage());
                log.error("User input validation failed {}", e.getMessage(), e);
            } catch (IOException e) {
                ValidationUtils.showError("I/O Error","Failed to save user.\nPlease try again.");
                log.error("Error while registering", e);
            }
        }

    }

    /**
     * Saves a new user to the users file after performing validation.
     * @param username the chosen username
     * @param password the chosen password
     * @param confirmPassword repeated password for confirmation
     * @throws IOException thrown if an I/O error occurs while writing to the file
     * @throws RegistrationException thrown if the input fails validation
     */
    public void saveUser(String username, String password, String confirmPassword) throws IOException, RegistrationException {
        StringBuilder errorString = new StringBuilder();
        if(users.containsKey(username)){
            errorString.append("Username already exists\n");
        }
        if(username.contains(":")){
            errorString.append("Username contains ':'\nUsername must not contain ':'");
        }
        if(!password.equals(confirmPassword)){
            errorString.append("Passwords do not match\n");
        }
        if(!errorString.isEmpty()){
            throw new RegistrationException(errorString.toString());
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

    /**
     * Loads the login view.
     * @throws IOException if the login view FXML cannot be loaded
     */
    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 750);
        CorporateTravelRiskAssessmentApplication.getMainStage().setTitle("Login Screen");
        CorporateTravelRiskAssessmentApplication.getMainStage().setScene(scene);
        CorporateTravelRiskAssessmentApplication.getMainStage().show();
    }
}
