package academy.prog.julia.controllers;

import academy.prog.julia.json_responses.UserCoursesResponse;
import academy.prog.julia.services.UserCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle requests related to user courses.
 * Exposes endpoints for retrieving the courses associated with a specific user.
 */
@RestController
@RequestMapping("/api/user-courses")
public class UserCourseController {

    private final UserCourseService userCourseService;

    /**
     * Constructor for injecting dependencies.
     *
     * @param userCourseService - Service handling the logic related to user courses.
     */
    public UserCourseController(UserCourseService userCourseService) {
        this.userCourseService = userCourseService;
    }

    /**
     * Retrieves the courses for a specific user, validating the session authorization.
     *
     * @param userId - ID of the user whose courses are being fetched.
     * @param sessionId - The session ID from the request header for authorization.
     * @param session - HttpSession object for session management.
     * @return ResponseEntity containing a list of UserCoursesResponse or an unauthorized message if validation fails.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserCoursesResponse>> getUserCourses(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String sessionId,
            HttpSession session
    ) {
        return userCourseService.getUserCoursesWithSessionValidation(userId, sessionId, session);
    }

}
