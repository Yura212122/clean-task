package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.AddUpdateCourseState;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import academy.prog.julia.telegram.states.NotifyGroupLessonState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This command handles adding or updating a course in the system via the Telegram bot.
 * It prompts the user for the necessary information such as a Google Spreadsheet link,
 * the sheet number, and the group name. After gathering this data, it processes the
 * course addition or update and notifies the group about the lesson.
 *
 * The command is available only to users with admin or manager roles.
 */
@Component
public class AddUpdateCourseCommand extends AdminOrManagerCommand {

    /**
     * Returns the command name that triggers this action in the Telegram bot.
     *
     * @return a string representing the command name, "/add_or_update_course"
     */
    @Override
    public String getName() {
        return "/add_or_update_course";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "add the course or update existed one"
     */
    @Override
    public String getDescription() {
        return "add the course or update existed one";
    }

    /**
     * Defines the sequence of states the bot goes through when processing this command.
     *
     * @return a list of CommandState objects representing each step the user will go through:
     * - EnterTextState: prompts for a Google Spreadsheet link.
     * - EnterTextState: prompts for the sheet number.
     * - EnterTextState: prompts for the group name.
     * - AddUpdateCourseState: processes the provided data (link, sheet number, group name) to add or update a course.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter google spreadsheet link", "link"),
                new EnterTextState("Enter sheet number", "number"),
                new EnterTextState("Enter group name", "groupName"),
                new AddUpdateCourseState("link", "number", "groupName")
        );
    }

}

