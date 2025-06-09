package academy.prog.julia.controllers;

import academy.prog.julia.dto.TaskAnswerDTO;
import academy.prog.julia.dto.TaskSubmissionDTO;
import academy.prog.julia.dto.TestSubmissionDTO;
import academy.prog.julia.json_requests.TaskAnswerRequest;
import academy.prog.julia.services.TeacherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * TeacherController is responsible for handling HTTP requests related to teacher actions,
 * such as fetching submissions, grading tasks, and downloading resources.
 */
@RestController
@RequestMapping("api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * Constructor to inject TeacherService dependency.
     *
     * @param teacherService service class responsible for handling business logic related to teacher actions.
     */
    public TeacherController(
            TeacherService teacherService
    ) {
        this.teacherService = teacherService;
    }

    /**
     * Retrieves a paginated list of all task submissions.
     *
     * @param page the current page number (default 0).
     * @param size the number of records per page (default 10).
     * @return a paginated response of task submissions.
     */
    @GetMapping("tasksSubmissionByTeacherId/{teacherId}")
    public ResponseEntity<Page<TaskSubmissionDTO>> getAllTasksSubmission(
            @PathVariable("teacherId") Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return teacherService.getAllTasksSubmissionDTO(page, size, teacherId);
    }

    /**
     * Retrieves a paginated list of all test submissions.
     *
     * @param page the current page number (default 0).
     * @param size the number of records per page (default 10).
     * @return a paginated response of test submissions.
     */
    @GetMapping("/testsSubmissionByTeacherId/{teacherId}")
    public ResponseEntity<Page<TestSubmissionDTO>> getAllTestsSubmission(
            @PathVariable("teacherId") Long teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return teacherService.getAllTestsSubmissionDTO(page, size, teacherId);
    }

    /**
     * Retrieves a specific task submission by its task ID.
     *
     * @param taskId the ID of the task submission to retrieve.
     * @return the details of the specific task submission.
     */
    @GetMapping("/tasksSubmission/{taskId}")
    public ResponseEntity<TaskAnswerDTO> getTaskSubmission(
            @PathVariable Long taskId
    ) {
        return teacherService.getTaskSubmissionDTO(taskId);
    }

    /**
     * Grades a specific task submission based on the provided data in the request body.
     * Stores necessary session data for task submission context.
     *
     * @param taskAnswerRequest the request object containing details for grading the task.
     * @param session           the HTTP session for storing temporary data during grading.
     * @return a response indicating the success or failure of the grading operation.
     */
    @PostMapping("/tasksSubmission/grade")
    public ResponseEntity<Map<String, Object>> gradeTaskSubmission(
            @RequestBody TaskAnswerRequest taskAnswerRequest,
            HttpSession session
    ) {
        return teacherService.gradeTaskSubmissionDTO(taskAnswerRequest, session);
    }

    @GetMapping("/tasksSubmission/{taskId}/download-zp")

    /**
     * Downloads a ZIP file containing a student's submission for a specific task.
     *
     * @param taskId        the ID of the task for which the ZIP is to be downloaded.
     * @param studentName   the student's first name.
     * @param studentSurname the student's last name.
     * @param studentEmail  the student's email address.
     * @param groupName     the name of the group the student belongs to.
     * @param sessionId     the authorization token of the current session.
     * @return a ByteArrayResource containing the ZIP file data.
     */
    public ResponseEntity<ByteArrayResource> downloadZip(
            @PathVariable Long taskId,
            @RequestParam(value = "studentName") String studentName,
            @RequestParam(value = "studentSurname") String studentSurname,
            @RequestParam(value = "studentEmail") String studentEmail,
            @RequestParam(value = "groupName") String groupName,
            @RequestHeader("Authorization") String sessionId
    ) {
        return teacherService.downloadZip(taskId, studentName, studentSurname, studentEmail, groupName, sessionId);
    }

}
