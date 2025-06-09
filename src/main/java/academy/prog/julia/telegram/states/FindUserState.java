package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;

/**
 * State class for handling user search operations.
 * This class extends CommandState and  processes the user's input to search for users based on phone number or email.
 */
public class FindUserState extends CommandState {

    private static final String SEPARATOR = ";";

    private final String userDataAttribute;

    /**
     * Constructor to initialize the state with the attribute name for user data.
     *
     * @param userDataAttribute the name of the attribute where user search data is stored
     */
    public FindUserState(String userDataAttribute) {
        super(false);
        this.userDataAttribute = userDataAttribute;
    }

    /**
     * Processes the user's input to search for users based on the provided phone number or email.
     * This method is called when the state is entered.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var search = context.getAttributeAsString(userDataAttribute);

        if (search == null) {
            context.sendMessage("Wrong user search pattern");
            return;
        }

        var parts = search.split(SEPARATOR);
        String phone = null;
        String email = null;

        // Assign phone and email based on the number of parts in the search pattern
        if (parts.length == 2) {
            phone = parts[0].trim();
            email = parts[1].trim();
        } else {
            // If there is no separator, determine whether the input is a phone or email
            if (search.indexOf('@') > 0) {
                email = search.trim();
            } else {
                phone = search.trim();
            }
        }

        var users = context.getUserService().findByPhoneOrEmailLike(phone, email);

        if (users.size() == 0) {
            context.sendMessage("No users found");
            return;
        }

        // Construct a message with user details for the found users
        var stringBuilder = new StringBuilder();
        for (var user : users) {
            stringBuilder
                .append("The person you are looking for: \n")
                .append("ID: ")
                .append(user.getId())
                .append("\n")
                .append("Name: ")
                .append(user.getName())
                .append("\r\n")
            ;
        }

        context.sendMessage(stringBuilder.toString());
    }

}
