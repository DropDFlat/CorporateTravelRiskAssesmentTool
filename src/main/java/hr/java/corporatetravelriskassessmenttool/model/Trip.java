package hr.java.corporatetravelriskassessmenttool.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
/**
 * Represents a business trip involving employees traveling to one or more destinations.
 *
 * @param <T> the type of Person involved in the trip, typically Employee or subclasses
 */
public class Trip<T extends Person> extends Entity implements Warnable{
    private Set<T> employees;
    private Set<Destination> destinations;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean hasWarning;
    private String warningMessage;
    /**
     * Constructs a Trip using the provided builder.
     *
     * @param builder the TripBuilder containing all the properties to set
     */
    public Trip(TripBuilder<T> builder) {
        super(builder.id);
        this.employees = builder.employees;
        this.destinations = builder.destinations;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.name = builder.name;
        this.warningMessage = "";
    }
    /**
     * Gets the name of the trip.
     *
     * @return the trip name
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the trip.
     *
     * @param name the trip name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the set of employees participating in this trip.
     *
     * @return the set of employees
     */
    public Set<T> getEmployees() {
        return employees;
    }

    /**
     * Sets the set of employees participating in this trip.
     *
     * @param employees the set of employees
     */
    public void setEmployees(Set<T> employees) {
        this.employees = employees;
    }
    /**
     * Gets the set of destinations for this trip.
     *
     * @return the set of destinations
     */
    public Set<Destination> getDestinations() {
        return destinations;
    }
    /**
     * Sets the set of destinations for this trip.
     *
     * @param destinations the set of destinations
     */
    public void setDestinations(Set<Destination> destinations) {
        this.destinations = destinations;
    }
    /**
     * Gets the trip's start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    /**
     * Sets the trip's start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    /**
     * Gets the trip's end date.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
    /**
     * Sets the trip's end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    /**
     * Adds a single employee to the trip.
     *
     * @param employee the employee to add
     */
    public void addEmployee(T employee) {
        this.employees.add(employee);
    }
    /**
     * Adds a single destination to the trip.
     *
     * @param destination the destination to add
     */
    public void addDestination(Destination destination) {
        this.destinations.add(destination);
    }
    /**
     * Checks if this trip is equal to another object.
     * Two trips are equal if their employees, destinations,
     * name, start date, and end date are equal.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip<T> trip = (Trip<T>) o;
        return Objects.equals(employees, trip.employees) && Objects.equals(destinations, trip.destinations) &&
                Objects.equals(name, trip.name) && Objects.equals(startDate, trip.startDate) && Objects.equals(endDate, trip.endDate);
    }
    /**
     * Computes the hash code for this trip based on employees,
     * destinations, name, start date, and end date.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(employees, destinations, name, startDate, endDate);
    }
    /**
     * Returns the string representation of the trip,
     * which is the trip's name.
     *
     * @return the name of the trip
     */
    @Override
    public String toString() {
        return this.name;
    }
    /**
     * Returns whether the trip currently has an active warning.
     *
     * @return true if there is a warning, false otherwise
     */
    @Override
    public boolean hasWarning() {
        return hasWarning;
    }
    /**
     * Gets the current warning message for the trip.
     *
     * @return the warning message if set, empty string otherwise
     */
    @Override
    public String getWarningMessage() {
        return warningMessage;
    }
    /**
     * Sets the warning message for the trip and marks
     * that the trip has a warning.
     *
     * @param warningMessage the warning message to set
     */
    @Override
    public void setWarningMessage(String warningMessage) {
        this.hasWarning = true;
        this.warningMessage = warningMessage;
    }
    /**
     * Clears the warning message and marks the trip as having no warnings.
     */
    @Override
    public void noWarning(){
        this.hasWarning = false;
        this.warningMessage = "";
    }
    /**
     * Builder class for constructing instances of Trip.
     *
     * @param <T> the type of person (extends Person)
     */
    public static class TripBuilder<T extends Person> {
        private Long id;
        private Set<T> employees;
        private Set<Destination> destinations;
        private LocalDate startDate;
        private LocalDate endDate;
        private String name;
        /**
         * Sets the ID of the trip.
         *
         * @param id the trip ID
         * @return the builder instance
         */
        public TripBuilder<T> setId(Long id) {
            this.id = id;
            return this;
        }
        /**
         * Sets the employees participating in the trip.
         *
         * @param employees the set of employees
         * @return the builder instance
         */
        public TripBuilder<T> setEmployees(Set<T> employees) {
            this.employees = employees;
            return this;

        }
        /**
         * Sets the name of the trip.
         *
         * @param name the trip name
         * @return the builder instance
         */
        public TripBuilder<T> setName(String name){
            this.name = name;
            return this;
        }

        /**
         * Sets the destinations of the trip.
         *
         * @param destinations the set of destinations
         * @return the builder instance
         */
        public TripBuilder<T> setDestinations(Set<Destination> destinations) {
            this.destinations = destinations;
            return this;
        }

        /**
         * Sets the start date of the trip.
         *
         * @param startDate the trip start date
         * @return the builder instance for chaining
         */
        public TripBuilder<T> setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }
        /**
         * Sets the end date of the trip.
         *
         * @param endDate the trip end date
         * @return the builder instance
         */
        public TripBuilder<T> setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }
        /**
         * Builds and returns a new Trip instance with the
         * configured parameters.
         *
         * @return a new Trip object
         */
        public Trip<T> build() {
            return new Trip<>(this);
        }
    }
}
