package academy.prog.julia.telegram.states;

import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * State class for generating and sending a shortened URL to view the list of users.
 * This class extends CommandState and provides a link to the frontend where a list of users can be viewed.
 */
public class ListUsersWebState extends CommandState{

    public static List<String> generatedUrlForUserList = new ArrayList<>();

    private final ApiConfigProperties apiConfigProperties;

    /**
     * Constructor to initialize the state with input requirement and API configuration properties.
     *
     * @param inputNeeded a boolean indicating if input is needed for this state
     * @param apiConfigProperties the configuration properties for API endpoints
     */
    public ListUsersWebState(
            boolean inputNeeded,
            ApiConfigProperties apiConfigProperties
    ) {
        super(inputNeeded);
        this.apiConfigProperties = apiConfigProperties;
    }

    /**
     * Handles the entry into this state by generating a shortened URL to access the user list.
     * Sends a message containing the shortened URL to the user.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        super.enter(context);

        String shortenedUrl = Utils.getShortenedUrl(apiConfigProperties.getFrontendUrl() + "/students");
        String message = "Students list: " + shortenedUrl;

        context.sendMessage(message);
    }

}
