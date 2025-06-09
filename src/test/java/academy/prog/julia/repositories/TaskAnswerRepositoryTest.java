package academy.prog.julia.repositories;

import academy.prog.julia.model.*;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.TaskAnswerRepository;
import academy.prog.julia.repos.TaskRepository;
import academy.prog.julia.repos.UserRepository;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskAnswerRepositoryTest {

    @Autowired
    private TaskAnswerRepository taskAnswerRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    private User user;

    @BeforeEach
    public void setup() {
        user = createUser();
        createGroupForUser(user);
    }


    public User createUser() {
        return  userRepository.save(new User(
                "User",
                "Surname",
                "1111111111",
                "user1@gmail.com",
                "password"));
    }


    public void createGroupForUser(User user) {
        Group group = new Group();
        group.setName("mock1Test");
        groupRepository.save(group);
        user.setGroups(new HashSet<>(Set.of(group)));
        userRepository.save(user);
    }

    public Task createTask() {
        Task task = new Task();
        task.setId(1L);
        task.setName("TestName");
        task.setDescriptionUrl("TestDescriptionUrl");
        task.setDeadline(LocalDate.now());
        task.setActive(true);
        task.setExpectedResult(ExpectedResult.LINK);
        return taskRepository.save(task);
    }

    @Test
    public void testFindByTaskIdAndUserId_returnsTaskAnswer() {
        Task task = createTask();
        TaskAnswer taskAnswer = taskAnswerRepository.save(
                new TaskAnswer(user, task, "answerUrl",
                        1L, 1L, 1,
                        "course", true, false,
                        "", false, new Date())
        );

        TaskAnswer actual = taskAnswerRepository.findByTaskIdAndUserId(
                taskAnswer.getTask().getId(), user.getId()
        );



        assertThat(actual)
                .isNotNull()
                .isEqualTo(taskAnswer);
    }


    @Test
    public void testFindPendingTask_returnsPageTaskAnswer() {
        List<TaskAnswer> expected = createAndSavePendingTasks(3);

        Page<TaskAnswer> actualPage = taskAnswerRepository.findPendingTask(PageRequest.of(0, 3));
        List<TaskAnswer> actual = actualPage.getContent();

        assertThat(actual)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    private List<TaskAnswer> createAndSavePendingTasks(int count) {
        List<TaskAnswer> taskAnswers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Task task = createTaskWithDeadline(LocalDate.now().plusDays(i + 10));
            TaskAnswer taskAnswer = new TaskAnswer(user, task, "Answer" + i, 1L, 1L, 1, "Course1", false, false, "Message" + i, false, new Date());
            taskAnswers.add(taskAnswerRepository.save(taskAnswer));
        }
        return taskAnswers;
    }

    private Task createTaskWithDeadline(LocalDate deadline) {
        Task task = new Task();
        task.setId((long) (Math.random() * 100)); // Generate a random ID
        task.setName("TestName");
        task.setDescriptionUrl("TestDescriptionUrl");
        task.setDeadline(deadline);
        task.setActive(true);
        task.setExpectedResult(ExpectedResult.LINK);
        return taskRepository.save(task);
    }

    @Test
    public void testFindPendingTaskByGroup_returnsPageTaskAnswer() {
        List<TaskAnswer> expected = createAndSavePendingTasksByGroup(2, "mock1Test");

        Page<TaskAnswer> actualPage = taskAnswerRepository.findPendingTaskByGroup(PageRequest.of(0, 4),
                "mock1Test");
        Page<TaskAnswer> nullTasksPage = taskAnswerRepository.findPendingTaskByGroup(PageRequest.of(0, 4),
                "mockNoGroupTest");
        List<TaskAnswer> actual = actualPage.getContent();

        assertThat(actual)
                .isNotEmpty()
                .containsExactlyInAnyOrderElementsOf(expected);
        assertEquals(0, nullTasksPage.getNumberOfElements());
    }

    private List<TaskAnswer> createAndSavePendingTasksByGroup(int count, String groupName) {
        List<TaskAnswer> taskAnswers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Task task = createTaskWithDeadline(LocalDate.now().plusDays(i + 5));
            TaskAnswer taskAnswer = new TaskAnswer(user, task, "Answer" + i, 1L, 1L, 1, "Course1", false, false, "Message" + i, false, new Date());
            taskAnswers.add(taskAnswerRepository.save(taskAnswer));
        }
        return taskAnswers;
    }


}
