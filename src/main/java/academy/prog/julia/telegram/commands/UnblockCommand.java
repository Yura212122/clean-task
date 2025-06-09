package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import academy.prog.julia.telegram.states.UnblockState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `UnblockCommand` class allows administrators and managers to unblock users.
 * It extends the `AdminOrManagerCommand` class, which restricts access to users
 * with ADMIN or MANAGER roles.
 */
@Component
public class UnblockCommand extends AdminOrManagerCommand {

    /**
     * Returns the name of the command used to unblock users.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/unblock";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "unblock users"
     */
    @Override
    public String getDescription() {
        return  "unblock users";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command involves:
     * 1. Entering the IDs, phone numbers, or email addresses of users to be unblocked.
     * 2. Processing the unblocking of the specified users.
     *
     * @return a list of `CommandState` objects representing the steps of the command.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter user's IDs and/or phone, mail", "unblock_list"),
                new UnblockState("unblock_list")
        );
    }

}
