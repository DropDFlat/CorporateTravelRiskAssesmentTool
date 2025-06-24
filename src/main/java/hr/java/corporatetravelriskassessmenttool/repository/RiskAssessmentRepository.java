package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.dto.RiskAssessmentStub;
import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.exception.EmptyRepositoryException;
import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.model.*;
import hr.java.corporatetravelriskassessmenttool.utils.ChangelogUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.changelogRepository;
/**
 * Repository class for managing {@link RiskAssessment} entities in the database.
 * <p>
 * This generic repository supports CRUD operations for risk assessments involving
 * a person and a risk related to a trip. It ensures thread-safe access to the database,
 * and integrates changelog logging for audit purposes.
 *
 * @param <T> the type of {@link RiskAssessment} managed by this repository, parameterized to allow subclasses
 */
public class RiskAssessmentRepository<T extends RiskAssessment<Person, Risk>> extends AbstractRepository<T>{
    private static final String DATABASE_ERROR_STRING = "Database config failed";

    /**
     * Finds a {@link RiskAssessment} by its unique ID.
     * <p>
     * Waits for database access to be available, then queries the risk_assessment table.
     *
     * @param id the unique identifier of the risk assessment
     * @return the risk assessment entity with the specified ID
     * @throws EmptyRepositoryException if no risk assessment with the given ID exists
     * @throws RepositoryAccessException if a database access error occurs
     */
    @Override
    public synchronized T findById(Long id){
        waitForDbAccess();
        String sql = "SELECT id, trip_id, employee_id, risk_id, assessment_date FROM risk_assessment WHERE id = ?";
        RiskAssessmentStub stub;
        try (Connection con = connectToDb()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stub = extractFromResultSet(rs);
                } else {
                    throw new EmptyRepositoryException("Assessment not found with ID: " + id);
                }
            }
        }catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }
        finally {
            databaseAccessInProgress = false;
            notifyAll();
        }
        return (T) buildFromStub(stub);
    }

    /**
     * Retrieves all {@link RiskAssessment} records from the database.
     * <p>
     * Waits for database access, then queries all records from risk_assessment.
     *
     * @return list of all risk assessments in the database
     * @throws RepositoryAccessException if a database access error occurs
     */
    @Override
    public synchronized List<T> findAll() {
        waitForDbAccess();
        List<RiskAssessmentStub> assessmentStubs = new ArrayList<>();
        String sql = "SELECT id, trip_id, employee_id, risk_id, assessment_date FROM risk_assessment";
        try (Connection con = connectToDb()){
             try(PreparedStatement ps = con.prepareStatement(sql)){
             ResultSet rs = ps.executeQuery();
                     while (rs.next()) {
                         assessmentStubs.add(extractFromResultSet(rs));
                     }
                 }
        }catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }
        finally {
            databaseAccessInProgress = false;
            notifyAll();
        }
        List<RiskAssessment<Person, Risk>> assessments = new ArrayList<>();
        assessmentStubs.forEach(stub -> assessments.add(buildFromStub(stub)));
        return (List<T>) assessments;
    }
    /**
     * Saves a new {@link RiskAssessment} or updates an existing one based on composite key.
     * <p>
     * First checks for existence by composite key (person, risk, trip). If present, calls update;
     * otherwise inserts a new record.
     * <p>
     * Logs creation events on successful insert.
     *
     * @param entity the risk assessment to save
     * @param user the user performing the operation, for changelog logging
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public synchronized void save(T entity, User user) {
        Optional<T> existing = Optional.empty();
        try {
            existing = findByCompositeKey(entity.getPerson().getId(), entity.getRisk().getId(), entity.getTrip().getId());
        } catch (SQLException e) {
            throw new RepositoryAccessException(e);
        }
        if (existing.isPresent()) {
            update(existing.get(), user);
        }else {
            waitForDbAccess();
            String sql = "INSERT INTO risk_assessment(employee_id, risk_id, trip_id, assessment_date) VALUES (?, ?, ?, ?)";
            try (Connection con = connectToDb()) {
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, entity.getPerson().getId());
                    ps.setLong(2, entity.getRisk().getId());
                    ps.setObject(3, entity.getTrip() != null ? entity.getTrip().getId() : null);
                    ps.setDate(4, Date.valueOf(entity.getAssessmentDate()));
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        Long id = rs.getLong(1);
                        ChangelogUtil.logCreation(user, "Created new risk assessment ",
                                "Id: " + id + " Assessment date " + entity.getAssessmentDate());
                    }
                }
            }catch (SQLException e) {
                throw new RepositoryAccessException(e);
            }catch(DatabaseConfigurationException e){
                throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
            }
            finally {
                databaseAccessInProgress = false;
                notifyAll();
            }
        }
    }
    /**
     * Updates an existing {@link RiskAssessment} in the database.
     * <p>
     * Loads the existing entity, then updates all fields.
     * Logs the update in the changelog.
     *
     * @param entity the risk assessment with updated data
     * @param user the user performing the update, for changelog logging
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public synchronized void update(T entity, User user) {
        RiskAssessment<Person, Risk> existingAssessment = findById(entity.getId());
        waitForDbAccess();
        String sql = "UPDATE risk_assessment SET employee_id = ?, risk_id = ?, trip_id = ?, assessment_date = ? WHERE id = ?";
        try (Connection con = connectToDb()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setLong(1, entity.getPerson().getId());
             ps.setLong(2, entity.getRisk().getId());
             ps.setLong(3, entity.getTrip().getId());
             ps.setDate(4, Date.valueOf(entity.getAssessmentDate()));
             ps.setLong(5, existingAssessment.getId());
             ps.executeUpdate();
                ChangelogUtil.logAssessmentUpdate(user, existingAssessment, entity);
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }
        finally {
            databaseAccessInProgress = false;
            notifyAll();
        }
    }
    /**
     * Deletes a {@link RiskAssessment} by its ID.
     * <p>
     * Logs the deletion event to the changelog.
     *
     * @param id the ID of the risk assessment to delete
     * @param user the user performing the deletion, for changelog logging
     * @throws RepositoryAccessException if a database error occurs
     */
    @Override
    public synchronized void delete(Long id, User user) {
        waitForDbAccess();
        try(Connection con = connectToDb()){
            try(PreparedStatement ps = con.prepareStatement("DELETE FROM risk_assessment WHERE id = ?")){
                ps.setLong(1, id);
                ps.executeUpdate();
                changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Deleted risk assessment",
                        "Id: " + id, LocalDateTime.now()));
            }
        }catch (SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }finally{
            databaseAccessInProgress = false;
            notifyAll();
        }
    }
    /**
     * Finds a risk assessment by a composite key consisting of person ID, risk ID, and trip ID.
     * <p>
     * Used internally to check if an assessment already exists for the combination.
     *
     * @param personId the ID of the person/employee
     * @param riskId the ID of the risk
     * @param tripId the ID of the trip
     * @return an {@link Optional} containing the found risk assessment, or empty if not found
     * @throws SQLException if a database access error occurs
     * @throws RepositoryAccessException if other database configuration errors occur
     */
    private synchronized Optional<T> findByCompositeKey(Long personId, Long riskId, Long tripId) throws SQLException {
        waitForDbAccess();
        String sql = "SELECT id, employee_id, risk_id, trip_id, assessment_date FROM risk_assessment WHERE employee_id = ? AND risk_id = ? AND trip_id = ?";
        Optional<RiskAssessmentStub> stub = Optional.empty();
        try (Connection con = connectToDb(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, personId);
            ps.setLong(2, riskId);
            ps.setLong(3, tripId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stub = Optional.of(extractFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }catch(DatabaseConfigurationException e){
            throw new RepositoryAccessException(DATABASE_ERROR_STRING, e);
        }
        finally {
            databaseAccessInProgress = false;
            notifyAll();
        }
        if(stub.isPresent()){
            return (Optional<T>) Optional.of(buildFromStub(stub.get()));
        }else return Optional.empty();
    }
    /**
     * Extracts a {@link RiskAssessmentStub} from the current row of a {@link ResultSet}.
     *
     * @param rs the {@link ResultSet} positioned at a row containing risk assessment data
     * @return a {@link RiskAssessmentStub} representing the data in the current row
     * @throws SQLException if an SQL error occurs reading from the result set
     */
    private RiskAssessmentStub extractFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long personId = rs.getLong("employee_id");
        Long riskId = rs.getLong("risk_id");
        Long tripId = rs.getLong("trip_id");
        LocalDate date = rs.getDate("assessment_date").toLocalDate();
        return new RiskAssessmentStub(id, personId, riskId, tripId, date);
    }
    /**
     * Builds a fully populated {@link RiskAssessment} entity from a {@link RiskAssessmentStub}.
     * <p>
     * Fetches related {@link Risk}, {@link Employee}, and {@link Trip} entities from their respective repositories.
     *
     * @param stub the risk assessment stub containing IDs and date
     * @return a fully constructed {@link RiskAssessment} entity
     */
    private RiskAssessment<Person, Risk> buildFromStub(RiskAssessmentStub stub) {
        AbstractRepository<Risk> riskRepository = new RiskRepository<>();
        AbstractRepository<Employee> employeeRepository = new EmployeeRepository<>();
        AbstractRepository<Trip<Person>> tripRepository = new TripRepository<>();
        Employee employee = employeeRepository.findById(stub.getPersonId());
        Risk risk = riskRepository.findById(stub.getRiskId());
        Trip<Person> trip = tripRepository.findById(stub.getTripId());
        return new RiskAssessment.Builder<>().setId(stub.getId()).setTrip(trip).setPerson(employee).setRisk(risk)
                .setAssessmentDate(stub.getDate()).build();
    }
}
