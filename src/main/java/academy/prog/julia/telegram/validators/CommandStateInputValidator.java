package academy.prog.julia.telegram.validators;

/**
 * Abstract class that defines the structure for input validation in command states.
 * This class is intended to be extended by specific validators, which will implement their own validation
 * logic for different types of inputs.
 */
public abstract class CommandStateInputValidator {

    /**
     * Abstract method to validate the input provided by the user.
     * Implementations of this method should define the specific validation logic for the input.
     *
     * @param input the string input that needs to be validated.
     * @throws StateInputValidationException if the input does not meet the validation criteria.
     */
    public abstract void validate(String input) throws StateInputValidationException;

}
