package academy.prog.julia.json_responses;

import academy.prog.julia.dto.TestAnswerDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;

import java.util.Date;

/**
 * Class representing the response for a test answer.
 *
 * This class is mutable, allowing modifications to its fields.
 */
public class TestAnswerResponse {

    private Long answerId;
    private String course;
    private Integer attempt;
    private Boolean isPassed;
    private  String totalScore;
    private Date submittedDate;
    private UserFromAnswerTaskDTO student;

    /**
     * Constructor to initialize the TestAnswerResponse object.
     *
     * @param answerId the unique identifier for the test answer
     * @param course the name of the course associated with the test answer
     * @param attempt the attempt number for this test answer
     * @param isPassed indicates whether the test has been passed
     * @param totalScore the total score achieved for the test
     * @param submittedDate the date when the test answer was submitted
     * @param student the user who submitted the test answer
     */
    public TestAnswerResponse(
            Long answerId,
            String course,
            Integer attempt,
            Boolean isPassed,
            String totalScore,
            Date submittedDate,
            UserFromAnswerTaskDTO student
    ) {
        this.answerId = answerId;
        this.course = course;
        this.attempt = attempt;
        this.isPassed = isPassed;
        this.totalScore = totalScore;
        this.submittedDate = submittedDate;
        this.student = student;
    }

    /**
     * Gets the unique identifier for the test answer.
     *
     * @return the test answer ID
     */
    public Long getAnswerId() {
        return answerId;
    }

    /**
     * Sets the unique identifier for the test answer.
     *
     * @param answerId the new test answer ID
     */
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    /**
     * Gets the name of the course associated with the test answer.
     *
     * @return the course name
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the name of the course associated with the test answer.
     *
     * @param course the new course name
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Gets the attempt number for this test answer.
     *
     * @return the attempt number
     */
    public Integer getAttempt() {
        return attempt;
    }

    /**
     * Sets the attempt number for this test answer.
     *
     * @param attempt the new attempt number
     */
    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    /**
     * Gets the status indicating whether the test has been passed.
     *
     * @return true if the test is passed, otherwise false
     */
    public Boolean getPassed() {
        return isPassed;
    }

    /**
     * Sets the status indicating whether the test has been passed.
     *
     * @param passed true if the test is passed, otherwise false
     */
    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    /**
     * Gets the total score achieved for the test.
     *
     * @return the total score
     */
    public String getTotalScore() {
        return totalScore;
    }

    /**
     * Sets the total score achieved for the test.
     *
     * @param totalScore the new total score
     */
    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * Gets the date when the test answer was submitted.
     *
     * @return the submission date
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
     * Sets the date when the test answer was submitted.
     *
     * @param submittedDate the new submission date
     */
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    /**
     * Gets the user who submitted the test answer.
     *
     * @return the user who submitted the answer
     */
    public UserFromAnswerTaskDTO getStudent() {
        return student;
    }

    /**
     * Sets the user who submitted the test answer.
     *
     * @param student the new user who submitted the answer
     */
    public void setStudent(UserFromAnswerTaskDTO student) {
        this.student = student;
    }

    /**
     * Converts a TestAnswerDTO object into a TestAnswerResponse object.
     * Useful for transforming DTOs from the service layer into response objects for the client.
     *
     * @param testAnswerDTO the DTO object containing test answer information
     * @return a new TestAnswerResponse object created from the provided DTO, or null if the DTO is null
     */
    public static TestAnswerResponse fromDTO(TestAnswerDTO testAnswerDTO) {
        if (testAnswerDTO == null) {
            return null;
        }

        return new TestAnswerResponse(
                testAnswerDTO.getAnswerId(),
                testAnswerDTO.getCourse(),
                testAnswerDTO.getAttempt(),
                testAnswerDTO.getIsPassed(),
                testAnswerDTO.getTotalScore(),
                testAnswerDTO.getSubmittedDate(),
                testAnswerDTO.getStudent()
        );
    }

}
