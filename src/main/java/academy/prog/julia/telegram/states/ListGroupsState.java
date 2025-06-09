package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;

/**
 * State class for listing all group names.
 * This class extends CommandState and retrieves and displays a list of all groups defined in the system.
 */
public class ListGroupsState extends CommandState {

    /**
     * Constructor to initialize the state.
     * Sets the state to not be finished.
     */
    public ListGroupsState() {
        super(false);
    }

    /**
     * Retrieves all group names from the user service and sends them as a message.
     * This method is called when the state is entered.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var userService = context.getUserService();
        var groupNames = userService.fillAllGroupNames();

        if (groupNames.size() == 0) {
            context.sendMessage("No groups defined");
            return;
        }

        var sb = new StringBuilder();
        for (var name : groupNames) {
            sb.append(name).append("\r\n");
        }

        context.sendMessage(sb.toString());
    }

}
