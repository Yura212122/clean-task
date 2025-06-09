package academy.prog.julia.repos;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> { //інтерфейс для роботи з таблицею Групи
    /**
     * Find a group by its name.
     *
     * @param name the name of group to search for
     * @return an {@code Optional <Group>} containing the group if found, of empty if not found
     */
    Optional<Group> findByName(String name);

    boolean existsById(Long id);
    /**
     * Checks whether a group with the specified name exists.
     *
     * @param name the name of the group to check for existence
     * @return {@code true} if a group with the specified name exists, {@code false} otherwise
     */
    boolean existsByName(String name);
    /**
     * Retrieves the names of all groups.
     * <p>
     * This method uses a JPQL (Java Persistence Query Language) query to select the names
     * of all groups and returns them as a list of strings.
     *
     * @return a list of all group names
     */
    @Query("SELECT g.name FROM Group g")
    //JPQL (Java Persistence Query Language) запит -
    List<String> findAllNames();
    /**
     * Finds all groups that are associated with a specific lesson.
     * <p>
     * This method performs a JPQL query that joins the {@code Group} and {@code Lesson} entities
     * based on the lesson's ID.
     *
     * @param lessonId the ID of the lesson to find groups for
     * @return a list of groups associated with the specified lesson
     */
    @Query("SELECT g FROM Group g JOIN g.lessons l WHERE l.id = :lessonId")
    List<Group> findAllByLessonId(@Param("lessonId") Long lessonId);
    /**
     * Finds all groups that a specific user (client) belongs to.
     * <p>
     * This method performs a JPQL query to find all groups associated with a given user.
     *
     * @param user the user (client) whose groups are to be retrieved
     * @return a list of groups associated with the specified user
     */
    @Query("SELECT g FROM Group g JOIN g.clients с WHERE с = :client")
    List<Group> findAllByUser(@Param("client") User user);
}
