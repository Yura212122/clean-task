package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.TaskGroupController;
import academy.prog.julia.dto.TasksGroupResponseDTO;
import academy.prog.julia.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exception handler for the TaskGroupController.
 *
 * This class handles exceptions specific to the TaskGroupController, such as EntityNotFoundException and
 * ResourceNotFoundException.
 * It provides appropriate responses to the client when these exceptions are thrown.
 *
 * @Order(9) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *           higher order values.
 */
@RestControllerAdvice(assignableTypes = {TaskGroupController.class})
@Order(9)
public class TaskGroupExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(TaskGroupExceptionHandler.class);

    /**
     * Handles EntityNotFoundException.
     *
     * This method is triggered when an entity (such as a task group) is not found in the TaskGroupController.
     * It logs the error and returns an empty list with a 404 NOT FOUND status.
     *
     * @param ex the EntityNotFoundException thrown
     * @return a ResponseEntity containing an empty list and a 404 NOT FOUND status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<List<TasksGroupResponseDTO>> handleEntityNotFoundException(EntityNotFoundException ex) {
        LOGGER.error("EntityNotFoundException in TaskGroupController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ArrayList<>())
        ;
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * This method is triggered when a specific resource (such as a task group) cannot be found by its ID.
     * It logs the error and returns the error message as the response body with a 404 NOT FOUND status.
     *
     * @param ex the ResourceNotFoundException thrown
     * @return a ResponseEntity containing the exception message and a 404 NOT FOUND status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        LOGGER.error("TaskGroupController: Group with id not found! Error: {}", ex.getMessage());

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
