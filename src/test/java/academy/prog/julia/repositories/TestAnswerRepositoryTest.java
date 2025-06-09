package academy.prog.julia.repositories;


import academy.prog.julia.model.Test;
import academy.prog.julia.model.TestAnswer;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.TestAnswerRepository;
import academy.prog.julia.repos.TestRepository;
import academy.prog.julia.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestAnswerRepositoryTest {

    @Autowired
    private TestAnswerRepository testAnswerRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Test test1;
    private Test test2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("prosto@example.com");
        user.setPhone("1234567890");
        user.setActive(true);
        user.setPassword("Password");
        user.setRegisterDate(LocalDateTime.now());
        user.setUniqueId("uniqueId");
        userRepository.save(user);

        test1 = new Test();
        test1.setName("Java1");
        testRepository.save(test1);

        test2 = new Test();
        test2.setName("Java2");
        testRepository.save(test2);

        TestAnswer testAnswer1 = new TestAnswer();
        testAnswer1.setTest(test1);
        testAnswer1.setUser(user);
        testAnswerRepository.save(testAnswer1);

        TestAnswer testAnswer2 = new TestAnswer();
        testAnswer2.setTest(test2);
        testAnswer2.setUser(user);
        testAnswerRepository.save(testAnswer2);
    }

    @org.junit.jupiter.api.Test
    void findByTestIdAndUserId_ShouldReturnTestAnswer_WhenExists() {
        TestAnswer foundAnswer = testAnswerRepository.findByTestIdAndUserId(test1.getId(), user.getId());
        assertThat(foundAnswer).isNotNull();
        assertThat(foundAnswer.getTest()).isEqualTo(test1);
        assertThat(foundAnswer.getUser()).isEqualTo(user);
    }

    @org.junit.jupiter.api.Test
    void findByTestIdAndUserId_ShouldReturnNull_WhenInvalidUserIdOrTestId() {
        TestAnswer foundAnswer = testAnswerRepository.findByTestIdAndUserId(test1.getId(), 999L);
        assertThat(foundAnswer).isNull();

        TestAnswer foundAnswer2 = testAnswerRepository.findByTestIdAndUserId(999L, user.getId());
        assertThat(foundAnswer2).isNull();
    }
}
