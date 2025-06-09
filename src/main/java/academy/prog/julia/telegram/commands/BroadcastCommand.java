package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.BroadcastGroupState;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `BroadcastCommand` class allows administrators and managers to send broadcast messages to
 * specific user groups.
 * It extends the `AdminOrManagerCommand` class, ensuring only users with admin or manager
 * roles can execute this command.
 * The command consists of multiple states, guiding the user through entering the recipient group and
 * the message to be broadcast.
 */
@Component
public class BroadcastCommand extends AdminOrManagerCommand {

    /**
     * Returns the name of the command, used to trigger the broadcast functionality.
     *
     * @return the command name, "/broadcast".
     */
    @Override
    public String getName() {
        return "/broadcast";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "broadcast message to users"
     */
    @Override
    public String getDescription() {
        return "broadcast message to users";
    }

    /**
     * Defines the sequence of states for this command.
     * The user is prompted to enter the name of the recipient group and the message to broadcast.
     * These inputs are processed by `BroadcastGroupState` to send the message.
     *
     * @return a list of `CommandState` objects representing the steps in the broadcast process.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter recipient group name:", "recipient_group_name"),
                new EnterTextState("Enter message to broadcast:", "broadcast_message"),
                new BroadcastGroupState("recipient_group_name", "broadcast_message")
        );
    }

}
