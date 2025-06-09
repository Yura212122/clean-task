package academy.prog.julia.telegram.executor;

import academy.prog.julia.model.Task;
import academy.prog.julia.model.User;
import academy.prog.julia.services.TaskService;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.MainBot;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NotifyTaskDeadline is responsible for informing students about the deadlines of homework assignments.
 * Notifications are sent to users who have not submitted their tasks, a certain number of days before the deadline.
 */
@Component
public class NotifyTaskDeadline {

    private final MainBot mainBot;
    private final TaskService taskService;
    private final static Integer PAGE_SIZE_DEFAULT = 5;
    private final UserService userService;
    private final Integer NUMBER_OF_DAYS_UNTIL_THE_DEADLINE = 1;

    /**
     * Constructor for initializing services.
     *
     * @param mainBot      bot for sending messages
     * @param taskService  service for managing tasks
     * @param userService  service for managing users
     */
    public NotifyTaskDeadline(
            MainBot mainBot,
            TaskService taskService,
            UserService userService
    ) {
        this.mainBot = mainBot;
        this.taskService = taskService;
        this.userService = userService;
    }

    /**
     * Method that runs on schedule and sends notifications to users
     * every two hours from 8:00 to 22:00.
     */
    @Scheduled(cron = "0 0 8-22/2 * * ?")
    private void sendNotificationForUserAboutTaskDeadline() {
        // Messages will be sent only to those users who are not banned and active
        long totalUsers = userService.countAllByIsBannedAndIsActive(false, true);
        long countPages = (totalUsers + PAGE_SIZE_DEFAULT - 1) / PAGE_SIZE_DEFAULT;

        if (countPages != 0) {
            for (int i = 0; i < countPages; i++) {
                List<User> allUsers = userService.findAllByIsBannedAndIsActive(
                        false, true, PageRequest.of(i, PAGE_SIZE_DEFAULT));

                // Create a notification message for each user about the unsubmitted tasks
                for (User user : allUsers) {
                    AtomicReference<StringBuilder> sb =
                            new AtomicReference<>(new StringBuilder("You haven't sent next home task(s): " + "\n"));

                    int lengthOfSb = sb.get().length();

                    for (int j = 0; j <= NUMBER_OF_DAYS_UNTIL_THE_DEADLINE; j++) {
                        List<Task> allTaskByUserIdWithDeadLine =
                                taskService.findAllActiveTaskByUserIdWithDeadLine(
                                        user.getId(),
                                        LocalDate.now().plusDays(j)
                                )
                        ;

                        Set<Task> filteredTasks =
                                taskService.filterTasksForNotifyToEndDeadLineDate(user, allTaskByUserIdWithDeadLine);

                        for (Task task : filteredTasks) {
                            sb.set(createMessageForTasksStartingWithGroupName(user, task, sb.get()));
                        }
                    }
                    // If the message was generated and the user has a Telegram chat ID, send the message
                    if (user.getTelegramChatId() != null && sb.get().length() != lengthOfSb) {
                        mainBot.sendMessage(Long.parseLong(user.getTelegramChatId()), sb.toString());
                    }
                }
            }
        }
    }

    /**
     * Method for creating a task message for each user.
     * The message contains the group name, lesson, and task details.
     *
     * @param user          the user for whom the message is being created
     * @param task          the task to be included in the message
     * @param stringBuilder StringBuilder object for accumulating the message text
     * @return updated StringBuilder object with the message
     */
    private StringBuilder createMessageForTasksStartingWithGroupName(
            User user,
            Task task,
            StringBuilder stringBuilder
    ) {
        task.getLesson().getGroups().stream().forEach(group -> {
            user.getGroups().forEach(g -> {
                if (g.getName().equals(group.getName())) {
                    stringBuilder
                            .append("Group: ")
                            .append(group.getName())
                            .append(",")
                    ;
                }
            });
        });

        stringBuilder.setCharAt(stringBuilder.length() - 1, ';');

        stringBuilder
                .append("\n")
                .append("Lesson: ")
                .append(task.getLesson().getName())
                .append(";")
                .append("\n")
                .append("Task: ")
                .append(task.getName())
                .append(".")
                .append("\n")
                .append("\n");

        return stringBuilder;
    }

}
