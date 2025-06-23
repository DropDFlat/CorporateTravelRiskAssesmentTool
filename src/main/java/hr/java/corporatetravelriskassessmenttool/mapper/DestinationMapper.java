package hr.java.corporatetravelriskassessmenttool.mapper;

import hr.java.corporatetravelriskassessmenttool.model.Destination;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class DestinationMapper {
    private DestinationMapper() {}
    public static Destination map(ResultSet rs) throws SQLException {
        return new Destination.Builder()
                .setId(rs.getLong("id"))
                .setCity(rs.getString("city"))
                .setCountry(rs.getString("country"))
                .setRisks(new HashSet<>())
                .createDestination();
    }
}
