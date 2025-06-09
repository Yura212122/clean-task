package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.LessonsController;
import academy.prog.julia.json_responses.LessonsResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

/**
 * Exception handler for LessonsController.
 *
 * This class handles exceptions specifically for the LessonsController. It is responsible for
 * managing errors related to lesson retrieval when the associated course cannot be found.
 * It uses Spring's @RestControllerAdvice to provide centralized exception handling for the
 * LessonsController, ensuring that appropriate error responses are returned and logged when
 * a lesson entity is not found.
 *
 * @Order(4)  sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *            higher order values.
 */
@RestControllerAdvice(assignableTypes = {LessonsController.class})
@Order(4)
public class LessonsExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(LessonsExceptionHandler.class);

    /**
     * Handles EntityNotFoundException for lesson entities when the associated course is not found.
     *
     * This method returns a 404 Not Found response when an EntityNotFoundException is thrown,
     * indicating that no lessons are available for the specified course. It logs the error details
     * and includes information about the course ID in the response. The course ID is retrieved from
     * the HTTP session.
     *
     * @param ex the EntityNotFoundException instance thrown
     * @param session the HTTP session used to retrieve the course ID
     * @return a ResponseEntity with HTTP 404 Not Found status and a List of LessonsResponse objects
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<List<LessonsResponse>> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpSession session
    ) {
        Long courseId = (Long) session.getAttribute("courseId");
        LOGGER.error("This course NOT FOUND. Error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonList(
                        new LessonsResponse(courseId, "This course has no lessons.", null))
                )
        ;
    }
    
}

