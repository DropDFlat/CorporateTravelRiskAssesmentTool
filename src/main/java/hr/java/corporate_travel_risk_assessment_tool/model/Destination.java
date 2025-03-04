package hr.java.corporate_travel_risk_assessment_tool.model;

import java.util.List;

public class Destination extends Entity{
    private String country;
    private String city;
    private List<TravelRisk> risks;

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

    public List<TravelRisk> getRisks() {
        return risks;
    }

    public void setRisks(List<TravelRisk> risks) {
        this.risks = risks;
    }

    public void addRisk(TravelRisk risk) {
        this.risks.add(risk);
    }

    public static class Builder {
        private Long id;
        private String country;
        private String city;
        private List<TravelRisk> risks;

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

        public Builder setRisks(List<TravelRisk> risks) {
            this.risks = risks;
            return this;
        }

        public Destination createDestination() {
            return new Destination(this);
        }
    }
}
