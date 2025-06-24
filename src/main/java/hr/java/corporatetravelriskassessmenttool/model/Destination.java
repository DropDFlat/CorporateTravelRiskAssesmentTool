package hr.java.corporatetravelriskassessmenttool.model;

import java.util.Objects;
import java.util.Set;
/**
 * Represents a travel destination consisting of a city and country, associated with a set of risks.
 * <p>
 * Each destination is uniquely identified by its {@code id}, which it inherits from the {@link Entity} superclass.
 * </p>
 */
public class Destination extends Entity{
    private String country;
    private String city;
    private Set<Risk> risks;

    /**
     * Constructs a {@code Destination} from a {@link Builder}.
     *
     * @param builder the builder used to initialize the destination
     */
    public Destination(Builder builder) {
        super(builder.id);
        this.country = builder.country;
        this.city = builder.city;
        this.risks = builder.risks;
    }

    /**
     * @return the country of the destination
     */
    public String getCountry() {
        return country;
    }
    /**
     * Sets the country of the destination.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * @return the city of the destination
     */
    public String getCity() {
        return city;
    }
    /**
     * Sets the city of the destination.
     *
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * @return the set of risks associated with the destination
     */
    public Set<Risk> getRisks() {
        return risks;
    }
    /**
     * Sets the risks associated with the destination.
     *
     * @param risks a set of risks
     */
    public void setRisks(Set<Risk> risks) {
        this.risks = risks;
    }
    /**
     * Adds a single risk to the destination's risk set.
     *
     * @param risk the risk to add
     */
    public void addRisk(Risk risk) {
        this.risks.add(risk);
    }
    /**
     * Compares this destination with another based on their IDs.
     *
     * @param o the object to compare
     * @return {@code true} if the other object is a destination with the same ID, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination destination = (Destination) o;
        return Objects.equals(getId(), destination.getId());
    }
    /**
     * Computes hash code based on the destination's ID.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    /**
     * @return a string representation in the format "City, Country"
     */
    @Override
    public String toString() {
        return city + ", " + country;
    }

    /**
     * Builder class for constructing {@link Destination} instances.
     */
    public static class Builder {
        private Long id;
        private String country;
        private String city;
        private Set<Risk> risks;
        /**
         * Sets the ID for the destination.
         *
         * @param id the ID to set
         * @return the builder instance
         */
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        /**
         * Sets the country for the destination.
         *
         * @param country the country to set
         * @return the builder instance
         */
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        /**
         * Sets the city for the destination.
         *
         * @param city the city to set
         * @return the builder instance
         */
        public Builder setCity(String city) {
            this.city = city;
            return this;
        }
        /**
         * Sets the set of risks associated with the destination.
         *
         * @param risks a set of risks
         * @return the builder instance
         */
        public Builder setRisks(Set<Risk> risks) {
            this.risks = risks;
            return this;
        }

        /**
         * Creates a new {@link Destination} instance using the builder's fields.
         *
         * @return a new {@code Destination}
         */
        public Destination createDestination() {
            return new Destination(this);
        }
    }
}
