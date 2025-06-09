package academy.prog.julia.controllers;

import academy.prog.julia.dto.TaskAnswerDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;
import academy.prog.julia.json_requests.TaskAnswerStartRequest;
import academy.prog.julia.json_responses.LessonProgressResponse;
import academy.prog.julia.json_responses.TaskAnswerResponse;
import academy.prog.julia.json_responses.TaskDetailsResponse;
import academy.prog.julia.json_responses.TaskProgressResponse;
import academy.prog.julia.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }


    @Test
    void testViewTaskDetails() throws Exception {
        Long taskId = 1L;

        TaskAnswerDTO mockTaskAnswerDTO = mock(TaskAnswerDTO.class);
        when(mockTaskAnswerDTO.getAnswerId()).thenReturn(1L);
        when(mockTaskAnswerDTO.getAnswerUrl()).thenReturn("http://example.com/answer");
        when(mockTaskAnswerDTO.getCourseId()).thenReturn(101L);
        when(mockTaskAnswerDTO.getLessonId()).thenReturn(202L);
        when(mockTaskAnswerDTO.getLessonNum()).thenReturn(1);
        when(mockTaskAnswerDTO.getTaskId()).thenReturn(taskId);
        when(mockTaskAnswerDTO.getTaskName()).thenReturn("Task Name");
        when(mockTaskAnswerDTO.getCourse()).thenReturn("Math");
        when(mockTaskAnswerDTO.getDescription()).thenReturn("Task description");
        when(mockTaskAnswerDTO.getIsPassed()).thenReturn(true);
        when(mockTaskAnswerDTO.getIsCorrection()).thenReturn(false);
        when(mockTaskAnswerDTO.getMessageForCorrection()).thenReturn("Needs review");
        when(mockTaskAnswerDTO.getIsRead()).thenReturn(true);
        when(mockTaskAnswerDTO.getSubmittedDate()).thenReturn(new Date());

        UserFromAnswerTaskDTO student = mock(UserFromAnswerTaskDTO.class);
        when(mockTaskAnswerDTO.getStudent()).thenReturn(student);

        Set<TaskAnswerDTO> answers = Collections.singleton(mockTaskAnswerDTO);

        TaskDetailsResponse taskDetailsResponse = new TaskDetailsResponse(
                "Task Name",
                "http://example.com/description",
                answers,
                LocalDate.now(),
                true
        );

        when(taskService.getTaskDetailsAsTaskDetailsResponse(taskId)).thenReturn(taskDetailsResponse);

        mockMvc.perform(get("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
        ;

        verify(taskService, times(1)).getTaskDetailsAsTaskDetailsResponse(taskId);
    }


    @Test
    void testSubmitTaskAnswer() throws Exception {
        TaskAnswerStartRequest request = new TaskAnswerStartRequest();
        MockHttpSession session = new MockHttpSession();

        when(taskService.submitTaskAnswerWithChecks(any(TaskAnswerStartRequest.class), eq(session)))
                .thenReturn(ResponseEntity.ok().build())
        ;

        mockMvc.perform(post("/api/tasks/submit")
                        .contentType("application/json")
                        .content("{}")
                        .session(session))
                .andExpect(status().isOk())
        ;

        verify(taskService, times(1))
                .submitTaskAnswerWithChecks(any(TaskAnswerStartRequest.class), eq(session))
        ;
    }


    @Test
    void testViewTaskByUser() throws Exception {
        Long userId = 1L;
        Long taskId = 1L;
        TaskAnswerResponse response = new TaskAnswerResponse(
                1L,
                "http://example.com/answer",
                1L,
                1L,
                1,
                1L,
                "Task Name",
                "Course Name",
                "Task Description",
                true,
                false,
                "Please correct your answer",
                true,
                new Date(),
                new UserFromAnswerTaskDTO()
        );
        when(taskService.getTaskAnswerAsTaskAnswerResponse(taskId, userId)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{userId}/{taskId}", userId, taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
        ;

        verify(taskService, times(1)).getTaskAnswerAsTaskAnswerResponse(taskId, userId);
    }


    @Test
    void testGetTaskAnswersByUserId() throws Exception {
        Long userId = 1L;
        TaskAnswerResponse response = new TaskAnswerResponse(
                1L,
                "http://example.com/answer",
                1L,
                1L,
                1,
                1L,
                "Task Name",
                "Course Name",
                "Task Description",
                true,
                false,
                "Please correct your answer",
                true,
                new Date(),
                new UserFromAnswerTaskDTO()
        );

        when(taskService.getTaskAnswersByUserId(userId)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/tasks/answers/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
        ;

        verify(taskService, times(1)).getTaskAnswersByUserId(userId);
    }


    @Test
    void testViewTaskProgress() throws Exception {
        Long userId = 1L;
        Long courseId = 1L;

        TaskProgressResponse response = new TaskProgressResponse(1L, 75);

        when(taskService.getTaskProgress(userId, courseId)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{userId}/{courseId}/progress", userId, courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.progress").value(75))
        ;

        verify(taskService, times(1)).getTaskProgress(userId, courseId);
    }


    @Test
    void testViewLessonProgress() throws Exception {
        Long userId = 1L;
        Long lessonId = 1L;

        LessonProgressResponse response = new LessonProgressResponse(1L, "In Progress", 75);

        when(taskService.getLessonProgressAdLessonProgressResponse(userId, lessonId)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/progress/{userId}/{lessonId}", userId, lessonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.progress").value("In Progress"))
                .andExpect(jsonPath("$.percent").value(75))
        ;

        verify(taskService, times(1)).getLessonProgressAdLessonProgressResponse(userId, lessonId);
    }

}