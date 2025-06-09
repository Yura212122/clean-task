package academy.prog.julia.controllers;

import academy.prog.julia.json_responses.LessonsResponse;
import academy.prog.julia.services.LessonsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing lessons within a course.
 */
@RestController
@RequestMapping("/api/courses")
public class LessonsController {
    private final LessonsService lessonsService;

    /**
     * Constructor that injects the LessonsService dependency.
     *
     * @param lessonsService the service for managing lesson-related operations
     */
    public LessonsController(LessonsService lessonsService) {
        this.lessonsService = lessonsService;
    }

    /**
     * Endpoint to fetch all lessons for a given course.
     *
     * @param courseId the ID of the course for which lessons are requested
     * @param sessionId the session ID used to verify the user's identity
     * @param session the HTTP session to store session-related information
     * @return ResponseEntity containing a list of lessons or an error message if unauthorized
     */
    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<List<LessonsResponse>> getCourseLessons(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String sessionId,
            HttpSession session
    ) {
        return lessonsService.getLessonsForCourse(courseId, sessionId, session);
    }

}
