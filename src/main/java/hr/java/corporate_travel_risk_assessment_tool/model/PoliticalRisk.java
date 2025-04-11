package hr.java.corporate_travel_risk_assessment_tool.model;

import hr.java.corporate_travel_risk_assessment_tool.enums.RiskLevel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PoliticalRisk implements TravelRisk{
    private RiskLevel riskLevel;
    private String description;
    private Long unrestIndex;
    private Long stabilityIndex;

    public PoliticalRisk(RiskLevel riskLevel, String description, Long unrestIndex, Long stabilityIndex) {
        this.riskLevel = riskLevel;
        this.description = description;
        this.unrestIndex = unrestIndex;
        this.stabilityIndex = stabilityIndex;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUnrestIndex() {
        return unrestIndex;
    }

    public void setUnrestIndex(Long unrestIndex) {
        this.unrestIndex = unrestIndex;
    }

    public Long getStabilityIndex() {
        return stabilityIndex;
    }

    public void setStabilityIndex(Long stabilityIndex) {
        this.stabilityIndex = stabilityIndex;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal calculateRisk() {
        return BigDecimal.valueOf(unrestIndex - stabilityIndex).divide(riskLevel.getLevel(), RoundingMode.FLOOR);
    }
}
