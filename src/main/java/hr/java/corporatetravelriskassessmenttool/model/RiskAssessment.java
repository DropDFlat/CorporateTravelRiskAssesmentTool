package hr.java.corporatetravelriskassessmenttool.model;

import java.time.LocalDate;
import java.util.Objects;
/**
 * Represents an assessment of a particular risk for a specific person during a trip.
 *
 * @param <T> the type of person (must extend Person)
 * @param <R> the type of risk (must extend Risk)
 */
public class RiskAssessment <T extends Person, R extends Risk> extends Entity implements Reportable{
    private T person;
    private R risk;
    private Trip<T> trip;
    private LocalDate assessmentDate;
    /**
     * Constructs a RiskAssessment instance using the builder.
     *
     * @param builder the builder containing all properties to set
     */
    public RiskAssessment(Builder<T, R> builder) {
        super(builder.id);
        this.person = builder.person;
        this.risk = builder.risk;
        this.assessmentDate = builder.assessmentDate;
        this.trip = builder.trip;
    }
    /**
     * Returns the person associated with this risk assessment.
     *
     * @return the person
     */
    public T getPerson() {
        return person;
    }
    /**
     * Sets the person for this risk assessment.
     *
     * @param person the person to set
     */
    public void setPerson(T person) {
        this.person = person;
    }
    /**
     * Returns the risk being assessed.
     *
     * @return the risk
     */
    public R getRisk() {
        return risk;
    }
    /**
     * Sets the risk being assessed.
     *
     * @param risk the risk to set
     */
    public void setRisk(R risk) {
        this.risk = risk;
    }
    /**
     * Returns the trip associated with this risk assessment.
     *
     * @return the trip
     */
    public Trip<T> getTrip() {
        return trip;
    }

    /**
     * Sets the trip associated with this risk assessment.
     *
     * @param trip the trip to set
     */
    public void setTrip(Trip<T> trip) {
        this.trip = trip;
    }
    /**
     * Returns the date when the risk assessment was conducted.
     *
     * @return the assessment date
     */
    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }
    /**
     * Sets the date when the risk assessment was conducted.
     *
     * @param assessmentDate the date to set
     */
    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }
    /**
     * Checks equality based on person, risk, trip, and assessment date.
     *
     * @param o the other object to compare
     * @return true if all fields are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskAssessment<?, ?> that = (RiskAssessment<?, ?>) o;
        return Objects.equals(person, that.person) && Objects.equals(risk, that.risk) && Objects.equals(trip, that.trip) && Objects.equals(assessmentDate, that.assessmentDate);
    }
    /**
     * Returns hash code based on person, risk, trip, and assessment date.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(person, risk, trip, assessmentDate);
    }

    /**
     * Generates a textual report summarizing the risk assessment.
     *
     * @return the generated report as a String
     */
    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append(person.getName()).append(" is facing a ").append(risk).append(" with a risk score of ")
                .append(risk.calculateRisk()).append(". Assessed on ").append(assessmentDate);
        return report.toString();

    }
    /**
     * Builder class for constructing instances of RiskAssessment.
     *
     * @param <T> the type of person (extends Person)
     * @param <R> the type of risk (extends Risk)
     */
    public static class Builder<T extends Person, R extends Risk> {
        private Long id;
        private T person;
        private R risk;
        private LocalDate assessmentDate;
        private Trip<T> trip;
        /**
         * Sets the ID for the risk assessment.
         * @param id the unique identifier
         * @return this builder
         */
        public RiskAssessment.Builder<T, R> setId(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the person.
         * @param person the person to be set
         * @return this builder
         */
        public RiskAssessment.Builder<T, R> setPerson(T person) {
            this.person = person;
            return this;
        }

        /**
         * Sets the risk.
         * @param risk the risk
         * @return this builder
         */
        public RiskAssessment.Builder<T, R> setRisk(R risk) {
            this.risk = risk;
            return this;
        }

        /**
         * Sets the trip
         * @param trip the trip
         * @return this builder
         */
        public RiskAssessment.Builder<T, R> setTrip(Trip<T> trip) {
            this.trip = trip;
            return this;
        }

        /**
         * Sets the assessment date.
         * @param assessmentDate the date
         * @return this builder
         */
        public RiskAssessment.Builder<T, R> setAssessmentDate(LocalDate assessmentDate) {
            this.assessmentDate = assessmentDate;
            return this;
        }

        /**
         * Creates a new {@link RiskAssessment} instance using the current builder state.
         *
         * @return a new RiskAssessment object
         */
        public RiskAssessment<T, R> build() {
            return new RiskAssessment<>(this);
        }
    }
}
