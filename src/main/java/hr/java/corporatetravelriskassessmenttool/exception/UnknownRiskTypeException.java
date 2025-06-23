package hr.java.corporatetravelriskassessmenttool.exception;
/**
 * Exception thrown when an unrecognized or unsupported risk type
 * is encountered within the Corporate Travel Risk Assessment application.
 * <p>
 * This exception typically indicates a logic error where the application
 * receives or attempts to process a risk type that it does not know how to handle.
 * </p>
 */
public class UnknownRiskTypeException extends RuntimeException {
    /**
     * @param message the detail message explaining the cause of the exception.
     */
    public UnknownRiskTypeException(String message) {
        super(message);
    }
    /**
     * @param message the detail message explaining the cause of the exception.
     * @param cause   the cause (which is saved for later retrieval).
     */
    public UnknownRiskTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause the cause (which is saved for later retrieval).
     */
    public UnknownRiskTypeException(Throwable cause) {
        super(cause);
    }
    /**
     * @param message            the detail message explaining the cause of the exception.
     * @param cause              the cause (which is saved for later retrieval).
     * @param enableSuppression  whether suppression is enabled or disabled.
     * @param writableStackTrace whether the stack trace should be writable.
     */
    public UnknownRiskTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * Constructs a new {@code UnknownRiskTypeException} with {@code null} as its detail message.
     */
    public UnknownRiskTypeException() {
    }
}
