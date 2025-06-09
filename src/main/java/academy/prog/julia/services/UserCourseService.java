package academy.prog.julia.services;

import academy.prog.julia.dto.UserCoursesDTO;
import academy.prog.julia.json_responses.UserCoursesResponse;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.UserCourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user-course relationships. This service handles operations
 * related to retrieving and validating user courses.
 */
@Service
public class UserCourseService {

    private final UserCourseRepository userCourseRepository;
    private final UserService userService;

    /**
     * Constructor for UserCourseService. Uses constructor-based dependency injection
     * to inject required repositories and services.
     *
     * @param userCourseRepository the repository for managing user-course data
     * @param userService the service for managing user-related operations
     */
    public UserCourseService(
            UserCourseRepository userCourseRepository,
            UserService userService
    ) {
        this.userCourseRepository = userCourseRepository;
        this.userService = userService;
    }

    /**
     * Retrieves a list of courses for a given user by their ID.
     * The method is marked as read-only since it's a retrieval operation.
     *
     * @param userId the ID of the user
     * @return a DTO containing the user's courses
     * @throws EntityNotFoundException if the user is not found
     * @throws EmptyResultDataAccessException if no courses are found for the user
     */
    @Transactional(readOnly = true)
    public UserCoursesDTO getUserCourses(Long userId) {
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        User user = optionalUser.get();
        List<Group> userCourses = userCourseRepository.findUserCourses(user.getId());

        if (userCourses.isEmpty()) {
            throw new EmptyResultDataAccessException("No courses found for user with id: " + userId, 1);
        }

        return new UserCoursesDTO(userCourses);
    }

    /**
     * Retrieves the list of courses for a user while validating the session ID.
     * If the session ID does not match the user's session, it returns an unauthorized response.
     * The method is read-only as it performs a retrieval operation.
     *
     * @param userId the ID of the user
     * @param sessionId the session ID to validate
     * @param session the HTTP session object to store relevant data
     * @return a ResponseEntity containing a list of user course responses or an unauthorized status
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserCoursesResponse>> getUserCoursesWithSessionValidation(
            Long userId,
            String sessionId,
            HttpSession session
    ) {
        String userEmail = userService.findById(userId)
                .orElseThrow()
                .getEmail();
        String sessionFromDB = userService.getSessionIdByPrincipalName(userEmail);

        if (sessionFromDB == null || !sessionFromDB.equals(sessionId)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonList(
                            new UserCoursesResponse(userId, "You unauthorized from UserCourseController"))
                    )
            ;
        }

        UserCoursesDTO userCoursesDTO = getUserCourses(userId);
        List<UserCoursesResponse> userCoursesResponses = UserCoursesResponse.fromDTO(userCoursesDTO);
        session.setAttribute("userId", userId);

        return ResponseEntity.ok(userCoursesResponses);
    }

}
