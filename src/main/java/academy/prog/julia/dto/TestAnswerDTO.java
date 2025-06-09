package academy.prog.julia.dto;

import academy.prog.julia.model.TestAnswer;

import java.util.Date;
import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) for representing a test answer.
 *
 * This class is used to transfer test answer data between different layers of the application, such as
 * between the persistence layer and the service layer or between the service layer and the presentation layer.
 * It provides a read-only view of the test answer information.
 */
public class TestAnswerDTO {

    private final Long answerId;
    private final String course;
    private final Integer attempt;
    private final Boolean isPassed;
    private final String totalScore;
    private final Date submittedDate;
    private final UserFromAnswerTaskDTO student;

    /**
     * Constructs a new {@code TestAnswerDTO} with the specified values.
     *
     * @param answerId      The unique identifier for the test answer. Cannot be null.
     * @param course        The course associated with the test answer. Cannot be null.
     * @param attempt       The attempt number for the test answer.
     * @param isPassed      Indicates whether the test answer was passed.
     * @param totalScore    The total score achieved in the test.
     * @param submittedDate The date and time when the test answer was submitted. Cannot be null.
     * @param student       Details of the student who provided the test answer. Cannot be null.
     */
    public TestAnswerDTO(
            Long answerId,
            String course,
            Integer attempt,
            Boolean isPassed,
            String totalScore,
            Date submittedDate,
            UserFromAnswerTaskDTO student
    ) {
        this.answerId = Objects.requireNonNull(answerId, "answerId cannot be null");
        this.course = Objects.requireNonNull(course, "course cannot be null");
        this.attempt = attempt;
        this.isPassed = isPassed;
        this.totalScore = totalScore;
        this.submittedDate = Objects.requireNonNull(submittedDate, "submittedDate cannot be null");
        this.student = Objects.requireNonNull(student, "student cannot be null");
    }

    /**
     * Returns the unique identifier for the test answer.
     *
     * @return The unique identifier for the test answer.
     */
    public Long getAnswerId() {
        return answerId;
    }

    /**
     * Returns the course associated with the test answer.
     *
     * @return The course associated with the test answer.
     */
    public String getCourse() {
        return course;
    }

    /**
     * Returns the attempt number for the test answer.
     *
     * @return The attempt number for the test answer.
     */
    public Integer getAttempt() {
        return attempt;
    }

    /**
     * Returns whether the test answer was passed.
     *
     * @return {@code true} if the test answer was passed, {@code false} otherwise.
     */
    public Boolean getIsPassed() {
        return isPassed;
    }

    /**
     * Returns the total score achieved in the test.
     *
     * @return The total score achieved in the test.
     */
    public String getTotalScore() {
        return totalScore;
    }

    /**
     * Returns the date and time when the test answer was submitted.
     *
     * @return The date and time when the test answer was submitted.
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
     * Returns details of the student who provided the test answer.
     *
     * @return Details of the student who provided the test answer.
     */
    public UserFromAnswerTaskDTO getStudent() {
        return student;
    }

    /**
     * Converts a {@code TestAnswer} entity to a {@code TestAnswerDTO}.
     * <p>
     * This method extracts relevant information from the given {@code TestAnswer} entity and constructs a
     * {@code TestAnswerDTO} with that information. If the provided {@code TestAnswer} or its associated user is null,
     * the method returns null.
     *
     * @param testAnswer The {@code TestAnswer} entity to convert. May be null.
     * @return A {@code TestAnswerDTO} representing the given {@code TestAnswer}, or {@code null} if the input is null.
     */
    public static TestAnswerDTO fromTestAnswer(TestAnswer testAnswer) {
        if (testAnswer == null || testAnswer.getUser() == null) {
            return null;
        }

        UserFromAnswerTaskDTO userFromAnswerTaskDTO = UserFromAnswerTaskDTO.fromUser(testAnswer.getUser());

        return new TestAnswerDTO(
                testAnswer.getId(),
                testAnswer.getCourse(),
                testAnswer.getAttempt(),
                testAnswer.getIsPassed(),
                testAnswer.getTotalScore(),
                testAnswer.getSubmittedDate(),
                userFromAnswerTaskDTO
        );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * Two TestAnswerDTO objects are considered equal if all their fields are equal.
     *
     * @param o the object to compare with
     * @return true if this object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAnswerDTO that = (TestAnswerDTO) o;

        return Objects.equals(answerId, that.answerId) &&
                Objects.equals(course, that.course) &&
                Objects.equals(attempt, that.attempt) &&
                Objects.equals(isPassed, that.isPassed) &&
                Objects.equals(totalScore, that.totalScore) &&
                Objects.equals(submittedDate, that.submittedDate) &&
                Objects.equals(student, that.student)
        ;
    }

    /**
     * Returns a hash code value for the object.
     *
     * The hash code is computed based on the values of the fields that are used in equals().
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                answerId,
                course,
                attempt,
                isPassed,
                totalScore,
                submittedDate,
                student
        );
    }

}
