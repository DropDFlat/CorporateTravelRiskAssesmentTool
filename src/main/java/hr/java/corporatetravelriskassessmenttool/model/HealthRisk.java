package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.Objects;
/**
 * Represents a health risk that could impact a travel destination.
 * <p>
 * Health risks include pandemics and other disease outbreaks in the destination,
 * characterized by severity.
 * </p>
 */
public final class HealthRisk extends Risk {
    private BigDecimal severity;
    /**
     * Constructs a HealthRisk from the provided builder.
     *
     * @param builder the builder containing all necessary fields
     */
    public HealthRisk(HealthRiskBuilder builder) {
        super(builder.id, builder.description, builder.riskLevel);
        this.severity = builder.severity;
    }
    /**
     * Returns the severity of the health risk.
     *
     * @return the severity value
     */
    public BigDecimal getSeverity() {
        return severity;
    }
    /**
     * Sets the severity of the health risk.
     *
     * @param severity the severity to set
     */
    public void setSeverity(BigDecimal severity) {
        this.severity = severity;
    }
    /**
     * Calculates the risk score for this health risk.
     * <p>
     * The formula used is:
     * <pre>
     *     risk = severity × riskLevel
     * </pre>
     *
     * @return the computed risk score
     */
    @Override
    public BigDecimal calculateRisk(){
        return severity.multiply(getRiskLevel().getLevel());
    }
    /**
     * Checks whether this health risk is equal to another object.
     *
     * @param o the object to compare
     * @return true if the other object is a HealthRisk with the same severity and inherited properties
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HealthRisk that = (HealthRisk) o;
        return Objects.equals(severity, that.severity);
    }
    /**
     * Returns the hash code for this health risk.
     *
     * @return a hash code based on severity and parent class
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), severity);
    }
    /**
     * Returns a string representation of the health risk.
     *
     * @return a formatted string with risk type, description, and total score
     */
    @Override
    public String toString() {
        return "Type: " + "Health risk\n" + getDescription() + "\n Total risk score: " + calculateRisk();
    }
    /**
     * Builder class for creating instances of {@link HealthRisk}.
     */
    public static class HealthRiskBuilder{
        private RiskLevel riskLevel;
        private BigDecimal severity;
        private String description;
        private Long id;
        /**
         * Sets the ID of the risk.
         *
         * @param id the unique identifier
         * @return the builder instance
         */
        public HealthRiskBuilder setId(Long id) {
            this.id = id;
            return this;
        }
        /**
         * Sets the level of the risk.
         *
         * @param riskLevel the level (e.g., LOW, MEDIUM, HIGH)
         * @return the builder instance
         */
        public HealthRiskBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }
        /**
         * Sets the severity of the health risk.
         *
         * @param severity the severity as a {@link BigDecimal}
         * @return the builder instance
         */
        public HealthRiskBuilder setSeverity(BigDecimal severity) {
            this.severity = severity;
            return this;
        }

        /**
         * Sets the description of the health risk.
         *
         * @param description textual description of the risk
         * @return the builder instance
         */
        public HealthRiskBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Creates a new {@link HealthRisk} instance with the configured values.
         *
         * @return a constructed HealthRisk
         */
        public HealthRisk createHealthRisk(){
            return new HealthRisk(this);
        }
    }
}
