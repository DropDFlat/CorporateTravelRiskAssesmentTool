package hr.java.corporate_travel_risk_assessment_tool.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class CorporateTravelRiskAssessmentApplication extends Application {
    public static final Logger log = LoggerFactory.getLogger(CorporateTravelRiskAssessmentApplication.class);
    private static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainStage() {return mainStage;}

    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        getMainStage().setTitle("Login Screen");
        getMainStage().setScene(scene);
        getMainStage().show();
    }

    public void showRegistrationView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(new URL("file:./src/main/resources/hr/java/RiskAssessmentTool/registration-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        getMainStage().setTitle("Registration Screen");
        getMainStage().setScene(scene);
        getMainStage().show();
    }
}