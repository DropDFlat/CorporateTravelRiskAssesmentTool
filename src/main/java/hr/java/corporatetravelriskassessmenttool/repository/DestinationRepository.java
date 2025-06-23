package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.exception.UnknownRiskTypeException;
import hr.java.corporatetravelriskassessmenttool.mapper.DestinationMapper;
import hr.java.corporatetravelriskassessmenttool.mapper.RiskMapper;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.changelogRepository;

public class DestinationRepository<T extends Destination> extends AbstractRepository<T> {
    private static final String DEST_ID = "destination_id";
    private static final String DATABASE_ERROR_STRING = "Database config failed";

    @Override
    public synchronized T findById(Long id){
        waitForDbAccess();
        try(Connection con = connectToDb()){
            String sql = "SELECT d.id, d.country, d.city FROM destinations d WHERE d.id = ?";
            Destination destination;
            try(PreparedStatement ps = con.prepareStatement(sql)){
                ps.setLong(1, id);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()){
                        throw new EmptyRepositoryException("No destination found with id " + id);
                    }
                    destination = DestinationMapper.map(rs);
                }
            }
            String riskSql = "SELECT r.id, r.description, r.level, r.type, e.damage_index, e.disaster_probability, h.severity," +
                    "p.unrest_index, p.stability_index FROM destination_risk dr JOIN risk r ON dr.risk_id = r.id " +
                    "LEFT JOIN environmental_risk e ON r.id = e.risk_id LEFT JOIN health_risk h ON r.id = h.risk_id " +
                    "LEFT JOIN political_risk p ON r.id = p.risk_id WHERE dr.destination_id = ?";
            try(PreparedStatement ps = con.prepareStatement(riskSql)){
                ps.setLong(1, id);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        destination.addRisk(RiskMapper.map(rs));
                    }
                }
            }
            return (T) destination;
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
    public synchronized List<T> findAll() {
        waitForDbAccess();
        Map<Long, Destination> destinationMap = new HashMap<>();

        try(Connection conn = connectToDb()){
            try(Statement stmt = conn.createStatement()){
                ResultSet rs = stmt.executeQuery("SELECT ID, CITY, COUNTRY FROM destinations");
                while(rs.next()){
                    Destination destination = DestinationMapper.map(rs);
                    destinationMap.put(destination.getId(), destination);
                }
            }
            populateRisks(conn, destinationMap);
        }catch(SQLException | UnknownRiskTypeException e){
            throw new RepositoryAccessException(e.getMessage(), e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
        return (List<T>) new ArrayList<>(destinationMap.values());
    }
    private void populateRisks(Connection conn, Map<Long, Destination> map)throws SQLException{
        String[] sqls = {"SELECT r.*, e.damage_index, e.disaster_probability, dr.destination_id " +
                "FROM risk r JOIN environmental_risk e ON r.id = e.risk_id JOIN destination_risk dr ON r.id = dr.risk_id",
                "SELECT r.*, p.unrest_index, p.stability_index, dr.destination_id " +
                        "FROM risk r JOIN political_risk p ON r.id = p.risk_id JOIN destination_risk dr ON r.id = dr.risk_id",
                "SELECT r.*, h.severity, dr.destination_id " +
                        "FROM risk r JOIN health_risk h ON r.id = h.risk_id JOIN destination_risk dr on r.id = dr.risk_id"
        };
        for(String sql : sqls){
            try(Statement stmt = conn.createStatement()){
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    map.get(rs.getLong(DEST_ID)).addRisk(RiskMapper.map(rs));
                }
            }
        }
    }
    @Override
    public synchronized void save(T entity, User user) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            con.setAutoCommit(false);
            String destinationSql = "INSERT INTO destinations(country, city) VALUES (?, ?)";
            String riskSql = "INSERT INTO destination_risk(destination_id, risk_id) VALUES (?, ?)";
            insertDestination(entity, destinationSql, riskSql, con, user);
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }

    private synchronized void insertDestination(T entity, String destinationSql, String riskSql, Connection con, User user) throws SQLException {
        try(PreparedStatement destinationStmt = con.prepareStatement(destinationSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement riskStmt = con.prepareStatement(riskSql)){
            destinationStmt.setString(1, entity.getCountry());
            destinationStmt.setString(2, entity.getCity());
            destinationStmt.executeUpdate();
            ResultSet rs = destinationStmt.getGeneratedKeys();
            if(rs.next()){
                Long destId = rs.getLong(1);
                riskStmt.setLong(1, destId);
                Set<Risk> risks = entity.getRisks();
                for(Risk risk : risks){
                    riskStmt.setLong(2, risk.getId());
                    riskStmt.addBatch();
                }
                riskStmt.executeBatch();
                con.commit();
                ChangelogUtil.logCreation(user, "Created new destination",
                        "Id: " + destId + " Country: " + entity.getCountry() + " City: "
                                + entity.getCity());
            }
        }catch(SQLException e){
            con.rollback();
            throw new RepositoryAccessException(e);
        }
    }

    @Override
    public synchronized void update(T entity, User user) {
        Destination existingDestination = findById(entity.getId());
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try (PreparedStatement destinationStmt = con.prepareStatement("UPDATE destinations SET country = ?, city = ?" +
                    " WHERE id = ?");
                 PreparedStatement deleteRiskStmt = con.prepareStatement("DELETE FROM destination_risk WHERE destination_id = ?");
                 PreparedStatement insertRiskStmt = con.prepareStatement("INSERT INTO destination_risk(destination_id, risk_id) VALUES (?, ?)");
            ) {
                con.setAutoCommit(false);
                destinationStmt.setString(1, entity.getCountry());
                destinationStmt.setString(2, entity.getCity());
                destinationStmt.setLong(3, entity.getId());
                destinationStmt.executeUpdate();
                deleteRiskStmt.setLong(1, entity.getId());
                deleteRiskStmt.executeUpdate();
                for(Risk risk : entity.getRisks()){
                    insertRiskStmt.setLong(1, entity.getId());
                    insertRiskStmt.setLong(2, risk.getId());
                    insertRiskStmt.addBatch();
                }
                insertRiskStmt.executeBatch();
                con.commit();
                ChangelogUtil.logDestinationUpdate(user, existingDestination, entity);
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

    @Override
    public synchronized void delete(Long id, User user) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try(PreparedStatement ps = con.prepareStatement("DELETE FROM destinations WHERE id = ?")){
                ps.setLong(1, id);
                ps.executeUpdate();
                changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Deleted destination"
                , "Id: " + id, LocalDateTime.now()));
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
