package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.IsUserBannedController;
import academy.prog.julia.exceptions.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for IsUserBannedController.
 *
 * This class uses Spring's @RestControllerAdvice to handle exceptions specifically for the
 * IsUserBannedController. It provides centralized exception handling for various scenarios
 * related to user banning functionality, ensuring that appropriate error responses are returned
 * and logged.
 *
 * @Order(2) sets the order of this advice, ensuring it has a lower precedence compared to other advice with a
 *           lower order value.
 */
@RestControllerAdvice(assignableTypes = {IsUserBannedController.class})
@Order(2)
public class IsUserBannedExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(IsUserBannedExceptionHandler.class);

    /**
     * Handles generic exceptions for the IsUserBannedController.
     *
     * This method returns a 404 Not Found response for any unexpected exceptions occurring
     * within the IsUserBannedController. The exception details are logged for further debugging.
     *
     * @param ex the Exception instance thrown
     * @return a ResponseEntity with HTTP 404 Not Found status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        LOGGER.error("Exception in IsUserBannedController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()
        ;
    }

    /**
     * Handles UserNotFoundException specifically for user not found scenarios.
     *
     * This method returns a 404 Not Found response when a UserNotFoundException is thrown.
     * The exception details are logged, and a descriptive error message is provided in the response.
     *
     * @param ex the UserNotFoundException instance thrown
     * @return a ResponseEntity with HTTP 404 Not Found status and a message about the missing user
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        LOGGER.error("UserNotFoundException: {} - error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found: " + ex.getMessage())
        ;
    }

}
