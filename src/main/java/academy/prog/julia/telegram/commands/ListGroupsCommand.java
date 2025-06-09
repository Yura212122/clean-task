package academy.prog.julia.telegram.commands;

import academy.prog.julia.model.UserRole;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.ListGroupsState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * The `ListGroupsCommand` class provides functionality to list all available groups.
 * It extends the `Command` class and is intended for users who are allowed to view group information.
 */
@Component
public class ListGroupsCommand extends Command {

    /**
     * Returns the name of the command used to list all groups.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/group_list";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "list all groups"
     */
    @Override
    public String getDescription() {
        return "list all groups";
    }

    /**
     * Specifies the roles that are allowed to execute this command.
     * Excludes the STUDENT role from accessing this command.
     *
     * @return a set of allowed user roles.
     */
    @Override
    public Set<UserRole> getAllowedRoles() {
        return getAllRolesExcept(UserRole.STUDENT);
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * The command includes a state to list all available groups.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(new ListGroupsState());
    }

}
