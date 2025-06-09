package academy.prog.julia.exception_handlers;

import academy.prog.julia.controllers.UserCourseController;
import academy.prog.julia.json_responses.UserCoursesResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

/**
 * Exception handler for UserCourseController.
 *
 * This class handles exceptions related to the UserCourseController, providing specific responses
 * based on the type of exception encountered. It ensures that appropriate HTTP status codes and
 * error messages are returned to the client.
 *
 * @Order(12) sets the order of this advice, ensuring it has a higher precedence compared to other advice with
 *            higher order values.
 */
@RestControllerAdvice(assignableTypes = {UserCourseController.class})
@Order(12)
public class UserCourseExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(UserCourseExceptionHandler.class);

    /**
     * Handles EntityNotFoundException.
     *
     * Logs the error message and returns a structured response indicating that the user does not have any courses.
     * Includes userId from the HTTP session for context.
     *
     * @param ex the EntityNotFoundException thrown
     * @param session the HttpSession from which userId is retrieved
     * @return ResponseEntity with a 404 NOT FOUND status and a structured response
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<List<UserCoursesResponse>> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        LOGGER.error("EntityNotFoundException in UserCourseController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonList(new UserCoursesResponse(userId, "You don't have course(s)")))
        ;
    }

    /**
     * Handles SessionAuthenticationException.
     *
     * Logs the error message and returns a structured response indicating that the user is unauthorized.
     * Includes userId from the HTTP session for context.
     *
     * @param ex the SessionAuthenticationException thrown
     * @param session the HttpSession from which userId is retrieved
     * @return ResponseEntity with a 401 UNAUTHORIZED status and a structured response
     */
    @ExceptionHandler(SessionAuthenticationException.class)
    public ResponseEntity<List<UserCoursesResponse>> handleSessionAuthenticationException(
            SessionAuthenticationException ex,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        LOGGER.error("SessionAuthenticationException in UserCourseController: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonList(new UserCoursesResponse(userId, "You are unauthorized!")))
        ;
    }

}
