package academy.prog.julia.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * BotCredentials is a Spring-managed component that loads bot configuration
 * properties from the `telegram.properties` file. It provides access to the bot's
 * name and token, which are required for connecting to the Telegram API.
 */
@Component
@PropertySource("classpath:telegram.properties")
public class BotCredentials {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    /**
     * Retrieves the bot's name.
     *
     * @return the name of the bot as specified in the properties file.
     */
    public String getBotName() {
        return botName;
    }

    /**
     * Retrieves the bot's API token.
     *
     * @return the token used for authentication with the Telegram API.
     */
    public String getBotToken() {
        return botToken;
    }

}
