package academy.prog.julia.exceptions;

/**
 * Custom exception class to represent a scenario where a user is not found.
 *
 * This exception is thrown when the system cannot find a user that is expected
 * to be present. It extends {@link RuntimeException}, indicating that this is an
 * unchecked exception, meaning it does not need to be declared in the method signature
 * or explicitly handled in a try-catch block.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message the detail message to be saved with the exception
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
