package academy.prog.julia.repos;

import academy.prog.julia.model.User;
import academy.prog.julia.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for managing {@code User} entities.
 * <p>
 * This interface provides standard JPA methods along with custom queries
 * to work with the {@code User} table in the database.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Checks whether a user exists with the specified phone number or email.
     *
     * @param phone the phone number to check
     * @param email the email address to check
     * @return {@code true} if a user with the specified phone or email exists, otherwise {@code false}
     */
    boolean existsByPhoneOrEmail(String phone, String email);
    /**
     * Finds a user by their unique ID.
     *
     * @param id the unique ID of the user
     * @return an {@code Optional<User>} containing the user if found, or empty if not found
     */
    Optional<User> findByUniqueId(String id);
    /**
     * Finds a user by their Telegram chat ID.
     *
     * @param chatId the Telegram chat ID
     * @return an {@code Optional<User>} containing the user if found, or empty if not found
     */
    Optional<User> findByTelegramChatId(String chatId);
    /**
     * Finds users whose phone number matches or is similar to the specified phone number.
     *
     * @param phone the phone number to search for
     * @return a list of users with phone numbers similar to the specified phone number
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.phones e WHERE u.phone LIKE %:phone% OR e LIKE %:phone%")
    List<User> findByPhoneLike(@Param("phone") String phone);
    /**
     * Finds users whose email address matches or is similar to the specified email.
     *
     * @param email the email address to search for
     * @return a list of users with email addresses similar to the specified email
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.emails e WHERE u.email LIKE %:email% OR e LIKE %:email%")
    List<User> findByEmailLike(@Param("email") String email);

    /**
     * Finds users whose email address matches or is similar to the specified email with pageable.
     *
     * @param email the email address to search for
     * @return a list of users with email addresses similar to the specified email
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.emails e WHERE u.email LIKE %:email% OR e LIKE %:email%")
    List<User> findByEmailLike(@Param("email") String email, Pageable pageable);
    /**
     * Finds all users in a group by the group name, with pagination support.
     *
     * @param groupName the name of the group
     * @param pageable  the pagination details
     * @return a page of users in the specified group
     */
    @Query("SELECT u FROM User u JOIN u.groups g WHERE g.name = :groupName")
    Page<User> findByGroupName(@Param("groupName") String groupName, Pageable pageable);
    /**
     * Finds all users in a group by the group name.
     *
     * @param groupName the name of the group
     * @return a list of users in the specified group
     */
    @Query("SELECT u FROM User u JOIN u.groups g WHERE g.name = :groupName")
    List<User> findAllUsersByGroups(@Param("groupName") String groupName);
    /**
     * Counts the number of users in a specific group.
     *
     * @param groupName the name of the group
     * @return the number of users in the specified group
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.groups g WHERE g.name = :groupName")
    long countByGroupName(@Param("groupName") String groupName);
    /**
     * Finds users by a list of certificate task IDs.
     *
     * @param userIds the list of user IDs to search for
     * @return a list of users corresponding to the specified certificate task IDs
     */
    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findUsersByCertificateTasksIds(@Param("ids") List<Long> userIds);
    /**
     * Checks if a user with the specified phone number exists.
     *
     * @param phoneNumber the phone number to check
     * @return {@code true} if a user with the phone number exists, otherwise {@code false}
     */
    boolean existsByPhone(String phoneNumber);
    /**
     * Checks if a user with the specified email exists.
     *
     * @param email the email to check
     * @return {@code true} if a user with the email exists, otherwise {@code false}
     */
    boolean existsByEmail(String email);
    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return the user with the specified email
     */
    User findByEmail(String email);
    /**
     * Finds all users associated with a specific task.
     *
     * @param taskId the ID of the task
     * @return a list of users associated with the specified task
     */
    @Query("SELECT u FROM User u JOIN u.groups g JOIN g.lessons l JOIN l.tasks t WHERE t.id = :id")
    List<User> findAllByTaskId(@Param("id") Long taskId);
    /**
     * Finds all users associated with a specific test.
     *
     * @param testId the ID of the test
     * @return a list of users associated with the specified test
     */
    @Query("SELECT u FROM User u JOIN u.groups g JOIN g.lessons l JOIN l.tests t WHERE t.id = :id")
    List<User> findAllByTestId(@Param("id") Long testId);
    /**
     * Finds all users who are banned or active, with pagination support.
     *
     * @param isBanned the banned status
     * @param isActive the active status
     * @param pageable the pagination details
     * @return a list of users who match the banned and active status
     */
    List<User> findAllByIsBannedAndIsActive(boolean isBanned, boolean isActive, Pageable pageable);
    /**
     * Counts all users who are banned or active.
     *
     * @param isBanned the banned status
     * @param isActive the active status
     * @return the count of users who match the banned and active status
     */
    long countAllByIsBannedAndIsActive(boolean isBanned, boolean isActive);

    @Query("SELECT u FROM User u WHERE TYPE(u) = :role")
    List<User> findAllByRole(@Param("role") Class<? extends User> role);
}