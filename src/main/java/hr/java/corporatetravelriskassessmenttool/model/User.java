package hr.java.corporatetravelriskassessmenttool.model;
/**
 * Represents a user of the system with a username, password, and role.
 *
 * @param username the unique username of the user
 * @param password the user's password (should be stored securely in a real system)
 * @param role     the role assigned to the user (e.g., admin, user)
 */
public record User(String username, String password, String role) {
}
