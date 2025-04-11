package hr.java.corporate_travel_risk_assessment_tool.enums;

import java.math.BigDecimal;

public enum RiskLevel {
    NONE(BigDecimal.valueOf(0)),
    LOW(BigDecimal.valueOf(1)),
    MEDIUM(BigDecimal.valueOf(2)),
    HIGH(BigDecimal.valueOf(3));
    private final BigDecimal level;

    RiskLevel(BigDecimal level) {
        this.level = level;
    }

    public BigDecimal getLevel() {
        return level;
    }
}
