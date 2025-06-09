package academy.prog.julia.controllers;

import academy.prog.julia.dto.LessonDetailDTO;
import academy.prog.julia.json_responses.LessonDetailResponse;
import academy.prog.julia.services.LessonsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing lesson details.
 * This controller provides an endpoint to retrieve lesson details based on lesson ID and user authorization.
 */
@RestController
@RequestMapping("/api")
public class LessonDetailController {
    private final LessonsService lessonsService;

    /**
     * Constructor that injects the LessonsService dependency.
     *
     * @param lessonsService the service responsible for handling lesson-related operations
     */
    public LessonDetailController(LessonsService lessonsService) {
        this.lessonsService = lessonsService;
    }

    /**
     * Endpoint to retrieve detailed information about a lesson.
     * This method checks if the current user is enrolled in the course before providing lesson details.
     *
     * @param lessonId the ID of the lesson to retrieve
     * @param currentUserId the ID of the current user making the request
     * @return ResponseEntity containing lesson details if the user is authorized, otherwise returns a FORBIDDEN status
     */
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonDetailResponse> getLessonDetails(
            @PathVariable Long lessonId,
            @RequestParam Long currentUserId
    ) {
        LessonDetailDTO lessonDetailDTO = lessonsService.getLessonDetails(lessonId);
        if (lessonsService.isUserInCourse(currentUserId, lessonId)) {
            LessonDetailResponse lessonDetailResponse = LessonDetailResponse.fromDTO(lessonDetailDTO);

            return ResponseEntity.ok(lessonDetailResponse);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
