package academy.prog.julia.services;

import academy.prog.julia.dto.*;
import academy.prog.julia.json_requests.TaskAnswerRequest;
import academy.prog.julia.model.TeacherUser;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private TestService testService;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private TeacherService teacherService;

    private UserFromAnswerTaskDTO studentDto;
    private TeacherUser teacherUser;
    private TaskAnswerDTO taskAnswerDto;

    @BeforeEach
    void setUp() {
        studentDto = new UserFromAnswerTaskDTO(
                1L,
                "Tony",
                "Romanov",
                "+123456789",
                "example@example.com",
                "@test",
                Set.of("Group1", "Group2")
        );
        teacherUser = new TeacherUser(
                "TeacherName",
                "TeacherSurname",
                "+123456789",
                "teacher@example.com",
                "password"
        );
        taskAnswerDto = new TaskAnswerDTO(
                10L, "https://github.com", 1L, 2L, 2, 30L,
                "Example", "Test", "Description",
                true, false, "", true, new Date(), studentDto
        );
    }


    @Test
    void getTaskSubmissionDTO_Found() {
        when(taskService.getTaskSubmission(10L)).thenReturn(taskAnswerDto);

        ResponseEntity<TaskAnswerDTO> response = teacherService.getTaskSubmissionDTO(10L);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getAnswerId());
        assertEquals(1L, response.getBody().getCourseId());
    }

    @Test
    void getTaskSubmissionDTO_NotFound() {
        when(taskService.getTaskSubmission(11L)).thenReturn(null);

        ResponseEntity<TaskAnswerDTO> response = teacherService.getTaskSubmissionDTO(11L);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void getAllTasksSubmissionDTO() {
        int page = 0;
        int size = 10;
        Long teacherId = 1L;
        @SuppressWarnings("unchecked")
        Page<TaskSubmissionDTO> mockPage = mock(Page.class);
        when(taskService.getAllTaskSubmissionsByTeacherId(page, size, teacherId)).thenReturn(mockPage);

        ResponseEntity<Page<TaskSubmissionDTO>> response = teacherService.getAllTasksSubmissionDTO(page, size, teacherId);
        assertEquals(200, response.getStatusCode().value());
        assertSame(mockPage, response.getBody());
    }

    @Test
    void getAllTestsSubmissionDTO() {
        int page = 0;
        int size = 10;
        Long teacherId = 2L;
        @SuppressWarnings("unchecked")
        Page<TestSubmissionDTO> mockTestPage = mock(Page.class);
        when(testService.getAllTestSubmissionsByTeacherId(page, size, teacherId)).thenReturn(mockTestPage);

        ResponseEntity<Page<TestSubmissionDTO>> response = teacherService.getAllTestsSubmissionDTO(page, size, teacherId);
        assertEquals(200, response.getStatusCode().value());
        assertSame(mockTestPage, response.getBody());
    }

    @Test
    void gradeTaskSubmissionDTO_Success() {
        Long answerId = 54L;
        boolean isCorrection = true;
        boolean isPassed = false;
        String messageForCorrection = "There is an error in your code. You need to fix it!!!";
        boolean isRead = true;

        TaskAnswerRequest request = new TaskAnswerRequest(answerId, isPassed, isCorrection, messageForCorrection, isRead);

        when(taskService.getTaskSubmission(answerId)).thenReturn(taskAnswerDto);

        ResponseEntity<?> response = teacherService.gradeTaskSubmissionDTO(request, httpSession);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());

        verify(taskService, times(1)).gradeTaskSubmission(answerId, isCorrection, isPassed, messageForCorrection, isRead);
        verify(userService, times(1)).sendMessageAboutCorrection(studentDto, "Test");

        verify(httpSession).setAttribute("TaskAnswerRequestGetAnswerId", answerId);
        verify(httpSession).setAttribute("TaskAnswerGetIsCorrection", isCorrection);
        verify(httpSession).setAttribute("TaskAnswerGetIsPassed", isPassed);
        verify(httpSession).setAttribute("TaskAnswerGetMessageForCorrection", messageForCorrection);
        verify(httpSession).setAttribute("TaskAnswerGetIsRead", isRead);
    }

    @Test
    void gradeTaskSubmissionDTO_TaskNotFound() {
        Long answerId = 300L;
        TaskAnswerRequest request = new TaskAnswerRequest(answerId, false, false, "", false);

        when(taskService.getTaskSubmission(answerId)).thenReturn(null);

        ResponseEntity<?> response = teacherService.gradeTaskSubmissionDTO(request, httpSession);
        assertEquals(404, response.getStatusCode().value());
        assertInstanceOf(Map.class, response.getBody());
        verify(taskService, never()).gradeTaskSubmission(anyLong(), anyBoolean(), anyBoolean(), anyString(), anyBoolean());
        verify(userService, never()).sendMessageAboutCorrection(any(), any());
    }

    @Test
    void downloadZip_Success() {
        Long taskId = 400L;
        String sessionId = "1";
        when(userService.getPrincipalNameBySessionId(sessionId)).thenReturn(teacherUser.getEmail());
        when(userService.findUserByEmail(teacherUser.getEmail())).thenReturn(teacherUser);
        byte[] zipContents = new byte[]{1, 2, 3, 4};
        when(taskService.getZipAnswerFile(taskId)).thenReturn(zipContents);
        ResponseEntity<ByteArrayResource> response =
                teacherService.downloadZip(
                        taskId,
                        studentDto.getName(),
                        studentDto.getSurname(),
                        studentDto.getEmail(),
                        "Group1",
                        sessionId
                );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(ByteArrayResource.class, response.getBody());
        assertEquals(zipContents.length, response.getBody().contentLength());
    }

    @Test
    void downloadZip_Failure_FileNotFound() {
        Long taskId = 500L;
        String sessionId = "1";
        when(userService.getPrincipalNameBySessionId(sessionId)).thenReturn(teacherUser.getEmail());
        when(taskService.getZipAnswerFile(taskId)).thenReturn(null);
        ResponseEntity<ByteArrayResource> response =
                teacherService.downloadZip(
                        taskId,
                        studentDto.getName(),
                        studentDto.getSurname(),
                        studentDto.getEmail(),
                        "Group1",
                        sessionId
                );

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
