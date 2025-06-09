package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.UserListController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for UserListController.
 *
 * This class handles exceptions specific to the UserListController, providing detailed error responses
 * based on the type of exception encountered. It ensures that appropriate HTTP status codes and
 * error messages are returned to the client.
 *
 * @Order(13) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *            higher order values.
 */
@RestControllerAdvice(assignableTypes = {UserListController.class})
@Order(13)
public class UserListExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserListExceptionHandler.class);

    /**
     * Handles IllegalArgumentException.
     *
     * Logs the error message and returns a structured response indicating that an invalid argument was provided.
     *
     * @param ex the IllegalArgumentException thrown
     * @return ResponseEntity with a 400 BAD REQUEST status and an error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error("IllegalArgumentException in UserListController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid argument: " + ex.getMessage())
        ;
    }

    /**
     * Handles RuntimeException.
     *
     * Logs the error message and returns a structured response indicating a general runtime error.
     *
     * @param ex the RuntimeException thrown
     * @return ResponseEntity with a 500 INTERNAL SERVER ERROR status and an error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        LOGGER.error("RuntimeException in UserListController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage())
        ;
    }

}
