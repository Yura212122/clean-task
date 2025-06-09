package academy.prog.julia.controllers;

import academy.prog.julia.json_responses.LessonsResponse;
import academy.prog.julia.dto.TasksGetNameDTO;
import academy.prog.julia.services.LessonsService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LessonControllerTest {

    private LessonsService lessonsService;
    private LessonsController lessonsController;

    @BeforeEach
    void setUp() {
        lessonsService = Mockito.mock(LessonsService.class);
        lessonsController = new LessonsController(lessonsService);
    }


    @Test
    void testGetCourseLessons_Success() {
        // given
        Long courseId = 1L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        List<TasksGetNameDTO> tasks = List.of(
                new TasksGetNameDTO("Task 1"),
                new TasksGetNameDTO("Task 2")
        );

        List<LessonsResponse> mockResponse = List.of(
                new LessonsResponse(1L, "Lesson 1", tasks),
                new LessonsResponse(2L, "Lesson 2", new ArrayList<>())
        );

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        LessonsResponse lesson1 = response.getBody().get(0);
        assertEquals(1L, lesson1.getId());
        assertEquals("Lesson 1", lesson1.getName());
        assertNotNull(lesson1.getTasks());
        assertEquals(2, lesson1.getTasks().size());
        assertEquals("Task 1", lesson1.getTasks().get(0).getName());

        LessonsResponse lesson2 = response.getBody().get(1);
        assertEquals(2L, lesson2.getId());
        assertEquals("Lesson 2", lesson2.getName());
        assertTrue(lesson2.getTasks().isEmpty());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_Unauthorized() {
        // given
        Long courseId = 1L;
        String sessionId = "invalid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(401).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // thn
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_EmptyLessons() {
        // given
        Long courseId = 1L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        List<LessonsResponse> emptyResponse = new ArrayList<>();

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.ok(emptyResponse));

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_CourseNotFound() {
        // given
        Long invalidCourseId = 999L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(invalidCourseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(404).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(invalidCourseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(invalidCourseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_InternalServerError() {
        // given
        Long courseId = 1L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(500).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_InvalidSessionIdFormat() {
        // given
        Long courseId = 1L;
        String invalidSessionId = "invalid-format";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(invalidSessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(401).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, invalidSessionId, session);

        // then
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(invalidSessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_LessonsWithoutTasks() {
        // given
        Long courseId = 1L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        List<LessonsResponse> mockResponse = List.of(
                new LessonsResponse(1L, "Lesson 1", null),
                new LessonsResponse(2L, "Lesson 2", null)
        );

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        LessonsResponse lesson1 = response.getBody().get(0);
        assertEquals(1L, lesson1.getId());
        assertEquals("Lesson 1", lesson1.getName());
        assertNull(lesson1.getTasks());

        LessonsResponse lesson2 = response.getBody().get(1);
        assertEquals(2L, lesson2.getId());
        assertEquals("Lesson 2", lesson2.getName());
        assertNull(lesson2.getTasks());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_CourseIdZero() {
        // given
        Long courseId = 0L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(400).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_NegativeCourseId() {
        // given
        Long courseId = -1L;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(400).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }


    @Test
    void testGetCourseLessons_LargeCourseId() {
        // given
        Long courseId = Long.MAX_VALUE;
        String sessionId = "valid-session-id";
        HttpSession session = Mockito.mock(HttpSession.class);

        when(lessonsService.getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(404).build());

        // when
        ResponseEntity<List<LessonsResponse>> response =
                lessonsController.getCourseLessons(courseId, sessionId, session);

        // then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(lessonsService, times(1))
                .getLessonsForCourse(eq(courseId), eq(sessionId), any(HttpSession.class));
    }

}