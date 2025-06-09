package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.model.UserRole;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;

import java.util.List;

/**
 * State class for blocking a user.
 * This class extends CommandState and handles the logic for blocking a user based on the provided input data.
 */
public class BlockState extends CommandState {

    private final String blockData;

    /**
     * Constructor for initializing the BlockState with the provided block data.
     *
     * @param blockData the data used to identify the user to be blocked (phone number, email, or user ID)
     */
    public BlockState(String blockData) {
        super(false);
        this.blockData = blockData;
    }

    /**
     * Handles entering this state, performing actions to block a user based on the input data.
     * This method overrides the enter method from CommandState to provide specific functionality for this state.
     *
     * @param context the execution context containing services and state information
     */
    @Override
    public void enter(StateExecutionContext context) {
        // Retrieve the input data (phone number, email, or user ID) from the context
        String input = context.getAttributeAsString(blockData);

        if (input == null || input.isEmpty()) {
            context.sendMessage("Incorrect input: phone number, email, or user ID is required!");
            return;
        }

        UserService userService = context.getUserService();
        User user = null;

        // Attempt to find the user based on the input data
        try {
            if (input.matches("\\+380\\d{9}")) {
                List<User> usersByPhone = userService.findByPhone(input);
                System.out.println(usersByPhone);

                if (!usersByPhone.isEmpty()) {
                    user = usersByPhone.get(0);
                }
            } else {
                Long userId = Long.parseLong(input);
                user = userService.findById(userId).orElse(null);
                System.out.println(input);
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

        if (user.getRole().equals(UserRole.ADMIN)) {
            context.sendMessage("You can't block an administrator with Id " + user.getId());
            return;
        }

        userService.blockUser(user.getId());
        context.sendMessage("User blocked successfully.");
    }

}
