package academy.prog.julia.telegram.commands;

import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.WebLinkState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `WebCommand` class is responsible for generating a link to log in to a user account.
 * It extends the `Command` class, which provides the base functionality for commands.
 */
@Component
public class WebCommand extends Command {

    private final ApiConfigProperties apiConfigProperties;

    /**
     * Constructor for the WebCommand class.
     * This constructor injects the ApiConfigProperties object, which contains
     * configuration properties required for the command's execution.
     *
     * @param apiConfigProperties the object holding API configuration properties
     */
    public WebCommand(ApiConfigProperties apiConfigProperties) {
        this.apiConfigProperties = apiConfigProperties;
    }

    /**
     * Returns the name of the command used to get a login link for the user's account.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/web";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Get a link to log in to your account"
     */
    @Override
    public String getDescription() {
        return "Get a link to log in to your account";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command involves:
     * 1. Generating a web link that users can use to log in to their accounts.
     *
     * @return a list of `CommandState` objects representing the steps of the command.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(new WebLinkState(false, apiConfigProperties));
    }

}