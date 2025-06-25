package hr.java.corporatetravelriskassessmenttool.changelog;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a single entry in the changelog.
 * Each entry includes the username, user role, the action performed, a message,
 * and the timestamp of when the action occurred.
 */
public class ChangelogEntry implements Serializable {
    private String username;
    private String role;
    private String action;
    private String message;
    private LocalDateTime timestamp;

    /**
     * Constructs a new ChangelogEntry.
     *
     * @param username the username of the user who performed the action.
     * @param role the role of the user
     * @param action the action performed
     * @param message a detailed message containing information about the entity that was changed
     * @param timestamp the date and time when the change occurred.
     */
    public ChangelogEntry(String username, String role, String action, String message, LocalDateTime timestamp) {
        this.username = username;
        this.role = role;
        this.action = action;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Returns the role that made the change.
     *
     * @return the role that made the change
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role that made the change.
     *
     * @param role the role to be set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the username of the user that made the change.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user that made the change.
     *
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the action that was taken.
     * @return the action that was taken
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action that was taken.
     * @param action the action to be set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns a message regarding the entity that was changed.
     * @return the message describing the entity that was changed.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message describing the entity that was changed.
     * @param message the message describing the change to be set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the timestamp of when the change occurred.
     * @return the time that the change occurred
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the change occurred.
     * @param timestamp the timestamp to be set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
