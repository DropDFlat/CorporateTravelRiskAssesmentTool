package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.Objects;
/**
 * Represents a general risk with a description and risk level.
 * This class is non-sealed and can be extended by specific risk types.
 */
public non-sealed class Risk extends Entity implements RiskCalculator {
    private String description;
    private RiskLevel riskLevel;
    /**
     * Constructs a Risk instance.
     *
     * @param id          the unique identifier of the risk
     * @param description textual description of the risk
     * @param riskLevel   the severity level of the risk
     */
    public Risk(Long id, String description, RiskLevel riskLevel) {
        super(id);
        this.description = description;
        this.riskLevel = riskLevel;
    }
    /**
     * Returns the description of the risk.
     *
     * @return the risk description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description of the risk.
     *
     * @param description the new risk description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Returns the risk level.
     *
     * @return the risk level
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * Sets the risk level.
     *
     * @param riskLevel the new risk level
     */
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    /**
     * Calculates the risk score.
     * This implementation returns the numeric value of the risk level.
     * Subclasses may override for more specific calculations.
     *
     * @return the calculated risk as BigDecimal
     */
    public BigDecimal calculateRisk() {
        return riskLevel.getLevel();
    }
    /**
     * Checks equality based on description and risk level.
     *
     * @param o the other object to compare
     * @return true if both risks have the same description and risk level, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Risk risk = (Risk) o;
        return Objects.equals(description, risk.description) && riskLevel == risk.riskLevel;
    }
    /**
     * Returns the hash code based on description and risk level.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(description, riskLevel);
    }
}
