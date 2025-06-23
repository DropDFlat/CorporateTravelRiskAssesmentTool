package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.InvalidTripDataException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;
import hr.java.corporatetravelriskassessmenttool.utils.TripDataFetcher;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.changelogRepository;

public class TripRepository<T extends Trip<Person>> extends AbstractRepository<T> {
    private static final String DATABASE_ERROR_STRING = "Database config failed";
    @Override
    public synchronized T findById(Long id) {
        AbstractRepository<Risk> riskRepo = new RiskRepository<>();
        List<Risk> allRisks = riskRepo.findAll();
        waitForDbAccess();
        try(Connection con = connectToDb()){
            Trip<Person> trip;
            String tripSql = "SELECT id, start_date, end_date, name FROM trip WHERE id = ?";
            try(PreparedStatement ps = con.prepareStatement(tripSql)){
                ps.setLong(1, id);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) throw new EmptyRepositoryException("No trip found with id: " + id);
                    trip = extractTripFromResultSet(rs);
                }
            }
            trip.setEmployees(TripDataFetcher.fetchEmployees(con, trip.getId()));
            trip.setDestinations(TripDataFetcher.fetchDestinations(con, trip.getId(), allRisks));
            return (T) trip;
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
        AbstractRepository<Risk> riskRepo = new RiskRepository<>();
        List<Risk> allRisks = riskRepo.findAll();
        waitForDbAccess();
        try(Connection con = connectToDb()){
            List<Trip<Person>> trips = new ArrayList<>();
            String tripSql = "SELECT id, name, start_date, end_date FROM trip";
            try(PreparedStatement ps = con.prepareStatement(tripSql)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    trips.add(createTrip(con, rs, allRisks));
                }
            }
            return (List<T>) trips;
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
    public synchronized void save(T entity, User user) {
        Trip<Person> entityCast = entity;
        waitForDbAccess();
        try(Connection con = connectToDb()){
            if(entity.getStartDate().isAfter(entity.getEndDate())){
                throw new InvalidTripDataException("Start date cannot be after end date");
            }
            String sql = "INSERT INTO trip ( name, start_date, end_date) VALUES (?, ?, ?)";
            try(PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getName());
                ps.setDate(2, Date.valueOf(entity.getStartDate()));
                ps.setDate(3, Date.valueOf(entity.getEndDate()));
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Long tripId = rs.getLong(1);
                    saveEmployeesAndDestinations(con, tripId, entityCast);
                    ChangelogUtil.logCreation(user, "New trip created",
                            "Id: "+ tripId + " Name: " + entity.getName());
                }
            }
        }catch (SQLException e){
            throw new RepositoryAccessException(e);
        }catch(InvalidTripDataException e){
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
    public synchronized void update(T entity, User user) {
        Trip<Person> existingTrip = findById(entity.getId());
        Trip<Person> entityCast = entity;
        waitForDbAccess();
        try(Connection con = connectToDb()){
            if(entity.getStartDate().isAfter(entity.getEndDate())){
                throw new InvalidTripDataException("Start date cannot be after end date");
            }
            con.setAutoCommit(false);
            updateTripData(con, entity);
            deleteOldRelations(con, entity.getId());
            saveEmployeesAndDestinations(con, entity.getId(), entityCast);
            con.commit();
            ChangelogUtil.logTripUpdate(user, existingTrip, entityCast);
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(InvalidTripDataException e){
            throw new RepositoryAccessException(e.getMessage(), e);
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
            try(PreparedStatement ps = con.prepareStatement("DELETE FROM trip WHERE id = ?")){
                ps.setLong(1, id);
                ps.executeUpdate();
                changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Deleted trip",
                        "Id: " + id, LocalDateTime.now()));
            }
        }catch (SQLException e){
            throw new RepositoryAccessException(e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }
    private Trip<Person> extractTripFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        LocalDate startDate = rs.getDate("start_date").toLocalDate();
        LocalDate endDate = rs.getDate("end_date").toLocalDate();
        Trip<Person> trip =new Trip.TripBuilder<Person>().setId(id).setName(name).setEmployees(new HashSet<>()).setDestinations(new HashSet<>())
                .setStartDate(startDate).setEndDate(endDate).build();
        if(startDate.isAfter(endDate)){
            trip.setWarningMessage("Start date if after end date");
        }
        return trip;
    }
    private Trip<Person> createTrip(Connection con, ResultSet rs, List<Risk> allRisks) throws SQLException{
        Trip<Person> trip = extractTripFromResultSet(rs);
        trip.setEmployees(TripDataFetcher.fetchEmployees(con, trip.getId()));
        trip.setDestinations(TripDataFetcher.fetchDestinations(con, trip.getId(), allRisks));
        return trip;
    }
    private void updateTripData(Connection con, Trip<Person> trip) throws SQLException{
        try(PreparedStatement ps = con.prepareStatement("UPDATE trip SET name = ?, start_date = ?, end_date = ? WHERE id = ?")){
            ps.setString(1, trip.getName());
            ps.setDate(2, Date.valueOf(trip.getStartDate()));
            ps.setDate(3, Date.valueOf(trip.getEndDate()));
            ps.setLong(4, trip.getId());
            ps.executeUpdate();
        }
    }
    private void deleteOldRelations(Connection con, Long tripId) throws SQLException {
        try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM trip_employee WHERE trip_id = ?");
             PreparedStatement ps2 = con.prepareStatement("DELETE FROM trip_destination WHERE trip_id = ?")) {
            ps1.setLong(1, tripId);
            ps2.setLong(1, tripId);
            ps1.executeUpdate();
            ps2.executeUpdate();
        }
    }
    private void saveEmployeesAndDestinations(Connection con, Long tripId, Trip<Person> trip) throws SQLException {
        try (PreparedStatement psEmp = con.prepareStatement("INSERT INTO trip_employee (trip_id, employee_id) VALUES (?, ?)")) {
            psEmp.setLong(1, tripId);
            for (Person emp : trip.getEmployees()) {
                psEmp.setLong(2, emp.getId());
                psEmp.addBatch();
            }
            psEmp.executeBatch();
        }
        try (PreparedStatement psDest = con.prepareStatement("INSERT INTO trip_destination (trip_id, destination_id) VALUES (?, ?)")) {
            psDest.setLong(1, tripId);
            for (Destination dest : trip.getDestinations()) {
                psDest.setLong(2, dest.getId());
                psDest.addBatch();
            }
            psDest.executeBatch();
        }
    }
}
