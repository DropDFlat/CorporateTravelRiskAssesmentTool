package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.HealthRisk;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;

import java.sql.*;


public class HealthRiskHandler {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    public synchronized void save(HealthRisk healthRisk, String insertRiskSql, Connection con, User user) throws SQLException {
        String insertHealthSql = "INSERT INTO health_risk(risk_id, severity) VALUES(?, ?)";
        try (PreparedStatement riskStmt = con.prepareStatement(insertRiskSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement healthStmt = con.prepareStatement(insertHealthSql);) {
            con.setAutoCommit(false);
            riskStmt.setString(1, healthRisk.getDescription());
            riskStmt.setString(2, healthRisk.getRiskLevel().toString());
            riskStmt.setString(3, "Health");
            riskStmt.executeUpdate();
            ResultSet rs = riskStmt.getGeneratedKeys();
            if (rs.next()) {
                Long riskId = rs.getLong(1);
                healthStmt.setLong(1, riskId);
                healthStmt.setBigDecimal(2, healthRisk.getSeverity());
                healthStmt.executeUpdate();
                con.commit();
                ChangelogUtil.logCreation(user, "Created new health risk",
                        "Id: " + riskId + " Description: " + healthRisk.getDescription() + " Severity: "
                                + healthRisk.getSeverity());
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            con.rollback();
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        } finally {
            con.setAutoCommit(true);
        }
    }
    public synchronized void update(HealthRisk updatedRisk, HealthRisk existingRisk, Connection con, User user) throws SQLException {
        con.setAutoCommit(false);
        try (
                PreparedStatement riskStmt = con.prepareStatement("UPDATE risk SET description = ?, level = ? WHERE id = ?");
                PreparedStatement healthStmt = con.prepareStatement("UPDATE health_risk SET severity = ? WHERE risk_id = ?")
        ) {
            riskStmt.setString(1, updatedRisk.getDescription());
            riskStmt.setString(2, updatedRisk.getRiskLevel().toString());
            riskStmt.setLong(3, updatedRisk.getId());
            riskStmt.executeUpdate();

            healthStmt.setBigDecimal(1, updatedRisk.getSeverity());
            healthStmt.setLong(2, updatedRisk.getId());
            healthStmt.executeUpdate();
            con.commit();
            ChangelogUtil.logHealthRiskUpdate(user, existingRisk, updatedRisk);
        } catch (SQLException e) {
            con.rollback();
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        } finally {
            con.setAutoCommit(true);
        }
    }
}
