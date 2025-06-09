package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.CertificateController;
import jakarta.ws.rs.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling exceptions specific to CertificateController.
 *
 * This class uses Spring's @RestControllerAdvice to provide a centralized exception handling
 * mechanism across all endpoints in the CertificateController class.
 *
 * @Order(1) defines the order of this advice, with lower numbers having higher precedence.
 */
@RestControllerAdvice(assignableTypes = {CertificateController.class})
@Order(1)
public class CertificateExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(CertificateExceptionHandler.class);

    /**
     * Handles NotFoundException thrown by the CertificateController.
     * <p>
     * This method logs the exception and returns an HTTP 404 Not Found response
     * with the exception message as the response body.
     * </p>
     *
     * @param ex the NotFoundException instance thrown
     * @return a ResponseEntity with a 404 Not Found status and the exception message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        LOGGER.error("Data not found: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage())
        ;
    }

}
