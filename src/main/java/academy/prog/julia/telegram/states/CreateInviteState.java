package academy.prog.julia.telegram.states;

import academy.prog.julia.controllers.RegistrationController;
import academy.prog.julia.model.UserRole;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * State class responsible for creating an invitation code for a specified group.
 * This class extends CommandState and validates the group name and the maximum number of participants, then generates
 * an invite-code and sends an invitation URL to the user.
 */
public class CreateInviteState extends CommandState {

    private static final Logger LOGGER = LogManager.getLogger(RegistrationController.class);

    private final String groupNameAttribute;
    private final String maxParticipantsCountAttribute;

    /**
     * Constructor to initialize the CreateInviteState with the attribute names
     * for group name and maximum participants count.
     *
     * @param groupNameAttribute the name of the attribute containing the group name
     * @param maxParticipantsCountAttribute the name of the attribute containing the max participants count
     */
    public CreateInviteState(
            String groupNameAttribute,
            String maxParticipantsCountAttribute
    ) {
        super(false);
        this.groupNameAttribute = groupNameAttribute;
        this.maxParticipantsCountAttribute = maxParticipantsCountAttribute;
    }

    /**
     * Handles the logic for creating an invite code for the specified group and
     * maximum number of participants. Validates the group name and participant count,
     * generates the invite code, and sends the invitation URL.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var userService = context.getUserService();
        var groupService = context.getGroupService();

        // Retrieve group name and maximum participants count from context
        var groupName = context.getAttributeAsString(groupNameAttribute);
        int maxUsers = context.getAttributeAsInt(maxParticipantsCountAttribute);

        if (maxUsers <= 0) {
            LOGGER.info("Error: Max users must be a positive integer.");
            context.sendMessage("Error: Max users must be a positive integer.");
            return;
        }

        List<String> allGroupNames = groupService.findAllNamesOfGroups();

        if (!allGroupNames.contains(groupName)) {
            LOGGER.info("Error: Group with name {} does not exist.", groupName);
            context.sendMessage("Error: Group with name " + groupName + " does not exist.");
            return;
        }

        var inviteCode = userService.createInviteCode(UserRole.STUDENT, 30, maxUsers, groupName);
        var url = String.format(Utils.REGISTER_URL, inviteCode);

        context.sendMessage("Invite URL is: " + url);
    }

}
