package academy.prog.julia.integration.controllers;

import academy.prog.julia.controllers.UserCourseController;
import academy.prog.julia.json_responses.UserCoursesResponse;
import academy.prog.julia.services.UserCourseService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


class UserCourseControllerTest {

    @Mock
    private UserCourseService userCourseService;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private UserCourseController userCourseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetUserCourses_Success() {
        // Arrange
        Long userId = 1L;
        String sessionId = "valid-session-id";
        List<UserCoursesResponse> mockResponse = List.of(
                new UserCoursesResponse(userId, "Course 1"),
                new UserCoursesResponse(userId, "Course 2")
        );

        when(userCourseService.getUserCoursesWithSessionValidation(eq(userId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.ok(mockResponse))
        ;

        // Act
        ResponseEntity<List<UserCoursesResponse>> response =
                userCourseController.getUserCourses(userId, sessionId, httpSession)
        ;

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }


    @Test
    void testGetUserCourses_Unauthorized() {
        // Arrange
        Long userId = 1L;
        String sessionId = "invalid-session-id";
        List<UserCoursesResponse> mockResponse = Collections.singletonList(
                new UserCoursesResponse(userId, "You unauthorized from UserCourseController")
        );

        when(userCourseService.getUserCoursesWithSessionValidation(eq(userId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mockResponse))
        ;

        // Act
        ResponseEntity<List<UserCoursesResponse>> response =
                userCourseController.getUserCourses(userId, sessionId, httpSession)
        ;

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }


    @Test
    void testGetUserCourses_UserNotFound() {
        // Arrange
        Long userId = 99L;
        String sessionId = "valid-session-id";

        when(userCourseService.getUserCoursesWithSessionValidation(eq(userId), eq(sessionId), any(HttpSession.class)))
                .thenThrow(new RuntimeException("User not found"))
        ;

        // Act & Assert
        try {
            userCourseController.getUserCourses(userId, sessionId, httpSession);
        } catch (RuntimeException e) {
            assertEquals("User not found", e.getMessage());
        }
    }


    @Test
    void testGetUserCourses_EmptyCourses() {
        // Arrange
        Long userId = 2L;
        String sessionId = "valid-session-id";
        List<UserCoursesResponse> mockResponse = Collections.emptyList();

        when(userCourseService.getUserCoursesWithSessionValidation(eq(userId), eq(sessionId), any(HttpSession.class)))
                .thenReturn(ResponseEntity.ok(mockResponse))
        ;

        // Act
        ResponseEntity<List<UserCoursesResponse>> response =
                userCourseController.getUserCourses(userId, sessionId, httpSession)
        ;

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }


    @Test
    void testGetUserCourses_InvalidUserId() {
        // Arrange
        Long userId = null;
        String sessionId = "valid-session-id";

        // Act & Assert
        try {
            userCourseController.getUserCourses(userId, sessionId, httpSession);
        } catch (IllegalArgumentException e) {
            assertEquals("User ID cannot be null", e.getMessage());
        }
    }


    @Test
    void testGetUserCourses_ServiceError() {
        // Arrange
        Long userId = 1L;
        String sessionId = "valid-session-id";

        when(userCourseService.getUserCoursesWithSessionValidation(eq(userId), eq(sessionId), any(HttpSession.class)))
                .thenThrow(new RuntimeException("Unexpected error"))
        ;

        // Act & Assert
        try {
            userCourseController.getUserCourses(userId, sessionId, httpSession);
        } catch (RuntimeException e) {
            assertEquals("Unexpected error", e.getMessage());
        }
    }


    @Test
    void testGetUserCourses_MissingSessionId() {
        // Arrange
        Long userId = 1L;
        String sessionId = null;

        // Act & Assert
        try {
            userCourseController.getUserCourses(userId, sessionId, httpSession);
        } catch (IllegalArgumentException e) {
            assertEquals("Session ID cannot be null", e.getMessage());
        }
    }


    @Test
    void testGetUserCourses_InvalidDataFormat() {
        // Arrange
        Long userId = -1L; // Некоректний ID користувача
        String sessionId = "valid-session-id";

        // Act & Assert
        try {
            userCourseController.getUserCourses(userId, sessionId, httpSession);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid user ID", e.getMessage());
        }
    }


    @Test
    void testGetUserCourses_ServiceTimeout() {
        // Arrange
        Long userId = 1L;
        String sessionId = "valid-session-id";

        when(userCourseService.getUserCoursesWithSessionValidation(eq(userId), eq(sessionId), any(HttpSession.class)))
                .thenAnswer(invocation -> {
                    Thread.sleep(5000); // Імітація затримки
                    return ResponseEntity.ok(Collections.emptyList());
                })
        ;

        // Act
        ResponseEntity<List<UserCoursesResponse>> response =
                userCourseController.getUserCourses(userId, sessionId, httpSession)
        ;

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

}