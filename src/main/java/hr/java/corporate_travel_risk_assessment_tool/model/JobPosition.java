package hr.java.corporate_travel_risk_assessment_tool.model;

public record JobPosition(String title, String department) {
    @Override
    public String toString() {
        return new StringBuilder("Position: ").append(title).append(" | Department: ").append(department).toString();
    }
}
