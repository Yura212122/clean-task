package academy.prog.julia.dto;

import academy.prog.julia.model.Group;

import java.util.List;
import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) for representing a user's courses.
 *
 * This class encapsulates the courses (groups) that a user is enrolled in.
 */
public class UserCoursesDTO {

    private final List<Group> courses;

    /**
     * Constructs a UserCoursesDTO with the specified courses.
     *
     * The constructor ensures that the provided list of courses is immutable to avoid
     * accidental modification of the internal state.
     *
     * @param courses the list of courses (groups) the user is enrolled in; can be null
     */
    public UserCoursesDTO(List<Group> courses) {
        // Ensure immutability by copying the provided list if it's not null
        if (courses != null) {
            courses = List.copyOf(courses);
        }
        this.courses = courses;
    }

    /**
     * Returns the list of courses (groups) that the user is enrolled in.
     *
     * @return an immutable list of courses or null if no courses were provided
     */
    public List<Group> getCourses() {
        return courses;
    }

    /**
     * Checks if this UserCoursesDTO is equal to another object.
     *
     * Two UserCoursesDTOs are considered equal if their course lists are identical.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCoursesDTO that = (UserCoursesDTO) o;

        return areCoursesEqual(courses, that.courses);
    }

    /**
     * Returns the hash code for this UserCoursesDTO.
     *
     * The hash code is computed based on the list of courses.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(extractCourseIds(courses));
    }

    /**
     * Compares two lists of courses (groups) for equality by checking their IDs.
     *
     * This prevents cyclic references in the object structure.
     *
     * @param list1 the first list of courses
     * @param list2 the second list of courses
     * @return true if the lists are equal, otherwise false
     */
    private boolean areCoursesEqual(
            List<Group> list1,
            List<Group> list2
    ) {
        if (list1 == null && list2 == null) {
            return true;
        }

        if (list1 == null || list2 == null) {
            return false;
        }

        return list1.stream().map(Group::getId).toList()
                .equals(list2.stream().map(Group::getId).toList());
    }

    /**
     * Extracts the IDs from a list of courses (groups).
     *
     * This method is used to ensure consistent hash code computation and to avoid cyclic references.
     *
     * @param list the list of courses
     * @return a list of course IDs
     */
    private List<Long> extractCourseIds(List<Group> list) {
        if (list == null) {
            return List.of();
        }

        return list
                .stream()
                .map(Group::getId).toList()
        ;
    }

}
