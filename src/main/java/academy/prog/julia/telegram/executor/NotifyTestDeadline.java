package academy.prog.julia.telegram.executor;

import academy.prog.julia.model.Test;
import academy.prog.julia.model.User;
import academy.prog.julia.services.TestService;
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
 * Class for notifying students who haven't passed a test.
 * Sends a message when the test deadline is near.
 */
@Component
public class NotifyTestDeadline {

    private final MainBot mainBot;
    private final UserService userService;
    private final static Integer PAGE_SIZE_DEFAULT = 5;
    private final TestService testService;

    /**
     * Constructor for initializing services.
     *
     * @param mainBot      bot for sending messages
     * @param userService  service for managing users
     * @param testService  service for managing tests
     */
    public NotifyTestDeadline(
            MainBot mainBot,
            UserService userService,
            TestService testService
    ) {
        this.mainBot = mainBot;
        this.userService = userService;
        this.testService = testService;
    }

    /**
     * Method that runs daily at 10:00 AM to send notifications to users who haven't passed tests.
     * Only active and non-banned users are considered.
     */
    @Scheduled(cron = "* 0 10 * * ?")
    public void sendNotificationForUserAboutTaskDeadline() {
        long totalUsers = userService.countAllByIsBannedAndIsActive(false, true);
        long countPages = (totalUsers + PAGE_SIZE_DEFAULT - 1) / PAGE_SIZE_DEFAULT;

        if (countPages != 0) {
            for (int i = 0; i < countPages; i++) {
                List<User> allUsers = userService.findAllByIsBannedAndIsActive(
                        false, true, PageRequest.of(i, PAGE_SIZE_DEFAULT));

                for (User user : allUsers) {
                    AtomicReference<StringBuilder> sb =
                            new AtomicReference<>(new StringBuilder("You did not pass the following test(s): " + "\n"));
                    int lengthOfSb = sb.get().length();

                    List<Test> allTestsByUserIdWithDeadLineAndMandatory =
                            testService.findAllTestsByUserIdWithDeadLineAndMandatory(
                                    user.getId(),
                                    LocalDate.now().plusDays(1),
                                    true
                            )
                    ;

                    Set<Test> filteredTests =
                            testService.filterTestsForNotifyToEndDeadLineDate(
                                    user,
                                    allTestsByUserIdWithDeadLineAndMandatory
                            )
                    ;

                    List<Test> sortedFilteredTests =
                            testService.sortingTestsFirstUntouchedThenFailed(user, filteredTests);

                    for (int j = 0; j < sortedFilteredTests.size(); j++) {
                        Test test = sortedFilteredTests.get(j);
                        if (test == null) {
                            if(j != sortedFilteredTests.size() - 1) {
                                sb.set(sb
                                        .get()
                                        .append(
                                                "You may try to successfully complete next test one more time," +
                                                " if you want" + "\n"
                                        )
                                );
                            }

                            if(j == 0) {
                                sb = new AtomicReference<>(
                                        new StringBuilder(
                                                "You may try to successfully complete next test(s) one more time," +
                                                " if you want" + "\n"
                                        )
                                );
                            }
                            continue;
                        }
                        sb.set(createMessageForTestsStartingWithGroupName(user, test, sb.get()));
                    }

                    if (user.getTelegramChatId() != null && sb.get().length() != lengthOfSb) {
                        mainBot.sendMessage(Long.parseLong(user.getTelegramChatId()), sb.toString());
                    }
                }
            }
        }
    }

    /**
     * Helper method to generate a message for each test a student needs to complete.
     * Appends information about the group, lesson, and test name.
     *
     * @param user          the user to be notified
     * @param test          the test with a pending deadline
     * @param stringBuilder the current message being built
     * @return updated message with the test information
     */
    private StringBuilder createMessageForTestsStartingWithGroupName(
            User user,
            Test test,
            StringBuilder stringBuilder
    ) {
        test.getLesson().getGroups().stream().forEach(group -> {
            user.getGroups().forEach(g -> {
                if (g.getName().equals(group.getName())) {
                    stringBuilder
                            .append("- Group: ")
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
                .append(test.getLesson().getName())
                .append(";")
                .append("\n")
                .append("Test: ")
                .append(test.getName())
                .append(".")
                .append("\n")
                .append("\n")
        ;

        return stringBuilder;
    }

}
