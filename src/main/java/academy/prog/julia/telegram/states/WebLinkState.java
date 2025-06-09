package academy.prog.julia.telegram.states;

import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.utils.Utils;

/**
 * State class that generates and sends a login web link to the user.
 * This class extends CommandState and uses configuration properties to construct the full login URL.
 */
public class WebLinkState extends CommandState {

    private final ApiConfigProperties apiConfigProperties;

    /**
     * Constructs a new WebLinkState.
     *
     * @param inputNeeded specifies whether the user needs to input data for this state.
     * @param apiConfigProperties injected configuration containing API properties, such as the frontend URL.
     */
    public WebLinkState(
            boolean inputNeeded,
            ApiConfigProperties apiConfigProperties
    ) {
        super(inputNeeded);
        this.apiConfigProperties = apiConfigProperties;
    }

    /**
     * The entry point of this state. When the state is entered, it constructs a login web link,
     * shortens it, and sends it as a message to the user.
     *
     * @param context the context of the current state execution, containing methods for interaction.
     */
    @Override
    public void enter(StateExecutionContext context) {
        super.enter(context);

        String originalUrl = apiConfigProperties.getFrontendUrl() + "/login";
        String shortenedUrl = Utils.getShortenedUrl(originalUrl);
        String message = "Link to log into your account: " + shortenedUrl;

        context.sendMessage(message);
    }

}
