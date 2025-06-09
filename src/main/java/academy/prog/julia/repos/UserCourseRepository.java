package academy.prog.julia.repos;

import academy.prog.julia.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing {@code Group} entities related to user courses.
 * <p>
 * This interface provides methods for retrieving course groups that a specific user is enrolled in.
 */
public interface UserCourseRepository extends JpaRepository<Group, Long> {
    /**
     * Finds all courses (groups) that a specific user is enrolled in.
     * <p>
     * This method joins the {@code Group} and {@code User} entities to retrieve all groups
     * where the user with the specified ID is a member.
     *
     * @param userId the ID of the user (client)
     * @return a list of groups (courses) that the specified user is enrolled in
     */
    @Query("SELECT g FROM Group g JOIN g.clients u WHERE u.id = :userId")
    List<Group> findUserCourses(@Param("userId") Long userId);

}