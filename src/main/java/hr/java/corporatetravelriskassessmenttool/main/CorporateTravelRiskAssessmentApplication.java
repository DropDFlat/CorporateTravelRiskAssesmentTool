package hr.java.corporatetravelriskassessmenttool.main;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogRepository;
import hr.java.corporatetravelriskassessmenttool.threads.FindLatestChangeThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
/**
 * Main class of the Corporate Travel Risk Assessment application.
 * <p>
 * It is responsible for launching the app, loading initial views,
 * and providing utility methods to switch between key application views
 * such as login and registration.
 * </p>
 * <p>
 * Also provides global access to the main {@link Stage}
 * </p>
 */
public class CorporateTravelRiskAssessmentApplication extends Application {
    public static final Logger log = LoggerFactory.getLogger(CorporateTravelRiskAssessmentApplication.class);
    public static final ChangelogRepository changelogRepository = new ChangelogRepository();
    public static final String APP_CSS = "/styles/app.css";
    private static Stage mainStage;
    /**
     * Starts the JavaFX application.
     * Loads the initial FXML view and sets up the main stage.
     * Also starts the FindLatestChangeThread, which sets the title
     * as the latest change found in the changelog file.
     * @param stage the primary stage for this application
     * @throws IOException if loading the FXML view fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        setStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(APP_CSS)).toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Timeline latestChangeTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    Thread runner = new Thread((new FindLatestChangeThread(stage, changelogRepository)));
                    runner.setDaemon(true);
                    runner.start();
                }),
                new KeyFrame(Duration.seconds(5))
        );
        latestChangeTimeline.setCycleCount(Animation.INDEFINITE);
        latestChangeTimeline.play();
    }
    /**
     * Main method that launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }
    /**
     * Returns the primary stage of the application.
     *
     * @return the main {@link Stage}
     */
    public static Stage getMainStage() {return mainStage;}
    /**
     * Sets the primary stage of the application.
     *
     * @param stage the main {@link Stage} to set
     */
    public static void setStage(Stage stage) {mainStage = stage;}

    /**
     * Switches the main stage to show the login view.
     *
     * @throws IOException if the FXML view cannot be loaded
     */
    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(APP_CSS)).toExternalForm());
        getMainStage().setTitle("Login Screen");
        getMainStage().setScene(scene);
        getMainStage().show();
    }

    /**
     * Switches the main stage to show the registration view.
     *
     * @throws IOException if the FXML view cannot be loaded
     */
    public void showRegistrationView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/java/RiskAssessmentTool/registration-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(APP_CSS)).toExternalForm());
        getMainStage().setTitle("Registration Screen");
        getMainStage().setScene(scene);
        getMainStage().show();
    }
}