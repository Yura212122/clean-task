package academy.prog.julia.telegram.states;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.User;
import academy.prog.julia.services.DocsSheetsService;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;

import java.util.List;

import static academy.prog.julia.components.DocsSheets.extractSpreadsheetID;

/**
 * Notifies all members of a course group about new or updated lessons.
 * This class extends CommandState and handles fetching lesson details from a spreadsheet and sending notifications
 * to users in a specified group.
 */
public class NotifyGroupLessonState extends CommandState {

    private final String link;
    private final String number;
    private final String groupName;

    /**
     * Constructs a NotifyGroupLessonState with the required parameters.
     *
     * @param link the attribute name for the spreadsheet link
     * @param number the attribute name for the sheet number
     * @param groupName the attribute name for the group name
     */
    public NotifyGroupLessonState(
            String link,
            String number,
            String groupName
    ) {
        super(false);
        this.link = link;
        this.number = number;
        this.groupName = groupName;
    }

    /**
     * Handles the entry into this state by sending notifications about new or updated lessons.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        DocsSheetsService docsSheetsService = context.getDocsSheetsService();
        String spreadsheetLink = context.getAttributeAsString(link);
        String sheetNumber = context.getAttributeAsString(number);
        String groupNames = context.getAttributeAsString(groupName);

        String spreadsheetID = extractSpreadsheetID(spreadsheetLink);

        if (spreadsheetID == null) {
            context.sendMessage("Incorrect spreadsheet link");
            return;
        }

        int sheetNum;

        try {
            sheetNum = Integer.parseInt(sheetNumber);
        } catch (NumberFormatException e) {
            context.sendMessage("Incorrect sheetNumber input");
            return;
        }

        // Fetch the lessons from the spreadsheet
        List<Lesson> lessonList = docsSheetsService.findLessonsBySpreadsheetIDAndNumber(spreadsheetID, sheetNum);
        List<String> lessonNames = lessonList.stream().map(Lesson::getName).toList();

        GroupService groupService = context.getGroupService();
        Group group = groupService.findGroupByName(groupNames).orElse(null);

        if (group == null) {
            context.sendMessage(
                    "The group with name '" +
                    groupNames +
                    "' was not found. Please check the group name and try again."
            );
            return;
        }

        UserService userService = context.getUserService();
        List<User> userList = userService.findByGroupName(group.getName());

        // Send a notification to each user with their Telegram chat ID
        userList.forEach(user -> {
            if (user.getTelegramChatId() != null) {
                context.sendMessage(user.getTelegramChatId(),
                        "In your course were added or updated next lessons: " + "\n" + lessonNames);
            }
        });
    }

}
