package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.Objects;

public non-sealed class Risk extends Entity implements RiskCalculator {
    private String description;
    private RiskLevel riskLevel;

    public Risk(Long id, String description, RiskLevel riskLevel) {
        super(id);
        this.description = description;
        this.riskLevel = riskLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal calculateRisk() {
        return riskLevel.getLevel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Risk risk = (Risk) o;
        return Objects.equals(description, risk.description) && riskLevel == risk.riskLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, riskLevel);
    }
}
