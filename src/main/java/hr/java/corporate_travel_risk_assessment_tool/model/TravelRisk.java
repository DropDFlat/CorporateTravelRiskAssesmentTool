package hr.java.corporate_travel_risk_assessment_tool.model;

import java.math.BigDecimal;

public sealed interface TravelRisk permits HealthRisk, PoliticalRisk, EnvironmentalRisk{
    String getDescription();
    BigDecimal calculateRisk();

}
