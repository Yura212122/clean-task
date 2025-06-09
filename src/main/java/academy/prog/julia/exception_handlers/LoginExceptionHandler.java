package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.LoginController;
import academy.prog.julia.json_responses.LoginResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handler for the LoginController.
 *
 * This class manages exceptions that occur in the LoginController. It handles specific exceptions
 * such as BadCredentialsException, HttpMediaTypeNotAcceptableException, and other generic exceptions,
 * ensuring that appropriate error messages are logged and returned to the client.
 *
 * @Order(5) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *           higher order values.
 */
@RestControllerAdvice(assignableTypes = {LoginController.class})
@Order(5)
public class LoginExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(LoginExceptionHandler.class);

    /**
     * Handles BadCredentialsException.
     *
     * This method is triggered when the user provides invalid login credentials. It logs the error
     * and returns a 404 Not Found response with a specific error message indicating that the user was not found.
     *
     * @param ex the BadCredentialsException thrown
     * @return a ResponseEntity containing a LoginResponse with the error message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<LoginResponse> handleBadCredentialsException(BadCredentialsException ex) {
        LOGGER.error("Bad credentials. From Login controller. Error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginResponse("User not found", null, null))
        ;
    }

    /**
     * Handles generic exceptions.
     *
     * This method is invoked for any exceptions that are not explicitly handled by other methods.
     * It logs the error and returns a 400 Bad Request response with a custom error message.
     *
     * @param ex the Exception thrown
     * @return a ResponseEntity containing a map with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        LOGGER.error("HandleAllException from LoginController. Error: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "path isn't correct");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
        ;
    }

    /**
     * Handles HttpMediaTypeNotAcceptableException.
     *
     * This method is triggered when the client sends a request with an unacceptable media type.
     * It logs the error and returns a 406 Not Acceptable response, indicating the correct media type.
     *
     * @return a ResponseEntity with a message about the acceptable media type
     */
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptableException() {
        LOGGER.error(
                "LoginController: HttpMediaTypeNotAcceptableException, acceptable MIME type: " +
                MediaType.APPLICATION_JSON_VALUE
        );

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.TEXT_PLAIN)
                .body("acceptable MIME type: " + MediaType.APPLICATION_JSON_VALUE)
        ;
    }

}
