package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import academy.prog.julia.telegram.states.GroupState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `GroupCommand` class handles the addition and removal of users to and from groups.
 * It extends the `AdminOrManagerCommand` class, allowing only users with ADMIN or MANAGER roles to execute it.
 */
@Component
public class GroupCommand extends AdminOrManagerCommand {

    /**
     * Returns the name of the command used to manage user groups.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/group";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Add/remove users to/from groups"
     */
    @Override
    public String getDescription() {
        return "Add/remove users to/from groups";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * The command first prompts for user data and group information, then processes the addition or removal of users.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter user's ID, add/remove, group: \n" +
                        "- to add, entering \"+\" (example -> 1+group1)\n" +
                        "- to remove, entering \"-\" (example -> 1-group1) ",
                        "data_group"),
                new GroupState("data_group")
        );
    }

}
