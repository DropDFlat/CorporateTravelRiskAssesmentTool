package hr.java.corporate_travel_risk_assessment_tool.model;

public sealed interface TravelRisk permits HealthRisk, SecurityRisk, EnvironmentalRisk{
    String getDescription();

}
