package academy.prog.julia.repos;

import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing Certificate entities.
 * <p>
 * This interface provides methods for CRUD operations and custom queries
 * related to the Certificate entity using Spring Data JPA.
 */

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    // JpaRepository is an interface from Spring Data that provides a set of
    // standard JPA methods for working with the database, specifically for
    // the Certificate table.
    /**
     * Finds a certificate by its unique identifier.
     *
     * @param certificateId the unique ID of the certificate
     * @return the certificate with the given unique ID, or {@code null} if not found
     */
    Certificate findByUniqueId(String certificateId);
    /**
     * Finds all certificates associated with a specific user.
     *
     * @param user the user whose certificates are being searched for
     * @return a list of certificates associated with the specified user
     */
    @Query("SELECT c FROM Certificate c WHERE c.user IS NULL OR c.user = :user")
    List<Certificate> findByUser(@Param("user") User user);



    /**
     * Finds a certificate by the course (group) name and the user’s ID.
     *
     * @param courseName the name of the course (group) associated with the certificate
     * @param userId the unique ID of the user
     * @return the certificate matching the course name and user ID, or {@code null} if not found
     */
    Certificate findByGroupNameAndUserId(String courseName, Long userId);

    List<Certificate> findByUserId(Long userId);

}