package academy.prog.julia.services;

import academy.prog.julia.dto.TaskAnswerDTO;
import academy.prog.julia.dto.TaskSubmissionDTO;
import academy.prog.julia.dto.TestSubmissionDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;
import academy.prog.julia.json_requests.TaskAnswerRequest;
import academy.prog.julia.model.UserRole;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for handling business logic related to teacher operations.
 */
@Service
public class TeacherService {

    private static final Logger LOGGER = LogManager.getLogger(TeacherService.class);

    private final UserService userService;
    private final TaskService taskService;
    private final TestService testService;

    /**
     * Constructor for TeacherService.
     * Injects required services using constructor-based dependency injection.
     *
     * @param userService   Service for handling user-related operations.
     * @param taskService   Service for handling task-related operations.
     * @param testService   Service for handling test-related operations.
     */
    public TeacherService(
            UserService userService,
            TaskService taskService,
            TestService testService
    ) {
        this.userService = userService;
        this.taskService = taskService;
        this.testService = testService;
    }

    /**
     * Retrieves paginated task submissions for a specific teacher by their ID.
     * Useful for displaying tasks assigned by the teacher with pagination support.
     *
     * @param page       The current page number for pagination.
     * @param size       The size of the page.
     * @param teacherId  The ID of the teacher to retrieve tasks for.
     * @return A paginated list of task submissions.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Page<TaskSubmissionDTO>> getAllTasksSubmissionDTO(
            int page,
            int size,
            Long teacherId
    ) {
        Page<TaskSubmissionDTO> allTaskSubmissions =
                taskService.getAllTaskSubmissionsByTeacherId(page, size, teacherId);

        return ResponseEntity.ok(allTaskSubmissions);
    }

    /**
     * Retrieves paginated test submissions for a specific teacher by their ID.
     * Useful for displaying test submissions associated with a teacher with pagination.
     *
     * @param page       The current page number for pagination.
     * @param size       The size of the page.
     * @param teacherId  The ID of the teacher to retrieve tests for.
     * @return A paginated list of test submissions.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Page<TestSubmissionDTO>> getAllTestsSubmissionDTO(
            int page,
            int size,
            Long teacherId
    ) {
        Page<TestSubmissionDTO> allTestSubmissions =
                testService.getAllTestSubmissionsByTeacherId(page, size, teacherId);

        return ResponseEntity.ok(allTestSubmissions);
    }

    /**
     * Retrieves a specific task submission by its ID.
     * If the task is found, it is returned. If not, a 404 error response is returned.
     *
     * @param taskId The ID of the task to retrieve.
     * @return The details of the task submission or a 404 error if not found.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<TaskAnswerDTO> getTaskSubmissionDTO(Long taskId) {
        TaskAnswerDTO taskSubmission = taskService.getTaskSubmission(taskId);

        if (taskSubmission != null) {
            return ResponseEntity.ok(taskSubmission);
        } else {
            LOGGER.error("Task with id _{}_ NOT FOUND.", taskId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Grades a task submission based on teacher input. The result is stored in the session,
     * and the task is updated with grading information.
     *
     * If a correction is provided, a notification is sent to the student about the correction.
     *
     * @param taskAnswerRequest The request containing task submission details.
     * @param session           The current HTTP session to store task grading information.
     * @return A success or failure response depending on task submission status.
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> gradeTaskSubmissionDTO(
            TaskAnswerRequest taskAnswerRequest,
            HttpSession session
    ) {
        session.setAttribute("TaskAnswerRequestGetAnswerId", taskAnswerRequest.getAnswerId());
        session.setAttribute("TaskAnswerGetIsCorrection", taskAnswerRequest.getIsCorrection());
        session.setAttribute("TaskAnswerGetIsPassed", taskAnswerRequest.getIsPassed());
        session.setAttribute("TaskAnswerGetMessageForCorrection", taskAnswerRequest.getMessageForCorrection());
        session.setAttribute("TaskAnswerGetIsRead", taskAnswerRequest.getIsRead());

        TaskAnswerDTO taskSubmission = taskService.getTaskSubmission(taskAnswerRequest.getAnswerId());

        if (taskSubmission != null) {
            taskService.gradeTaskSubmission(taskAnswerRequest.getAnswerId(),
                    taskAnswerRequest.getIsCorrection(), taskAnswerRequest.getIsPassed(),
                    taskAnswerRequest.getMessageForCorrection(), taskAnswerRequest.getIsRead());

            Map<String, Object> successResponse =
                    buildSuccessResponse("Task graded successfully", taskAnswerRequest.getAnswerId(),
                            taskAnswerRequest.getIsCorrection(), taskAnswerRequest.getIsPassed(),
                            taskAnswerRequest.getMessageForCorrection(), taskAnswerRequest.getIsRead());

            if (!taskAnswerRequest.getMessageForCorrection().isEmpty()) {
                UserFromAnswerTaskDTO user = taskSubmission.getStudent();
                userService.sendMessageAboutCorrection(user, taskSubmission.getCourse());
            }

            return ResponseEntity.ok(successResponse);

        } else {
            Map<String, Object> failedResponse =
                    buildFailedResponse("Task not found", taskAnswerRequest.getAnswerId(),
                            taskAnswerRequest.getIsCorrection(), taskAnswerRequest.getIsPassed(),
                            taskAnswerRequest.getMessageForCorrection(), taskAnswerRequest.getIsRead());

            LOGGER.error("TeacherService. Error: {}", failedResponse);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failedResponse);
        }
    }

    /**
     * Builds a success response with task grading details.
     *
     * @param successMessage      The success message to display.
     * @param answerId            The task answer ID.
     * @param isCorrection        Indicates if the task answer is a correction.
     * @param isPassed            Indicates if the task answer passed.
     * @param messageForCorrection Message for corrections, if any.
     * @param isRead              Indicates if the answer has been read.
     * @return A map containing the success response data.
     */
    private Map<String, Object> buildSuccessResponse(
            String successMessage,
            Long answerId,
            Boolean isCorrection,
            Boolean isPassed,
            String messageForCorrection,
            Boolean isRead
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", successMessage);
        response.put("answerId", answerId);
        response.put("isCorrection", isCorrection);
        response.put("isPassed", isPassed);
        response.put("messageForCorrection", messageForCorrection);
        response.put("isRead", isRead);

        return response;
    }

