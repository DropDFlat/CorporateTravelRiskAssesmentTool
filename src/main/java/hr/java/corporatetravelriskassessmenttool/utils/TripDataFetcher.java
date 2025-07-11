package hr.java.corporatetravelriskassessmenttool.utils;

import hr.java.corporatetravelriskassessmenttool.exception.RepositoryAccessException;
import hr.java.corporatetravelriskassessmenttool.mapper.DestinationMapper;
import hr.java.corporatetravelriskassessmenttool.mapper.EmployeeMapper;
import hr.java.corporatetravelriskassessmenttool.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * Utility class responsible for fetching trip-related data from the database.
 * Provides methods to retrieve employees and destinations associated with a given trip.
 * This class is not instantiable.
 */
public class TripDataFetcher {
    /**
     * Private constructor to prevent instantiation
     */
    private TripDataFetcher() {}

    /**
     * Fetches the set of employees participating in a specified trip.
     *
     * @param con the active database connection
     * @param tripId the ID of the trip for which to fetch employees
     * @return a set of {@link Person} objects representing employees linked to the trip
     * @throws RepositoryAccessException if a database access error occurs
     */
    public static Set<Person> fetchEmployees(Connection con, Long tripId) {
        Set<Person> employees = new HashSet<>();
        String sql = "SELECT e.id, e.name, e.salary, e.department, e.job_title, e.date_of_birth FROM employees e " +
                "JOIN trip_employee te ON e.id = te.employee_id WHERE te.trip_id = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, tripId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                employees.add(EmployeeMapper.map(rs));
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }
        return employees;
    }
    /**
     * Fetches the set of destinations associated with a specified trip, including their related risks.
     *
     * @param con the active database connection
     * @param tripId the ID of the trip for which to fetch destinations
     * @param allRisks the list of all available risks to be linked to destinations
     * @return a set of {@link Destination} objects linked to the trip, each populated with associated risks
     * @throws RepositoryAccessException if a database access error occurs
     */
    public static Set<Destination> fetchDestinations(Connection con, Long tripId, List<Risk> allRisks) {
        Set<Destination> destinations = new HashSet<>();
        Map<Long, Destination> destinationMap = new HashMap<>();
        String sql = "SELECT d.id, d.country, d.city FROM destinations d JOIN trip_destination td ON d.id = td.destination_id" +
                " WHERE td.trip_id = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, tripId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Destination destination = DestinationMapper.map(rs);
                destinations.add(destination);
                destinationMap.put(destination.getId(), destination);
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }
        Map<Long, Risk> riskMap = allRisks.stream().collect(Collectors.toMap(Risk::getId, Function.identity()));
        String riskSql = "SELECT destination_id, risk_id FROM destination_risk WHERE destination_id IN (SELECT destination_id" +
                " FROM trip_destination where trip_id = ?)";
        try(PreparedStatement ps = con.prepareStatement(riskSql)){
            ps.setLong(1, tripId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Long destinationId = rs.getLong("destination_id");
                Long riskId = rs.getLong("risk_id");
                Destination destination = destinationMap.get(destinationId);
                if(destination != null){
                    Risk risk = riskMap.get(riskId);
                    if(risk != null){
                        destination.getRisks().add(risk);
                    }
                }
            }
        }catch(SQLException e){
            throw new RepositoryAccessException(e);
        }
        return destinations;
    }
}
