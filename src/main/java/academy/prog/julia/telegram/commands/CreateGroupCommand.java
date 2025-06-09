package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.CreateGroupState;
import academy.prog.julia.telegram.states.EnterTextState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `CreateGroupCommand` class handles the creation of a new user group.
 * It extends `AdminOrManagerCommand` and provides the functionality for
 * creating a group by interacting with the bot.
 */
@Component
public class CreateGroupCommand extends AdminOrManagerCommand {

    private static final String ATTRIBUTE_NAME = "group_name";

    /**
     * Returns the command name for creating a new group.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/group_new";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "create new group of users"
     */
    @Override
    public String getDescription() {
        return "create new group of users";
    }

    /**
     * Returns the list of states for this command.
     * States define the steps required to execute the command.
     *
     * @return a list of `CommandState` objects representing the states.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter new group name:", ATTRIBUTE_NAME),
                new CreateGroupState(ATTRIBUTE_NAME)
        );
    }

}
