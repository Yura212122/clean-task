package academy.prog.julia.repos;

import academy.prog.julia.model.TestQuestionFromGoogleDocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repository interface for managing {@code TestQuestionFromGoogleDocs} entities.
 * <p>
 * This interface extends {@code JpaRepository} to provide CRUD operations and query methods
 * for {@code TestQuestionFromGoogleDocs} entities.
 */
@Repository
public interface TestQuestionFromGoogleDocsRepository extends JpaRepository<TestQuestionFromGoogleDocs, Long> {
    // Additional query methods can be defined here if needed
}
