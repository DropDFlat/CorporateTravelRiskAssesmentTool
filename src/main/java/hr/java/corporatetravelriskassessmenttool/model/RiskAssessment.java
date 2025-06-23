package hr.java.corporatetravelriskassessmenttool.model;

import java.time.LocalDate;
import java.util.Objects;

public class RiskAssessment <T extends Person, R extends Risk> extends Entity implements Reportable{
    private T person;
    private R risk;
    private Trip<Person> trip;
    private LocalDate assessmentDate;

    public RiskAssessment(Builder<T, R> builder) {
        super(builder.id);
        this.person = builder.person;
        this.risk = builder.risk;
        this.assessmentDate = builder.assessmentDate;
        this.trip = builder.trip;
    }

    public T getPerson() {
        return person;
    }

    public void setPerson(T person) {
        this.person = person;
    }

    public R getRisk() {
        return risk;
    }

    public void setRisk(R risk) {
        this.risk = risk;
    }

    public Trip<Person> getTrip() {
        return trip;
    }

    public void setTrip(Trip<Person> trip) {
        this.trip = trip;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskAssessment<?, ?> that = (RiskAssessment<?, ?>) o;
        return Objects.equals(person, that.person) && Objects.equals(risk, that.risk) && Objects.equals(trip, that.trip) && Objects.equals(assessmentDate, that.assessmentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, risk, trip, assessmentDate);
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append(person.getName()).append(" is facing a ").append(risk).append(" with a risk score of ")
                .append(risk.calculateRisk()).append(". Assessed on ").append(assessmentDate);
        return report.toString();

    }
    public static class Builder<T extends Person, R extends Risk> {
        private Long id;
        private T person;
        private R risk;
        private LocalDate assessmentDate;
        private Trip<Person> trip;
        public RiskAssessment.Builder<T, R> setId(Long id) {
            this.id = id;
            return this;
        }

        public RiskAssessment.Builder<T, R> setPerson(T person) {
            this.person = person;
            return this;
        }

        public RiskAssessment.Builder<T, R> setRisk(R risk) {
            this.risk = risk;
            return this;
        }
        public RiskAssessment.Builder<T, R> setTrip(Trip<Person> trip) {
            this.trip = trip;
            return this;
        }
        public RiskAssessment.Builder<T, R> setAssessmentDate(LocalDate assessmentDate) {
            this.assessmentDate = assessmentDate;
            return this;
        }

        public RiskAssessment<T, R> build() {
            return new RiskAssessment<>(this);
        }
    }
}
