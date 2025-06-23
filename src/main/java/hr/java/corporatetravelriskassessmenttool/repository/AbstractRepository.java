package hr.java.corporatetravelriskassessmenttool.repository;

import hr.java.corporatetravelriskassessmenttool.exception.DatabaseConfigurationException;
import hr.java.corporatetravelriskassessmenttool.model.Entity;
import hr.java.corporatetravelriskassessmenttool.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public abstract class AbstractRepository<T extends Entity> {
    private static final String DATABASE_FILE = "src/main/resources/database.properties";
    private static final Logger log = LoggerFactory.getLogger(AbstractRepository.class);
    protected static Boolean databaseAccessInProgress = false;
    public abstract T findById(Long id);
    public abstract List<T> findAll();
    public abstract void save(T entity, User user);
    public abstract void update(T entity, User user);
    public abstract void delete(Long id, User user);
    protected Connection connectToDb() throws SQLException {
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(DATABASE_FILE)) {
            props.load(fis);
            String url = props.getProperty("url");
            String user = props.getProperty("username");
            String password = props.getProperty("password");
            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            throw new DatabaseConfigurationException("Error loading database properties", e);
        }
    }
    protected void disconnectFromDb(Connection connection) throws SQLException {
        connection.close();
    }
    protected synchronized void waitForDbAccess(){
        while(Boolean.TRUE.equals(databaseAccessInProgress)){
            try{
                wait();
            }catch(InterruptedException e){
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        databaseAccessInProgress = true;
    }
}
