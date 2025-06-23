package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class EnvironmentalRisk extends Risk {

    private Integer damageIndex;
    private BigDecimal disasterProbability;

    public EnvironmentalRisk(EnvironmentalRiskBuilder builder) {
        super(builder.id, builder.description, builder.riskLevel);
        this.damageIndex = builder.damageIndex;
        this.disasterProbability = builder.disasterProbability;
    }

    public Integer getDamageIndex() {
        return damageIndex;
    }

    public void setDamageIndex(Integer damageIndex) {
        this.damageIndex = damageIndex;
    }

    public BigDecimal getDisasterProbability() {
        return disasterProbability;
    }

    public void setDisasterProbability(BigDecimal disasterProbability) {
        this.disasterProbability = disasterProbability;
    }

    @Override
    public BigDecimal calculateRisk(){
        return disasterProbability.multiply(BigDecimal.valueOf(damageIndex)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))
                .multiply(getRiskLevel().getLevel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnvironmentalRisk that = (EnvironmentalRisk) o;
        return Objects.equals(damageIndex, that.damageIndex) && Objects.equals(disasterProbability, that.disasterProbability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), damageIndex, disasterProbability);
    }

    @Override
    public String toString() {
        return "Type: " + "Environmental risk\n" + getDescription() + "\n Total risk score: " + calculateRisk();
    }
    public static class EnvironmentalRiskBuilder {
        private Long id;
        private RiskLevel riskLevel;
        private String description;
        private Integer damageIndex;
        private BigDecimal disasterProbability;

        public EnvironmentalRiskBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public EnvironmentalRiskBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public EnvironmentalRiskBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public EnvironmentalRiskBuilder setDamageIndex(Integer damageIndex) {
            this.damageIndex = damageIndex;
            return this;
        }

        public EnvironmentalRiskBuilder setDisasterProbability(BigDecimal disasterProbability) {
            this.disasterProbability = disasterProbability;
            return this;
        }

        public EnvironmentalRisk createEnvironmentalRisk() {
            return new EnvironmentalRisk(this);
        }
    }
}

