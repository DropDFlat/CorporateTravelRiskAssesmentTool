package hr.java.corporatetravelriskassessmenttool.enums;

import java.math.BigDecimal;

/**
 * Enumeration representing the severity level of a risk
 * Each risk level is associated with a numeric BigDecimal value used for calculation risk scores.
 */
public enum RiskLevel {
    NONE(BigDecimal.valueOf(0)),
    LOW(BigDecimal.valueOf(1)),
    MEDIUM(BigDecimal.valueOf(2)),
    HIGH(BigDecimal.valueOf(3));
    private final BigDecimal level;

    /**
     * @param level the numeric value representing the severity
     */
    RiskLevel(BigDecimal level) {
        this.level = level;
    }

    /**
     * @return the risk severity level as BigDecimal
     */
    public BigDecimal getLevel() {
        return level;
    }
}
