package hr.java.corporate_travel_risk_assessment_tool.repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static Boolean DATABASE_ACCESS_IN_PROGRESS = false;
    private Connection connectToDatabase() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileReader("database.properties"));
        return DriverManager.getConnection(
                properties.getProperty("databaseUrl"),
                properties.getProperty("username"),
                properties.getProperty("password"));
    }
}
