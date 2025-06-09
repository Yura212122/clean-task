package academy.prog.julia.json_requests;

/**
 * Represents a request for user login containing email and password.
 *
 * This class is used to encapsulate the login credentials of a user, which includes:
 *     Email address
 *     Password
 *
 */
public class LoginRequest {

    private  String email;
    private  String password;

    /**
     * Constructs a new {@code LoginRequest} with the specified email and password.
     *
     * @param email the email address of the user
     * @param password the password of the user
     */
    public LoginRequest(
            String email,
            String password
    ) {
        this.email = email;
        this.password = password;
    }

    /**
     * Default constructor for {@code LoginRequest}.
     *
     * This constructor is used for creating an instance of {@code LoginRequest}
     * without initializing the email and password fields.
     *
     */
    public LoginRequest() {}

    /**
     * Gets the email address of the user.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the new password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
