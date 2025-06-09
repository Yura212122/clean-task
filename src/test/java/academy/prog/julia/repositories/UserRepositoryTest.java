package academy.prog.julia.repositories;


import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.Task;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.*;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
@Rollback
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestRepository testRepository;


    private User user;
    private User user2;
    private User user3;
    private Group group;
    private Group group2;

    private Task task1;
    private Task task2;

    private academy.prog.julia.model.Test test1;
    private academy.prog.julia.model.Test test2;



    @BeforeEach
    void setUp() {

        Lesson lesson = new Lesson();
        lesson.setName("Test Lesson");
        Lesson lesson2 = new Lesson();
        lesson2.setName("Test Lesson 2");

        lessonRepository.save(lesson);
        lessonRepository.save(lesson2);

        task1 = new Task();
        task1.setName("Java");
        task1.setLesson(lesson);
        taskRepository.save(task1);

        task2 = new Task();
        task2.setName("Python");
        task2.setLesson(lesson2);
        taskRepository.save(task2);

        test1 = new academy.prog.julia.model.Test();
        test1.setName("Test 1");
        test1.setLesson(lesson);
        testRepository.save(test1);

        test2 = new academy.prog.julia.model.Test();
        test2.setName("Test 2");
        test2.setLesson(lesson2);
        testRepository.save(test2);

        group = new Group("TestGroup", new HashSet<>());
        group.getLessons().add(lesson); // Mutable collection
        groupRepository.save(group);

        group2 = new Group("TestGroup2", new HashSet<>());
        group2.getLessons().add(lesson2); // Mutable collection
        groupRepository.save(group2);

        lesson.getGroups().add(group);
        lesson2.getGroups().add(group2);
        lessonRepository.save(lesson);
        lessonRepository.save(lesson2);

        user = new User("User", "Surname", "3801111111", "user1@gmail.com", "password");
        user.setUniqueId("unique1");
        user.addGroup(group);
        user.setActive(true);
        user.setTasks(new HashSet<>());
        userRepository.save(user);

        user2 = new User("Newuser", "Newsurname", "3802222222", "user2@gmail.com", "password2");
        user2.setUniqueId("unique2");
        user2.addGroup(group);
        user2.setTasks(new HashSet<>(Set.of(task2)));
        user2.setActive(true);
        user2.setTasks(new HashSet<>());
        userRepository.save(user2);

        user3 = new User("Test", "Test", "0672222222", "test3@gmail.com", "password3");
        user3.setUniqueId("unique3");
        user3.setActive(true);
        user3.addGroup(group);
        user3.addGroup(group2);
        user3.setTasks(new HashSet<>());

        userRepository.save(user3);

        task1.setStudents(new HashSet<>(Set.of(user, user2)));
        task2.setStudents(new HashSet<>(Set.of(user3)));
        taskRepository.save(task1);
        taskRepository.save(task2);
    }


    @Test
    void testExistByPhoneOrEmailTest(){
        boolean test1 = userRepository.existsByPhoneOrEmail(user.getPhone(), "");
        boolean test2 = userRepository.existsByPhoneOrEmail("",  user2.getEmail());
        boolean test3 = userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail());
        boolean test4 = userRepository.existsByPhoneOrEmail(user.getPhone(), user2.getEmail());
        assertThat(Arrays.asList(test1, test2, test3, test4)).allMatch(x -> x);

        boolean testFalse = userRepository.existsByPhoneOrEmail("", "");
        assertThat(testFalse).isEqualTo(false);
    }

    @Test
    void testFindByUniqueId(){
        Optional<User> userOpt  = userRepository.findByUniqueId(user.getUniqueId());
        assertAll("User must be found",
                ()-> assertTrue(userOpt.isPresent()),
                ()-> assertEquals(user.getName(), userOpt.get().getName())
        );

        Optional<User> userOptNone = userRepository.findByUniqueId("NoSuchUniqueId");
        assertFalse(userOptNone.isPresent(), "User mustn`t exist");
    }

    @Test
    void testFindByPhoneLike(){
        //some patern from number
        List<User> test1 = userRepository.findByPhoneLike("380");
        assertThat(test1).hasSize(3);

        //full phone number
        List<User> test2 = userRepository.findByPhoneLike("0672222222");
        assertThat(test2).hasSize(1);


        // multiple numbers for user tests
        user.addPhone("0679324444");
        userRepository.save(user);

        List<User> test3 = userRepository.findByPhoneLike("067");
        assertThat(test3).hasSize(2);



        List<User> test4 = userRepository.findByPhoneLike("0679324444");
        assertThat(test4).hasSize(1);
        assertThat(test4.get(0).getName()).isEqualTo("User");


        List<User> testEmptyList = userRepository.findByPhoneLike("9898989898");
        assertThat(testEmptyList).hasSize(0);


    }

    @Test
    void testFindByEmailLike(){
        //some patern from email
        List<User> test1 = userRepository.findByEmailLike("user");
        assertThat(test1).hasSize(2);

        //full email
        List<User> test2 = userRepository.findByEmailLike("test3@gmail.com");
        assertThat(test2).hasSize(1);
        assertThat(test2.get(0).getName()).isEqualTo("Test");



        // multiple emails for user
        user.addEmail("test3@ukr.net");
        userRepository.save(user);

        List<User> test3 = userRepository.findByEmailLike("test3");
        assertThat(test3).hasSize(2);



        List<User> test4 = userRepository.findByEmailLike("test3@ukr.net");
        assertThat(test4).hasSize(1);
        assertThat(test4.get(0).getName()).isEqualTo("User");


        List<User> testEmptyList = userRepository.findByEmailLike("NonExistingEmail@gmail.com");
        assertThat(testEmptyList).hasSize(0);


    }

    @Test
    void testFindByGroupName(){
        Page<User> testPage = userRepository.findByGroupName(group.getName(), PageRequest.of(0, 2));
        assertAll("Testing page with 3 found elements  and page size = 2",
                ()-> assertEquals(2, testPage.getNumberOfElements()),
                ()-> assertEquals(3, testPage.getTotalElements()));


        Page<User> testPageNone = userRepository.findByGroupName("TestGroupDoesntExist", PageRequest.of(0, 2));
        assertEquals(0, testPageNone.getTotalElements());

    }

    @Test
    void testCountByGroupName(){
        assertEquals(3, userRepository.countByGroupName(group.getName()));
        assertEquals(0, userRepository.countByGroupName("NonExistingGroup"));
    }

    @Test
    void testExistsByPhone(){
        assertTrue(userRepository.existsByPhone(user.getPhone()));
        assertFalse(userRepository.existsByPhone("9898989898"));
    }

    @Test
    void testExistsByEmail(){
        assertTrue(userRepository.existsByEmail(user.getEmail()));
        assertFalse(userRepository.existsByEmail("NonExistingEmail@gmail.com"));
    }

    @Test
    void testFindByEmail(){
        assertNull(userRepository.findByEmail("NonExistingEmail@gmail.com"));

        User userFound = userRepository.findByEmail(user3.getEmail());
        assertNotNull(userFound);
        assertThat(userFound.getName()).isEqualTo(user3.getName());


    }

    @Test
    void testCountAllByIsBannedAndIsActive(){
        List<User> users = userRepository.findAll();
        assertEquals(3, userRepository.countAllByIsBannedAndIsActive(false, true));

        user3.setBannedStatus(true);
        userRepository.save(user3);
        assertEquals(1, userRepository.countAllByIsBannedAndIsActive(true, true));

        user2.setActive(false);
        userRepository.save(user2);
        assertEquals(1, userRepository.countAllByIsBannedAndIsActive(false, false));

    }

    @Test
    void testFindAllByTaskId(){
        assertThat(userRepository.findAllByTaskId(task1.getId())).hasSize(3);

        assertThat(userRepository.findAllByTaskId(task2.getId())).hasSize(1);

        //non-existing id
        assertThat(userRepository.findAllByTaskId(999L)).hasSize(0);

    }

    @Test
    void testFindAllByTestId(){
        assertThat(userRepository.findAllByTestId(test1.getId())).hasSize(3);

        assertThat(userRepository.findAllByTestId(test2.getId())).hasSize(1);

        //non-existing id
        assertThat(userRepository.findAllByTestId(999L)).hasSize(0);

    }

    @Test
    void testFindAllByIsBannedAndIsActive(){
        List<User>  usersTestOne = userRepository
                .findAllByIsBannedAndIsActive(false, true, PageRequest.of(0,2));

        assertAll("Testing list. Found 3 users, list size = 2",
                ()-> assertThat(usersTestOne).hasSize(2),
                ()-> assertTrue(usersTestOne.stream().allMatch(i -> i.getActive())),
                ()-> assertFalse(usersTestOne.stream().allMatch(i -> i.getBannedStatus()))
        );


        user.setBannedStatus(true);
        user2.setBannedStatus(true);
        userRepository.save(user);
        userRepository.save(user2);

        List<User>  usersTestTwo = userRepository
                .findAllByIsBannedAndIsActive(true, true, PageRequest.of(0,3));

        assertAll("Testing list. Found 2 users, list size = 3",
                ()-> assertThat(usersTestTwo).hasSize(2),
                ()-> assertTrue(usersTestTwo.stream().allMatch(i -> i.getActive())),
                ()-> assertTrue(usersTestTwo.stream().allMatch(i -> i.getBannedStatus()))
        );
    }





}
