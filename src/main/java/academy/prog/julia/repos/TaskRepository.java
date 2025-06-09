package academy.prog.julia.repos;

import academy.prog.julia.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Finds all active tasks for a user with a specific deadline.
     * <p>
     * This method joins the {@code Task}, {@code Lesson}, {@code Group}, and {@code User} entities
     * to retrieve tasks assigned to the user, filtering by user ID and task deadline.
     *
     * @param userId   the ID of the user (client)
     * @param deadLine the deadline of the task
     * @return a list of active tasks for the specified user and deadline
     */
    @Query("SELECT t FROM Task t JOIN t.lesson l JOIN l.groups g JOIN g.clients u WHERE u.id = :id AND t.deadline = :deadLine")
    List<Task> findAllActiveTaskByUserIdWithDeadLine(@Param("id") Long userId, @Param("deadLine") LocalDate deadLine);
    /**
     * Finds all tasks associated with a teacher (user) by their ID.
     * <p>
     * This method joins the {@code Task}, {@code Lesson}, {@code Group}, and {@code User} entities
     * to retrieve tasks associated with the teacher (user), filtering by user ID.
     * The result is paginated.
     *
     * @param userId   the ID of the teacher (user)
     * @param pageable a {@code Pageable} object to support pagination
     * @return a page of tasks associated with the specified teacher
     */
    @Query("SELECT t FROM Task t JOIN t.lesson l JOIN l.groups g JOIN g.clients u WHERE u.id = :id")
    Page<Task> findTaskByTeacherId(@Param("id") Long userId, Pageable pageable);


    @Query("""
    SELECT t FROM Task t
    JOIN t.lesson l
    JOIN l.groups g
    JOIN g.clients u
    WHERE u.id = :userId AND t.deadline <= :now
""")
    List<Task> findExpiredTasks(@Param("now") LocalDate now, @Param("userId") Long userId);


}
