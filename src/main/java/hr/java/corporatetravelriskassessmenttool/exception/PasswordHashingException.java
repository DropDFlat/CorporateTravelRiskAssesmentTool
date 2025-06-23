package hr.java.corporatetravelriskassessmenttool.exception;
/**
 * {@code PasswordHashingException} is a runtime exception thrown when an error occurs
 * during the password hashing process.
 * <p>
 * This may happen due to unsupported encoding, algorithm issues, or problems with cryptographic libraries.
 * It is an unchecked exception, indicating a programming error or misconfiguration.
 * </p>
 */
public class PasswordHashingException extends RuntimeException {
    /**
     * @param message the detail message
     */
    public PasswordHashingException(String message) {
        super(message);
    }
    /**
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public PasswordHashingException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause the cause of the exception
     */
    public PasswordHashingException(Throwable cause) {
        super(cause);
    }
    /**
     * @param message             the detail message
     * @param cause               the cause
     * @param enableSuppression  whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public PasswordHashingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    /**
     * Constructs a new {@code PasswordHashingException} with {@code null} as its detail message.
     */
    public PasswordHashingException() {
    }
}
