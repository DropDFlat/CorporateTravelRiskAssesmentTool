package hr.java.corporatetravelriskassessmenttool.model;

import java.util.Objects;
import java.util.Set;

public class Destination extends Entity{
    private String country;
    private String city;
    private Set<Risk> risks;

    public Destination(Builder builder) {
        super(builder.id);
        this.country = builder.country;
        this.city = builder.city;
        this.risks = builder.risks;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<Risk> getRisks() {
        return risks;
    }

    public void setRisks(Set<Risk> risks) {
        this.risks = risks;
    }

    public void addRisk(Risk risk) {
        this.risks.add(risk);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination destination = (Destination) o;
        return Objects.equals(getId(), destination.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return city + ", " + country;
    }

    public static class Builder {
        private Long id;
        private String country;
        private String city;
        private Set<Risk> risks;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setRisks(Set<Risk> risks) {
            this.risks = risks;
            return this;
        }

        public Destination createDestination() {
            return new Destination(this);
        }
    }
}
