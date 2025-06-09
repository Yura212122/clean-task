package academy.prog.julia.repositories;


import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateTaskRepository;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.LessonRepository;
import academy.prog.julia.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupRepositoryTest {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    private Group group1;
    private Group group2;
    private Group group3;

    private Lesson lesson;
    private User user;



    @BeforeEach
    void setUp(){

        lesson = new Lesson();
        lesson.setName("Lesson");
        lessonRepository.save(lesson);

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

        group1 = new Group();
        group1.setName("Group1");
        group1.setClients(new HashSet<>());
        group1.setLessons(new HashSet<>());
        group1.getLessons().add(lesson);
        groupRepository.save(group1);


        group2 = new Group();
        group2.setName("Group2");
        group2.setClients(new HashSet<>(Arrays.asList(user)));
        group2.setLessons(new HashSet<>());
        group2.getLessons().add(lesson);
        groupRepository.save(group2);

        group3 = new Group();
        group3.setName("Group3");
        group3.setClients(new HashSet<>(Arrays.asList(user)));
        group3.setLessons(new HashSet<>());
        groupRepository.save(group3);

        lesson.setGroups(new HashSet<>(Arrays.asList(group1, group2)));
        lessonRepository.save(lesson);

        user.setGroups(new HashSet<>(Arrays.asList(group2, group3)));
        userRepository.save(user);


    }

    @Test
    void findByName(){
        Optional<Group> groupTest = groupRepository.findByName(group1.getName());
        assertTrue(groupTest.isPresent());
        assertEquals(group1.getName(), groupTest.get().getName());

        Optional<Group> groupTestNotFound = groupRepository.findByName("NotExistingName");
        assertTrue(groupTestNotFound.isEmpty());


    }

    @Test
    void existsByName(){
        boolean testExist = groupRepository.existsByName(group1.getName());
        assertTrue(testExist);

        boolean testNotExist = groupRepository.existsByName("NotExistingName");
        assertFalse(testNotExist);

        boolean testNullName = groupRepository.existsByName(null);
        assertFalse(testNullName);

    }

    @Test
    void findAllNames(){
        List<String> testList = groupRepository.findAllNames();

        assertAll("Result list testing",
                ()-> assertThat(testList).hasSize(3),
                ()-> assertThat(testList).containsExactly(group1.getName(), group2.getName(), group3.getName()));

    }

    @Test
    void findAllByLessonId(){
        List<Group> testList = groupRepository.findAllByLessonId(lesson.getId());
        assertAll("Result list testing",
                ()-> assertThat(testList).hasSize(2),
                ()-> assertEquals(group1, testList.get(0)));


        List<Group> testListNull = groupRepository.findAllByLessonId(null);
        assertThat(testListNull).hasSize(0);

    }


    @Test
    void findAllByUser(){
        List<Group> testList = groupRepository.findAllByUser(user);
        assertAll("Result list testing",
                ()-> assertThat(testList).hasSize(2),
                ()-> assertEquals(group2, testList.get(0)));


        List<Group> testListNull = groupRepository.findAllByUser(null);
        assertThat(testListNull).hasSize(0);

    }
}
