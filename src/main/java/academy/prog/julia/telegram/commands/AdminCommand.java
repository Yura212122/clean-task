package academy.prog.julia.telegram.commands;

import academy.prog.julia.model.UserRole;

import java.util.Set;

/**
 * The `AdminCommand` class is an abstract class that extends the base `Command` class.
 * It defines a specific type of command that is restricted to users with the `ADMIN` role.
 *
 * This class ensures that only users with administrative privileges can execute
 * commands that extend from `AdminCommand`.
 */
public abstract class AdminCommand extends Command {

    /**
     * Specifies the user roles that are allowed to execute the command.
     *
     * @return a set containing only the `ADMIN` role, restricting access to this command
     *         to users with administrative privileges.
     */
    @Override
    public Set<UserRole> getAllowedRoles() {
        return Set.of(UserRole.ADMIN);
    }

}
