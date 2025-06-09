package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.SseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * Exception handler for the SseController.
 *
 * This class handles exceptions that occur in the SseController, specifically related to Server-Sent Events (SSE).
 * It captures IOExceptions that may occur when sending SSE data and returns an appropriate error response to the client.
 *
 * @Order(7) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *           higher order values.
 */
@RestControllerAdvice(assignableTypes = {SseController.class})
@Order(7)
public class SseExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(SseExceptionHandler.class);

    /**
     * Handles IOException during SSE transmission.
     *
     * This method is triggered when an IOException occurs while trying to send SSE data.
     * It logs the exception details and returns a 500 Internal Server Error response with an error message.
     *
     * @param ex the IOException that occurred while sending SSE
     * @return a ResponseEntity containing an error message
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        LOGGER.error("Error sending SSE: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending SSE: " + ex.getMessage())
        ;
    }

}
