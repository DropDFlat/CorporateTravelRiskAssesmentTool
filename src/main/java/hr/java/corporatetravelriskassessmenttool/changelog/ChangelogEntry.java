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
     *
     * @return
     */
    public String getRole() {
        return role;
    }

    /**
     *
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
