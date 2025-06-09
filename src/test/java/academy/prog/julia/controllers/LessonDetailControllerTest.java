package academy.prog.julia.controllers;

import academy.prog.julia.dto.LessonDetailDTO;
import academy.prog.julia.json_responses.LessonDetailResponse;
import academy.prog.julia.services.LessonsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class LessonDetailControllerTest {

    @Mock
    private LessonsService lessonsService;

    @InjectMocks
    private LessonDetailController lessonDetailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLessonDetails_UserInCourse_ReturnsLessonDetails() {
        // Given(Arrange)
        Long lessonId = 1L;
        Long currentUserId = 100L;

        LessonDetailDTO mockDTO = new LessonDetailDTO(
                "Lesson 1",
                "http://example.com/description",
                "http://example.com/video",
                Set.of(),
                Set.of()
        );

        when(lessonsService.getLessonDetails(lessonId)).thenReturn(mockDTO);
        when(lessonsService.isUserInCourse(currentUserId, lessonId)).thenReturn(true);

        // When(Act)
        ResponseEntity<LessonDetailResponse> response =
                lessonDetailController.getLessonDetails(lessonId, currentUserId)
        ;

        // Then(Assert)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Lesson 1", response.getBody().getName());
        verify(lessonsService).getLessonDetails(lessonId);
        verify(lessonsService).isUserInCourse(currentUserId, lessonId);
    }


    @Test
    void testGetLessonDetails_ValidDTO_TransformsCorrectly() {
        // Given(Arrange)
        Long lessonId = 1L;
        Long currentUserId = 100L;

        LessonDetailDTO mockDTO = new LessonDetailDTO(
                "Lesson 1",
                "http://example.com/description",
                "http://example.com/video",
                Set.of(),
                Set.of()
        );

        when(lessonsService.getLessonDetails(lessonId)).thenReturn(mockDTO);
        when(lessonsService.isUserInCourse(currentUserId, lessonId)).thenReturn(true);

        // When(Act)
        ResponseEntity<LessonDetailResponse> response =
                lessonDetailController.getLessonDetails(lessonId, currentUserId)
        ;

        // Then(Assert)
        assertEquals("Lesson 1", response.getBody().getName());
        assertEquals("http://example.com/description", response.getBody().getDescriptionUrl());
        assertEquals("http://example.com/video", response.getBody().getVideoUrl());
    }


    @Test
    void testGetLessonDetails_NoUserIdProvided_ReturnsForbidden() {
        // Given(Arrange)
        Long lessonId = 1L;

        // when(Act)
        ResponseEntity<LessonDetailResponse> response =
                lessonDetailController.getLessonDetails(lessonId, null)
        ;

        // Then(Assert)
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    void testGetLessonDetails_UserNotInCourse_ReturnsForbidden() {
        // Given(Arrange)
        Long lessonId = 1L;
        Long currentUserId = 100L;

        when(lessonsService.getLessonDetails(lessonId))
                .thenReturn(new LessonDetailDTO("Lesson 1", "", "", Set.of(), Set.of()))
        ;
        when(lessonsService.isUserInCourse(currentUserId, lessonId)).thenReturn(false);

        // When(Act)
        ResponseEntity<LessonDetailResponse> response =
                lessonDetailController.getLessonDetails(lessonId, currentUserId)
        ;

        // Then(Assert)
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(lessonsService).getLessonDetails(lessonId);
        verify(lessonsService).isUserInCourse(currentUserId, lessonId);
    }

    @Test
    void testGetLessonDetails_ServiceMethodsCalledInOrder() {
        // Given(Arrange)
        Long lessonId = 1L;
        Long currentUserId = 100L;

        LessonDetailDTO mockDTO = new LessonDetailDTO(
                "Lesson 1",
                "http://example.com/description",
                "http://example.com/video",
                Set.of(),
                Set.of()
        );

        when(lessonsService.getLessonDetails(lessonId)).thenReturn(mockDTO);
        when(lessonsService.isUserInCourse(currentUserId, lessonId)).thenReturn(true);

        // When(Act
        lessonDetailController.getLessonDetails(lessonId, currentUserId);

        // Then(Assert)
        InOrder inOrder = inOrder(lessonsService);
        inOrder.verify(lessonsService).getLessonDetails(lessonId);
        inOrder.verify(lessonsService).isUserInCourse(currentUserId, lessonId);
    }


    @Test
    void testLessonDetailDTO_MissingName_ThrowsNullPointerException() {
        // Given(Arrange)
        try {
            // When(Act)
            LessonDetailDTO dto = new LessonDetailDTO(
                    null,
                    "http://example.com/description",
                    "http://example.com/video",
                    Set.of(),
                    Set.of()
            );
        } catch (NullPointerException e) {
            // Then(Assert)
            assertEquals("name cannot be null", e.getMessage());
        }
    }


    @Test
    void testLessonDetailDTO_MissingDescriptionUrl_ThrowsNullPointerException() {
        // given(Arrange)
        try {
            // When(Act)
            LessonDetailDTO dto = new LessonDetailDTO(
                    "Lesson 1",
                    null,
                    "http://example.com/video",
                    Set.of(),
                    Set.of()
            );
        } catch (NullPointerException e) {
            // Then(Assert)
            assertEquals("descriptionUrl cannot be null", e.getMessage());
        }
    }


    @Test
    void testLessonDetailDTO_MissingVideoUrl_ThrowsNullPointerException() {
        // Given(Arrange)
        try {
            // When(Act)
            LessonDetailDTO dto = new LessonDetailDTO(
                    "Lesson 1",
                    "http://example.com/description",
                    null,
                    Set.of(),
                    Set.of()
            );
        } catch (NullPointerException e) {
            // Then(Assert)
            assertEquals("videoUrl cannot be null", e.getMessage());
        }
    }


    @Test
    void testLessonDetailDTO_MissingTasks_ThrowsNullPointerException() {
        // Given(Arrange)
        try {
            // When(Act)
            LessonDetailDTO dto = new LessonDetailDTO(
                    "Lesson 1",
                    "http://example.com/description",
                    "http://example.com/video",
                    null,
                    Set.of()
            );
        } catch (NullPointerException e) {
            // Then(Assert)
            assertEquals("tasks cannot be null", e.getMessage());
        }
    }


    @Test
    void testLessonDetailDTO_MissingTests_ThrowsNullPointerException() {
        // Given(Arrange)
        try {
            // When(Act)
            LessonDetailDTO dto = new LessonDetailDTO(
                    "Lesson 1",
                    "http://example.com/description",
                    "http://example.com/video",
                    Set.of(),
                    null
            );
        } catch (NullPointerException e) {
            // Then(Assert)
            assertEquals("tests cannot be null", e.getMessage());
        }
    }

}
