package academy.prog.julia.json_responses;

import academy.prog.julia.dto.UserCoursesDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing the response for user courses.
 *
 * This class is mutable, allowing modifications to its fields.
 */
public class UserCoursesResponse {

    private Long id;
    private String name;

    /**
     * Constructor to initialize the UserCoursesResponse object.
     *
     * @param id the unique identifier for the course
     * @param name the name of the course
     */
    public UserCoursesResponse(
            Long id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    /**
     * Converts a UserCoursesDTO object into a list of UserCoursesResponse objects.
     * This method transforms the DTO data into a format suitable for the response to the client.
     *
     * @param userCoursesDTO the DTO object containing user course information
     * @return a list of UserCoursesResponse objects created from the provided DTO
     */
    public static List<UserCoursesResponse> fromDTO(UserCoursesDTO userCoursesDTO) {
        return userCoursesDTO.getCourses().stream()
                .map(course ->
                        new UserCoursesResponse(
                                course.getId(),
                                course.getName()
                        )
                )
                .collect(Collectors.toList())
        ;
    }

    /**
     * Gets the unique identifier for the course.
     *
     * @return the course ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the course.
     *
     * @param id the new course ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the course.
     *
     * @return the course name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the course.
     *
     * @param name the new course name
     */
    public void setName(String name) {
        this.name = name;
    }

}
