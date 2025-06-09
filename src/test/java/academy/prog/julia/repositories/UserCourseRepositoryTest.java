package academy.prog.julia.repositories;


import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.UserCourseRepository;
import academy.prog.julia.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserCourseRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserCourseRepository userCourseRepository;

    private User user;
    private Group group1;
    private Group group2;
    private Group group3;


    @BeforeEach
    void prePersist() {
        this.group1 = new Group();
        group1.setName("TestGroup 1");
        groupRepository.save(group1);

        this.group2 = new Group();
        group2.setName("TestGroup 2");
        groupRepository.save(group2);

        this.group3 = new Group();
        group3.setName("TestGroup 3");
        groupRepository.save(group3);

        user = new User(
                "User",
                "Surname",
                "1111111111",
                "user1@gmail.com",
                "password");
        user.addGroup(group1);
        user.addGroup(group2);
        userRepository.save(user);
    }

    @Test
    void findUserCourses_returnList_withMultipleGroups() {
        List<Group> courses = userCourseRepository.findUserCourses(user.getId());

        assertThat(courses).hasSize(2);
        assertThat(courses).containsExactlyInAnyOrder(group1, group2);
    }


    @Test
    void findUserCourses_returnEmptyList_forUserWithNoGroups() {
        User emptyUser = new User("Test",
                "User",
                "3333333333",
                "testuser@gmail.com",
                "password");
        userRepository.save(emptyUser);

        List<Group> courses = userCourseRepository.findUserCourses(emptyUser.getId());

        assertThat(courses).isEmpty();
    }

    @Test
    void findUserCourses_returnEmptyList_forNonExistingUser() {
        List<Group> courses = userCourseRepository.findUserCourses(999L); // assuming this ID does not exist

        assertThat(courses).isEmpty();
    }
}

