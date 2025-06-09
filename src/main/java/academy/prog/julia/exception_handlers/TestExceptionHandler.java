package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.TestAnswerController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * Exception handler for TestController.
 *
 * This class handles exceptions related to the TestController, providing specific responses
 * based on the type of exception encountered.
 *
 * @Order(11) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *            higher order values.
 */
@RestControllerAdvice(assignableTypes = {TestAnswerController.class})
@Order(11)
public class TestExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(TestExceptionHandler.class);

    /**
     * Handles EntityNotFoundException.
     *
     * Logs the error message and returns a structured response indicating that the test was not found.
     * Includes information from the HTTP session for better context.
     *
     * @param e the EntityNotFoundException thrown
     * @param session the HttpSession from which testId is retrieved
     * @return ResponseEntity with a 404 NOT FOUND status and a structured error response
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException e,
            HttpSession session
    ) {
        Long testId = (Long) session.getAttribute("testId");
        LOGGER.error("EntityNotFoundException in TestController: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Test not found", testId))
        ;
    }

    /**
     * Handles PersistenceException.
     *
     * Logs the error message and returns a structured response indicating a persistence issue,
     * including information from the HTTP session for better context.
     *
     * @param e the PersistenceException thrown
     * @param session the HttpSession from which testId is retrieved
     * @return ResponseEntity with a 400 BAD REQUEST status and a structured error response
     */
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Object> handlePersistenceException(
            PersistenceException e,
            HttpSession session
    ) {
        Long testId = (Long) session.getAttribute("testId");
        LOGGER.error("PersistenceException in TestController: {}", e.getMessage(), e);

        return ResponseEntity
                .badRequest()
                .body(createErrorResponse("Invalid answer: " + e.getMessage(), testId))
        ;
    }

    /**
     * Helper method to create a structured error response.
     *
     * This method formats the error response as a Map containing status, message, testId, and additional fields.
     *
     * @param message the error message to include in the response
     * @param testId the ID of the test related to the error
     * @return a Map containing the structured error response
     */
    private Object createErrorResponse(
            String message,
            Long testId
    ) {
        return Map.of(
                "status", "failed",
                "message", message,
                "testId", testId,
                "answerUrl", "",
                "course", ""
        );
    }
}
