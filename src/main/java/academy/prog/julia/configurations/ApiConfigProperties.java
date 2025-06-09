package academy.prog.julia.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that holds the properties related to API configurations.
 * It uses Spring's @Value annotation to inject values from application.properties or
 * application.yml into fields. These values include URLs for Telegram API, Google API,
 * and file paths used in the application.
 */
@Configuration
public class ApiConfigProperties {

    @Value("${telegram.api.url}")
    public String telegramApiUrlPartOne;

    @Value("${telegram.api.download.file.part.two.url}")
    public String telegramApiUrlPartTwo;

    @Value("${telegram.path.from.package.google.credentials}")
    public String pathUpCredentials;

    @Value("${file.via.telegram.url}")
    public String downloadViaTelegram;

    @Value("${google.security.account.credentials.part.one.url}")
    public String googleSecurityAccountPartOneUrl;

    @Value("${google.security.account.credentials.part.two.url}")
    public String googleSecurityAccountPartTwoUrl;

    @Value("${allowed_cross_origin}")
    public String frontendUrl;

    /**
     * Returns the Telegram API URL part 1.
     *
     * @return the Telegram API URL part 1
     */
    public String getTelegramApiUrlPartOne() {
        return telegramApiUrlPartOne;
    }

    /**
     * Returns the Telegram API URL part 2.
     *
     * @return the Telegram API URL part 2
     */
    public String getTelegramApiUrlPartTwo() {
        return telegramApiUrlPartTwo;
    }

    /**
     * Returns the path where Google credentials are stored.
     *
     * @return the path where Google credentials are stored
     */
    public String getPathUpCredentials() {
        return pathUpCredentials;
    }

    /**
     * Returns the URL used to download files via the Telegram API.
     *
     * @return the URL used to download files via Telegram API
     */
    public String getDownloadViaTelegram() {
        return downloadViaTelegram;
    }

    /**
     * Returns the Google security account credentials URL part 1.
     *
     * @return the Google security account credentials URL part 1
     */
    public String getGoogleSecurityAccountPartOneUrl() {
        return googleSecurityAccountPartOneUrl;
    }

    /**
     * Returns the Google security account credentials URL part 2.
     *
     * @return the Google security account credentials URL part 2
     */
    public String getGoogleSecurityAccountPartTwoUrl() {
        return googleSecurityAccountPartTwoUrl;
    }

    /**
     * Returns the frontend side url.
     *
     * @return the frontend side url
     */
    public String getFrontendUrl() {
        return frontendUrl;
    }

}

