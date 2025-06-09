package academy.prog.julia.exception_handlers;

import academy.prog.julia.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import org.apache.http.NoHttpResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global exception handler for handling various types of exceptions across the application.
 *
 * This class uses Spring's @RestControllerAdvice to provide a centralized exception handling
 * mechanism for different types of exceptions. It ensures consistent error responses for various
 * exception scenarios and logs detailed error messages for debugging purposes.
 *
 * @Order(100) sets the order of this advice, lower numbers have higher precedence.
 */
@RestControllerAdvice
@Order(100)
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all exceptions not specifically handled by other methods.
     *
     * This method returns a generic internal server error response for unexpected exceptions.
     *
     * @param ex the Exception instance thrown
     * @return a ResponseEntity with HTTP 500 Internal Server Error status and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An internal server error occurred.");

        LOGGER.error("Error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
        ;
    }

    /**
     * Handles HttpMediaTypeNotAcceptableException indicating unacceptable media type.
     *
     * This method returns a 406 Not Acceptable response when the requested media type is not supported.
     *
     * @param ex the HttpMediaTypeNotAcceptableException instance thrown
     * @return a ResponseEntity with HTTP 406 Not Acceptable status and a plain text message
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        LOGGER.warn("Media type not acceptable: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Acceptable MIME type: " + MediaType.APPLICATION_JSON_VALUE)
        ;
    }

    /**
     * Handles RuntimeException indicating an unexpected error.
     *
     * This method returns a 500 Internal Server Error response for unexpected runtime errors.
     *
     * @param ex the RuntimeException instance thrown
     * @return a ResponseEntity with HTTP 500 Internal Server Error status and a message about the unexpected error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        LOGGER.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage())
        ;
    }

    /**
     * Handles IllegalArgumentException indicating invalid arguments provided.
     *
     * This method returns a 400 Bad Request response when invalid arguments are passed.
     *
     * @param ex the IllegalArgumentException instance thrown
     * @return a ResponseEntity with HTTP 400 Bad Request status and a message about the invalid argument
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error("Invalid argument: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid argument: " + ex.getMessage())
        ;
    }

    /**
     * Handles NotFoundException when data is not found.
     *
     * This method returns a 404 Not Found response when requested data is not found.
     *
     * @param ex the NotFoundException instance thrown
     * @return a ResponseEntity with HTTP 404 Not Found status and a message about the missing data
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        LOGGER.error("Data not found: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Data not found: " + ex.getMessage())
        ;
    }

    /**
     * Handles AccessDeniedException indicating lack of permission to access a resource.
     *
     * This method returns a 403 Forbidden response when access is denied.
     *
     * @param ex the AccessDeniedException instance thrown
     * @param request the HttpServletRequest for obtaining request URI
     * @return a ResponseEntity with HTTP 403 Forbidden status and details about the denied access
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        String requestUri = request.getRequestURI();

        LOGGER.error("AccessDeniedException: {} - URI: {}", ex.getMessage(), requestUri);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "status", "failed",
                        "message", "You do not have permission to access this resource.",
                        "path", requestUri,
                        "error", HttpStatus.FORBIDDEN.getReasonPhrase()
                ))
        ;
    }

    /**
     * Handles NoSuchElementException when an element is not found.
     *
     * This method returns a 404 Not Found response when a requested element is missing.
     *
     * @param ex the NoSuchElementException instance thrown
     * @return a ResponseEntity with HTTP 404 Not Found status and a message about the missing resource
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)  // Sets the status code directly
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {

        LOGGER.error("NoSuchElementException: {} - error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Resource not found: " + ex.getMessage())
        ;
    }

    /**
     * Handles UserNotFoundException specific to user-related operations.
     *
     * This method returns a 404 Not Found response when a requested user is not found.
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

    /**
     * Handles MissingServletRequestParameterException when a required request parameter is missing.
     *
     * This method returns a 400 Bad Request response indicating the missing parameter.
     *
     * @param ex the MissingServletRequestParameterException instance thrown
     * @return a ResponseEntity with HTTP 400 Bad Request status and a message about the missing parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        LOGGER.error("Missing request parameter exception! Error: {}", ex.getMessage());

        String parameterName = ex.getParameterName();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Required request parameter '" + parameterName + "' is missing.")
        ;
    }

    /**
     * Handles MissingRequestHeaderException when a required request header is missing.
     *
     * This method returns a 400 Bad Request response indicating the missing header.
     *
     * @param ex the MissingRequestHeaderException instance thrown
     * @return a ResponseEntity with HTTP 400 Bad Request status and a message about the missing header
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        LOGGER.error("Missing request header exception! Error: {}", ex.getMessage());

        String headerName = ex.getHeaderName();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Required request header '" + headerName + "' is missing.")
        ;
    }

    /**
     * Handles HttpRequestMethodNotSupportedException when the request method is not supported.
     *
     * This method returns a 405 Method Not Allowed response when the HTTP method is not supported for the endpoint.
     *
     * @param ex the HttpRequestMethodNotSupportedException instance thrown
     * @return a ResponseEntity with HTTP 405 Method Not Allowed status and a message about the unsupported method
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex
    ) {
        LOGGER.error("Request method not supported exception! Error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Request method '" + ex.getMethod() + "' is not supported for this endpoint.")
        ;
    }

    /**
     * Handles AsyncRequestTimeoutException when an asynchronous request times out.
     *
     * This method returns a 408 Request Timeout response when an asynchronous request exceeds the timeout limit.
     *
     * @param ex the AsyncRequestTimeoutException instance thrown
     * @return a ResponseEntity with HTTP 408 Request Timeout status and a message about the request timeout
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity<String> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        LOGGER.error(
                "Asynchronous request timed out! Error: {}. Request timed out. Please try again later.",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body("Request timed out. Please try again later.")
        ;
    }

    /**
     * Handles MethodArgumentTypeMismatchException when there is a data type mismatch for a method argument.
     *
     * This method returns a 400 Bad Request response indicating an invalid data type for a parameter.
     *
     * @param ex the MethodArgumentTypeMismatchException instance thrown
     * @return a ResponseEntity with HTTP 400 Bad Request status and a message about the type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        LOGGER.error("Invalid data type for parameter! Error: {}", ex.getMessage());

        String errorMessage = "Invalid data type for parameter: " + ex.getName();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles HttpMessageNotReadableException when the request body is not readable.
     *
     * This method returns a 400 Bad Request response when the request body is missing or malformed.
     *
     * @param ex the HttpMessageNotReadableException instance thrown
     * @return a ResponseEntity with HTTP 400 Bad Request status and a message about the missing or
     *         incorrect request body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.error("Required request body is missing. Error: {}", ex.getMessage());

        return new ResponseEntity<>("Required request body is missing or incorrect", HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles NoHttpResponseException indicating no response from an external service.
     *
     * This method returns a 503 Service Unavailable response when an external service fails to respond.
     *
     * @param ex the NoHttpResponseException instance thrown
     * @return a ResponseEntity with HTTP 503 Service Unavailable status and a message about the external service failure
     */
    @ExceptionHandler(NoHttpResponseException.class)
    public ResponseEntity<String> handleNoHttpResponseException(NoHttpResponseException ex) {
        LOGGER.error(
                "GlobalExceptionHandler: No response from external service. Error: {}",
                ex.getMessage()
        );

        return new ResponseEntity<>(
                "External service failed to respond. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    /**
     * Handles CannotCreateTransactionException when a transaction cannot be created.
     *
     * This method returns a 500 Internal Server Error response when there is an issue with database transactions.
     *
     * @param ex the CannotCreateTransactionException instance thrown
     * @return a ResponseEntity with HTTP 500 Internal Server Error status and a message about the database transaction error
     */
    @ExceptionHandler(CannotCreateTransactionException.class)
    public ResponseEntity<String> handleCannotCreateTransactionException(CannotCreateTransactionException ex) {
        LOGGER.error("Cannot create transaction: {}", ex.getMessage());

        return new ResponseEntity<>(
                "Database transaction error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles UnknownHostException indicating an unknown host or network issue.
     *
     * This method returns a 503 Service Unavailable response when the service cannot connect due to an unknown host.
     *
     * @param ex the UnknownHostException instance thrown
     * @return a ResponseEntity with HTTP 503 Service Unavailable status and a message indicating a temporary service issue
     */
    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<String> handleUnknownHostException(UnknownHostException ex) {
        LOGGER.error("UnknownHostException occurred: {}", ex.getMessage());

        return new ResponseEntity<>(
                "Service is currently unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    /**
     * Handles SocketException indicating a network connection reset or other socket-related issues.
     *
     * This method returns a 503 Service Unavailable response when there is a network-related issue such as a
     * connection reset.
     * The exception is logged, and the user receives a message indicating the temporary unavailability of the service.
     *
     * @param ex the SocketException instance thrown
     * @return a ResponseEntity with HTTP 503 Service Unavailable status and a message indicating a
     * temporary service issue
     */
    @ExceptionHandler(SocketException.class)
    public ResponseEntity<String> handleSocketException(SocketException ex) {
        LOGGER.error("SocketException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service is temporarily unavailable. Please try again later.")
        ;
    }

    /**
     * Handles SocketTimeoutException indicating that a network connection timed out.
     *
     * This method returns a 504 Gateway Timeout response when a network request takes too long to complete,
     * typically indicating that the server did not respond in time.
     *
     * @param ex the SocketTimeoutException instance thrown
     * @return a ResponseEntity with HTTP 504 Gateway Timeout status and a message indicating a timeout issue
     */
    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<String> handleSocketTimeoutException(SocketTimeoutException ex) {
        LOGGER.error("SocketTimeoutException occurred: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body("The service took too long to respond. Please try again later.")
        ;
    }

    /**
     * Handles DataIntegrityViolationException indicating that a database integrity constraint has been violated.
     *
     * This method returns a 400 Bad Request response when there is an issue with data integrity, such as trying to
     * insert or update data that violates a constraint (e.g., data too long for a column).
     *
     * If the root cause of the violation is an SQL exception, it checks for specific error messages like
     * "Data too long for column" and returns a more detailed message. Otherwise, it returns a generic
     * data integrity violation message.
     *
     * @param ex the DataIntegrityViolationException instance thrown
     * @return a String with a message describing the error
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof SQLException) {
            SQLException sqlException = (SQLException) rootCause;

            String message = sqlException.getMessage();

            LOGGER.error("SQL Error: {}", message);

            if (message.contains("Data too long for column")) {
                String columnName = extractColumnName(message);

                return "Data too long for column '" + columnName + "'";
            }
        }

        return "Data integrity violation occurred";
    }

    /**
     * Extracts the column name from an SQL error message.
     *
     * This helper method is used when an SQL exception contains a message indicating that data is too long for a specific column.
     * It extracts the column name from the SQL error message to provide a more detailed error response.
     *
     * @param sqlMessage the SQL exception message that contains information about the column
     * @return a String representing the name of the column that caused the issue
     */
    private String extractColumnName(String sqlMessage) {
        int startIndex = sqlMessage.indexOf("'") + 1;
        int endIndex = sqlMessage.indexOf("'", startIndex);

        return sqlMessage.substring(startIndex, endIndex);
    }

}
