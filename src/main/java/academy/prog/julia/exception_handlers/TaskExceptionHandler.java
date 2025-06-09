package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.TaskController;
import academy.prog.julia.json_responses.LessonProgressResponse;
import academy.prog.julia.json_responses.TaskAnswerResponse;
import academy.prog.julia.json_responses.TaskDetailsResponse;
import academy.prog.julia.json_responses.TaskProgressResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Exception handler for the TaskController.
 *
 * This class handles exceptions thrown in the TaskController. It captures various exceptions such as
 * EntityNotFoundException, PersistenceException, and NullPointerException and provides appropriate
 * error responses for each case.
 * @Order(8) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *           higher order values.
 */
@RestControllerAdvice(assignableTypes = {TaskController.class})
@Order(8)
public class TaskExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(TaskExceptionHandler.class);

    /**
     * Handles EntityNotFoundException.
     *
     * This method is triggered when an entity (such as a task) is not found.
     * It logs the error and returns an appropriate response based on the URI pattern.
     *
     * @param ex      the EntityNotFoundException thrown
     * @param request the HttpServletRequest object, which contains request details such as URI
     * @return a ResponseEntity with a detailed error message and the appropriate status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        String requestUri = request.getRequestURI();

        LOGGER.error("EntityNotFoundException: {} - URI: {}", ex.getMessage(), requestUri);

        if (requestUri.matches("/api/tasks/\\d+")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new TaskDetailsResponse(
                            "The task with this id NOT FOUND.",
                            null, null, null, null)
                    )
            ;
        } else if (requestUri.matches("/api/tasks/\\d+/\\d+")) {
            if (requestUri.contains("progress")) {
                if (requestUri.matches("/api/tasks/\\d+/\\d+/progress")) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(new TaskProgressResponse(null, null))
                    ;
                } else if (requestUri.matches("/api/tasks/progress/\\d+/\\d+")) {
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(new LessonProgressResponse(null, null, null))
                    ;
                }
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new TaskAnswerResponse(null,"The task or the user with these id's NOT FOUND.",
                                null, null, null, null, null, null,
                                null, null,null, null, null,
                                null, null)
                        );
            }
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Entity not found", null))
        ;
    }

    /**
     * Handles PersistenceException.
     *
     * This method is triggered when a PersistenceException occurs, typically due to database-related errors.
     * It logs the error and returns a response indicating a persistence error.
     *
     * @param ex      the PersistenceException thrown
     * @param request the HttpServletRequest object, which contains request details such as URI
     * @return a ResponseEntity with a detailed error message
     */
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Object> handlePersistenceException(
            PersistenceException ex,
            HttpServletRequest request
    ) {
        String requestUri = request.getRequestURI();

        LOGGER.error("PersistenceException: {} - URI: {}", ex.getMessage(), requestUri);

        if (requestUri.matches("/api/tasks/\\d+/\\d+/submit")) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Invalid answer: " + ex.getMessage(), null))
            ;
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Persistence error occurred", null))
            ;
        }
    }

    /**
     * Handles NullPointerException.
     *
     * This method is triggered when a NullPointerException occurs.
     * It logs the exception and returns a 500 Internal Server Error response.
     *
     * @param ex      the NullPointerException thrown
     * @param request the HttpServletRequest object, which contains request details such as URI
     * @return a ResponseEntity with an error message indicating a server error
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(
            NullPointerException ex,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "NullPointerException at URI: {} - Exception: {}",
                request.getRequestURI(), ex.getMessage(), ex
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Unexpected server error occurred", null));
    }

    /**
     * Helper method to create a structured error response.
     *
     * This method constructs a map containing error details such as the status, message, taskId, and
     * additional information.
     *
     * @param message the error message to include in the response
     * @param taskId  the task ID, or null if not applicable
     * @return an object (map) representing the error response
     */
    private Object createErrorResponse(
            String message,
            Long taskId
    ) {
        return Map.of(
                "status", "failed",
                "message", message,
                "taskId", taskId != null ? taskId : "not provided",
                "answerUrl", "",
                "course", ""
        );
    }

}
