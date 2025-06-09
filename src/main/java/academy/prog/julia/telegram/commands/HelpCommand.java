package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.CreateHelpState;
import academy.prog.julia.telegram.states.PrintTextState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `HelpCommand` class provides functionality for listing all available commands.
 * It extends the `Command` class, allowing for the execution of a command that displays a list of all commands.
 */
@Component
public class HelpCommand extends Command {

    private static final String ATTRIBUTE_NAME = "help_text";

    /**
     * Returns the name of the command used to list all commands.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/help";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "List all commands"
     */
    @Override
    public String getDescription() {
        return "List all commands";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * The command first creates the help text and then prints it to the user.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new CreateHelpState(ATTRIBUTE_NAME),
                new PrintTextState(ATTRIBUTE_NAME)
        );
    }

}
