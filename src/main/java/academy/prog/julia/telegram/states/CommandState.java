package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.telegram.validators.CommandStateInputValidator;
import academy.prog.julia.telegram.validators.StateInputValidationException;

import java.util.List;

/**
 * Abstract class representing a state in the command handling process.
 * This class is responsible for managing state transitions and input validation.
 */
public abstract class CommandState {

    // Flag indicating whether input is needed for this state
    private final boolean inputNeeded;

    /**
     * Constructor to initialize the CommandState with a flag indicating if input is needed.
     *
     * @param inputNeeded a boolean flag indicating whether input is needed for this state
     */
    public CommandState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    /**
     * Returns whether input is needed for this state.
     *
     * @return true if input is needed, false otherwise
     */
    public boolean isInputNeeded() {
        return inputNeeded;
    }

    /**
     * Provides a list of input validators for this state.
     * This can be overridden by subclasses to provide specific validators.
     *
     * @return a list of input validators (empty list by default)
     */
    public List<CommandStateInputValidator> getValidators() {
        return List.of();
    }

    /**
     * Validates the input using the list of validators provided by getValidators().
     *
     * @param input the input to be validated
     * @throws StateInputValidationException if validation fails
     */
    protected void validateInput(String input) throws StateInputValidationException {
        var validators = getValidators();

        if (validators == null || validators.size() == 0) {
            return;
        }

        for (var validator : validators) {
            validator.validate(input);
        }
    }

    /**
     * Method called when entering this state. Default implementation does nothing.
     * Subclasses can override this method to provide specific behavior upon entering the state.
     *
     * @param context the execution context containing state and service information
     */
    public void enter(StateExecutionContext context) {
        // do nothing be default
    }

    /**
     * Handles input received in this state. Validates the input before processing.
     *
     * @param context the execution context containing state and service information
     * @throws StateInputValidationException if validation fails
     */
    public void handleInput(StateExecutionContext context) throws StateInputValidationException {
        validateInput(context.getMessage());
    }

    /**
     * Handles updates for this state. Default implementation does nothing.
     * Subclasses can override this method to provide specific behavior for updates.
     *
     * @param context the execution context containing state and service information
     */
    public void handleUpdate(StateExecutionContext context) {
        // Default implementation (can be overridden)
    }

}
