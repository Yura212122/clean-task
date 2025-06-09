package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;

import java.util.List;

/**
 * State for unblocking a user based on phone number, email, or user ID.
 * This class extends CommandState.
 */
public class UnblockState extends CommandState {

    private final String unblockData;

    /**
     * Constructs an UnblockState with the specified attribute name for unblock data.
     *
     * @param unblockData the attribute name containing the unblock data
     */
    public UnblockState(String unblockData) {
        super(false);
        this.unblockData = unblockData;
    }

    /**
     * Handles the entry into this state by unblocking a user based on the provided data.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        String input = context.getAttributeAsString(unblockData);

        if (input == null || input.isEmpty()) {
            context.sendMessage("Incorrect input: phone number, email, or user ID is required!");
            return;
        }

        UserService userService = context.getUserService();
        User user = null;

        try {
            if (input.matches("\\+380\\d{9}")) {
                List<User> usersByPhone = userService.findByPhone(input);

                if (!usersByPhone.isEmpty()) {
                    user = usersByPhone.get(0);
                }
            } else {
                Long userId = Long.parseLong(input);
                user = userService.findById(userId).orElse(null);
            }
        } catch (NumberFormatException e) {
            if (input.contains("@")) {
                user = userService.findByEmail(input);
            }
        }

        if (user == null) {
            context.sendMessage("User not found!");
            return;
        }

        // Check the user's banned status and unblock if necessary
        boolean isBanned = user.getBannedStatus();

        if (!isBanned) {
            context.sendMessage("User is not blocked.");
        } else {
            userService.unblockUser(user.getId());
            context.sendMessage("User unblocked successfully.");
        }
    }

}
