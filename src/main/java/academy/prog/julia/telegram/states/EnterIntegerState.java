package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.telegram.validators.CommandStateInputValidator;
import academy.prog.julia.telegram.validators.IntegerValidator;
import academy.prog.julia.telegram.validators.StateInputValidationException;

import java.util.List;

/**
 * State class for handling input where an integer value is expected from the user.
 * This state extends EnterTextState and focuses on validating and processing
 * integer inputs provided by the user.
 */
public class EnterIntegerState extends EnterTextState {

    /**
     * Constructor to initialize the state with a message and input attribute name.
     *
     * @param message the message to prompt the user to enter an integer
     * @param inputAttributeName the name of the attribute where the integer will be stored
     */
    public EnterIntegerState(
            String message,
            String inputAttributeName
    ) {
        super(message, inputAttributeName);
    }

    /**
     * Returns a list of validators for this state. In this case, it returns a single
     * validator that checks whether the input can be parsed as an integer.
     *
     * @return a list containing the {@link IntegerValidator} instance
     */
    @Override
    public List<CommandStateInputValidator> getValidators() {
        return List.of(new IntegerValidator());
    }

    /**
     * Handles user input by validating and parsing it as an integer. The input is then
     * stored in the context with the specified attribute name.
     *
     * @param context the execution context containing state and service information
     * @throws StateInputValidationException if the input is invalid or cannot be parsed as an integer
     */
    @Override
    public void handleInput(StateExecutionContext context) throws StateInputValidationException {
        super.handleInput(context);
        context.putAttribute(inputAttributeName, Integer.parseInt(context.getMessage()));
    }

}
