package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.TeacherController;
import academy.prog.julia.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Exception handler for TeacherController.
 *
 * This handler manages all exceptions related to TeacherController.
 * It provides appropriate responses to the client when these exceptions are thrown.
 *
 * @Order(10) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *            higher order values.
 */
@RestControllerAdvice(assignableTypes = {TeacherController.class})
@Order(10)
public class TeacherExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(TeacherExceptionHandler.class);

    /**
     * Handles EntityNotFoundException.
     *
     * Logs the error and returns a structured JSON response with status "error" and the exception message.
     *
     * @param ex the EntityNotFoundException thrown
     * @return ResponseEntity with a structured error response and a 404 NOT FOUND status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        LOGGER.error("EntityNotFoundException in TeacherController: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response)
        ;
    }

    /**
     * Handles any general Exception.
     *
     * Retrieves session attributes to build a failed response with task-related data.
     * Logs the error and returns the failed response with status "failed".
     *
     * @param ex      the Exception thrown
     * @param session the HttpSession from which task-related attributes are retrieved
     * @return ResponseEntity with a failed response and a 500 INTERNAL SERVER ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex,
            HttpSession session
    ) {
        Long taskAnswerRequestGetAnswerId = (Long) session.getAttribute("TaskAnswerRequestGetAnswerId");
        Boolean  taskAnswerGetIsCorrection = (Boolean) session.getAttribute("TaskAnswerGetIsCorrection");
        Boolean  taskAnswerGetIsPassed = (Boolean) session.getAttribute("TaskAnswerGetIsPassed");
        String taskAnswerGetMessageForCorrection = (String) session.getAttribute("TaskAnswerGetMessageForCorrection");
        Boolean taskAnswerGetIsRead = (Boolean) session.getAttribute("TaskAnswerGetIsRead");

        taskAnswerRequestGetAnswerId = taskAnswerRequestGetAnswerId != null ? taskAnswerRequestGetAnswerId : 0L;
        taskAnswerGetIsCorrection = taskAnswerGetIsCorrection != null ? taskAnswerGetIsCorrection : false;
        taskAnswerGetIsPassed = taskAnswerGetIsPassed != null ? taskAnswerGetIsPassed : false;
        taskAnswerGetMessageForCorrection = taskAnswerGetMessageForCorrection != null ? taskAnswerGetMessageForCorrection : "";
        taskAnswerGetIsRead = taskAnswerGetIsRead != null ? taskAnswerGetIsRead : false;


        Map<String, Object> failedResponse = buildFailedResponse(
                ex.getMessage(), taskAnswerRequestGetAnswerId,
                taskAnswerGetIsCorrection, taskAnswerGetIsPassed,
                taskAnswerGetMessageForCorrection, taskAnswerGetIsRead
        );

        LOGGER.error("Exception in TeacherController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(failedResponse)
        ;
    }

    /**
     * Helper method to create a structured error response.
     *
     * This method constructs a map containing error details such as the status, message, taskId,
     * isCorrection, isPassed, messageForCorrection, isRead and additional information.
     *
     * @param errorMessage the error message to include in the response
     * @param taskId the task ID associated with the error
     * @param isCorrection flag indicating if the error is related to correction
     * @param isPassed flag indicating if the task is marked as passed
     * @param messageForCorrection additional message for correction
     * @param isRead flag indicating if the task has been read
     * @return an object (map) representing the failed(error) response
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
     * Handles AccessDeniedException.
     *
     * Logs the error and returns a JSON response with an access denied message and a 403 FORBIDDEN status.
     *
     * @param ex the AccessDeniedException thrown
     * @return ResponseEntity with access denied message and a 403 FORBIDDEN status
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.error("AccessDeniedException. Error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("{\"error\": \"Access Denied\", \"message\": \"" + ex.getMessage() + "\"}")
        ;
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * Logs the error and returns a structured JSON response with status "error" and the exception message.
     *
     * @param ex the ResourceNotFoundException thrown
     * @return ResponseEntity with a structured error response and a 404 NOT FOUND status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        LOGGER.error("TeacherController: Resource not found! Error: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response)
        ;
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     *
     * Logs the error and returns a detailed error message with a 400 BAD REQUEST status.
     *
     * @param ex the MethodArgumentTypeMismatchException thrown
     * @return ResponseEntity with error message and a 400 BAD REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = String.format(
                "Invalid type of data '%s'. Expected type '%s', but got value '%s'.",
                ex.getName(),
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName(),
                ex.getValue()
        );

        LOGGER.error("Invalid type of data in TeacherController. Error: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException.
     *
     * Logs the error and returns a message indicating that the request body is missing or incorrect.
     *
     * @param ex the HttpMessageNotReadableException thrown
     * @return ResponseEntity with error message and a 400 BAD REQUEST status
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.error(
                "Exception in TeacherController: required request body is missing. Error: {}",
                ex.getMessage()
        );

        return new ResponseEntity<>(
                "Exception in TeacherController: Required request body is missing or incorrect",
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles MissingServletRequestParameterException.
     *
     * Logs the error and returns a message indicating that a required parameter is missing.
     *
     * @param ex the MissingServletRequestParameterException thrown
     * @return ResponseEntity with error message and a 400 BAD REQUEST status
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        LOGGER.error("TeacherController, parameter not present. Missing parameter: {}", ex.getParameterName());

        return new ResponseEntity<>("Missing parameter: " + ex.getParameterName(), HttpStatus.BAD_REQUEST);
    }

}
