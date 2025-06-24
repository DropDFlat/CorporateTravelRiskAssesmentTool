package hr.java.corporatetravelriskassessmenttool.mapper;

import hr.java.corporatetravelriskassessmenttool.model.Destination;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * Utility class for mapping SQL {@link ResultSet} rows to {@link Destination} model objects.
 * <p>
 * This class provides a single static method to map a database result row into a
 * fully constructed {@link Destination} instance using the builder pattern.
 * </p>
 */
public class DestinationMapper {
    /**
     * Private constructor to prevent instantiation.
     */
    private DestinationMapper() {}
    /**
     * Maps the current row of the given {@link ResultSet} to a {@link Destination} object.
     * <p>
     * Only the basic destination data (ID, city, and country) is mapped.
     * The associated risks set is initialized as empty and should be populated separately.
     * </p>
     *
     * @param rs the {@link ResultSet} positioned at the row to map
     * @return a new {@link Destination} instance based on the result set data
     * @throws SQLException if any SQL error occurs while accessing result set data
     */
    public static Destination map(ResultSet rs) throws SQLException {
        return new Destination.Builder()
                .setId(rs.getLong("id"))
                .setCity(rs.getString("city"))
                .setCountry(rs.getString("country"))
                .setRisks(new HashSet<>())
                .createDestination();
    }
}
