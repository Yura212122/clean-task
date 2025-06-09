package academy.prog.julia.dto;

import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing a test question within the context of a test in a lesson detail response.
 *
 * This DTO includes the question itself, possible answer options, and the correct answers.
 */
public class TestQuestionDTOForTestDTOForLessonDetailResponse {

    private Long id;
    //added
    @Size(max = 255, message = "The data is too long.The question cannot contain more than 255 characters")

    private String question;

    private List<String> options = new ArrayList<>();
    private List<String> correctAnswers = new ArrayList<>();



    /**
     * Default constructor for creating an empty TestQuestionDTOForTestDTOForLessonDetailResponse.
     */
    public TestQuestionDTOForTestDTOForLessonDetailResponse() {
    }

    /**
     * Constructor to initialize a TestQuestionDTOForTestDTOForLessonDetailResponse with all required fields.
     *
     * @param id            Unique identifier for the test question. (Cannot be null)
     * @param question      The content of the test question. (Cannot be null)
     * @param options       List of possible answer options. (Cannot be null)
     * @param correctAnswers List of correct answers for the question. (Cannot be null)
     */
    public TestQuestionDTOForTestDTOForLessonDetailResponse(
            Long id,
            String question,
            List<String> options,
            List<String> correctAnswers
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.question = Objects.requireNonNull(question, "question cannot be null");
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.correctAnswers = Objects.requireNonNull(correctAnswers, "correctAnswers cannot be null");
    }

    /**
     * Returns the unique identifier of the test question.
     *
     * @return The ID of the test question.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the test question.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the content of the test question.
     *
     * @return The test question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Sets the content of the test question.
     *
     * @param question The question content to set.
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Returns the list of possible answer options for the test question.
     *
     * @return The list of answer options.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Sets the list of possible answer options for the test question.
     *
     * @param options The list of options to set.
     */
    public void setOptions(List<String> options) {
        this.options = options;
    }

    /**
     * Returns the list of correct answers for the test question.
     *
     * @return The list of correct answers.
     */
    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    /**
     * Sets the list of correct answers for the test question.
     *
     * @param correctAnswers The list of correct answers to set.
     */
    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    /**
     * Provides a string representation of the TestQuestionDTOForTestDTOForLessonDetailResponse instance.
     *
     * @return A string representation of the test question.
     */
    @Override
    public String toString() {
        return "TestQuestionDTOForTestDTOForLessonDetailResponse{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", options=" + options +
                ", correctAnswers=" + correctAnswers +
                '}'
        ;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * Two TestQuestionDTOForTestDTOForLessonDetailResponse objects are considered equal if all their fields are equal.
     *
     * @param o the object to compare with
     * @return true if this object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestQuestionDTOForTestDTOForLessonDetailResponse that = (TestQuestionDTOForTestDTOForLessonDetailResponse) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(question, that.question) &&
                Objects.equals(options, that.options) &&
                Objects.equals(correctAnswers, that.correctAnswers)
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
                id,
                question,
                options,
                correctAnswers
        );
    }

}
