package academy.prog.julia.telegram.validators;

/**
 * Custom exception class for handling validation errors during state input processing.
 * This exception is thrown when an invalid input is provided to a validator.
 */
public class StateInputValidationException extends Exception {

    /**
     * Constructs a new StateInputValidationException with the specified detail message.
     *
     * @param message the detail message that explains the cause of the exception.
     */
    public StateInputValidationException(String message) {
        super(message);
    }

}
