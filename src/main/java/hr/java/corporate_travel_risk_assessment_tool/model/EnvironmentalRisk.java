package hr.java.corporate_travel_risk_assessment_tool.model;

import hr.java.corporate_travel_risk_assessment_tool.enums.RiskLevel;

import java.math.BigDecimal;

public final class EnvironmentalRisk implements TravelRisk{

    private RiskLevel riskLevel;
    private String description;
    private int damageIndex;
    private BigDecimal disasterProbability;

    public EnvironmentalRisk(RiskLevel riskLevel, String description, int damageIndex, BigDecimal disasterProbability) {
        this.riskLevel = riskLevel;
        this.description = description;
        this.damageIndex = damageIndex;
        this.disasterProbability = disasterProbability;
    }

    public int getDamageIndex() {
        return damageIndex;
    }

    public void setDamageIndex(int damageIndex) {
        this.damageIndex = damageIndex;
    }

    public BigDecimal getDisasterProbability() {
        return disasterProbability;
    }

    public void setDisasterProbability(BigDecimal disasterProbability) {
        this.disasterProbability = disasterProbability;
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

    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public BigDecimal calculateRisk(){
        return disasterProbability.multiply(BigDecimal.valueOf(damageIndex)).multiply(riskLevel.getLevel());
    }
}
