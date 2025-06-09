package academy.prog.julia.telegram.commands;

import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.ListUsersWebState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `ListUserWebCommand` class provides functionality to generate a link
 * for viewing a list of all students in a web browser.
 * It extends the `AdminOrManagerCommand` class, allowing access to users
 * with ADMIN or MANAGER roles.
 */
@Component
public class ListUserWebCommand extends AdminOrManagerCommand {

    private final ApiConfigProperties apiConfigProperties;

    /**
     * Constructor for the ListUserWebCommand class.
     * This constructor injects the ApiConfigProperties object, which contains
     * configuration properties required for the command's execution.
     *
     * @param apiConfigProperties the object holding API configuration properties
     */
    public ListUserWebCommand(ApiConfigProperties apiConfigProperties) {
        this.apiConfigProperties = apiConfigProperties;
    }

    /**
     * Returns the name of the command used to get the link to list all students in a web browser.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/users_list_web";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Get link to list of all students in browser"
     */
    @Override
    public String getDescription() {
        return "Get link to list of all students in browser";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command includes a state to generate a link for viewing the list of students in a web browser.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(new ListUsersWebState(false, apiConfigProperties));
    }

}

