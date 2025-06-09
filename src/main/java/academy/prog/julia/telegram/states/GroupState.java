package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * State class for managing user group operations.
 * This class extends CommandState and processes input data to add or remove users from groups.
 */
public class GroupState extends CommandState {

    private static final String SEPARATOR = ";";

    private final String groupData;

    /**
     * Constructor to initialize the state with the group data attribute.
     *
     * @param groupData the name of the attribute where group data is stored
     */
    public GroupState(String groupData) {
        super(false);
        this.groupData = groupData;
    }

    /**
     * Processes the input data to perform group operations such as adding or removing users from groups.
     * This method is called when the state is entered.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var data = context.getAttributeAsString(groupData);

        // Check if the data contains at least one "+" or "-" character
        if (data == null || !data.matches(".*[+\\-].*")) {
            context.sendMessage("Incorrect input: at least one \"+\" or \"-\" is required!");
            return;
        }

        UserService userService = context.getUserService();
        GroupService groupService = context.getGroupService();
        var groupNames = groupService.findAllNamesOfGroups();

        List<String> parsedInfo = new ArrayList<>();
        try {
            // Split the data by separator and parse each element
            if (data.contains(SEPARATOR)) {
                String[] elements = data.split(SEPARATOR);
                for (String element : elements) {
                    parsedInfo.addAll(parser(element));
                }
            } else {
                parsedInfo = parser(data);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            context.sendMessage("Incorrect input");
            return;
        }

        long id;
        for (int i = 0; i < parsedInfo.size(); i += 3) {
            try {
                // Parse user ID and verify if the user exists
                id = Long.parseLong(parsedInfo.get(i));

                try {
                    User user = userService.findById(id).orElse(null);

                    if(user == null) throw new EntityNotFoundException();

                } catch (EntityNotFoundException e) {
                    context.sendMessage("User not found!");
                    return;
                }
            } catch (NumberFormatException e) {
                context.sendMessage("Incorrect input of user's id!");
                return;
            }

            // Verify if the group exists
            if (!groupNames.contains(parsedInfo.get(i + 1))) {
                context.sendMessage("Incorrect input: group \"" + parsedInfo.get(i + 1) + "\" doesn't exist!");
                return;
            }

            // Perform the group operation based on the sign
            if (parsedInfo.get(i + 2).equals("+")) userService.addUserToGroup(id, parsedInfo.get(i + 1));
            if (parsedInfo.get(i + 2).equals("-")) userService.removeUserFromGroup(id, parsedInfo.get(i + 1));
        }
    }

    /**
     * Parses the input data to extract user ID, group name, and operation sign.
     *
     * @param data the input data containing user ID, group name, and sign
     * @return a list containing the extracted ID, group name, and sign
     * @throws ArrayIndexOutOfBoundsException if the input data format is incorrect
     */
    private List<String> parser(String data) throws ArrayIndexOutOfBoundsException {
        // Split the data by "+" or "-" and extract relevant parts
        String[] parts = data.split("[+\\-]");
        String idPart = parts[0];
        String group = parts[1];
        String sign = String.valueOf(data.charAt(idPart.length()));
        return List.of(idPart, group, sign);
    }

}
