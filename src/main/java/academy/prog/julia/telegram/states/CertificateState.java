package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.telegram.executor.StateExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * State class for handling certificate creation tasks based on user criteria.
 * This class extends CommandState and is responsible for processing user data and creating certificates.
 */
public class CertificateState extends CommandState {

    private static final String SEMICOLON = ";";
    private static final String COLON = ":";
    private static final String SEPARATOR = ",";
    private static final String USER_ID = "user_id";
    private static final String GROUP = "group";
    private static final String USER_PHONE = "user_phone";
    private static final String USER_EMAIL = "user_email";

    private final String userDataAttribute;

    /**
     * Constructor for initializing the CertificateState with user data attribute.
     *
     * @param userDataAttribute the attribute name for user data input
     */
    public CertificateState(String userDataAttribute) {
        super(false);
        this.userDataAttribute = userDataAttribute;
    }

    /**
     * Handles entering this state and processes user data to create certificates.
     * This method overrides the enter method from CommandState to provide specific functionality for this state.
     *
     * @param context the execution context containing services and state information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var search = context.getAttributeAsString(userDataAttribute);
        if (search == null) {
            context.sendMessage("Wrong user search pattern");
            return;
        }

        var sets = search.split(SEMICOLON);
        Map<String, List<User>> userMap;
        List<String> userIds = new ArrayList<>();
        List<String> groups = new ArrayList<>();
        List<String> userPhones = new ArrayList<>();
        List<String> userEmails = new ArrayList<>();

        for (String set : sets) {
            if (!set.contains(COLON)) {
                context.sendMessage("At least one \":\" is required");
                return;
            }

            var general = set.split(COLON);

            if (general.length != 2 || general[0].isBlank() || general[0].contains(SEPARATOR)
                    || !(general[0].equals(USER_ID) || general[0].equals(GROUP)
                    || general[0].equals(USER_PHONE) || general[0].equals(USER_EMAIL))
            ) {
                context.sendMessage("Wrong input, try again, incorrect request!");
                return;
            }

            var key = general[0];
            var values = general[1];
            var parts = values.split(SEPARATOR);

            // Add values to the corresponding lists based on the key
            for (String part : parts) {
                switch (key) {
                    case USER_ID:
                        userIds.add(part.trim());
                        break;
                    case GROUP:
                        groups.add(part.trim());
                        break;
                    case USER_PHONE:
                        userPhones.add(part.trim());
                        break;
                    case USER_EMAIL:
                        userEmails.add(part.trim());
                        break;
                    default:
                        context.sendMessage("Invalid field: " + key);
                        return;
                }
            }
        }

        // Retrieve the list of users based on the criteria and process them
        userMap = context
                .getCertificateService()
                .getUserListByCriteria(context, userIds, groups, userPhones, userEmails);

        if (!userMap.isEmpty()) {
            context.getCertificateService().processUsers(userMap);
            context.sendMessage("Create certificate tasks");
        } else {
            context.sendMessage("No data matching your criteria. Please enter the correct data.");
        }
    }

}
