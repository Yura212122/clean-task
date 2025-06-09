package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;

/**
 * State class responsible for creating a help message.
 * This class extends CommandState and compiles a list of commands and their descriptions into a help message
 * based on the roles allowed to execute each command.
 */
public class CreateHelpState extends CommandState {

    protected final String resultAttributeName;

    /**
     * Constructor to initialize the CreateHelpState with the attribute name
     * where the result will be stored.
     *
     * @param resultAttributeName the name of the attribute where the help message will be stored
     */
    public CreateHelpState(String resultAttributeName) {
        super(false);
        this.resultAttributeName = resultAttributeName;
    }

    /**
     * Handles the logic for entering this state and generating the help message.
     * Retrieves all commands, filters them based on allowed roles, and builds
     * a help message listing command names and their descriptions.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var sb = new StringBuilder();
        var commands = context.getCommandRegistry().getAll();

        for (var command : commands) {
            var allowedRoles = command.getAllowedRoles();

            if (allowedRoles.size() == 0 || allowedRoles.contains(context.getUser().getRole())) {
                sb.append(command.getName())
                        .append(": ")
                        .append(command.getDescription())
                        .append("\r\n")
                ;
            }
        }

        context.putAttribute(resultAttributeName, sb.toString());
    }

}
