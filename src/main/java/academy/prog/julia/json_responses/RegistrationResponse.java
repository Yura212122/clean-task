package academy.prog.julia.json_responses;

import academy.prog.julia.helpers.ErrorDescription;

import java.util.List;

/**
 * Represents the response returned after a user registration attempt.
 *
 * It includes fields for registration status, error descriptions, and an optional Telegram URL for account linking.
 *
 */
public class RegistrationResponse {

    private String registration;
    private List<ErrorDescription> description;
    private String telegramURL;

    /**
     * Constructor for {@link RegistrationResponse}.
     *
     * @param registration a string representing the registration status
     * @param description a list of error descriptions if any issues occurred during registration
     * @param telegramURL a URL for Telegram account connection (optional)
     */
    public RegistrationResponse(
            String registration,
            List<ErrorDescription> description,
            String telegramURL
    ) {
        this.registration = registration;
        this.description = description;
        this.telegramURL = telegramURL;
    }

    /**
     * Retrieves the registration status.
     *
     * @return the registration status.
     */
    public String getRegistration() {
        return registration;
    }

    /**
     * Sets the registration status.
     *
     * @param registration representing the registration status
     */
    public void setRegistration(String registration) {
        this.registration = registration;
    }

    /**
     * Retrieves the list of error descriptions.
     *
     * @return a list of {@link ErrorDescription} objects representing errors during registration
     */
    public List<ErrorDescription> getDescription() {
        return description;
    }

    /**
     * Sets the list of error descriptions.
     *
     * @param description a list of {@link ErrorDescription} objects for describing registration errors
     */
    public void setDescription(List<ErrorDescription> description) {
        this.description = description;
    }

    /**
     * Retrieves the Telegram URL for linking the user's account.
     *
     * @return the Telegram URL.
     */
    public String getTelegramURL() {
        return telegramURL;
    }

    /**
     * Sets the Telegram URL for account connection.
     *
     * @param telegramURL a {@link String} representing the Telegram connection URL
     */
    public void setTelegramURL(String telegramURL) {
        this.telegramURL = telegramURL;
    }

}