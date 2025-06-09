package academy.prog.julia.telegram.states;

import academy.prog.julia.components.DocsSheets;
import academy.prog.julia.exceptions.TestQuestionsNotFound;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.services.DocsSheetsService;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.NoSuchElementException;

import static academy.prog.julia.components.DocsSheets.extractSpreadsheetID;

/**
 * State class for adding or updating a course.
 * This class extends CommandState and handles the logic for interacting with Google Sheets and updating lessons.
 */
public class AddUpdateCourseState extends CommandState {

    private final String link;
    private final String number;
    private final String groupName;

    /**
     * Constructor for initializing the AddUpdateCourseState with the provided parameters.
     *
     * @param link      the link to the Google Sheets spreadsheet
     * @param number    the sheet number in the spreadsheet
     * @param groupName the name of the group
     */
    public AddUpdateCourseState(
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
     * Handles entering this state, performing actions to add or update lessons.
     * This method overrides the enter method from CommandState to provide specific functionality for this state.
     *
     * @param context the execution context containing services and state information
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

        // Parse the sheet number and handle any parsing errors
        try {
            sheetNum = Integer.parseInt(sheetNumber);
        } catch (NumberFormatException e) {
            context.sendMessage("Incorrect sheetNumber input");
            return;
        }

        GroupService groupService = context.getGroupService();
        Group group;

        // Find the group by name and handle errors if the group does not exist
        try {
            group = groupService.findGroupByName(groupNames).orElseThrow();

            String userIdKey = DocsSheets.getUserIdKey();

            if ("default_name".equals(userIdKey)) {
                context.sendMessage("Lessons save/update operation failed: userIdKey is incorrect." +
                        "\n Please, update the credentials Google data (run command: /google_credentials")
                ;

                return;
            }

        } catch (NoSuchElementException e) {
            context.sendMessage("This group is not exist");
            return;
        }

        // Handle saving or updating lessons in the Google Sheets
        List<Lesson> lessonList = docsSheetsService.findLessonsBySpreadsheetIDAndNumber(spreadsheetID, sheetNum);
        try {
            if (lessonList == null || lessonList.isEmpty()) {
                docsSheetsService.lessonsSave(spreadsheetLink, sheetNum, groupNames);
//                docsSheetsService.lessonsSave(spreadsheetID, sheetNum, groupNames);
            } else {
                docsSheetsService.replaceLesson(lessonList, spreadsheetLink, sheetNum, group);
            }

            // Notification to the group about the lesson after course update/addition
            NotifyGroupLessonState notifyState = new NotifyGroupLessonState(link, number, groupName);
            notifyState.enter(context);

        } catch (GeneralSecurityException | IOException e) {
            context.sendMessage("Lessons save/update operation failed: " + e.getMessage() +
                    "\n Please, update the credentials Google data (run command: /google_credentials"
            );
        } catch (TestQuestionsNotFound e) {
            context.sendMessage("Lessons save/update operation failed: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {

            Throwable rootCause = e.getRootCause();

            if (rootCause != null && rootCause.getMessage() != null) {
                String errorMessage = rootCause.getMessage();
                if (errorMessage.contains("Data too long")) {
                    String fieldName = extractFieldNameFromMessage(errorMessage);
                    context.sendMessage(
                            "Data integrity error: the input for field '" +
                            fieldName +
                            "' exceeds the allowed size."
                    );
                } else {
                    context.sendMessage("Data integrity error occurred: " + errorMessage);
                }
            } else {
                context.sendMessage("Data integrity error occurred. Please check your input.");
            }
        }

    }

    /**
     * A method to extract the field name from a database error message.
     *
     * @param errorMessage  a message that describes the error received during processing
     * @return  the name of the field that is present in the message
     */
    public String extractFieldNameFromMessage(String errorMessage) {
        if (errorMessage.contains("column")) {
            int columnIndex = errorMessage.indexOf("column");
            return errorMessage.substring(
                    columnIndex + 7,
                    errorMessage.indexOf(' ', columnIndex + 7)
            ).trim();
        }

        return "unknown field";
    }

}