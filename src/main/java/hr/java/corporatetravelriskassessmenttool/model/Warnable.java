package hr.java.corporatetravelriskassessmenttool.model;
/**
 * Interface to indicate that an object can have warning messages associated with it.
 */
public interface Warnable {
    /**
     * Checks if there is currently a warning.
     *
     * @return {@code true} if a warning is set, {@code false} otherwise
     */
    boolean hasWarning();
    /**
     * Gets the current warning message.
     *
     * @return the warning message, or an empty string if no warning is set
     */
    String getWarningMessage();
    /**
     * Sets a warning message and marks that the object has a warning.
     *
     * @param warningMessage the warning message to set
     */
    void setWarningMessage(String warningMessage);
    /**
     * Clears any warning message and marks that the object has no warning.
     */
    void noWarning();
}
