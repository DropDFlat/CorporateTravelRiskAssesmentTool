package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.PoliticalRisk;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;


import java.sql.*;
/**
 * Handler class responsible for managing persistence operations related to {@link PoliticalRisk} entities.
 * <p>
 * This class handles saving and updating health risk records in the database,
 * ensuring transactional integrity across the general "risk" table and the specific "political_risk" table.
 * <p>
 * It also logs creation and update events to the changelog for auditing purposes.
 * Methods are synchronized to provide thread safety during database operations.
 */
public class PoliticalRiskHandler {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    /**
     * Saves a new {@link PoliticalRisk} entity to the database.
     * <p>
     * Inserts the political risk details into the "risk" table and related "political_risk" table
     * within a single transaction. If the insertion fails, the transaction is rolled back.
     * <p>
     * Logs the creation of the new political risk with relevant details.
     *
     * @param polRisk the {@link PoliticalRisk} entity to save
     * @param insertRiskSql the SQL insert statement for the "risk" table
     * @param con the active database connection
     * @param user the user performing the save operation, used for changelog logging
     * @throws SQLException if a database access error occurs during inserts
     * @throws RepositoryAccessException if a rollback occurs or other database error happens
     */
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
    /**
     * Updates an existing {@link PoliticalRisk} entity in the database.
     * <p>
     * Updates the general "risk" table and the specific "political_risk" table within a single transaction.
     * Rolls back the transaction if any update operation fails.
     * <p>
     * Logs the update with details on changes made.
     *
     * @param updatedRisk the {@link PoliticalRisk} entity with updated information
     * @param existingRisk the current {@link PoliticalRisk} entity prior to the update
     * @param con the active database connection
     * @param user the user performing the update, used for changelog logging
     * @throws SQLException if a database access error occurs during updates
     * @throws RepositoryAccessException if a rollback occurs or other database error happens
     */
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
            ChangelogUtil.logPoliticalRiskUpdate(user, existingRisk, updatedRisk);
            con.commit();
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
