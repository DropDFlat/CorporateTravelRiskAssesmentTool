package hr.java.corporatetravelriskassessmenttool.mapper;

import hr.java.corporatetravelriskassessmenttool.enums.RiskLevel;
import hr.java.corporatetravelriskassessmenttool.exception.UnknownRiskTypeException;
import hr.java.corporatetravelriskassessmenttool.model.EnvironmentalRisk;
import hr.java.corporatetravelriskassessmenttool.model.HealthRisk;
import hr.java.corporatetravelriskassessmenttool.model.PoliticalRisk;
import hr.java.corporatetravelriskassessmenttool.model.Risk;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RiskMapper {
    private RiskMapper() {
    }
    public static Risk map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String description = rs.getString("description");
        RiskLevel level = RiskLevel.valueOf(rs.getString("level").toUpperCase());
        String type = rs.getString("type");

        return switch (type) {
            case "Environmental" -> new EnvironmentalRisk.EnvironmentalRiskBuilder()
                    .setId(id).setRiskLevel(level).setDescription(description)
                    .setDamageIndex(rs.getInt("damage_index"))
                    .setDisasterProbability(rs.getBigDecimal("disaster_probability"))
                    .createEnvironmentalRisk();

            case "Political" -> new PoliticalRisk.PoliticalRiskBuilder()
                    .setId(id).setDescription(description).setRiskLevel(level)
                    .setStabilityIndex(rs.getInt("stability_index"))
                    .setUnrestIndex(rs.getInt("unrest_index"))
                    .createPoliticalRisk();

            case "Health" -> new HealthRisk.HealthRiskBuilder()
                    .setId(id).setDescription(description).setSeverity(rs.getBigDecimal("severity"))
                    .setRiskLevel(level).createHealthRisk();

            default -> throw new UnknownRiskTypeException("Unknown risk type: " + type);
        };
    }
}
