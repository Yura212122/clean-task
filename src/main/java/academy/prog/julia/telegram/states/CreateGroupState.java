package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;

/**
 * State class responsible for creating a new group.
 * This class handles the process of validating and creating a group based on the provided name.
 */
public class CreateGroupState extends CommandState {

    private final String groupNameAttribute;

    /**
     * Constructor to initialize the CreateGroupState with the attribute name for the group name.
     *
     * @param groupNameAttribute the attribute name used to retrieve the group name from the context
     */
    public CreateGroupState(String groupNameAttribute) {
        super(false);
        this.groupNameAttribute = groupNameAttribute;
    }

    /**
     * Handles the logic for entering this state and creating a new group.
     * Retrieves the group name from the context, performs validation, and attempts to create the group.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var userService = context.getUserService();

        var groupName = context.getAttributeAsString(groupNameAttribute);
        if (groupName == null || groupName.trim().isEmpty()) {
            context.sendMessage("Wrong group name");
            return;
        }

        // Check if the group name is reserved
        if (groupName.equals("ProgAcademy")) {
            context.sendMessage("This group name is reserved and cannot be used");
            return;
        }

        if (!userService.groupAdd(groupName)) {
            context.sendMessage("Group already exists");
            return;
        }
    }

}
