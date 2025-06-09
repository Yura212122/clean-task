package academy.prog.julia.repos;

import academy.prog.julia.model.TestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {
    /**
     * Finds a test answer by the test ID and user (student) ID.
     *
     * @param testId the ID of the test
     * @param studentId the ID of the student (user)
     * @return the test answer associated with the specified test ID and student ID
     */
    TestAnswer findByTestIdAndUserId(Long testId, Long studentId);
}
