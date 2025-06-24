package hr.java.corporatetravelriskassessmenttool.model;

import java.math.BigDecimal;

/**
 * Interface for calculating the risk score.
 *
 * Only classes permitted by this sealed interface can implement it (currently {@link Risk}).
 */
public sealed interface RiskCalculator permits Risk{
    /**
     * Calculates and returns the risk score as a {@link BigDecimal}.
     *
     * @return the calculated risk score
     */
    BigDecimal calculateRisk();

}
