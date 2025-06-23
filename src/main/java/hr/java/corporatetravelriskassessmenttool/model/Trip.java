package hr.java.corporatetravelriskassessmenttool.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Trip<T extends Person> extends Entity implements Warnable{
    private Set<T> employees;
    private Set<Destination> destinations;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean hasWarning;
    private String warningMessage;

    public Trip(TripBuilder<T> builder) {
        super(builder.id);
        this.employees = builder.employees;
        this.destinations = builder.destinations;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<T> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<T> employees) {
        this.employees = employees;
    }

    public Set<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<Destination> destinations) {
        this.destinations = destinations;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public void addEmployee(T employee) {
        this.employees.add(employee);
    }
    public void addDestination(Destination destination) {
        this.destinations.add(destination);
    }
    public BigDecimal totalRiskScore(){
        BigDecimal totalRiskScore = BigDecimal.ZERO;
        Set<Risk> risks = new HashSet<>();
        destinations.forEach(destination -> risks.addAll(destination.getRisks()));
        risks.forEach(risk -> totalRiskScore.add(risk.calculateRisk()));
        return totalRiskScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip<T> trip = (Trip<T>) o;
        return Objects.equals(employees, trip.employees) && Objects.equals(destinations, trip.destinations) &&
                Objects.equals(name, trip.name) && Objects.equals(startDate, trip.startDate) && Objects.equals(endDate, trip.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employees, destinations, name, startDate, endDate);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean hasWarning() {
        return hasWarning;
    }

    @Override
    public String getWarningMessage() {
        return warningMessage;
    }

    @Override
    public void setWarningMessage(String warningMessage) {
        this.hasWarning = true;
        this.warningMessage = warningMessage;
    }
    @Override
    public void noWarning(){
        this.hasWarning = false;
        this.warningMessage = "";
    }

    public static class TripBuilder<T extends Person> {
        private Long id;
        private Set<T> employees;
        private Set<Destination> destinations;
        private LocalDate startDate;
        private LocalDate endDate;
        private String name;
        public TripBuilder<T> setId(Long id) {
            this.id = id;
            return this;
        }
        public TripBuilder<T> setEmployees(Set<T> employees) {
            this.employees = employees;
            return this;

        }
        public TripBuilder<T> setName(String name){
            this.name = name;
            return this;
        }
        public TripBuilder<T> setDestinations(Set<Destination> destinations) {
            this.destinations = destinations;
            return this;
        }
        public TripBuilder<T> setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
        public TripBuilder<T> setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }
        public Trip<T> build() {
            return new Trip<>(this);
        }
    }
}
