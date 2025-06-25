package hr.java.corporatetravelriskassessmenttool.threads;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogRepository;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Runnable task that finds the latest change logged in the changelog
 * and displays it as a title of the app.
 */
public class FindLatestChangeThread implements Runnable {
    private final Stage mainStage;
    private final ChangelogRepository changelogRepository;

    /**
     * Constructs a new FindLatestChangeThread with the MainStage and ChangeLogRepository.
     * @param mainStage the stage where the title will be set
     * @param changelogRepository the repository class where the changes are read and written
     */
    public FindLatestChangeThread(Stage mainStage, ChangelogRepository changelogRepository) {
        this.mainStage = mainStage;
        this.changelogRepository = changelogRepository;
    }

    /**
     * Executes the logic to fetch the latest changelog entry and set it as the window title.
     * <p>
     * If a changelog entry exists, the window title is updated with the username,
     * action, and message from the entry.
     * </p>
     */
    @Override
    public void run() {
        Optional<ChangelogEntry> latestChange = changelogRepository.readLastEntry();
        if (latestChange.isPresent()) {
            String title = "Corporate Travel Risk Assessment Tool - Latest Change: " + latestChange.get().getUsername()
                    + " " + latestChange.get().getAction() + " " + latestChange.get().getMessage();
            Platform.runLater(() -> mainStage.setTitle(title));
        }
    }
}
