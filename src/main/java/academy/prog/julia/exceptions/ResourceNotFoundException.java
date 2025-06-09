package academy.prog.julia.exceptions;

/**
 * Custom exception class to represent a resource not found scenario.
 *
 * This exception is thrown when a requested resource (such as an entity in the database)
 * cannot be found. It extends {@link RuntimeException}, allowing it to be used in cases
 * where the absence of a resource is considered an exceptional condition that should
 * be handled by the application.
 *
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message the detail message to be saved with the exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
