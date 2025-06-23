package hr.java.corporatetravelriskassessmenttool.exception;

/**
 * Thrown to indicate a problem during database configuration.
 * <p>
 *     This exception typically signals issues such as:
 *     <ul>
 *         <li>Missing or malformed database properties</li>
 *         <li>Invalid JDBC URL, username or password</li>
 *         <li>Failed attempts to load configuration files</li>
 *     </ul>
 * </p>
 */
public class DatabaseConfigurationException extends RuntimeException {
    /**
     * Constructs a new {@code DatabaseConfigurationException} with {@code null} as its detail message.
     */
    public DatabaseConfigurationException() {
    }

    /**
     * @param message the detail message explaining the reason for the exception
     */
    public DatabaseConfigurationException(String message) {
        super(message);
    }

    /**
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public DatabaseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause the cause of this exception
     */
    public DatabaseConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message the detail message
     * @param cause the cause
     * @param enableSuppression whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public DatabaseConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
