package hr.java.corporatetravelriskassessmenttool.exception;
/**
 * Exception thrown to indicate an error during user registration.
 * This exception may be used to signal issues such as invalid user data,
 * duplicate usernames, or other registration-related failures.
 */
public class RegistrationException extends Exception {
    /**
     * @param message the detail message
     */
    public RegistrationException(String message) {
        super(message);
    }
    /**
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause the cause of the exception
     */
    public RegistrationException(Throwable cause) {
        super(cause);
    }
    /**
     * @param message             the detail message
     * @param cause               the cause
     * @param enableSuppression   whether suppression is enabled
     * @param writableStackTrace  whether the stack trace should be writable
     */
    public RegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * Constructs a new {@code RegistrationException} with {@code null} as its detail message.
     */
    public RegistrationException() {
    }
}
