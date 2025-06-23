package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.Objects;

public final class HealthRisk extends Risk {
    private BigDecimal severity;

    public HealthRisk(HealthRiskBuilder builder) {
        super(builder.id, builder.description, builder.riskLevel);
        this.severity = builder.severity;
    }

    public BigDecimal getSeverity() {
        return severity;
    }

    public void setSeverity(BigDecimal severity) {
        this.severity = severity;
    }

    @Override
    public BigDecimal calculateRisk(){
        return severity.multiply(getRiskLevel().getLevel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HealthRisk that = (HealthRisk) o;
        return Objects.equals(severity, that.severity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), severity);
    }

    @Override
    public String toString() {
        return "Type: " + "Health risk\n" + getDescription() + "\n Total risk score: " + calculateRisk();
    }

    public static class HealthRiskBuilder{
        private RiskLevel riskLevel;
        private BigDecimal severity;
        private String description;
        private Long id;
        public HealthRiskBuilder setId(Long id) {
            this.id = id;
            return this;
        }
        public HealthRiskBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }
        public HealthRiskBuilder setSeverity(BigDecimal severity) {
            this.severity = severity;
            return this;
        }
        public HealthRiskBuilder setDescription(String description) {
            this.description = description;
            return this;
        }
        public HealthRisk createHealthRisk(){
            return new HealthRisk(this);
        }
    }
}
