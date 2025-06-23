package hr.java.corporatetravelriskassessmenttool.controller;

import hr.java.corporatetravelriskassessmenttool.model.User;

/**
 * Interface for controllers that require awareness of the currently logged-in user.
 */
public interface RoleAware {
    /**
     * Sets the logged-in user.
     * @param user the user associated with this session
     */
    void setUser(User user);
}
