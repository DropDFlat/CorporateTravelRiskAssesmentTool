package hr.java.corporate_travel_risk_assessment_tool.model;

import java.util.Set;

public class Trip extends Entity{
    private Set<Employee> employees;
    private Set<Destination> destinations;
    private Integer durationInDays;


    public Trip(Long id) {
        super(id);
    }
}
