package hr.java.corporatetravelriskassessmenttool.threads;

import hr.java.corporatetravelriskassessmenttool.controller.TripSearchController;
import hr.java.corporatetravelriskassessmenttool.model.Person;
import hr.java.corporatetravelriskassessmenttool.model.Trip;
import hr.java.corporatetravelriskassessmenttool.repository.AbstractRepository;
import hr.java.corporatetravelriskassessmenttool.repository.TripRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FindHighestRiskTripThread implements Runnable {
    private AbstractRepository<Trip<Person>> tripRepository;
    private TableView<Trip<Person>> tripTableView;
    private TripSearchController tripSearchController;
    public FindHighestRiskTripThread(AbstractRepository<Trip<Person>> tripRepository, TableView<Trip<Person>> tripTableView
            , TripSearchController tripSearchController) {
        this.tripRepository = tripRepository;
        this.tripTableView = tripTableView;
        this.tripSearchController = tripSearchController;
    }

    @Override
    public void run() {
        if (tripRepository instanceof TripRepository<Trip<Person>> tr) {

            Trip<Person> selectedTrip = tripTableView.getSelectionModel().getSelectedItem();
            Long selectedTripId = selectedTrip != null ? selectedTrip.getId() : null;
            List<Trip<Person>> allTrips = tr.findAll();

            Optional<Trip<Person>> riskiestTrip = allTrips.stream()
                    .max(Comparator.comparingDouble(trip ->
                            trip.getDestinations().stream()
                                    .flatMap(destination -> destination.getRisks().stream())
                                    .mapToDouble(risk -> risk.calculateRisk().doubleValue())
                                    .sum()
                    ));

            Platform.runLater(() -> {
                tripTableView.setItems(FXCollections.observableArrayList(allTrips));

                if (selectedTripId != null) {
                    allTrips.stream()
                            .filter(trip -> trip.getId().equals(selectedTripId))
                            .findFirst()
                            .ifPresent(trip -> tripTableView.getSelectionModel().select(trip));
                }

                tripSearchController.setRiskiestTrip(riskiestTrip);
                tripTableView.refresh();
            });
        }
    }


}
