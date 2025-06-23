package hr.java.corporatetravelriskassessmenttool.model;

import java.math.BigDecimal;

public sealed interface RiskCalculator permits Risk{
    BigDecimal calculateRisk();

}
