package hr.java.corporate_travel_risk_assessment_tool.model;

import java.time.LocalDate;

public class RiskAssessment <T extends Person, R extends TravelRisk> implements Reportable{
    private T person;
    private R risk;
    private LocalDate assessmentDate;

    public RiskAssessment(T person, R risk, LocalDate assessmentDate) {
        this.person = person;
        this.risk = risk;
        this.assessmentDate = assessmentDate;
    }

    public T getPerson() {
        return person;
    }

    public void setPerson(T person) {
        this.person = person;
    }

    public R getRisk() {
        return risk;
    }

    public void setRisk(R risk) {
        this.risk = risk;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append(person.getName()).append(" is facing ");
        return report.toString();

    }
}
