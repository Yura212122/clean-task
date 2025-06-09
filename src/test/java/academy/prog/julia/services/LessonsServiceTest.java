package academy.prog.julia.services;

import academy.prog.julia.dto.LessonDetailDTO;
import academy.prog.julia.dto.LessonsDTO;
import academy.prog.julia.json_responses.LessonsResponse;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.LessonRepository;
import academy.prog.julia.repos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Extend the class with MockitoExtension to enable Mockito annotations.
public class LessonsServiceTest {

    @Mock
    private LessonRepository lessonRepository;  // Mock the LessonRepository for testing without using real DB.

    @Mock
    private GroupRepository groupRepository;  // Mock the GroupRepository to avoid actual DB interactions.

    @Mock
    private UserRepository userRepository; //Mock the UserRepository to avoid actual DB interactions.

    @Mock
    private GroupService groupService;

    @Mock
    private UserService userService;

    @Spy
    @InjectMocks
    private LessonsService lessonsService;  // Inject mocks into the LessonsService to test it in isolation.

    @Test
    public void test() {
        // Check if the lessonsService object is properly initialized and not null.
        assertThat(lessonsService).isNotNull();
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetCourseLessons_WhenLessonsExist() {
        // Create a course for the test.
        Group course = createGroup(1L, "Test Courses");

        // Create lessons associated with the course.
        Lesson lesson1 = createLesson(1L, "Lesson1", "description1", "video1", course);
        Lesson lesson2 = createLesson(2L, "Lesson2", "description2", "video2", course);

        Long courseId = course.getId();

        // Set up mock behavior for groupRepository and lessonRepository.
        doReturn(Optional.of(course)).when(groupRepository).findById(courseId);
        when(lessonRepository.findCourseLessons(courseId)).thenReturn(Arrays.asList(lesson1, lesson2));

        // Call the service method being tested.
        List<LessonsDTO> result = lessonsService.getCourseLessons(courseId);

        // Verify the number of lessons and their details.
        assertEquals(2, result.size(), "The number of lessons should be 2");
        assertEquals("Lesson1", result.get(0).getName(), "The name of the first lesson should be 'Lesson1'");
        assertEquals("Lesson2", result.get(1).getName(), "The name of the second lesson should be 'Lesson2'");

        // Verify that the repository methods were called exactly once.
        verify(lessonRepository, times(1)).findCourseLessons(courseId);
        verify(groupRepository, times(1)).findById(courseId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetCourseLessons_WhenNoLessonsExist() {
        Long courseId = 1L;
        Group course = createGroup(courseId, "Test Courses");

        // Mock behavior when there are no lessons for the course.
        when(groupRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findCourseLessons(courseId)).thenReturn(Collections.emptyList());

        // Call the method.
        List<LessonsDTO> result = lessonsService.getCourseLessons(courseId);

        // Verify that the result list is empty.
        assertTrue(result.isEmpty(), "The lessons list should be empty");
        verify(lessonRepository, times(1)).findCourseLessons(courseId);
        verify(groupRepository, times(1)).findById(courseId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetCourseLessons_WhenNoLessonsAndCourseDoesNotExist() {
        Long courseId = 2L;

        // Mock behavior for a non-existent course.
        when(groupRepository.findById(courseId)).thenReturn(Optional.empty());

        // Expect EntityNotFoundException to be thrown.
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> lessonsService.getCourseLessons(courseId));

        // Verify exception message.
        assertEquals("Courses not found with id: " + courseId, exception.getMessage());
        verify(groupRepository, times(1)).findById(courseId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetLessonDetails_WhenLessonExists() {
        Long lessonId = 1L;
        Lesson lesson = createLesson(lessonId, "Lesson1", "description", "video", null);

        // Mock the repository to return an existing lesson.
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // Call the method being tested.
        LessonDetailDTO result = lessonsService.getLessonDetails(lessonId);

        // Verify the lesson details are as expected.
        assertNotNull(result);
        assertEquals("Lesson1", result.getName());
        assertEquals("description", result.getDescriptionUrl());
        assertEquals("video", result.getVideoUrl());
        verify(lessonRepository, times(1)).findById(lessonId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetLessonDetails_WhenLessonDoesNotExist() {
        Long lessonId = 2L;

        // Mock behavior for a non-existent lesson.
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        // Expect NoSuchElementException to be thrown.
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> lessonsService.getLessonDetails(lessonId));

        // Verify the exception type.
        assertEquals(NoSuchElementException.class, exception.getClass());
        verify(lessonRepository, times(1)).findById(lessonId);
    }

    @Test
    @Transactional
    @Rollback
    public void testDoesCourseExist_WhenCourseExists() {
        Long courseId = 1L;

        // Mock a course that exists.
        when(groupRepository.findById(courseId)).thenReturn(Optional.of(new Group()));

        // Call the method.
        boolean result = lessonsService.doesCourseExist(courseId);

        // Verify that the course exists.
        assertTrue(result, "The course should exist in the database");
        verify(groupRepository, times(1)).findById(courseId);
    }

    @Test
    @Transactional
    @Rollback
    public void testDoesCourseExist_WhenCourseDoesNotExist() {
        Long courseId = 2L;

        // Mock behavior for a course that doesn't exist.
        when(groupRepository.findById(courseId)).thenReturn(Optional.empty());

        // Call the method.
        boolean result = lessonsService.doesCourseExist(courseId);

        // Verify that the course does not exist.
        assertFalse(result, "The course should not exist in the database");
        verify(groupRepository, times(1)).findById(courseId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testIsUserInCourse_WhenUserExists() {
        Long userId = 1L;
        Long lessonId = 1L;
        User user = new User("user", "surname", "38000000000", "username@mail.com", "password");
        List<Group> groups = List.of(new Group("First", Set.of(user)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findAllByLessonId(lessonId)).thenReturn(groups);

        boolean result = lessonsService.isUserInCourse(userId, lessonId);
        assertTrue(result, "The user should exist in the database");
        verify(groupRepository, times(1)).findAllByLessonId(lessonId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testIsUserInCourse_WhenUserDoesNotExistsAndHasGroups() {
        Long userId = 10L;
        Long lessonId = 1L;
        User user = new User("user", "surname", "38000000000", "username@mail.com", "password");
        List<Group> groups = List.of(new Group("First", Set.of(user)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(groupRepository.findAllByLessonId(lessonId)).thenReturn(groups);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> lessonsService.isUserInCourse(userId, lessonId));
        assertEquals(NullPointerException.class, exception.getClass());
        verify(groupRepository, times(1)).findAllByLessonId(lessonId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testIsUserInCourse_WhenUserDoesNotExistsAndNoGroups() {
        Long userId = 1L;
        Long lessonId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(groupRepository.findAllByLessonId(lessonId)).thenReturn(Collections.emptyList());

        boolean result = lessonsService.isUserInCourse(userId, lessonId);
        assertFalse(result, "The user should not exist in the database");
        verify(groupRepository, times(1)).findAllByLessonId(lessonId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetLessonsForCourse_WhenUserAuthorizedAndCourseExist() {
        Long courseId = 1L;
        Long userId = 1L;
        String principalNameAsEmail = "username@mail.com";
        String sessionId = "someSessionId";
        MockHttpSession session = new MockHttpSession();
        User user = new User("user", "surname", "38000000000", principalNameAsEmail, "password");
        user.setId(userId);
        List<LessonsDTO> lessonDTOList = Collections.singletonList(
                new LessonsDTO(1L, "Lesson1", Collections.emptyList())
        );

        when(userService.getPrincipalNameBySessionId(sessionId)).thenReturn(principalNameAsEmail);
        when(userService.findUserByEmail(principalNameAsEmail)).thenReturn(user);
        doReturn(true).when(lessonsService).doesCourseExist(courseId);
        when(groupService.isStudentInGroup(user.getId(), courseId)).thenReturn(true);
        doReturn(lessonDTOList).when(lessonsService).getCourseLessons(courseId);

        ResponseEntity<List<LessonsResponse>> responseEntity = lessonsService.getLessonsForCourse(courseId, sessionId, session);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(lessonDTOList.size(), Objects.requireNonNull(responseEntity.getBody()).size());

        verify(lessonsService, times(1)).doesCourseExist(courseId);
        verify(groupService, times(1)).isStudentInGroup(user.getId(), courseId);
        verify(lessonsService, times(1)).getCourseLessons(courseId);
        verify(userService, times(1)).findUserByEmail(principalNameAsEmail);
        verify(userService, times(1)).getPrincipalNameBySessionId(sessionId);

    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetLessonsForCourse_WhenUserUnauthorized() {
        Long userId = 1L;
        Long courseId = 1L;
        String principalNameAsEmail = "username@mail.com";
        String sessionId = "someSessionId";
        User user = new User("user", "surname", "38000000000", principalNameAsEmail, "password");
        user.setId(userId);
        MockHttpSession session = new MockHttpSession();
        when(userService.getPrincipalNameBySessionId(sessionId)).thenReturn(principalNameAsEmail);
        when(groupService.isStudentInGroup(user.getId(), courseId)).thenReturn(false);
        when(userService.findUserByEmail(principalNameAsEmail)).thenReturn(user);

        ResponseEntity<List<LessonsResponse>> responseEntity = lessonsService.getLessonsForCourse(courseId, sessionId, session);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        verify(userService, times(1)).findUserByEmail(principalNameAsEmail);
        verify(groupService, times(1)).isStudentInGroup(user.getId(), courseId);
        verify(userService, times(1)).getPrincipalNameBySessionId(sessionId);
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    public void testGetLessonsForCourse_WhenUserAuthorizedAndCourseNotExist() {
        Long userId = 1L;
        Long courseId = 1L;
        String principalNameAsEmail = "username@mail.com";
        String sessionId = "someSessionId";
        User user = new User("user", "surname", "38000000000", principalNameAsEmail, "password");
        user.setId(userId);
        MockHttpSession session = new MockHttpSession();
        when(userService.getPrincipalNameBySessionId(sessionId)).thenReturn(principalNameAsEmail);
        when(groupService.isStudentInGroup(user.getId(), courseId)).thenReturn(true);
        when(userService.findUserByEmail(principalNameAsEmail)).thenReturn(user);

        ResponseEntity<List<LessonsResponse>> responseEntity = lessonsService.getLessonsForCourse(courseId, sessionId, session);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(userService, times(1)).findUserByEmail(principalNameAsEmail);
        verify(groupService, times(1)).isStudentInGroup(user.getId(), courseId);
        verify(userService, times(1)).getPrincipalNameBySessionId(sessionId);
    }


    // Helper method to create Lesson objects.
    private Lesson createLesson(Long id, String name, String descriptionUrl, String videoUrl, Group group) {
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setName(name);
        lesson.setDescriptionUrl(descriptionUrl);
        lesson.setVideoUrl(videoUrl);
        if (group != null) {
            lesson.getGroups().add(group);
        }
        return lesson;
    }

    // Helper method to create Group objects.
    private Group createGroup(Long id, String name) {
        Group group = new Group();
        group.setId(id);
        group.setName(name);
        return group;
    }

}
