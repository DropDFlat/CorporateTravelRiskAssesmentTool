package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
/**
 * Represents a political risk with associated stability and unrest indices.
 * The risk is calculated based on these indices and a risk level.
 */
public final class PoliticalRisk extends Risk {
    private Integer unrestIndex;
    private Integer stabilityIndex;
    /**
     * Constructs a PoliticalRisk instance from the specified builder.
     *
     * @param builder the builder containing values to set
     */
    public PoliticalRisk(PoliticalRiskBuilder builder) {
        super(builder.id, builder.description, builder.riskLevel);
        this.unrestIndex = builder.unrestIndex;
        this.stabilityIndex = builder.stabilityIndex;
    }

    /**
     * Returns the unrest index representing the level of unrest.
     *
     * @return the unrest index
     */
    public Integer getUnrestIndex() {
        return unrestIndex;
    }
    /**
     * Sets the unrest index.
     *
     * @param unrestIndex the unrest index to set
     */
    public void setUnrestIndex(Integer unrestIndex) {
        this.unrestIndex = unrestIndex;
    }
    /**
     * Returns the stability index representing the political stability level.
     *
     * @return the stability index
     */
    public Integer getStabilityIndex() {
        return stabilityIndex;
    }
    /**
     * Sets the stability index.
     *
     * @param stabilityIndex the stability index to set
     */
    public void setStabilityIndex(Integer stabilityIndex) {
        this.stabilityIndex = stabilityIndex;
    }
    /**
     * Checks whether this political risk is equal to another object.
     *
     * @param o the object to compare
     * @return true if the other object is a PoliticalRisk with the same stability index, unrest index and inherited properties
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PoliticalRisk that = (PoliticalRisk) o;
        return Objects.equals(unrestIndex, that.unrestIndex) && Objects.equals(stabilityIndex, that.stabilityIndex);
    }
    /**
     * Returns the hash code for this political risk.
     *
     * @return a hash code based on unrest index, stability index, and parent class
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unrestIndex, stabilityIndex);
    }
    /**
     * Computes the political risk score based on unrest and stability indices,
     * adjusted by the risk level.
     * <p>
     * The formula is (unrestIndex - stabilityIndex) / riskLevel.
     * If the computed risk is negative, returns zero.
     *
     * @return the calculated risk as a {@link BigDecimal}
     */
    @Override
    public BigDecimal calculateRisk() {
        BigDecimal totalScore = BigDecimal.valueOf((long) unrestIndex - stabilityIndex).divide(getRiskLevel().getLevel(), RoundingMode.FLOOR);
        return totalScore.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalScore;
    }
    /**
     * Returns a string representation of the political risk.
     *
     * @return string describing the type, description, and calculated risk score
     */
    @Override
    public String toString() {
        return "Type: " + "Political risk\n" + getDescription() + "\n Total risk score: " + calculateRisk();
    }
    /**
     * Builder class to construct instances of {@link PoliticalRisk}.
     */
    public static class PoliticalRiskBuilder {
        private Long id;
        private RiskLevel riskLevel;
        private String description;
        private Integer stabilityIndex;
        private Integer unrestIndex;
        /**
         * Sets the ID for the political risk.
         *
         * @param id the unique identifier
         * @return this builder instance
         */
        public PoliticalRiskBuilder setId(Long id) {
            this.id = id;
            return this;
        }
        /**
         * Sets the risk level.
         *
         * @param riskLevel the risk level enum value
         * @return this builder instance
         */
        public PoliticalRiskBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }
        /**
         * Sets the description of the political risk.
         *
         * @param description the textual description
         * @return this builder instance
         */
        public PoliticalRiskBuilder setDescription(String description) {
            this.description = description;
            return this;
        }
        /**
         * Sets the stability index.
         *
         * @param stabilityIndex the political stability index
         * @return this builder instance
         */
        public PoliticalRiskBuilder setStabilityIndex(Integer stabilityIndex) {
            this.stabilityIndex = stabilityIndex;
            return this;
        }

        /**
         * Sets the unrest index.
         *
         * @param unrestIndex the political unrest index
         * @return this builder instance
         */
        public PoliticalRiskBuilder setUnrestIndex(Integer unrestIndex) {
            this.unrestIndex = unrestIndex;
            return this;
        }
        /**
         * Creates a new {@link PoliticalRisk} instance using the current builder state.
         *
         * @return a new PoliticalRisk object
         */
        public PoliticalRisk createPoliticalRisk() {
            return new PoliticalRisk(this);
        }
    }
}
