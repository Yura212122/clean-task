package academy.prog.julia.integration.controllers;

import academy.prog.julia.controllers.TaskController;
import academy.prog.julia.json_requests.TaskAnswerStartRequest;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.TaskAnswerRepository;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.services.TaskService;
import academy.prog.julia.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.*;

import static academy.prog.julia.services.TaskService.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class TaskControllerSubmitTest {

    @Autowired
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void setUpAuthentication(User currentUser) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    public void testNotNull() {
        assertThat(taskController).isNotNull();
    }


    @Test
    @Sql(value = {"/create-user-before.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-user-after.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSubmitTaskAnswer_Failure_Forbidden() {
        Long taskId = 2L;
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        String answerUrl = "http://example.com/answer";
        String course = "course";

        Group group = mock(Group.class);
        when(group.getId()).thenReturn(1L);
        when(group.getName()).thenReturn("Group A");

        User currentUser = new User();
        currentUser.setId(1L);

        Task task = mock(Task.class);
        when(task.getId()).thenReturn(taskId);
        when(task.getName()).thenReturn("Test Task");

        Lesson lesson = mock(Lesson.class);
        when(lesson.getId()).thenReturn(lessonId);
        when(lesson.getGroups()).thenReturn(Collections.singleton(group));

        setUpAuthentication(currentUser);

        when(task.getLesson()).thenReturn(lesson);

        Set<User> clients = new HashSet<>();
        clients.add(currentUser);
        when(group.getClients()).thenReturn(clients);

        when(task.getLesson().getGroups()).thenReturn(Collections.singleton(group));

        GroupService groupService = mock(GroupService.class);
        when(groupService.isStudentInGroup(currentUser.getId(), group.getId())).thenReturn(false);

        doNothing()
                .when(taskService)
                .submitTaskAnswer(anyLong(), anyString(), anyLong(), anyString(), anyLong(), anyInt(), eq(currentUser))
        ;

        MockHttpSession session = new MockHttpSession();
        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, currentUser.getId(), courseId, lessonId, lessonNum, taskId, course);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You don't have permission to do this", responseBody.get("message"));

        assertEquals(2L, responseBody.get("taskId"));

        verify(taskService, times(0))
                .submitTaskAnswer(anyLong(), anyString(), anyLong(), anyString(), anyLong(), anyInt(), eq(currentUser))
        ;
    }


    @Test
    public void testSubmitTaskAnswer_Unauthorized() {
        Long taskId = 2L;
        Long courseId = 2L;
        Long lessonId = 2L;
        Integer lessonNum = 2;
        String answerUrl = "http://example.com/answer";
        String course = "course";

        MockHttpSession session = new MockHttpSession();

        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, null, courseId, lessonId, lessonNum, taskId, course);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not authenticated", responseBody.get("message"));
    }


    @Test
    public void testSubmitTaskAnswer_Forbidden() {
        Long taskId = 3L;
        Long courseId = 3L;
        Long lessonId = 3L;
        Integer lessonNum = 3;
        String answerUrl = "http://example.com/answer";
        String course = "course";
        String description = "description";
        User currentUser = new User();
        currentUser.setId(4L);
        currentUser.setName("ForbiddenUser");
        currentUser.setSurname("Bbb");

        setUpAuthentication(currentUser);

        MockHttpSession session = new MockHttpSession();
        Long userId = currentUser.getId();
        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, userId, courseId, lessonId, lessonNum, taskId, course);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You don't have permission to do this",
                ((Map<String, Object>) response.getBody()).get("message")
        );
    }


    @Test
    public void testSubmitTaskAnswer_NotAuthenticated() {
        Long taskId = 2L;
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        String answerUrl = "http://example.com/answer";
        String course = "course";

        MockHttpSession session = new MockHttpSession();

        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, null, courseId, lessonId, lessonNum, taskId, course);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, session);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not authenticated", responseBody.get("message"));
    }


    @Test
    public void testSubmitTaskAnswer_UserNotInGroup() {
        Long taskId = 2L;
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        String answerUrl = "http://example.com/answer";
        String course = "course";

        User currentUser = new User();
        currentUser.setId(1L);

        Group group = mock(Group.class);
        when(group.getId()).thenReturn(1L);
        when(group.getName()).thenReturn("Group A");

        when(group.getClients()).thenReturn(Collections.emptySet());

        Task task = mock(Task.class);
        when(task.getId()).thenReturn(taskId);
        when(task.getName()).thenReturn("Test Task");

        Lesson lesson = mock(Lesson.class);
        when(lesson.getId()).thenReturn(lessonId);
        when(lesson.getGroups()).thenReturn(Collections.singleton(group));

        setUpAuthentication(currentUser);

        when(task.getLesson()).thenReturn(lesson);

        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, currentUser.getId(), courseId, lessonId, lessonNum, taskId, course);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, new MockHttpSession());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You don't have permission to do this", responseBody.get("message"));
    }


    @Test
    public void testSubmitTaskAnswer_UserWithoutPermission() {
        Long taskId = 2L;
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        String answerUrl = "http://example.com/answer";
        String course = "course";

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("NoPermissionUser");

        Group group = mock(Group.class);
        when(group.getId()).thenReturn(1L);
        when(group.getName()).thenReturn("Group A");

        Task task = mock(Task.class);
        when(task.getId()).thenReturn(taskId);
        when(task.getName()).thenReturn("Test Task");

        Lesson lesson = mock(Lesson.class);
        when(lesson.getId()).thenReturn(lessonId);
        when(lesson.getGroups()).thenReturn(Collections.singleton(group));

        setUpAuthentication(currentUser);

        when(task.getLesson()).thenReturn(lesson);

        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, currentUser.getId(), courseId, lessonId, lessonNum, taskId, course);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, new MockHttpSession());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You don't have permission to do this", responseBody.get("message"));
    }


    @Test
    @Sql(value = {"/create-user-before.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-user-after.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSubmitTaskAnswer_EmptyData() {
        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest("", 2L, null, null, null, null, "");

        User currentUser = new User();
        currentUser.setId(2L);
        setUpAuthentication(currentUser);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, new MockHttpSession());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid task ID provided.", responseBody.get("message"));
    }


    @Test
    @Sql(value = {"/create-user-before.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-user-after.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSubmitTaskAnswer_InvalidData() {
        Long invalidTaskId = -1L;
        Long validUserId = 2L;
        String answerUrl = "http://example.com/answer";

        TaskAnswerStartRequest taskAnswerStartRequest =
                new TaskAnswerStartRequest(answerUrl, validUserId, 1L, 1L, 1, invalidTaskId, "course");

        User currentUser = new User();
        currentUser.setId(validUserId);
        setUpAuthentication(currentUser);

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, new MockHttpSession());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid task ID provided.", responseBody.get("message"));
    }


    @Test
    public void testSubmitTaskAnswerWithChecks_InvalidCourseId() {
        String answerUrl = "https://github.com/yourGitHubName/nameOfRepository";
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(2L);
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        Long taskId = 2L;
        String course = "course";

        TaskAnswerStartRequest taskAnswerStartRequest = new TaskAnswerStartRequest(
                null, currentUser.getId(), null, lessonId, lessonNum, taskId, course
        );

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("course"));
        assertTrue(responseBody.containsKey("status"));
        assertTrue(responseBody.containsKey("answerUrl"));
        assertTrue(responseBody.containsKey("taskId"));

        assertEquals("Invalid course ID provided.", responseBody.get("message"));
        assertEquals("", responseBody.get("course"));
        assertEquals("failed", responseBody.get("status"));
        assertEquals("", responseBody.get("answerUrl"));
        assertEquals(-1L, responseBody.get("taskId"));
    }


    @Test
    public void testSubmitTaskAnswerWithChecks_InvalidLessonId() {
        String answerUrl = "https://github.com/yourGitHubName/nameOfRepository";
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(2L);
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        Long taskId = 2L;
        String course = "course";

        TaskAnswerStartRequest taskAnswerStartRequest = new TaskAnswerStartRequest(
                null, currentUser.getId(), courseId, null, lessonNum, taskId, course
        );

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("course"));
        assertTrue(responseBody.containsKey("status"));
        assertTrue(responseBody.containsKey("answerUrl"));
        assertTrue(responseBody.containsKey("taskId"));

        assertEquals("Invalid lesson ID provided.", responseBody.get("message"));
        assertEquals("", responseBody.get("course"));
        assertEquals("failed", responseBody.get("status"));
        assertEquals("", responseBody.get("answerUrl"));
        assertEquals(-1L, responseBody.get("taskId"));
    }


    @Test
    public void testSubmitTaskAnswerWithChecks_InvalidAnswerUrl() {
        String answerUrl = "https://invalid-url";
        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(2L);
        Long courseId = 1L;
        Long lessonId = 5L;
        Integer lessonNum = 1;
        Long taskId = 2L;
        String course = "course";

        TaskAnswerStartRequest taskAnswerStartRequest = new TaskAnswerStartRequest(
                null, currentUser.getId(), courseId, null, lessonNum, taskId, course
        );

        ResponseEntity<Object> response = taskController.submitTaskAnswer(taskAnswerStartRequest, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("course"));
        assertTrue(responseBody.containsKey("status"));
        assertTrue(responseBody.containsKey("answerUrl"));
        assertTrue(responseBody.containsKey("taskId"));

        assertEquals("Invalid lesson ID provided.", responseBody.get("message"));
        assertEquals("", responseBody.get("course"));
        assertEquals("failed", responseBody.get("status"));
        assertEquals("", responseBody.get("answerUrl"));
        assertEquals(-1L, responseBody.get("taskId"));
    }

}
