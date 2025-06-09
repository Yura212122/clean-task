package academy.prog.julia.repos;

import academy.prog.julia.model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing {@code Test} entities.
 * <p>
 * This interface provides methods to find tests by various criteria, including user ID,
 * deadline, and whether the test is mandatory. It also supports pagination for retrieving
 * tests by a teacher's ID.
 */
public interface TestRepository extends JpaRepository<Test, Long> {

    /**
     * Finds all tests assigned to a specific user with a given deadline and mandatory flag.
     * <p>
     * This method joins the {@code Test}, {@code Lesson}, {@code Group}, and {@code User} entities
     * to retrieve tests associated with the user, filtered by deadline and whether the test is mandatory.
     *
     * @param userId    the ID of the user (client)
     * @param deadLine  the deadline of the test
     * @param mandatory the flag indicating whether the test is mandatory
     * @return a list of tests assigned to the specified user with the given deadline and mandatory flag
     */
    @Query("SELECT t FROM Test t JOIN t.lesson l JOIN l.groups g JOIN g.clients u WHERE u.id = :id AND t.deadline = :deadLine AND t.mandatory = :mandatory")
    List<Test> findAllTestsByUserIdWithDeadLineAndMandatory(@Param("id") Long userId, @Param("deadLine") LocalDate deadLine, @Param("mandatory") boolean mandatory);

    /**
     * Finds all tests associated with a teacher (user) by their ID.
     * <p>
     * This method joins the {@code Test}, {@code Lesson}, {@code Group}, and {@code User} entities
     * to retrieve tests associated with the teacher (user), filtered by user ID. The results
     * are paginated.
     *
     * @param userId   the ID of the teacher (user)
     * @param pageable a {@code Pageable} object to support pagination
     * @return a paginated list of tests associated with the specified teacher
     */
    @Query("SELECT t FROM Test t JOIN t.lesson l JOIN l.groups g JOIN g.clients u WHERE u.id = :id")
    Page<Test> findTestByTeacherId(@Param("id") Long userId, Pageable pageable);
}
