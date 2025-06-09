package academy.prog.julia.telegram.states;

import academy.prog.julia.controllers.RegistrationController;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * State class responsible for creating an invitation code based on a user role.
 * This class extends CommandState and validates the role name and the maximum number of participants, then generates
 * an invite-code and sends it as a message.
 */
public class CreateInviteByRoleState extends CommandState {

    private static final Logger LOGGER = LogManager.getLogger(RegistrationController.class);

    private final String roleNameAttribute;
    private final String maxParticipantsCountAttribute;

    /**
     * Constructor to initialize the CreateInviteByRoleState with the attribute names
     * for role name and maximum participants count.
     *
     * @param roleNameAttribute the name of the attribute containing the role name
     * @param maxParticipantsCountAttribute the name of the attribute containing the max participants count
     */
    public CreateInviteByRoleState(
            String roleNameAttribute,
            String maxParticipantsCountAttribute
    ) {
        super(false); // No input needed by default
        this.roleNameAttribute = roleNameAttribute;
        this.maxParticipantsCountAttribute = maxParticipantsCountAttribute;
    }

    /**
     * Handles the logic for creating an invite-code(30 max) based on the specified role and
     * maximum number of participants.
     * Validates the role and maximum participants count, generates the invite code,
     * and sends a message with the invite code.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var userService = context.getUserService();
        var roleName = context.getAttributeAsString(roleNameAttribute);

        if (!isValidRole(roleName)) {
            LOGGER.info("Wrong role name! Please select from ADMIN, TEACHER, MANAGER, MENTOR.");
            context.sendMessage("Wrong role name! Please select from ADMIN, TEACHER, MANAGER, MENTOR.");
            return;
        }

        var maxUsers = context.getAttributeAsInt(maxParticipantsCountAttribute);

        if (maxUsers <= 0) {
            LOGGER.info("Error: Max users must be a positive integer.");
            context.sendMessage("Error: Max users must be a positive integer.");
            return;
        }

        var inviteCode = userService.createInviteCodeByRole(roleName, 30, maxUsers);

        String message = String.format("New invite code for %s(s) is: %s", roleName.toUpperCase(), inviteCode);

        context.sendMessage(message);
    }

    /**
     * Validates the provided role name.
     * Checks if the role name is one of the allowed roles (ADMIN, TEACHER, MANAGER, MENTOR).
     *
     * @param roleName the role name to validate
     * @return true if the role name is valid, false otherwise
     */
    private boolean isValidRole(String roleName) {
        return roleName != null && (
                roleName.equalsIgnoreCase("ADMIN")
                || roleName.equalsIgnoreCase("TEACHER")
                || roleName.equalsIgnoreCase("MANAGER")
                || roleName.equalsIgnoreCase("MENTOR")
        );
    }

}
