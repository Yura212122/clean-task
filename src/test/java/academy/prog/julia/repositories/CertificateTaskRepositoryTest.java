package academy.prog.julia.repositories;

import academy.prog.julia.model.CertificateTask;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateTaskRepository;
import academy.prog.julia.repos.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CertificateTaskRepositoryTest {
    @Autowired
    private CertificateTaskRepository certificateTaskRepository;

    @Autowired
    private UserRepository userRepository;

    private CertificateTask certificateTask1;
    private CertificateTask certificateTask2;
    private CertificateTask certificateTaskNullUser;


    @BeforeEach
    void setUp(){
        User user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("prosto@example.com");
        user.setPhone("1234567890");
        user.setActive(true);
        user.setPassword("Password");
        user.setRegisterDate(LocalDateTime.now());
        user.setUniqueId("uniqueId");
        userRepository.save(user);



        certificateTask1 = new CertificateTask();
        certificateTask1.setGenerated(false);
        certificateTask1.setUserId(user.getId());
        certificateTask1.setSend(false);
        certificateTaskRepository.save(certificateTask1);

        certificateTask2 = new CertificateTask();
        certificateTask2.setGenerated(false);
        certificateTask2.setUserId(user.getId());
        certificateTask2.setSend(false);
        certificateTaskRepository.save(certificateTask2);

        certificateTaskNullUser = new CertificateTask();
        certificateTaskNullUser.setGenerated(false);
        certificateTaskNullUser.setUserId(null);
        certificateTaskNullUser.setSend(true);
        certificateTaskRepository.save(certificateTaskNullUser);

    }
    @AfterEach
    void deleteCertificateTask(){
        userRepository.deleteAll();
        certificateTaskRepository.deleteAll();
    }

    @Test
    void testFindCertificateTasksToGenerate(){
        List<CertificateTask> taskList = certificateTaskRepository.findCertificateTasksToGenerate();

        assertThat(taskList).hasSize(2);
        assertTrue(taskList.stream().allMatch(i -> !i.isGenerated()));
        assertTrue(taskList.stream().allMatch(i -> i.getUserId().describeConstable().isPresent()));

    }

    @Test
    void testFindTasksToBeSent(){

        certificateTask1.setGenerated(true);
        certificateTask2.setGenerated(true);
        certificateTaskRepository.save(certificateTask1);
        certificateTaskRepository.save(certificateTask2);


        List<CertificateTask> taskList = certificateTaskRepository.findTasksToBeSent();


        assertThat(taskList).hasSize(2);
        assertTrue(taskList.stream().allMatch(i -> i.isGenerated()));
        assertTrue(taskList.stream().allMatch(i -> !i.isSend()));

    }





}