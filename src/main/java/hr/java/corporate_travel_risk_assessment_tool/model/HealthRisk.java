package hr.java.corporate_travel_risk_assessment_tool.model;

import hr.java.corporate_travel_risk_assessment_tool.enums.RiskLevel;

import java.math.BigDecimal;

public final class HealthRisk implements TravelRisk{
    private RiskLevel riskLevel;
    private BigDecimal severity;
    private String description;

    public HealthRisk(RiskLevel riskLevel, BigDecimal severity, String description) {
        this.riskLevel = riskLevel;
        this.severity = severity;
        this.description = description;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getSeverity() {
        return severity;
    }

    public void setSeverity(BigDecimal severity) {
        this.severity = severity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public BigDecimal calculateRisk(){
        return severity.multiply(riskLevel.getLevel());
    }
}
