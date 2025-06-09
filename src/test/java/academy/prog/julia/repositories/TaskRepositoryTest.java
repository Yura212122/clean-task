package academy.prog.julia.repositories;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.Task;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.LessonRepository;
import academy.prog.julia.repos.TaskRepository;
import academy.prog.julia.repos.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private GroupRepository groupRepository;

    private User user;
    private Group group;
    private Lesson lesson;
    private Task task1;
    private Task task2;
    private Task task3;


    @BeforeEach
    public void setUp() {
        User us = new User();
        us.setName("Name");
        us.setSurname("Surname");
        us.setEmail("prosto@example.com");
        us.setPhone("1234567890");
        us.setActive(true);
        us.setPassword("Password");
        us.setRegisterDate(LocalDateTime.now());
        us.setUniqueId("uniqueId");
        user = userRepository.save(us);

        Group gr = new Group();
        gr.setId(1L);
        gr.setName("Group A");
        gr.addClient(user);
        group = groupRepository.save(gr);

        user.addGroup(group);


        Lesson les = new Lesson();
        les.setId(1L);
        les.setName("Java 100");
        les.setGroups(Set.of(group));
        lesson = lessonRepository.save(les);

        task1 = new Task();
        task1.setName("Task 1");
        task1.setDeadline(LocalDate.now().plusDays(1));
        task1.setActive(true);
        task1.setLesson(lesson);
        task1.setStudents(Set.of(user));


        task2 = new Task();
        task2.setName("Task 2");
        task2.setDeadline(LocalDate.now().plusDays(1));
        task2.setActive(true);
        task2.setLesson(lesson);
        task2.setStudents(Set.of(user));

        task3 = new Task();
        task3.setName("Task 3");
        task3.setDeadline(LocalDate.now().minusDays(4));
        task3.setActive(true);
        task3.setLesson(lesson);
        task3.setStudents(Set.of(user));

        userRepository.save(user);
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);



    }


    @Test
    public void testFindAllActiveTaskByUserIdWithDeadline_returnsList() {
        LocalDate deadline = LocalDate.now().plusDays(1);
        List<Task> tasks = taskRepository.findAllActiveTaskByUserIdWithDeadLine(user.getId(), deadline);

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting("name").containsExactlyInAnyOrder("Task 1", "Task 2");
    }

    @Test
    public void testFindAllActiveTaskByUserId_returnNull() {
        LocalDate deadline = LocalDate.now().plusDays(5);
        List<Task> tasks = taskRepository.findAllActiveTaskByUserIdWithDeadLine(user.getId(), deadline);

        assertThat(tasks).isEmpty();
    }


    @Test
    public void testFindTaskByTeacherId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasks = taskRepository.findTaskByTeacherId(user.getId(), pageable);

        assertThat(tasks).hasSize(3);

        Page<Task> tasksNone = taskRepository.findTaskByTeacherId(69L, pageable);
        assertThat(tasksNone).hasSize(0);
    }

    @Test
    public void testFindTaskByTeacherId_returnPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Task> tasksPage1 = taskRepository.findTaskByTeacherId(user.getId(), pageable);
        Page<Task> tasksPage2 = taskRepository.findTaskByTeacherId(user.getId(), PageRequest.of(1, 2));

        assertThat(tasksPage1.getTotalElements()).isEqualTo(3);
        assertThat(tasksPage1.getContent()).hasSize(2);
        assertThat(tasksPage2.getContent()).hasSize(1);
    }

}



