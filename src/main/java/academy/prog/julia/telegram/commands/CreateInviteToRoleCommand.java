package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `CreateInviteToRoleCommand` class handles the creation of an invite code
 * for users based on their roles. It extends `AdminCommand` and facilitates
 * the process of generating invite codes for users with specific roles.
 */
@Component
public class CreateInviteToRoleCommand extends AdminCommand{

    /**
     * Returns the command name for creating an invite code for users by role.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/invite_for_user_by_role";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "create invite code to join the user by role"
     */
    @Override
    public String getDescription() {
        return "create invite code to join the user by role";
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
                new EnterTextState("Enter role:\n" +
                        "(ADMIN, TEACHER, MANAGER, MENTOR)", "user_role"),
                new EnterIntegerState("Enter number of participants:", "number_users"),
                new CreateInviteByRoleState("user_role", "number_users")
        );
    }

}
