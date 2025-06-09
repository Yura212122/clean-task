package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.BlockState;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `BlockCommand` class is a command that allows administrators and managers to block users.
 * It extends the `AdminOrManagerCommand` class, ensuring that only users with admin or manager
 * roles can execute this command.
 * The command operates in multiple states, guiding the user through the process of entering IDs, phone
 * numbers, or emails to block specific users.
 */
@Component
public class BlockCommand extends AdminOrManagerCommand {

    /**
     * Returns the name of the command, which is used to trigger the block functionality.
     *
     * @return the command name, "/block".
     */
    @Override
    public String getName() {
        return "/block";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "block users"
     */
    @Override
    public String getDescription() {
        return  "block users";
    }

    /**
     * Defines the sequence of states that the command goes through when executed.
     * The user will first be prompted to enter a list of user IDs, phone numbers, or emails,
     * which will then be processed by the `BlockState` to complete the blocking operation.
     *
     * @return a list of `CommandState` objects representing the steps of the block process.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter user's IDs and/or phone, mail", "block_list"),
                new BlockState("block_list")
        );
    }

}
