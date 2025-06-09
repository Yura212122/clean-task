package academy.prog.julia.telegram.commands;

import academy.prog.julia.model.UserRole;
import academy.prog.julia.telegram.states.CommandState;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The `Command` class serves as an abstract base class for various commands in the Telegram bot.
 * It defines the structure and common functionality for all commands, including role-based access
 * control and command states.
 */
public abstract class Command {

    // List of states for this command, representing the steps to execute.
    private List<CommandState> states;

    /**
     * Returns the roles allowed to execute this command.
     * By default, all commands have no restrictions on allowed roles.
     *
     * @return a set of user roles allowed to execute the command.
     */
    public Set<UserRole> getAllowedRoles() {
        return Set.of();
    }

    /**
     * Retrieves the command state at the specified index.
     * Initializes the states list if it is not already initialized.
     *
     * @param index the index of the command state to retrieve
     * @return the `CommandState` object at the specified index, or null if the index is invalid.
     */
    public CommandState getState(int index) {
        if (states == null) states = getStates();

        return (index >= 0 && index < states.size()) ? states.get(index) : null;
    }

    /**
     * Provides a detailed description of the command.
     * This can be overridden by subclasses to give more information about the command.
     *
     * @return a detailed description of the command, or null by default.
     *
     * Not in use now.
     */
    public String getDetailedDescription() {
        return null;
    }

    // Abstract methods to be implemented by subclasses, defining the command's name, description, and states.

    /**
     * Abstract method that returns the command name.
     *
     * @return the command name.
     */
    public abstract String getName();

    /**
     * Abstract method that returns the command description.
     *
     * @return the command description.
     */
    public abstract String getDescription();

    /**
     * Abstract method that returns the list of states associated with this command.
     * Each state represents a step in the execution of the command.
     *
     * @return a list of `CommandState` objects representing the states.
     */
    public abstract List<CommandState> getStates();

    /**
     * Utility method that returns all user roles except the specified ones.
     * This is useful when you want to exclude certain roles from being allowed to execute a command.
     *
     * @param ignore the roles to ignore
     * @return an unmodifiable set of user roles, excluding the specified roles.
     */
    protected Set<UserRole> getAllRolesExcept(UserRole... ignore) {
        var roles = UserRole.values();
        var ignoreRoles = Set.of(ignore);
        var result = new HashSet<UserRole>();

        for (var role : roles) {
            if (!ignoreRoles.contains(role))
                result.add(role);
        }

        return Collections.unmodifiableSet(result);
    }

}
