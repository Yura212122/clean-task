package academy.prog.julia.telegram.commands;

import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterCredentialsFileState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `OverwriteGoogleCredentialsCommand` class allows administrators to
 * overwrite Google credentials. It extends the `AdminCommand` class,
 * restricting its usage to users with ADMIN roles.
 */
@Component
public class OverwriteGoogleCredentialsCommand extends AdminCommand {

    /**
     * The bot token used for authentication when overwriting Google credentials.
     * It is injected from application(s) properties.
     */
    @Value("${bot.token}")
    private String botToken;

    private final ApiConfigProperties apiConfigProperties;

    /**
     * Constructor for the OverwriteGoogleCredentialsCommand class.
     * This constructor injects the ApiConfigProperties object, which contains
     * configuration properties required for the command's execution.
     *
     * @param apiConfigProperties the object holding API configuration properties
     */
    public OverwriteGoogleCredentialsCommand(ApiConfigProperties apiConfigProperties) {
        this.apiConfigProperties = apiConfigProperties;
    }

    /**
     * Returns the name of the command for overwriting Google credentials.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/google_credentials";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Overwrite Google credentials."
     */
    @Override
    public String getDescription() {
        return "Overwrite Google credentials.";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command includes a state to enter the path to the credentials file and use the bot token.
     *
     * @return a list of `CommandState` objects representing the execution flow.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterCredentialsFileState("credentialsFilePath", botToken, apiConfigProperties)
        );
    }

}

