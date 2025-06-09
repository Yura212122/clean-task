package academy.prog.julia.telegram.executor;

import academy.prog.julia.telegram.commands.Command;
import academy.prog.julia.telegram.states.CommandState;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an active command instance, tracking the current state of a command's execution
 * and storing attributes related to its execution context.
 */
public class ActiveCommand {

    private final Command command;
    private final Map<String, Object> attributes = new HashMap<>();

    private int stateIndex;
    private LocalDateTime lastAction;

    /**
     * Creates an ActiveCommand with a specific state index.
     *
     * @param command   the command to track
     * @param stateIndex the initial state index
     */
    public ActiveCommand(
            Command command,
            int stateIndex
    ) {
        this.command = command;
        this.stateIndex = stateIndex;
        updateLastAction();
    }

    /**
     * Creates an ActiveCommand with a default initial state (index 0).
     *
     * @param command the command to track
     */
    public ActiveCommand(Command command) {
        this(command, 0);
    }

    /**
     * Gets the next state of the command without updating the state index.
     *
     * @return the next CommandState in the sequence
     */
    public CommandState getNextState() {
        updateLastAction();
        return command.getState(stateIndex + 1);
    }

    /**
     * Advances to the next state of the command by incrementing the state index.
     *
     * @return the next CommandState after incrementing the state index
     */
    public CommandState nextState() {
        updateLastAction();
        return command.getState(++stateIndex);
    }

    /**
     * Gets the current state of the command.
     *
     * @return the current CommandState based on the state index
     */
    public CommandState getCurrentState() {
        updateLastAction();
        return command.getState(stateIndex);
    }

    /**
     * Updates the last action time to the current time.
     */
    public void updateLastAction() {
        lastAction = LocalDateTime.now();
    }

    /**
     * Gets the time of the last action performed on this command.
     *
     * @return the LocalDateTime of the last action
     */
    public LocalDateTime getLastAction() {
        return lastAction;
    }

    /**
     * Gets the command associated with this ActiveCommand.
     *
     * @return the Command being tracked
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Gets the current state index.
     *
     * @return the index of the current state
     */
    public int getStateIndex() {
        return stateIndex;
    }

    /**
     * Gets the attributes map for this command's execution context.
     *
     * @return a map of attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
