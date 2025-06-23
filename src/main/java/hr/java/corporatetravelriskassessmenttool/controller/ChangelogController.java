package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller class responsible for displaying changelog entries.
 * <p>
 *     This controller:
 *     <ul>
 *         <li>Loads changelog entries from the {@link ChangelogRepository}</li>
 *         <li>Binds {@link ChangelogEntry} data to table columns</li>
 *     </ul>
 * </p>
 *
 * @see ChangelogEntry
 * @see ChangelogRepository
 */
public class ChangelogController {
    @FXML
    private TableView<ChangelogEntry> changelogTableView;
    @FXML
    private TableColumn<ChangelogEntry, String> actionTableColumn;
    @FXML
    private TableColumn<ChangelogEntry, String> roleTableColumn;
    @FXML
    private TableColumn<ChangelogEntry, String> messageTableColumn;
    @FXML
    private TableColumn<ChangelogEntry, String> dateTimeTableColumn;

    /**
     * Initializes the changelog table and binds specific properties of {@link ChangelogEntry} to table columns.
     */
    public void initialize() {
        List<ChangelogEntry> changelogEntries = new ChangelogRepository().readAll();
        changelogTableView.getItems().setAll(changelogEntries);
        actionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAction()));
        roleTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        messageTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateTimeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp()
                .format(format)));

        actionTableColumn.setPrefWidth(100);
        roleTableColumn.setPrefWidth(100);
        messageTableColumn.setPrefWidth(300);
        dateTimeTableColumn.setPrefWidth(150);
        changelogTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
}
