package academy.prog.julia.services;

import academy.prog.julia.dto.LessonDetailDTO;
import academy.prog.julia.dto.LessonsDTO;
import academy.prog.julia.dto.TasksGetNameDTO;
import academy.prog.julia.json_responses.LessonsResponse;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling lesson-related operations.
 * It provides methods to fetch lessons for a course, retrieve lesson details,
 * and check if a course or user is valid and authorized.
 */
@Service
public class LessonsService {

    private final LessonRepository lessonsRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final UserService userService;

    /**
     * Constructor to inject necessary repositories and services.
     *
     * @param lessonsRepository repository for lessons
     * @param groupRepository repository for groups (courses)
     * @param userRepository repository for users
     * @param groupService service to handle group-related logic
     * @param userService service to handle user-related logic
     */

    public LessonsService(
            LessonRepository lessonsRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupService groupService,
            UserService userService
    ) {
        this.lessonsRepository = lessonsRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
        this.userService = userService;
    }

    /**
     * Retrieves all lessons for a given course (group).
     * Throws an exception if the course is not found.
     *
     * @param courseId the ID of the course
     * @return a list of lessons as LessonsDTO objects
     */
    @Transactional(readOnly = true)
    public List<LessonsDTO> getCourseLessons(Long courseId) {
        Optional<Group> courseOptional = groupRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new EntityNotFoundException("Courses not found with id: " + courseId);
        }

        List<Lesson> lessons = lessonsRepository.findCourseLessons(courseId);

        return lessons.stream()
                .map(lesson -> new LessonsDTO(
                        lesson.getId(),
                        lesson.getName(),
                        lesson.getTasks().stream()
                                .map(TasksGetNameDTO::fromTask)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList())
        ;
    }

    /**
     * Retrieves detailed information about a specific lesson.
     * Throws an exception if the lesson is not found.
     *
     * @param lessonId the ID of the lesson
     * @return a LessonDetailDTO object containing lesson details
     */
    @Transactional(readOnly = true)
    public LessonDetailDTO getLessonDetails(Long lessonId) {
        Lesson lesson = lessonsRepository
                .findById(lessonId)
                .orElseThrow(NoSuchElementException::new)
        ;

        return convertToLessonDetailDTO(lesson);
    }

    /**
     * Converts a Lesson entity into a LessonDetailDTO object.
     * Filters out duplicate tasks and tests associated with the lesson.
     *
     * @param lesson the Lesson entity
     * @return a LessonDetailDTO object
     */
    private LessonDetailDTO convertToLessonDetailDTO(Lesson lesson) {
        Set<Task> distinctTasks = lesson.getTasks().stream().distinct().collect(Collectors.toSet());
        Set<Test> distinctTests = lesson.getTests().stream().distinct().collect(Collectors.toSet());

        LessonDetailDTO lessonDetailDTO = new LessonDetailDTO(
                lesson.getName(),
                lesson.getDescriptionUrl(),
                lesson.getVideoUrl(),
                distinctTasks,
                distinctTests
        );

        return lessonDetailDTO;
    }

    @Transactional(readOnly = true)
    public boolean doesCourseExist(Long courseId) {
        return groupRepository.findById(courseId).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isUserInCourse(Long currentUserId, Long lessonId) {
        Optional<User> optionalUser = userRepository.findById(currentUserId);
        User user = optionalUser.orElse(null);

        List<Group> groups = groupRepository.findAllByLessonId(lessonId);

        for (Group group : groups) {
            if (group.getClients().contains(user)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handles the logic of fetching lessons for a course and checks if the user is authorized.
     *
     * @param courseId the ID of the course
     * @param sessionId the session ID of the user
     * @param session the HTTP session to store session-related information
     * @return ResponseEntity containing a list of lessons or an error message if unauthorized
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<LessonsResponse>> getLessonsForCourse(
            Long courseId,
            String sessionId,
            HttpSession session
    ) {
        String principalNameAsEmail = userService.getPrincipalNameBySessionId(sessionId);

        if (!groupService.isStudentInGroup(userService.findUserByEmail(principalNameAsEmail).getId(), courseId)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonList(
                            new LessonsResponse(
                                    courseId,
                                    "User is not authorized to access these lessons.",
                                    null
                            )
                    ))
            ;
        }

        if (!doesCourseExist(courseId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonList(
                            new LessonsResponse(courseId, "Courses not found.", null))
                    )
            ;
        }

        session.setAttribute("courseId", courseId);

        List<LessonsDTO> lessonDTOList = getCourseLessons(courseId);
        List<LessonsResponse> lessonsResponses = LessonsResponse.fromDTO(lessonDTOList);

        return ResponseEntity.ok(lessonsResponses);
    }

}
