package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `CreateInviteToGroupCommand` class handles the creation of an invite code
 * for users to join a group. It extends `AdminOrManagerCommand` and facilitates
 * the process of generating invite codes for new groups.
 */
@Component
public class CreateInviteToGroupCommand extends AdminOrManagerCommand {

    /**
     * Returns the command name for creating an invite-code for a group.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/invite_group_new";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "create invite code to join the group of users"
     */
    @Override
    public String getDescription() {
        return "create invite code to join the group of users";
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
                new EnterTextState("Enter group name:", "group_name"),
                new EnterIntegerState("Enter maximum number of participants:", "group_max_users"),
                new CreateInviteState("group_name", "group_max_users")
        );
    }

}
