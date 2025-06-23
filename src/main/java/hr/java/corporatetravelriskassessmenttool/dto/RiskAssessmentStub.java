package hr.java.corporatetravelriskassessmenttool.dto;

import java.time.LocalDate;

/**
 * Data transfer object used to represent a lightweight version of a risk assessment.
 * This class is intended for use when only IDs and assessment date are needed
 */
public class RiskAssessmentStub {
    private Long id;
    private long personId;
    private long riskId;
    private long tripId;
    private LocalDate date;

    /**
     * @param id the ID of the assessment
     * @param personId the ID of the assessed person
     * @param riskId the ID of the associated risk
     * @param tripId the ID of the associated trip
     * @param date the date of the assessment
     */
    public RiskAssessmentStub(Long id, Long personId, Long riskId, Long tripId, LocalDate date) {
        this.id = id;
        this.personId = personId;
        this.riskId = riskId;
        this.tripId = tripId;
        this.date = date;
    }

    /**
     * @return the person ID
     */
    public Long getPersonId() {
        return personId;
    }

    /**
     * @return the assessment ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the associated risk ID
     */
    public Long getRiskId() {
        return riskId;
    }

    /**
     * @return the associated trip ID
     */
    public Long getTripId() {
        return tripId;
    }

    /**
     * @return the date of the assessment
     */
    public LocalDate getDate() {
        return date;
    }
}
