package academy.prog.julia.controllers;

import academy.prog.julia.json_requests.TaskAnswerStartRequest;
import academy.prog.julia.json_responses.TaskAnswerResponse;
import academy.prog.julia.json_responses.TaskDetailsResponse;
import academy.prog.julia.json_responses.TaskProgressResponse;
import academy.prog.julia.json_responses.LessonProgressResponse;
import academy.prog.julia.services.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling operations related to tasks.
 * This controller provides endpoints for task details, submission, and progress tracking.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Constructor to inject the TaskService.
     *
     * @param taskService service layer for managing tasks
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Retrieves the details of a specific task.
     *
     * @param taskId the ID of the task to retrieve
     * @return ResponseEntity containing the task details in the TaskDetailsResponse format
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDetailsResponse> viewTaskDetails(
            @PathVariable Long taskId
    ) {
        TaskDetailsResponse response = taskService.getTaskDetailsAsTaskDetailsResponse(taskId);
        return ResponseEntity.ok(response);
    }

    /**
     * Submits a task answer. The answer is processed and validated in the service layer.
     *
     * @param taskAnswerStartRequest object containing the task answer details
     * @param session HttpSession object to track user-specific session information
     * @return ResponseEntity containing either success or error information
     */
    @PostMapping("/submit")
    public ResponseEntity<Object> submitTaskAnswer(
            @RequestBody TaskAnswerStartRequest taskAnswerStartRequest,
            HttpSession session
    ) {
        return taskService.submitTaskAnswerWithChecks(taskAnswerStartRequest, session);
    }

    /**
     * Retrieves a specific task answer by a user.
     *
     * @param userId the ID of the user
     * @param taskId the ID of the task
     * @return ResponseEntity containing the task answer details for the specified user
     */
    @GetMapping("/{userId}/{taskId}")
    public ResponseEntity<TaskAnswerResponse> viewTaskByUser(
            @PathVariable Long userId,
            @PathVariable Long taskId
    ) {
        TaskAnswerResponse response = taskService.getTaskAnswerAsTaskAnswerResponse(taskId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all task answers submitted by a specific user.
     *
     * @param userId the ID of the user whose task answers are to be retrieved
     * @return ResponseEntity containing a list of task answers in TaskAnswerResponse format
     */
    @GetMapping("/answers/{userId}")
    public ResponseEntity<List<TaskAnswerResponse>> getTaskAnswersByUserId(
            @PathVariable Long userId
    ) {
        List<TaskAnswerResponse> responses = taskService.getTaskAnswersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves the progress of a specific user in a specific course.
     *
     * @param userId the ID of the user
     * @param courseId the ID of the course
     * @return ResponseEntity containing the user's task progress for the course
     */
    @GetMapping("/{userId}/{courseId}/progress")
    public ResponseEntity<TaskProgressResponse> viewTaskProgress(
            @PathVariable Long userId,
            @PathVariable Long courseId
    ) {
        TaskProgressResponse response = taskService.getTaskProgress(userId, courseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the progress of a specific user in a specific lesson.
     *
     * @param userId the ID of the user
     * @param lessonId the ID of the lesson
     * @return ResponseEntity containing the user's lesson progress
     */
    @GetMapping("/progress/{userId}/{lessonId}")
    public ResponseEntity<LessonProgressResponse> viewLessonProgress(
            @PathVariable Long userId,
            @PathVariable Long lessonId
    ) {
        LessonProgressResponse response =
                taskService.getLessonProgressAdLessonProgressResponse(userId, lessonId);
        return ResponseEntity.ok(response);
    }
    
}

