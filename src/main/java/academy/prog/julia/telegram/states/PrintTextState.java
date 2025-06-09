package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;

/**
 * A state that prints a text message stored in the context's attributes.
 * This class extends CommandState.
 */
public class PrintTextState extends CommandState {

    protected final String textAttributeName;

    /**
     * Constructs a PrintTextState with the specified attribute name.
     *
     * @param textAttributeName the name of the attribute containing the text to print
     */
    public PrintTextState(String textAttributeName) {
        super(false);
        this.textAttributeName = textAttributeName;
    }

    /**
     * Handles entering this state by sending the text message stored in the context's attributes.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var text = context.getAttributeAsString(textAttributeName);
        context.sendMessage(text);
    }

}
