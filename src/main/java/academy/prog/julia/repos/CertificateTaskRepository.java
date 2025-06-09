package academy.prog.julia.repos;

import academy.prog.julia.model.CertificateTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertificateTaskRepository extends JpaRepository<CertificateTask, Long> {
    /**
     * Finds certificate tasks that are not yet generated and have a non-null user ID.
     *
     * @return a list of certificate tasks that are pending generation
     */
    @Query("SELECT c FROM CertificateTask c WHERE c.isGenerated = false AND c.userId IS NOT NULL")
    List<CertificateTask> findCertificateTasksToGenerate();
    /**
     * Finds certificate tasks that have been generated but not yet sent.
     *
     * @return a list of certificate tasks that are generated but pending sending
     */

    @Query("SELECT ct FROM CertificateTask ct WHERE ct.isGenerated = true AND ct.isSend = false")
    List<CertificateTask> findTasksToBeSent();
    // Retrieves tasks from the CertificateTask table that were generated but not sent
}
