package hr.java.corporatetravelriskassessmenttool.model;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class PoliticalRisk extends Risk {
    private Integer unrestIndex;
    private Integer stabilityIndex;

    public PoliticalRisk(PoliticalRiskBuilder builder) {
        super(builder.id, builder.description, builder.riskLevel);
        this.unrestIndex = builder.unrestIndex;
        this.stabilityIndex = builder.stabilityIndex;
    }

    public Integer getUnrestIndex() {
        return unrestIndex;
    }

    public void setUnrestIndex(Integer unrestIndex) {
        this.unrestIndex = unrestIndex;
    }

    public Integer getStabilityIndex() {
        return stabilityIndex;
    }

    public void setStabilityIndex(Integer stabilityIndex) {
        this.stabilityIndex = stabilityIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PoliticalRisk that = (PoliticalRisk) o;
        return Objects.equals(unrestIndex, that.unrestIndex) && Objects.equals(stabilityIndex, that.stabilityIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unrestIndex, stabilityIndex);
    }

    @Override
    public BigDecimal calculateRisk() {
        BigDecimal totalScore = BigDecimal.valueOf((long) unrestIndex - stabilityIndex).divide(getRiskLevel().getLevel(), RoundingMode.FLOOR);
        return totalScore.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalScore;
    }
    @Override
    public String toString() {
        return "Type: " + "Political risk\n" + getDescription() + "\n Total risk score: " + calculateRisk();
    }
    public static class PoliticalRiskBuilder {
        private Long id;
        private RiskLevel riskLevel;
        private String description;
        private Integer stabilityIndex;
        private Integer unrestIndex;

        public PoliticalRiskBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public PoliticalRiskBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public PoliticalRiskBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public PoliticalRiskBuilder setStabilityIndex(Integer stabilityIndex) {
            this.stabilityIndex = stabilityIndex;
            return this;
        }
        public PoliticalRiskBuilder setUnrestIndex(Integer unrestIndex) {
            this.unrestIndex = unrestIndex;
            return this;
        }

        public PoliticalRisk createPoliticalRisk() {
            return new PoliticalRisk(this);
        }
    }
}
