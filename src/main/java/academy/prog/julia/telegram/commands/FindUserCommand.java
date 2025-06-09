package academy.prog.julia.telegram.commands;

import academy.prog.julia.model.UserRole;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import academy.prog.julia.telegram.states.FindUserState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * The `FindUserCommand` class is designed to find users by phone number or email.
 * It extends the `Command` class and is accessible to all roles except students.
 */
@Component
public class FindUserCommand extends Command {

    /**
     * Returns the name of the command to be executed for finding users.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/user_find";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Find user by phone or/and e-mail"
     */
    @Override
    public String getDescription() {
        return "Find user by phone or/and e-mail";
    }

    /**
     * Specifies which user roles are allowed to execute this command.
     * The command is available to all roles except the `STUDENT` role.
     *
     * @return a set of allowed user roles.
     */
    @Override
    public Set<UserRole> getAllowedRoles() {
        return getAllRolesExcept(UserRole.STUDENT);
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * The command first prompts for user phone or email, then proceeds to the user search.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter user phone or/and e-mail:\n" +
                        "(example -> 380111111111;aa@gmail.com)", "user_data"),
                new FindUserState("user_data")
        );
    }

}
