package hr.java.corporatetravelriskassessmenttool.exception;
/**
 * Thrown to indicate that a repository returned no data when some was expected.
 * This exception is typically used in cases where operations such as findById
 * return empty results, signaling that the expected data is missing from the repository.
 */
public class EmptyRepositoryException extends RuntimeException {

    /**
     * Constructs a new {@code EmptyRepositoryException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public EmptyRepositoryException(String message) {
        super(message);
    }
    /**
     * Constructs a new {@code EmptyRepositoryException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public EmptyRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause the cause of this exception
     */
    public EmptyRepositoryException(Throwable cause) {
        super(cause);
    }
    /**
     * @param message            the detail message
     * @param cause              the cause
     * @param enableSuppression whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public EmptyRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * Constructs a new {@code EmptyRepositoryException} with {@code null} as its detail message.
     */
    public EmptyRepositoryException() {
    }
}