    /**
     * Builds a failed response when a task submission is not found or an error occurs.
     *
     * @param errorMessage        The error message to display.
     * @param taskId              The ID of the task submission.
     * @param isCorrection        Indicates if the task answer is a correction.
     * @param isPassed            Indicates if the task answer passed.
     * @param messageForCorrection Message for corrections, if any.
     * @param isRead              Indicates if the answer has been read.
     * @return A map containing the failed response data.
     */
    private Map<String, Object> buildFailedResponse(
            String errorMessage,
            Long taskId,
            boolean isCorrection,
            boolean isPassed,
            String messageForCorrection,
            Boolean isRead
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failed");
        response.put("message", errorMessage);
        response.put("taskId", taskId);
        response.put("isCorrection", isCorrection);
        response.put("isPassed", isPassed);
        response.put("messageForCorrection", messageForCorrection);
        response.put("isRead", isRead);

        return response;
    }

    /**
     * Downloads the zip file of a task submission for a student. Ensures the user has the proper
     * permissions (teacher role) before downloading the file.
     *
     * @param taskId        The ID of the task to download.
     * @param studentName   The student's name.
     * @param studentSurname The student's surname.
     * @param studentEmail  The student's email.
     * @param groupName     The name of the group the student belongs to.
     * @param sessionId     The current session ID for the user.
     * @return A response entity containing the zip file as a ByteArrayResource, or a 404 error if not found.
     */
    @Transactional
    public ResponseEntity<ByteArrayResource> downloadZip(
            Long taskId,
            String studentName,
            String studentSurname,
            String studentEmail,
            String groupName,
            String sessionId
    ) {
        String principalNameAsEmail = userService.getPrincipalNameBySessionId(sessionId);

        byte[] zipContents = taskService.getZipAnswerFile(taskId);

        if (zipContents != null && zipContents.length > 0 &&
                userService.findUserByEmail(principalNameAsEmail).getRole().equals(UserRole.TEACHER)
        ) {
            ByteArrayResource resource = new ByteArrayResource(zipContents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData(
                    "attachment",
                    studentName + "_" + studentSurname + "_" +
                            studentEmail + "_" +
                            groupName +
                            "_Task" + taskId + ".zip"
            );

            headers.setContentLength(zipContents.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            LOGGER.error("ZIP-file by taskId _{}_ NOT FOUND.", taskId);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
