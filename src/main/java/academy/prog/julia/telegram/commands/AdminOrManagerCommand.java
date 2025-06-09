package academy.prog.julia.telegram.commands;

import academy.prog.julia.model.UserRole;

import java.util.Set;

/**
 * The `AdminOrManagerCommand` class is an abstract class that extends the base `Command` class.
 * It defines a command type that can be executed by users with either the `ADMIN` or `MANAGER` role.
 *
 * This class ensures that both administrative users and managers can execute commands that extend
 * from `AdminOrManagerCommand`.
 */
public abstract class AdminOrManagerCommand extends Command {

    /**
     * Specifies the user roles that are allowed to execute the command.
     *
     * @return a set containing both the `ADMIN` and `MANAGER` roles, allowing access to these commands
     *         for users with either administrative or managerial privileges.
     */
    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.ADMIN, UserRole.MANAGER);
    }

}
