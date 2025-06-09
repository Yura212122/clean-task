package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.repos.UserRepository;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * State class for listing all users in the system.
 * This class extends CommandState, it retrieves and displays a list of all users, excluding the current user.
 */
public class ListUsersState extends CommandState {

    private final UserRepository userRepository;

    /**
     * Constructor to initialize the state with user repository and input requirement.
     *
     * @param inputNeeded a boolean indicating if input is needed for this state
     * @param userRepository the repository used to access user data
     */
    public ListUsersState(
            boolean inputNeeded,
            UserRepository userRepository
    ) {
        super(inputNeeded);
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users from the user repository and sends their details as a message.
     * This method is called when the state is entered.
     *
     * This method is marked as read-only to ensure no unintended modifications occur.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    @Transactional(readOnly = true)
    public void enter(StateExecutionContext context) {
        StringBuilder sb = new StringBuilder("All user list:\r\n");
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            context.sendMessage("There are no users, except you");
            return;
        }

        users.forEach(user -> sb
                .append("Id: ")
                .append(user.getId())
                .append(", ")
                .append("name: ")
                .append(user.getName())
                .append(", ")
                .append("surname: ")
                .append(user.getSurname())
                .append(", ")
                .append("email: ")
                .append(user.getEmail())
                .append(", ")
                .append("phone: ")
                .append(user.getPhone())
                .append("\r\n")
        );

        context.sendMessage(context.getUser().getTelegramChatId(), sb.toString());
    }

}

