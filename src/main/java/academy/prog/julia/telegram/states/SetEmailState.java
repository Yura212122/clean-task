package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;

import java.util.*;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * State for setting or updating the email addresses of a user.
 * This class extends CommandState.
 */
public class SetEmailState extends CommandState {

    private static final String SEPARATOR = ",";

    private final String userDataAttribute;
    private final String emailsListAttribute;

    /**
     * Constructs a SetEmailState with specified attributes for user data and email list.
     *
     * @param userDataAttribute the attribute name containing user search data
     * @param emailsListAttribute the attribute name containing a list of emails
     */
    public SetEmailState(
            String userDataAttribute,
            String emailsListAttribute
    ) {
        super(false);
        this.userDataAttribute = userDataAttribute;
        this.emailsListAttribute = emailsListAttribute;
    }

    /**
     * Handles the entry into this state by updating the user's email addresses.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {

        String search = context.getAttributeAsString(userDataAttribute);
        String emails = context.getAttributeAsString(emailsListAttribute);

        if (search == null || emails == null) {
            context.sendMessage("Wrong user search pattern");
            return;
        }

        // Parse the search string to extract phone and email
        var parts = search.split(";");
        String phone = null;
        String email = null;

        if (parts.length == 2) {
            phone = parts[0].trim();
            email = parts[1].trim();
        } else {
            if (search.indexOf('@') > 0) {
                email = search.trim();
            } else {
                phone = search.trim();
            }
        }

        UserService userService = context.getUserService();
        List<User> users = userService.findByPhoneOrEmailLike(phone, email);

        if (users.isEmpty()) {
            context.sendMessage("No users found");
            return;
        }

        User user = users.get(0);
        String[] tempArray = emails.split(SEPARATOR);
        String[] emailsArray = normalizeMail(tempArray);

        for (String oneEmail : emailsArray) {

            User existingUser = userService.findByEmail(oneEmail);
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                context.sendMessage("Email address " + oneEmail + " is already in use by another user.");
                return;
            }

            if (!isEmailValid(oneEmail)) {
                context.sendMessage("Something went wrong with additional emails. Main email was stored");
                return;
            }
        }

        // Update user emails and save changes
        try {
            user.setEmail(emailsArray[0]);
            Set<String> emailSet = new HashSet<>(Arrays.asList(emailsArray));
            user.setEmails(emailSet);
            userService.saveUser(user);

            context.sendMessage("User emails successfully updated");
            context.sendMessage("Now user has the following emails:");
            context.sendMessage(user.getEmails().toString());
        } catch (IllegalArgumentException e) {
            context.sendMessage("Something went wrong");
        }
    }

    /**
     * Validates the format of an email address.
     *
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * Trims whitespace from email addresses in the provided array.
     *
     * @param emails an array of email addresses
     * @return a new array with trimmed email addresses
     */
    private String[] normalizeMail(String[] emails) {
        String[] newEmails = new String[emails.length];
        for (int i = 0; i < emails.length; i++) {
            newEmails[i] = emails[i].trim();
        }

        return newEmails;
    }

}
