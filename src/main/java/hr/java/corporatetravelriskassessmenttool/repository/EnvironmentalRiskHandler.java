package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.EnvironmentalRisk;
import hr.java.corporatetravelriskassessmenttool.model.User;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Handler class responsible for managing persistence operations for {@link EnvironmentalRisk} entities.
 * <p>
 * This class encapsulates database save and update operations specific to environmental risks.
 * <p>
 * It also logs changes to the changelog system.
 * Methods are synchronized to ensure thread safety during database interactions.
 */
public class EnvironmentalRiskHandler {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    /**
     * Saves a new {@link EnvironmentalRisk} entity into the database.
     * <p>
     * Inserts data into the "risk" table and the related "environmental_risk" table within a single transaction.
     * If either insert fails, the transaction is rolled back.
     * <p>
     * Logs the creation event with details about the new environmental risk.
     *
     * @param envRisk the {@link EnvironmentalRisk} entity to save
     * @param insertRiskSql the SQL insert statement for the "risk" table
     * @param con the active database connection
     * @param user the user performing the save operation, used for changelog logging
     * @throws SQLException if a database access error occurs during insert operations
     * @throws RepositoryAccessException if there is a database error or rollback occurs
     */
    public synchronized void save(EnvironmentalRisk envRisk, String insertRiskSql, Connection con, User user) throws SQLException {
        String insertEnvSql = "INSERT INTO environmental_risk(risk_id, damage_index, disaster_probability) VALUES(?, ?, ?)";
        try (PreparedStatement riskStmt = con.prepareStatement(insertRiskSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement envStmt = con.prepareStatement(insertEnvSql)) {
            con.setAutoCommit(false);
            riskStmt.setString(1, envRisk.getDescription());
            riskStmt.setString(2, envRisk.getRiskLevel().toString());
            riskStmt.setString(3, "Environmental");
            riskStmt.executeUpdate();
            ResultSet rs = riskStmt.getGeneratedKeys();
            if (rs.next()) {
                Long riskId = rs.getLong(1);
                envStmt.setLong(1, riskId);
                envStmt.setLong(2, envRisk.getDamageIndex());
                envStmt.setBigDecimal(3, envRisk.getDisasterProbability());
                envStmt.executeUpdate();
                con.commit();
                ChangelogUtil.logCreation(user, "Created new environmental risk",
                        "Id: " + riskId + " Description: " + envRisk.getDescription() + " Damage index: " + envRisk.getDamageIndex()
                         + " Disaster probability: " + envRisk.getDisasterProbability());
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
     * Updates an existing {@link EnvironmentalRisk} entity in the database.
     * <p>
     * Updates are performed within a transaction, modifying both the "risk" and "environmental_risk" tables.
     * The method detects and logs any changes between the existing and updated entities.
     * <p>
     * If any update operation fails, the transaction is rolled back to maintain data consistency.
     *
     * @param updatedRisk the {@link EnvironmentalRisk} entity containing updated data
     * @param existingRisk the current {@link EnvironmentalRisk} entity from the database before update
     * @param con the active database connection
     * @param user the user performing the update operation, used for changelog logging
     * @throws SQLException if a database access error occurs during update operations
     * @throws RepositoryAccessException if there is a database error or rollback occurs
     */
    public synchronized void update(EnvironmentalRisk updatedRisk, EnvironmentalRisk existingRisk, Connection con, User user) throws SQLException {
        con.setAutoCommit(false);
        List<String> changes = new ArrayList<>();
        if (!existingRisk.getDescription().equals(updatedRisk.getDescription())) {
            changes.add("description: '" + existingRisk.getDescription() + "' → '" + updatedRisk.getDescription() + "'\n");
        }
        if (!existingRisk.getRiskLevel().equals(updatedRisk.getRiskLevel())) {
            changes.add("riskLevel: '" + existingRisk.getRiskLevel() + "' → '" + updatedRisk.getRiskLevel() + "'\n");
        }
        if (existingRisk.getDamageIndex().compareTo(updatedRisk.getDamageIndex()) != 0) {
            changes.add("damage index: " + existingRisk.getDamageIndex() + " → " + updatedRisk.getDamageIndex() + "\n");
        }
        if(existingRisk.getDisasterProbability().compareTo(updatedRisk.getDisasterProbability()) != 0) {
            changes.add("disaster probability: " + existingRisk.getDisasterProbability() + " → " + updatedRisk.getDisasterProbability());
        }
        try (
                PreparedStatement riskStmt = con.prepareStatement("UPDATE risk SET description = ?, level = ? WHERE id = ?");
                PreparedStatement healthStmt = con.prepareStatement("UPDATE environmental_risk SET damage_index = ?," +
                        " disaster_probability = ? WHERE risk_id = ?")
        ) {
            riskStmt.setString(1, updatedRisk.getDescription());
            riskStmt.setString(2, updatedRisk.getRiskLevel().toString());
            riskStmt.setLong(3, updatedRisk.getId());
            riskStmt.executeUpdate();

            healthStmt.setInt(1, updatedRisk.getDamageIndex());
            healthStmt.setBigDecimal(2, updatedRisk.getDisasterProbability());
            healthStmt.setLong(3, updatedRisk.getId());
            healthStmt.executeUpdate();
            con.commit();
            ChangelogUtil.logEnvironmentalRiskUpdate(user, existingRisk, updatedRisk);
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
