package academy.prog.julia.json_requests;

/**
 * Represents a request for user registration containing personal details and validation fields.
 *
 * This class is used to encapsulate the necessary information for user registration, which includes:
 *     Name and surname
 *     Phone number
 *     Email address
 *     Password and password confirmation
 *     Client invite code
 *     Captcha validation response
 *
 *  Today (9/11/2024) only used in testing (RegistrationControllerTest.java)
 */
public class RegistrationRequest {

    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;
    private String passwordConfirm;
    private String clientInvite;
    private String captchaResponse;

    /**
     * Default constructor for {@code RegistrationRequest}.
     *
     * This constructor is used for creating an instance of {@code RegistrationRequest}
     * without initializing its fields.
     *
     */
    public RegistrationRequest() {}

    /**
     * Constructs a new {@code RegistrationRequest} with the specified details.
     *
     * @param name            the user's first name
     * @param surname         the user's surname
     * @param phone           the user's phone number
     * @param email           the user's email address
     * @param password        the user's password
     * @param passwordConfirm confirmation of the user's password
     * @param clientInvite    the client invite code
     * @param captchaResponse the captcha response for validation
     */
    public RegistrationRequest(
            String name,
            String surname,
            String phone,
            String email,
            String password,
            String passwordConfirm,
            String clientInvite,
            String captchaResponse
    ) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.clientInvite = clientInvite;
        this.captchaResponse = captchaResponse;
    }

    /**
     * Gets the user's first name.
     *
     * @return the user's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's first name.
     *
     * @param name the user's first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's surname.
     *
     * @return the user's surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the user's surname.
     *
     * @param surname the user's surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the user's phone number.
     *
     * @return the user's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone the user's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the user's email address.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the user's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password confirmation.
     *
     * @return the password confirmation
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * Sets the password confirmation.
     *
     * @param passwordConfirm the password confirmation
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * Gets the client invite code.
     *
     * @return the client invite code
     */
    public String getClientInvite() {
        return clientInvite;
    }

    /**
     * Sets the client invite code.
     *
     * @param clientInvite the client invite code
     */
    public void setClientInvite(String clientInvite) {
        this.clientInvite = clientInvite;
    }

    /**
     * Gets the captcha response for validation.
     *
     * @return the captcha response for validation
     */
    public String getCaptchaResponse() {
        return captchaResponse;
    }

    /**
     * Sets the captcha response for validation.
     *
     * @param captchaResponse the captcha response
     */
    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }

}
