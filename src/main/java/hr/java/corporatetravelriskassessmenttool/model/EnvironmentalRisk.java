package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
/**
 * Represents an environmental risk that could impact a travel destination.
 * <p>
 * Environmental risks include natural disasters and environmental hazards,
 * characterized by a damage index and a probability of disaster.
 * </p>
 */
public final class EnvironmentalRisk extends Risk {

    private Integer damageIndex;
    private BigDecimal disasterProbability;
    /**
     * Constructs a new {@code EnvironmentalRisk} instance using the given builder.
     *
     * @param builder the builder containing risk attributes
     */
    public EnvironmentalRisk(EnvironmentalRiskBuilder builder) {
        super(builder.id, builder.description, builder.riskLevel);
        this.damageIndex = builder.damageIndex;
        this.disasterProbability = builder.disasterProbability;
    }
    /**
     * Returns the damage index, which quantifies the severity of potential damage.
     *
     * @return the damage index (0–100)
     */
    public Integer getDamageIndex() {
        return damageIndex;
    }
    /**
     * Sets the damage index for the risk.
     *
     * @param damageIndex an integer between 0 and 100 representing damage severity
     */
    public void setDamageIndex(Integer damageIndex) {
        this.damageIndex = damageIndex;
    }
    /**
     * Returns the probability of a disaster occurring.
     *
     * @return the probability as a {@code BigDecimal} between 0.0 and 1.0
     */
    public BigDecimal getDisasterProbability() {
        return disasterProbability;
    }
    /**
     * Sets the disaster probability.
     *
     * @param disasterProbability the likelihood of disaster as a decimal (e.g., 0.25 for 25%)
     */
    public void setDisasterProbability(BigDecimal disasterProbability) {
        this.disasterProbability = disasterProbability;
    }
    /**
     * Calculates the total environmental risk score based on:
     * <pre>
     * risk = (damageIndex / 100) * disasterProbability * riskLevel
     * </pre>
     *
     * @return the calculated risk score as a {@code BigDecimal}
     */
    @Override
    public BigDecimal calculateRisk(){
        return disasterProbability.multiply(BigDecimal.valueOf(damageIndex)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))
                .multiply(getRiskLevel().getLevel());
    }

    /**
     * Compares this risk to another object for equality.
     *
     * @param o the object to compare
     * @return {@code true} if the other object represents the same risk
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnvironmentalRisk that = (EnvironmentalRisk) o;
        return Objects.equals(damageIndex, that.damageIndex) && Objects.equals(disasterProbability, that.disasterProbability);
    }
    /**
     * @return a hash code based on damage index, disaster probability and parent class hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), damageIndex, disasterProbability);
    }

    /**
     * @return the risk's description and total risk score
     */
    @Override
    public String toString() {
        return "Type: " + "Environmental risk\n" + getDescription() + "\n Total risk score: " + calculateRisk();
    }

    /**
     * Builder for {@link EnvironmentalRisk}.
     */
    public static class EnvironmentalRiskBuilder {
        private Long id;
        private RiskLevel riskLevel;
        private String description;
        private Integer damageIndex;
        private BigDecimal disasterProbability;
        /**
         * Sets the ID of the risk.
         *
         * @param id the risk ID
         * @return the builder
         */
        public EnvironmentalRiskBuilder setId(Long id) {
            this.id = id;
            return this;
        }
        /**
         * Sets the risk level.
         *
         * @param riskLevel the risk level
         * @return the builder
         */
        public EnvironmentalRiskBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }
        /**
         * Sets the risk description.
         *
         * @param description the description of the risk
         * @return the builder
         */
        public EnvironmentalRiskBuilder setDescription(String description) {
            this.description = description;
            return this;
        }
        /**
         * Sets the damage index.
         *
         * @param damageIndex an integer representing the damage index (0–100)
         * @return the builder
         */
        public EnvironmentalRiskBuilder setDamageIndex(Integer damageIndex) {
            this.damageIndex = damageIndex;
            return this;
        }
        /**
         * Sets the disaster probability.
         *
         * @param disasterProbability the probability as a decimal (0.0–1.0)
         * @return the builder
         */
        public EnvironmentalRiskBuilder setDisasterProbability(BigDecimal disasterProbability) {
            this.disasterProbability = disasterProbability;
            return this;
        }
        /**
         * Builds the {@link EnvironmentalRisk} instance.
         *
         * @return a new {@code EnvironmentalRisk}
         */
        public EnvironmentalRisk createEnvironmentalRisk() {
            return new EnvironmentalRisk(this);
        }
    }
}

