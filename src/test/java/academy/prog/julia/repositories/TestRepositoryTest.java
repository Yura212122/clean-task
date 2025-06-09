package academy.prog.julia.repositories;


import academy.prog.julia.model.*;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.LessonRepository;
import academy.prog.julia.repos.TestRepository;
import academy.prog.julia.repos.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestRepositoryTest {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private GroupRepository groupRepository;

    private User user;
    private Lesson lesson;
    private Group group;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("email@example.com");
        user.setPhone("1234567899");
        user.setActive(true);
        user.setPassword("Password");
        user.setRegisterDate(LocalDateTime.now());
        user.setUniqueId("unique");
        userRepository.save(user);

        group = new Group();
        group.setName("Group B");
        group.addClient(user);
        groupRepository.save(group);
        user.addGroup(group);
        userRepository.save(user);

        lesson = new Lesson();
        lesson.setName("Java 101");
        lesson.setGroups(Set.of(group));
        lessonRepository.save(lesson);

        createTest("Test 1", LocalDate.now().plusDays(1), true);
        createTest("Test 2", LocalDate.now().plusDays(1), false);
        createTest("Test 3", LocalDate.now(), true);
    }


    private void createTest(String name, LocalDate deadline, boolean mandatory) {
        Test test = new Test();
        test.setName(name);
        test.setTestUrl(name + "_Url");
        test.setLesson(lesson);
        test.setMandatory(mandatory);
        test.setDeadline(deadline);

        testRepository.save(test);
    }


    @org.junit.jupiter.api.Test
    void findAllTestsByUserIdWithDeadLineAndMandatory_ValidInputs_ReturnsExpectedTests() {
        List<Test> tests = testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(user.getId(), LocalDate.now().plusDays(1), true);
        assertThat(tests).hasSize(1);
        assertThat(tests.get(0).getName()).isEqualTo("Test 1");
    }


    @org.junit.jupiter.api.Test
    void findAllTestsByUserIdWithDeadLineAndMandatory_InvalidUserId_ReturnsEmptyList() {
        List<Test> tests = testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(999L, LocalDate.now().plusDays(1), true);
        assertThat(tests).isEmpty();
    }

    @org.junit.jupiter.api.Test
    void findAllTestsByUserIdWithDeadLineAndMandatory_NoTestsAvailable_ReturnsEmptyList() {
        List<Test> tests = testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(user.getId(), LocalDate.now().plusDays(2), true);
        assertThat(tests).isEmpty();
    }


    @org.junit.jupiter.api.Test
    void findAllTestsByUserIdWithDeadLineAndMandatory_DifferentMandatoryFlag_ReturnsExpectedTests() {
        List<Test> mandatoryTests = testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(user.getId(), LocalDate.now().plusDays(1), true);
        List<Test> nonMandatoryTests = testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(user.getId(), LocalDate.now().plusDays(1), false);

        assertThat(mandatoryTests).hasSize(1);
        assertThat(nonMandatoryTests).hasSize(1);
        assertThat(nonMandatoryTests.get(0).getName()).isEqualTo("Test 2");
    }


    @org.junit.jupiter.api.Test
    void findAllTestsByUserIdWithDeadLineAndMandatory_DeadlineMismatch_ReturnsEmptyList() {
        List<Test> tests = testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(user.getId(), LocalDate.now().minusDays(1), true);
        assertThat(tests).isEmpty();
    }


}

