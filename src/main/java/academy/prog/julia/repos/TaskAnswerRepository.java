package academy.prog.julia.repos;

import academy.prog.julia.model.TaskAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TaskAnswerRepository extends JpaRepository<TaskAnswer, Long> {
    /**
     * Finds a task answer by task ID and user ID.
     *
     * @param taskId    the ID of the task
     * @param studentId the ID of the student (user)
     * @return the task answer associated with the specified task ID and user ID
     */
    TaskAnswer findByTaskIdAndUserId(Long taskId, Long studentId);
    //SELECT t FROM Task t WHERE t.deadline >= :dateNow
    /**
     * Finds all task answers submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of task answers submitted by the specified user
     */
    List<TaskAnswer> findByUserId(Long userId);
    /**
     * Finds a task answer by task ID.
     * <p>
     * This method joins the {@code TaskAnswer} and {@code Task} entities and retrieves the task
     * answer associated with the specified task ID.
     *
     * @param taskId the ID of the task
     * @return the task answer associated with the given task ID
     */
    @Query("SELECT ta FROM TaskAnswer ta JOIN ta.task t WHERE t.id = :taskId")
    TaskAnswer findByTaskId(@Param("taskId") Long taskId);
    /**
     * Finds pending task answers that were submitted before the task deadline and are not yet passed.
     * <p>
     * This method retrieves task answers where the submission date is less than or equal to the task's
     * deadline, and the task answer has not yet passed.
     *
     * @param pageable a {@code Pageable} object to support pagination
     * @return a page of pending task answers that have not passed and were submitted before the deadline
     */
    @Query("SELECT ta FROM TaskAnswer ta JOIN ta.task t WHERE ta.submittedDate <= t.deadline AND ta.isPassed = false")
    Page<TaskAnswer> findPendingTask(Pageable pageable);
    /**
     * Finds pending task answers by group name that were submitted before the task deadline and are not yet passed.
     * <p>
     * This method joins the {@code TaskAnswer}, {@code Task}, and {@code Group} entities and retrieves
     * task answers submitted before the deadline, not yet passed, and filtered by the specified group name.
     *
     * @param pageable  a {@code Pageable} object to support pagination
     * @param groupName the name of the group
     * @return a page of pending task answers for the specified group that have not passed and were submitted before the deadline
     */
    @Query("SELECT ta FROM TaskAnswer ta JOIN ta.task t JOIN ta.user u JOIN u.groups g WHERE ta.submittedDate <= t.deadline AND ta.isPassed = false AND g.name = :groupName")
    Page<TaskAnswer> findPendingTaskByGroup(Pageable pageable, @Param("groupName") String groupName);
}
