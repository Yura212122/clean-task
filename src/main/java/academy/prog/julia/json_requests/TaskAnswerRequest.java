package academy.prog.julia.json_requests;

/**
 * Represents a request to update the status of a task answer, including whether it was passed,
 * requires correction, or has been read.
 *
 * This class is used to encapsulate the necessary information for task answers from students, which includes:
 *      Task answer id
 *      Task is passed or not
 *      Need task correction or not
 *      Message with teacher comment
 *      Is read or not
 */
public class TaskAnswerRequest {

    private Long answerId;
    private Boolean isPassed;
    private Boolean isCorrection;
    private String messageForCorrection;
    private Boolean isRead;

    /**
     * Default constructor for TaskAnswerRequest.
     *
     * This constructor is used for creating an instance of {@code TaskAnswerRequest}
     * without initializing its fields.
     *
     */
    public TaskAnswerRequest() {}

    /**
     * Constructs a new TaskAnswerRequest with the specified details.
     *
     * @param answerId             the unique identifier of the task answer
     * @param isPassed             whether the task answer is passed
     * @param isCorrection         whether the task answer requires correction
     * @param messageForCorrection message describing corrections, if needed
     * @param isRead               whether the task answer has been read
     */
    public TaskAnswerRequest(
            Long answerId,
            Boolean isPassed,
            Boolean isCorrection,
            String messageForCorrection,
            Boolean isRead
    ) {
        this.answerId = answerId;
        this.isPassed = isPassed;
        this.isCorrection = isCorrection;
        this.messageForCorrection = messageForCorrection;
        this.isRead = isRead;
    }

    /**
     * Gets the unique identifier of the answer.
     *
     * @return the unique identifier of the answer
     */
    public Long getAnswerId() {
        return answerId;
    }

    /**
     * Sets the unique identifier of the answer.
     *
     * @param answerId the unique identifier of the answer
     */
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    /**
     * Gets whether the task answer is passed.
     *
     * @return {@code true} if the task answer is passed, {@code false} otherwise
     */
    public Boolean getIsPassed() {
        return isPassed;
    }

    /**
     * Sets whether the task answer is passed.
     *
     * @param passed {@code true} if the task answer is passed, {@code false} otherwise
     */
    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    /**
     * Gets whether the task answer requires correction.
     *
     * @return {@code true} if the task answer requires correction, {@code false} otherwise
     */
    public Boolean getIsCorrection() {
        return isCorrection;
    }

    /**
     * Sets whether the task answer requires correction.
     *
     * @param forCorrection {@code true} if the task answer requires correction, {@code false} otherwise
     */
    public void setIsCorrection(Boolean forCorrection) {
        isCorrection = forCorrection;
    }

    /**
     * Gets the message describing required corrections, if any.
     *
     * @return the correction message
     */
    public String getMessageForCorrection() {
        return messageForCorrection;
    }

    /**
     * Sets the message describing required corrections.
     *
     * @param messageForCorrection the correction message
     */
    public void setMessageForCorrection(String messageForCorrection) {
        this.messageForCorrection = messageForCorrection;
    }

    /**
     * Gets whether the task answer has been read.
     *
     * @return {@code true} if the task answer has been read, {@code false} otherwise
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * Sets whether the task answer has been read.
     *
     * @param read {@code true} if the task answer has been read, {@code false} otherwise
     */
    public void setIsRead(Boolean read) {
        isRead = read;
    }

}
