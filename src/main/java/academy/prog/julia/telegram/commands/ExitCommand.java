package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.CommandState;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * The `ExitCommand` class provides functionality to exit the current command execution.
 * It extends `AdminOrManagerCommand`, indicating that it is available for users with
 * admin or manager roles.
 */
@Component
public class ExitCommand extends AdminOrManagerCommand {

    /**
     * Returns the command name for exiting the current command execution.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/exit";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Exit the current command execution"
     */
    @Override
    public String getDescription() {
        return "Exit the current command execution";
    }

    /**
     * Returns an empty list of states for this command.
     * Since this command is meant to exit the current execution, no additional states are required.
     *
     * @return an empty list of `CommandState` objects.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of();
    }

}
