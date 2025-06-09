package academy.prog.julia.telegram.commands;

import academy.prog.julia.repos.UserRepository;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.ListUsersState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `ListUsersCommand` class provides functionality to list all users.
 * It extends the `AdminOrManagerCommand` class, allowing access to users with ADMIN or MANAGER roles.
 */
@Component
public class ListUsersCommand extends AdminOrManagerCommand {

    private final UserRepository userRepository;

    /**
     * Constructs a `ListUsersCommand` instance with the specified `UserRepository`.
     *
     * @param userRepository the repository used to access user data.
     */
    public ListUsersCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the name of the command used to list all users.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/users_list";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "show list of all users"
     */
    @Override
    public String getDescription() {
        return "show list of all users";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command includes a state to list all users, using the provided `UserRepository`.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(new ListUsersState(false, userRepository));
    }

}
