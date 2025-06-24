package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.HealthRisk;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;

import java.sql.*;

/**
 * Handler class responsible for managing persistence operations related to {@link HealthRisk} entities.
 * <p>
 * This class handles saving and updating health risk records in the database,
 * ensuring transactional integrity across the general "risk" table and the specific "health_risk" table.
 * <p>
 * It also logs creation and update events to the changelog for auditing purposes.
 * Methods are synchronized to provide thread safety during database operations.
 */
public class HealthRiskHandler {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    /**
     * Saves a new {@link HealthRisk} entity to the database.
     * <p>
     * Inserts the health risk details into the "risk" table and related "health_risk" table
     * within a single transaction. If the insertion fails, the transaction is rolled back.
     * <p>
     * Logs the creation of the new health risk with relevant details.
     *
     * @param healthRisk the {@link HealthRisk} entity to save
     * @param insertRiskSql the SQL insert statement for the "risk" table
     * @param con the active database connection
     * @param user the user performing the save operation, used for changelog logging
     * @throws SQLException if a database access error occurs during inserts
     * @throws RepositoryAccessException if a rollback occurs or other database error happens
     */
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
    /**
     * Updates an existing {@link HealthRisk} entity in the database.
     * <p>
     * Updates the general "risk" table and the specific "health_risk" table within a single transaction.
     * Rolls back the transaction if any update operation fails.
     * <p>
     * Logs the update with details on changes made.
     *
     * @param updatedRisk the {@link HealthRisk} entity with updated information
     * @param existingRisk the current {@link HealthRisk} entity prior to the update
     * @param con the active database connection
     * @param user the user performing the update, used for changelog logging
     * @throws SQLException if a database access error occurs during updates
     * @throws RepositoryAccessException if a rollback occurs or other database error happens
     */
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
