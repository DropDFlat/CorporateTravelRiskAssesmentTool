package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.PoliticalRisk;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;


import java.sql.*;

public class PoliticalRiskHandler {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    public synchronized void save(PoliticalRisk polRisk, String insertRiskSql, Connection con, User user) throws SQLException {
        String insertPolSql = "INSERT INTO political_risk(risk_id, unrest_index, stability_index) VALUES(?, ?, ?)";
        try (PreparedStatement riskStmt = con.prepareStatement(insertRiskSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement polStmt = con.prepareStatement(insertPolSql);) {
            con.setAutoCommit(false);
            riskStmt.setString(1, polRisk.getDescription());
            riskStmt.setString(2, polRisk.getRiskLevel().toString());
            riskStmt.setString(3, "Political");
            riskStmt.executeUpdate();
            ResultSet rs = riskStmt.getGeneratedKeys();
            if (rs.next()) {
                Long riskId = rs.getLong(1);
                polStmt.setLong(1, riskId);
                polStmt.setLong(2, polRisk.getUnrestIndex());
                polStmt.setLong(3, polRisk.getStabilityIndex());
                polStmt.executeUpdate();
                con.commit();
                ChangelogUtil.logCreation(user, "Created new political risk",
                        "Id: " + riskId + " Description: " + polRisk.getDescription()
                        + " Unrest index: " + polRisk.getUnrestIndex() + " Stability index: " + polRisk.getStabilityIndex());
            } else con.rollback();
        } catch (SQLException e) {
            con.rollback();
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        } finally {
            con.setAutoCommit(true);
        }
    }
    public synchronized void update(PoliticalRisk updatedRisk, PoliticalRisk existingRisk, Connection con, User user) throws SQLException {
        con.setAutoCommit(false);
        try (
                PreparedStatement riskStmt = con.prepareStatement("UPDATE risk SET description = ?, level = ? WHERE id = ?");
                PreparedStatement healthStmt = con.prepareStatement("UPDATE political_risk SET stability_index = ?," +
                        " unrest_index = ? WHERE risk_id = ?")
        ) {
            riskStmt.setString(1, updatedRisk.getDescription());
            riskStmt.setString(2, updatedRisk.getRiskLevel().toString());
            riskStmt.setLong(3, updatedRisk.getId());
            riskStmt.executeUpdate();

            healthStmt.setLong(1, updatedRisk.getStabilityIndex());
            healthStmt.setLong(2, updatedRisk.getUnrestIndex());
            healthStmt.setLong(3, updatedRisk.getId());
            healthStmt.executeUpdate();
            con.commit();
            ChangelogUtil.logPoliticalRiskUpdate(user, existingRisk, existingRisk);
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
