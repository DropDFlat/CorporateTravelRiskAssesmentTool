package hr.java.corporatetravelriskassessmenttool.exception;
/**
 * Runtime exception indicating an error occurred during repository operations,
 * often acting as a wrapper for lower-level exceptions, such as database or IO errors, and validation exception
 * such as {@link InvalidTripDataException}.
 * This exception is typically thrown when the underlying data access layer
 * encounters issues, providing a consistent abstraction for repository failures.
 */
public class RepositoryAccessException extends RuntimeException {
    /**
     * @param message the detail message
     */
    public RepositoryAccessException(String message) {
        super(message);
    }
    /**
     * @param message the detail message
     * @param cause   the cause of the exception
     */
  public RepositoryAccessException(String message, Throwable cause) {
    super(message, cause);
  }
    /**
     * @param cause the cause of the exception
     */
  public RepositoryAccessException(Throwable cause) {
    super(cause);
  }
    /**
     * @param message             the detail message
     * @param cause               the cause
     * @param enableSuppression   whether suppression is enabled
     * @param writableStackTrace  whether the stack trace should be writable
     */
  public RepositoryAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
    /**
     * Constructs a new {@code RepositoryAccessException} with {@code null} as its detail message.
     */
  public RepositoryAccessException() {
  }
}
