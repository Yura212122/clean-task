package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.RegistrationController;
import academy.prog.julia.helpers.ErrorDescription;
import academy.prog.julia.json_responses.RegistrationResponse;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception handler for the RegistrationController.
 *
 * This class handles exceptions that occur in the RegistrationController.
 * It captures both specific exceptions (like JuliaRuntimeException) and generic exceptions,
 * logging the errors and returning appropriate error responses to the client.
 *
 * @Order(6) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *           higher order values.
 */
@RestControllerAdvice(assignableTypes = {RegistrationController.class})
@Order(6)
public class RegistrationExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(RegistrationExceptionHandler.class);

    /**
     * Handles JuliaRuntimeException.
     *
     * This method is triggered when a custom JuliaRuntimeException occurs during the registration process.
     * It logs the error details and returns a 500 Internal Server Error response with a structured error message.
     *
     * @param ex the JuliaRuntimeException thrown during registration
     * @return a ResponseEntity containing a RegistrationResponse with the error description
     */
    @ExceptionHandler(JuliaRuntimeException.class)
    public ResponseEntity<RegistrationResponse> handleJuliaRuntimeException(JuliaRuntimeException ex) {
        LOGGER.error("JuliaRuntimeException occurred: {}", ex.getMessage(), ex);

        List<ErrorDescription> errorList = new ArrayList<>();
        errorList.add(new ErrorDescription("registration", ex.getMessage()));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RegistrationResponse("failed", errorList, ""))
        ;
    }

    /**
     * Handles generic exceptions.
     *
     * This method is invoked when any other exception occurs that is not explicitly handled.
     * It logs the error and returns a 500 Internal Server Error response with a generic error message.
     *
     * @param ex the Exception thrown during registration
     * @return a ResponseEntity containing a RegistrationResponse with a generic error description
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RegistrationResponse> handleGenericException(Exception ex) {
        LOGGER.error("Internal server error occurred. Error: {}", ex.getMessage(), ex);

        List<ErrorDescription> errorList = new ArrayList<>();
        errorList.add(new ErrorDescription("registration", "Internal server error occurred."));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RegistrationResponse("failed", errorList, ""))
        ;
    }

}
