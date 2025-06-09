package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.LessonDetailController;
import academy.prog.julia.json_responses.LessonDetailResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

/**
 * Exception handler for LessonDetailController.
 *
 * This class handles exceptions specifically for the LessonDetailController. It uses
 * Spring's @RestControllerAdvice to provide centralized exception handling, ensuring that appropriate
 * error responses are returned and logged when a lesson entity is not found.
 *
 * @Order(3) sets the order of this advice, ensuring it has a lower precedence compared to other advice with a
 *           lower order value.
 */
@RestControllerAdvice(assignableTypes = {LessonDetailController.class})
@Order(3)
public class LessonDetailExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(LessonDetailExceptionHandler.class);

    /**
     * Handles EntityNotFoundException for lesson entities.
     *
     * This method returns a 404 Not Found response when an EntityNotFoundException is thrown,
     * indicating that a lesson with the specified ID was not found. It also logs the error
     * details for debugging purposes and returns a custom response object with an appropriate
     * message.
     *
     * @param ex the EntityNotFoundException instance thrown
     * @return a ResponseEntity with HTTP 404 Not Found status and a LessonDetailResponse object
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<LessonDetailResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        LOGGER.error("The lesson with this id NOT FOUND: {}", ex.getMessage(), ex);

        LessonDetailResponse response = new LessonDetailResponse(
                "The lesson with this id NOT FOUND.",
                null,
                null,
                null,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
