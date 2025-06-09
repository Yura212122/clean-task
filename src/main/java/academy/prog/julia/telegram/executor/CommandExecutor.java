package academy.prog.julia.telegram.executor;

import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.validators.StateInputValidationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * The CommandExecutor class is responsible for executing commands in a Telegram bot,
 * managing the state of active commands, and handling user inputs.
 */
@Component
public class CommandExecutor {

    private static final String EXIT_COMMAND = "/exit";
    private static final long CANCEL_COMMAND_TIMEOUT = 20; // 20 min

    private final Map<Long, ActiveCommand> activeCommands = new HashMap<>();

    private final CommandRegistry commandRegistry;

    /**
     * Constructor for CommandExecutor, initializing the command registry.
     *
     * @param commandRegistry the registry of available commands
     */
    public CommandExecutor(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    /**
     * Executes a command based on the current context and user input.
     *
     * @param context the context in which the command is executed, including user input and metadata
     */
    public void execute(StateExecutionContext context) {
        context.setCommandRegistry(commandRegistry);

        var chatId = context.getChatId();
        var activeCommand = getActiveCommand(chatId);
        CommandState state;

        // Check for "/exit" command to force termination of active command
        if (EXIT_COMMAND.equals(context.getMessage())) {
            if (activeCommand == null) {
                context.sendMessage(chatId, "There are no commands that need exit");
                return;
            } else {
                removeActiveCommand(chatId);
                context.sendMessage(
                        chatId,
                        "The \"" + activeCommand.getCommand().getName() +
                                "\" command execution was forced to finished"
                );
                return;
            }
        }

        // If there is no active command, initializes a new one
        if (activeCommand == null) {
            var command = commandRegistry.getByName(context.getCommand());

            if (command == null) {
                context.sendMessage(chatId, "Unknown command");
                return;
            }

            var allowedRoles = command.getAllowedRoles();
            if (!allowedRoles.isEmpty() && !allowedRoles.contains(context.getUser().getRole())) {
                context.sendMessage(chatId, "Command not allowed for this user role");
                return;
            }

            activeCommand = new ActiveCommand(command);
            putActiveCommand(context.getChatId(), activeCommand);

            context.setActiveCommand(activeCommand);

            state = command.getState(0);
            state.enter(context);
        } else {
            context.setActiveCommand(activeCommand);

            state = activeCommand.getCurrentState();
            try {
                state.handleInput(context);
            } catch (StateInputValidationException ex) {
                context.sendMessage(chatId, String.format("Wrong input: %s", ex.getMessage()));
                return;
            }

            state = activeCommand.nextState();
            if (state != null) state.enter(context);
        }

        // Transition to subsequent states until user input is required
        while (state != null && !state.isInputNeeded()) {
            state = activeCommand.nextState();
            if (state != null) state.enter(context);
        };

        // If all states are fulfilled, end the active command
        if (state == null) {
            context.sendMessage(
                    chatId,
                    "The \"" + activeCommand.getCommand().getName() +
                    "\" command execution finished successfully!"
            );
            removeActiveCommand(chatId);
        }
    }

    /**
     * Scheduled task to clean inactive commands that have timed out after a defined period.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 120000)
    public void cleanInactiveCommands() {
        var now = LocalDateTime.now();

        synchronized (activeCommands) {
            for (var chatId : activeCommands.keySet()) {
                var command = activeCommands.get(chatId);

                if (command != null) {
                    var minutes = ChronoUnit.MINUTES.between(now, command.getLastAction());

                    // Delete commands if inactive for more than CANCEL_COMMAND_TIMEOUT minutes
                    if (minutes > CANCEL_COMMAND_TIMEOUT) {
                        activeCommands.remove(chatId);
                    }
                }
            }
        }
    }

    /**
     * Removes the active command for a given chatId.
     *
     * @param chatId the chat ID for which to remove the active command
     * @return true if a command was removed, false otherwise
     */
    private boolean removeActiveCommand(Long chatId) {
        synchronized (activeCommands) {
            return activeCommands.remove(chatId) != null;
        }
    }

    /**
     * Puts a new active command for the given chatId.
     *
     * @param chatId the chat ID for which to set the active command
     * @param activeCommand the active command to be set
     */
    private void putActiveCommand(Long chatId, ActiveCommand activeCommand) {
        synchronized (activeCommands) {
            activeCommands.put(chatId, activeCommand);
        }
    }

    /**
     * Gets the active command for a given chatId.
     *
     * @param chatId the chat ID to retrieve the active command for
     * @return the active command associated with the chatId
     */
    private ActiveCommand getActiveCommand(Long chatId) {
        synchronized (activeCommands) {
            return activeCommands.get(chatId);
        }
    }

}
