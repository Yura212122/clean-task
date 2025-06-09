package academy.prog.julia.telegram.validators;

import academy.prog.julia.controllers.RegistrationController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validator class that checks if the provided input is a valid integer.
 * This class extends the abstract CommandStateInputValidator and implements the logic to validate integer inputs.
 */
public class IntegerValidator extends CommandStateInputValidator {

    private static final Logger LOGGER = LogManager.getLogger(RegistrationController.class);

    /**
     * Validates whether the input string can be parsed as an integer.
     * If the input is not a valid integer, a StateInputValidationException is thrown.
     *
     * @param input the string input to validate.
     * @throws StateInputValidationException if the input is not a valid integer.
     */
    @Override
    public void validate(String input) throws StateInputValidationException {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            LOGGER.error("{}: wrong integer value {}", getClass(), input);

            throw new StateInputValidationException(" is not the integer: " + input + ". Please, enter number.");
        }
    }

}
