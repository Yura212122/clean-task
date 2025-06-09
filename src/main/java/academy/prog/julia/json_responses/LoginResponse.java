package academy.prog.julia.json_responses;

/**
 * Represents the response returned after a successful login attempt.
 *
 * It includes a message, principal object (user details), Telegram integration URL, and session ID.
 *
 */
public class LoginResponse {

    private String message;
    private Object principal;
    private String telegramUrl;
    private String jSessionId;

    /**
     * Default constructor for LoginResponse.
     *
     * This constructor creates an empty LoginResponse object.
     *
     */
    public LoginResponse() {}

    /**
     * Constructs a {@link LoginResponse} with the specified message, principal, and Telegram URL.
     *
     * @param message a string representing the login status message
     * @param principal an object containing the authenticated user's details
     * @param telegramUrl a URL for Telegram account connection (optional)
     */
    public LoginResponse(
            String message,
            Object principal,
            String telegramUrl
    ) {
        this.message = message;
        this.principal = principal;
        this.telegramUrl = telegramUrl;
    }

    /**
     * Retrieves the login message.
     *
     * @return the login message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the login message.
     *
     * @param message containing the login message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the authenticated user's principal object.
     *
     * @return the principal object (user details)
     */
    public Object getPrincipal() {
        return principal;
    }

    /**
     * Sets the authenticated user's principal object.
     *
     * @param principal an object containing the user details
     */
    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    /**
     * Retrieves the Telegram URL for linking the user's account.
     *
     * @return the Telegram URL
     */
    public String getTelegramUrl() {
        return telegramUrl;
    }

    /**
     * Sets the Telegram URL for account connection.
     *
     * @param telegramUrl representing the Telegram connection URL
     */
    public void setTelegramUrl(String telegramUrl) {
        this.telegramUrl = telegramUrl;
    }

    /**
     * Retrieves the JSESSIONID for the session.
     *
     * @return the session ID
     */
    public String getJSessionId() {
        return jSessionId;
    }

    /**
     * Sets the JSESSIONID for the session.
     *
     * @param jSessionId a {@link String} representing the session ID
     */
    public void setJSessionId(String jSessionId) {
        this.jSessionId = jSessionId;
    }

}