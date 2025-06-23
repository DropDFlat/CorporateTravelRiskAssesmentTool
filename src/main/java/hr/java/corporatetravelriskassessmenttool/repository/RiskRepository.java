package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.exception.UnknownRiskTypeException;
import hr.java.corporatetravelriskassessmenttool.mapper.RiskMapper;
import hr.java.corporatetravelriskassessmenttool.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.changelogRepository;

public class RiskRepository<T extends Risk> extends AbstractRepository<T> {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    @Override
    public synchronized T findById(Long id) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            String sql = " SELECT r.id, r.description, r.level, r.type, e.damage_index, " +
                    "e.disaster_probability, h.severity, p.unrest_index, p.stability_index " +
                    "FROM risk r LEFT JOIN environmental_risk e ON r.id = e.risk_id " +
                    "LEFT JOIN health_risk h ON r.id = h.risk_id " +
                    "LEFT JOIN political_risk p ON r.id = p.risk_id WHERE r.id = ?";
            try(PreparedStatement ps = con.prepareStatement(sql)){
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return (T) RiskMapper.map(rs);
                }else{
                    throw new EmptyRepositoryException("Risk with id " + id + " not found");
                }
            }
        }catch (SQLException | UnknownRiskTypeException e){
            throw new RepositoryAccessException(e.getMessage(), e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    @Override
    public synchronized List<T> findAll() {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            String sql = " SELECT r.id, r.description, r.level, r.type, e.damage_index, " +
                    "e.disaster_probability, h.severity, p.unrest_index, p.stability_index " +
                    "FROM risk r LEFT JOIN environmental_risk e ON r.id = e.risk_id " +
                    "LEFT JOIN health_risk h ON r.id = h.risk_id " +
                    "LEFT JOIN political_risk p ON r.id = p.risk_id";
            try(Statement stmt = con.createStatement()){
                ResultSet rs = stmt.executeQuery(sql);
                List<T> risks = new ArrayList<>();
                while(rs.next()) risks.add((T) RiskMapper.map(rs));
                return risks;
            }
        }catch(SQLException | UnknownRiskTypeException e){
            throw new RepositoryAccessException(e.getMessage(), e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }
        finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }
    @Override
    public synchronized void save(T entity, User user){
        waitForDbAccess();
        String insertRiskSql = "INSERT INTO risk(description, level, type) VALUES(?, ?, ?)";
        try(Connection con = connectToDb()) {
            switch (entity) {
                case HealthRisk healthRisk -> new HealthRiskHandler().save(healthRisk, insertRiskSql, con, user);
                case PoliticalRisk politicalRisk -> new PoliticalRiskHandler().save(politicalRisk, insertRiskSql, con, user);
                case EnvironmentalRisk environmentalRisk -> new EnvironmentalRiskHandler().save(environmentalRisk, insertRiskSql, con, user);
                default -> throw new IllegalArgumentException("Unsupported entity type " + entity.getClass().getName());
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }
        finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    @Override
    public synchronized void update(T entity, User user) {
        Risk existingRisk= findById(entity.getId());
        waitForDbAccess();
        try(Connection con = connectToDb()) {
            if (entity instanceof HealthRisk healthRisk) {
                new HealthRiskHandler().update(healthRisk, (HealthRisk) existingRisk, con, user);
            }
            if(entity instanceof PoliticalRisk politicalRisk) {
                new PoliticalRiskHandler().update(politicalRisk, (PoliticalRisk) existingRisk, con, user);
            }
            if(entity instanceof EnvironmentalRisk environmentalRisk) {
                new EnvironmentalRiskHandler().update(environmentalRisk, (EnvironmentalRisk) existingRisk, con, user);
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }
        finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    @Override
    public synchronized void delete(Long id, User user) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try(PreparedStatement ps = con.prepareStatement("DELETE FROM risk WHERE id = ?")){
                ps.setLong(1, id);
                ps.executeUpdate();
                changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Deleted risk",
                        "Id: " + id, LocalDateTime.now()));
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

}
