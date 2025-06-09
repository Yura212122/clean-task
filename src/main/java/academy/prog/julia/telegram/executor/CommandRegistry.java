package academy.prog.julia.telegram.executor;

import academy.prog.julia.telegram.commands.Command;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CommandRegistry is a registry that stores and provides access to available commands.
 * It maps command names to their corresponding Command objects.
 */
@Component
public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Constructor that initializes the registry with a set of commands.
     *
     * @param commandsFound a set of Command objects found in the application context
     */
    public CommandRegistry(Set<Command> commandsFound) {
        for (var command : commandsFound) {
            commands.put(command.getName(), command);
        }
    }

    /**
     * Retrieves a command by its name.
     *
     * @param name the name of the command
     * @return the Command object corresponding to the given name, or null if not found
     */
    public Command getByName(String name) {
        return commands.get(name);
    }

    /**
     * Retrieves all registered commands sorted alphabetically by their names.
     *
     * @return an unmodifiable collection of sorted Command objects
     */
    public Collection<Command> getAll() {
        List<Command> sortedCommands = new ArrayList<>(commands.values());
        sortedCommands.sort(Comparator.comparing(Command::getName));

        return Collections.unmodifiableCollection(sortedCommands);
    }

}
