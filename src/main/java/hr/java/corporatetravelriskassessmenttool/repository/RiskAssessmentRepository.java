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

public class RiskAssessmentRepository<T extends RiskAssessment<Person, Risk>> extends AbstractRepository<T>{
    private static final String DATABASE_ERROR_STRING = "Database config failed";
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
    private RiskAssessmentStub extractFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long personId = rs.getLong("employee_id");
        Long riskId = rs.getLong("risk_id");
        Long tripId = rs.getLong("trip_id");
        LocalDate date = rs.getDate("assessment_date").toLocalDate();
        return new RiskAssessmentStub(id, personId, riskId, tripId, date);
    }
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
