package hr.java.corporatetravelriskassessmenttool.threads;

import hr.java.corporatetravelriskassessmenttool.controller.TripSearchController;
import hr.java.corporatetravelriskassessmenttool.model.Person;
import hr.java.corporatetravelriskassessmenttool.model.Trip;
import javafx.application.Platform;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Runnable task that finds the trip with the highest aggregated risk
 * from its destinations and updates the provided TableView and controller accordingly.
 * <p>
 * This thread gets all the trips from the table in the controller and calculates total risk score
 * for each trip by summing the risk values of all associated destinations,
 * and identifies the trip with the highest risk score.
 * It then updates the TableView with all trips, preserving the current selection if any,
 * and notifies the TripSearchController of the riskiest trip found.
 * </p>
 */
public class FindHighestRiskTripThread implements Runnable {
    private TableView<Trip<Person>> tripTableView;
    private TripSearchController tripSearchController;

    /**
     * Constructs a new FindHighestRiskTripThread with the specified TableView, and controller.
     * @param tripTableView the TableView to update with the trip data
     * @param tripSearchController the controller to notify about the riskiest trip
     */
    public FindHighestRiskTripThread(TableView<Trip<Person>> tripTableView
            , TripSearchController tripSearchController) {
        this.tripTableView = tripTableView;
        this.tripSearchController = tripSearchController;
    }
    /**
     * Executes the task to find the trip with the highest total risk value.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Calculates the aggregated risk score for each trip by summing risks from all destinations.</li>
     *   <li>Finds the trip with the highest risk score.</li>
     *   <li>Updates the TableView with all trips, preserving the previously selected trip if any.</li>
     *   <li>Notifies the TripSearchController with the riskiest trip.</li>
     * </ul>
     * </p>
     */
    @Override
    public void run() {

            List<Trip<Person>> trips = new ArrayList<>(tripTableView.getItems());

            Optional<Trip<Person>> riskiestTrip = trips.stream()
                    .max(Comparator.comparingDouble(trip ->
                            trip.getDestinations().stream()
                                    .flatMap(destination -> destination.getRisks().stream())
                                    .mapToDouble(risk -> risk.calculateRisk().doubleValue())
                                    .sum()
                    ));

            Platform.runLater(() -> {
                Trip<Person> selectedTrip = tripTableView.getSelectionModel().getSelectedItem();
                Long selectedTripId = selectedTrip != null ? selectedTrip.getId() : null;

                tripSearchController.updateTrips(trips);

                if (selectedTripId != null) {
                    trips.stream()
                            .filter(trip -> trip.getId().equals(selectedTripId))
                            .findFirst()
                            .ifPresent(trip -> tripTableView.getSelectionModel().select(trip));
                }

                tripSearchController.setRiskiestTrip(riskiestTrip);
                tripTableView.refresh();
            });

    }



}
