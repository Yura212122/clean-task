package academy.prog.julia.dto;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for representing a test in the context of lesson detail responses,
 * including associated test questions.
 *
 * This class is used to transfer test information and its associated questions between different
 * layers of the application, such as between the service layer and the presentation layer.
 */
public class TestDTOForLessonDetailResponseWithTestAnswers {

    private Long id;
    private String name;
    private Boolean isMandatory;
    private LocalDate deadline;
    Set<TestQuestionDTOForTestDTOForLessonDetailResponse> questions;

    /**
     * Default constructor for creating an empty TestDTOForLessonDetailResponseWithTestAnswer.
     */
    public TestDTOForLessonDetailResponseWithTestAnswers() {
    }

    /**
     * Constructs a new {@code TestDTOForLessonDetailResponseWithTestAnswers} with the specified values.
     *
     * @param id         The unique identifier for the test. (Cannot be null)
     * @param name       The name of the test. (Cannot be null)
     * @param isMandatory Indicates whether the test is mandatory.
     * @param deadline   The deadline for the test.
     * @param questions  Set of questions associated with the test. (Cannot be null)
     */
    public TestDTOForLessonDetailResponseWithTestAnswers(
            Long id,
            String name,
            Boolean isMandatory,
            LocalDate deadline,
            Set<TestQuestionDTOForTestDTOForLessonDetailResponse> questions
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.isMandatory = isMandatory;
        this.deadline = deadline;
        this.questions = Objects.requireNonNull(questions, "questions cannot be null");
    }

    /**
     * Returns the unique identifier for the test.
     *
     * @return The unique identifier for the test.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the test.
     *
     * @param id The unique identifier for the test.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the test.
     *
     * @return The name of the test.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the test.
     *
     * @param name The name of the test.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether the test is mandatory.
     *
     * @return {@code true} if the test is mandatory, {@code false} otherwise.
     */
    public Boolean getMandatory() {
        return isMandatory;
    }

    /**
     * Sets whether the test is mandatory.
     *
     * @param mandatory {@code true} if the test is mandatory, {@code false} otherwise.
     */
    public void setMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }

    /**
     * Returns the deadline for the test.
     *
     * @return The deadline for the test.
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline for the test.
     *
     * @param deadline The deadline for the test.
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * Returns the set of questions associated with the test.
     *
     * @return The set of questions associated with the test.
     */
    public Set<TestQuestionDTOForTestDTOForLessonDetailResponse> getQuestions() {
        return questions;
    }

    /**
     * Sets the set of questions associated with the test.
     *
     * @param questions The set of questions associated with the test.
     */
    public void setQuestions(Set<TestQuestionDTOForTestDTOForLessonDetailResponse> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "TestDTOForLessonDetailResponseWithTestAnswers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isMandatory=" + isMandatory +
                ", deadline=" + deadline +
                ", questions=" + questions +
                '}'
        ;
    }

    /**
     * Checks if this {@code TestDTOForLessonDetailResponseWithTestAnswers} is equal to another object.
     *
     * @param o The other object to compare to.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestDTOForLessonDetailResponseWithTestAnswers that = (TestDTOForLessonDetailResponseWithTestAnswers) o;

        return id.equals(that.id) &&
                name.equals(that.name) &&
                Objects.equals(isMandatory, that.isMandatory) &&
                Objects.equals(deadline, that.deadline) &&
                areTestQuestionDTOForTestDTOForLessonDetailResponseEqual(this.questions, that.questions)
        ;
    }

    /**
     * Computes the hash code for this {@code TestDTOForLessonDetailResponseWithTestAnswers}.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                isMandatory,
                deadline,
                extractTestQuestionDTOForTestDTOForLessonDetailResponseIds(questions)
        );
    }

    /**
     * Extracts the set of question IDs from the set of {@code TestQuestionDTOForTestDTOForLessonDetailResponse}.
     *
     * @param questions The set of test questions.
     * @return A set of IDs corresponding to the test questions.
     */
    private static Set<Long> extractTestQuestionDTOForTestDTOForLessonDetailResponseIds(
            Set<TestQuestionDTOForTestDTOForLessonDetailResponse> questions
    ) {
        return questions.stream()
                .map(TestQuestionDTOForTestDTOForLessonDetailResponse::getId)
                .collect(Collectors.toSet())
        ;
    }

    /**
     * Compares two sets of {@code TestQuestionDTOForTestDTOForLessonDetailResponse} for equality.
     *
     * @param set1 The first set of test questions.
     * @param set2 The second set of test questions.
     * @return {@code true} if both sets contain the same question IDs, {@code false} otherwise.
     */
    private static boolean areTestQuestionDTOForTestDTOForLessonDetailResponseEqual(
            Set<TestQuestionDTOForTestDTOForLessonDetailResponse> set1,
            Set<TestQuestionDTOForTestDTOForLessonDetailResponse> set2
    ) {
        return extractTestQuestionDTOForTestDTOForLessonDetailResponseIds(set1)
                .equals(extractTestQuestionDTOForTestDTOForLessonDetailResponseIds(set2))
        ;
    }

}
