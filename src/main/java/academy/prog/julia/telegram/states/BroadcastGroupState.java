package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

/**
 * State class for broadcasting a message to users in a specific group.
 * This class extends CommandState and handles the logic for sending a message to all users in a given group.
 */
public class BroadcastGroupState extends CommandState {

    private static final int PAGE_SIZE = 100;

    private final String groupNameAttribute;
    private final String messageAttribute;

    /**
     * Constructor for initializing the BroadcastGroupState with group name and message attributes.
     *
     * @param groupNameAttribute the attribute name for the group name
     * @param messageAttribute the attribute name for the message
     */
    public BroadcastGroupState(
            String groupNameAttribute,
            String messageAttribute
    ) {
        super(false);
        this.groupNameAttribute = groupNameAttribute;
        this.messageAttribute = messageAttribute;
    }

    /**
     * Handles entering this state, broadcasting a message to all users in the specified group.
     * This method overrides the enter method from CommandState to provide specific functionality for this state.
     *
     * @param context the execution context containing services and state information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var userService = context.getUserService();
        var groupService = context.getGroupService();

        Set<User> users;

        var groupName = context.getAttributeAsString(groupNameAttribute);
        var message = context.getAttributeAsString(messageAttribute);

        var groupNameFromDB = groupService.findGroupByName(groupName);

        if (groupNameFromDB.isEmpty()) {
            context.sendMessage("This Group does not exist. Please try again.");
            return;
        }

        long totalCount = userService.countUsersByGroupName(groupName);

        long pageCount = getPageCount(totalCount);

        for (int i = 0; i < pageCount; i++) {
            users = userService.findByGroupName(groupName, PageRequest.of(i, PAGE_SIZE));
            for (User user : users) {
                String chatId = user.getTelegramChatId();
                if (chatId != null) {
                    context.sendMessage(chatId, message);
                } else {
                    System.err.println("User " + user.getUsername() + " does not have TelegramChatId.");
                }
            }
        }
    }

    /**
     * Calculates the number of pages needed to retrieve all users.
     *
     * @param totalCount the total number of users
     * @return the number of pages required
     */
    private long getPageCount(long totalCount) {
        return (totalCount / PAGE_SIZE) + ((totalCount % PAGE_SIZE > 0) ? 1 : 0);
    }

    /**
     * Перевіряє, чи існує група з таким іменем.
     *
     * @param context контекст виконання
     * @param groupName назва групи
     * @return true, якщо група існує, і false, якщо ні
     */
    private boolean isValidGroupName(
            StateExecutionContext context,
            String groupName
    ) {
        var userService = context.getUserService();
        long totalCount = userService.countUsersByGroupName(groupName);
        return totalCount > 0;
    }

}
