package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.telegram.validators.StateInputValidationException;

/**
 * Base state class for handling text input from the user.
 * This class extends CommandState and is responsible for prompting the user to enter text and storing
 * the input value in the provided attribute.
 */
public class EnterTextState extends CommandState {

    protected final String prompt;
    protected final String inputAttributeName;

    /**
     * Constructor to initialize the state with a prompt message and input attribute name.
     *
     * @param message the message to prompt the user to enter text
     * @param inputAttributeName the name of the attribute where the input text will be stored
     */
    public EnterTextState(
            String message,
            String inputAttributeName
    ) {
        super(true);
        this.prompt = message;
        this.inputAttributeName = inputAttributeName;
    }

    /**
     * Sends a message to the user to prompt them to enter text.
     * This method is called when the state is entered.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        context.sendMessage(prompt);
    }

    /**
     * Handles the user input by storing the input text in the context.
     * The input text is associated with the specified attribute name.
     *
     * @param context the execution context containing state and service information
     * @throws StateInputValidationException if any validation errors occur during input handling
     */
    @Override
    public void handleInput(StateExecutionContext context) throws StateInputValidationException {
        super.handleInput(context);
        context.putAttribute(inputAttributeName, context.getMessage());
    }

}
