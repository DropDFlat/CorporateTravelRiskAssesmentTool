package hr.java.corporatetravelriskassessmenttool.exception;
/**
 * Exception thrown to indicate that the data associated with a trip is invalid or incomplete.
 * This exception should be used in scenarios where a {@code Trip} entity has missing,
 * inconsistent, or otherwise unacceptable data during validation or processing.
 */
public class InvalidTripDataException extends Exception {
    /**
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidTripDataException(String message) {
        super(message);
    }
    /**
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public InvalidTripDataException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause the cause of this exception
     */
    public InvalidTripDataException(Throwable cause) {
        super(cause);
    }
    /**
     * @param message             the detail message
     * @param cause               the cause
     * @param enableSuppression  whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public InvalidTripDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * Constructs a new {@code InvalidTripDataException} with {@code null} as its detail message.
     */
    public InvalidTripDataException() {
    }
}
