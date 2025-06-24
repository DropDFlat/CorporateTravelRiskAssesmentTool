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
/**
 * Abstract base repository class providing common database connection
 * management and concurrency control for entities extending {@link Entity}.
 *
 * @param <T> the type of entity managed by this repository
 */
public abstract class AbstractRepository<T extends Entity> {
    private static final String DATABASE_FILE = "src/main/resources/database.properties";
    private static final Logger log = LoggerFactory.getLogger(AbstractRepository.class);
    protected static Boolean databaseAccessInProgress = false;
    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the ID of the entity to find
     * @return the entity if found, or throw an exception if not
     */
    public abstract T findById(Long id);
    /**
     * Retrieves all entities of this type.
     *
     * @return a list of all entities
     */
    public abstract List<T> findAll();
    /**
     * Saves a new entity to the database.
     *
     * @param entity the entity to save
     * @param user the user performing the save operation
     */
    public abstract void save(T entity, User user);
    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to update
     * @param user the user performing the update operation
     */
    public abstract void update(T entity, User user);
    /**
     * Deletes an entity from the database by its ID.
     *
     * @param id the ID of the entity to delete
     * @param user the user performing the delete operation
     */
    public abstract void delete(Long id, User user);
    /**
     * Establishes a connection to the database using the configuration
     * provided in the {@code database.properties} file.
     *
     * @return a new {@link Connection} object to the database
     * @throws SQLException if a database access error occurs
     * @throws DatabaseConfigurationException if loading the properties file fails
     */
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
    /**
     * Closes the given database connection.
     *
     * @param connection the {@link Connection} to close
     * @throws SQLException if an error occurs while closing the connection
     */
    protected void disconnectFromDb(Connection connection) throws SQLException {
        connection.close();
    }
    /**
     * Synchronizes database access by waiting if another thread is
     * currently accessing the database. Once access is granted,
     * sets the flag indicating database access is in progress.
     */
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
