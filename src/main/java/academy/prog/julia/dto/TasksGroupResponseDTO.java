package academy.prog.julia.dto;

import academy.prog.julia.model.TaskAnswer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for representing a task answer in a grouped response.
 *
 * This class encapsulates information about a task answer, including its URL, pass status,
 * submission date, and user name.
 */
public class TasksGroupResponseDTO {

    private String answerUrl;
    private Boolean isPassed;
    private String submittedDate;
    private String userName;

    /**
     * Default constructor for creating an empty TasksGroupResponseDTO.
     */
    public TasksGroupResponseDTO() {
    }

    /**
     * Constructs a TasksGroupResponseDTO with the specified details.
     *
     * @param answerUrl the URL of the task answer (must not be null)
     * @param isPassed indicates whether the task has been passed
     * @param submittedDate the submission date of the task in "yyyy-MM-dd" format (must not be null)
     * @param userName the name of the user who submitted the task (must not be null)
     * @throws NullPointerException if any of the required parameters are null
     */
    public TasksGroupResponseDTO(
            String answerUrl,
            Boolean isPassed,
            String submittedDate,
            String userName
    ) {
        this.answerUrl = Objects.requireNonNull(answerUrl, "answerUrl cannot be null");
        this.isPassed = isPassed;
        this.submittedDate = Objects.requireNonNull(submittedDate, "submittedDate cannot be null");
        this.userName = Objects.requireNonNull(userName, "userName cannot be null");
    }

    /**
     * Returns the URL of the task answer.
     *
     * @return the URL of the task answer
     */
    public String getAnswerUrl() {
        return answerUrl;
    }

    /**
     * Sets the URL of the task answer.
     *
     * @param answerUrl the URL to set (must not be null)
     * @throws NullPointerException if answerUrl is null
     */
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    /**
     * Returns whether the task has been passed.
     *
     * @return true if the task has been passed, false otherwise
     */
    public Boolean getPassed() {
        return isPassed;
    }

    /**
     * Sets whether the task has been passed.
     *
     * @param passed true if the task has been passed, false otherwise
     */
    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    /**
     * Returns the submission date of the task.
     *
     * @return the submission date as a string in "yyyy-MM-dd" format
     */
    public String getSubmittedDate() {
        return submittedDate;
    }

    /**
     * Sets the submission date of the task.
     *
     * @param submittedDate the date to set in "yyyy-MM-dd" format (must not be null)
     * @throws NullPointerException if submittedDate is null
     */
    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    /**
     * Returns the name of the user who submitted the task.
     *
     * @return the user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the user who submitted the task.
     *
     * @param userName the name to set (must not be null)
     * @throws NullPointerException if userName is null
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Creates a TasksGroupResponseDTO from a TaskAnswer entity.
     *
     * This method converts a TaskAnswer entity into a TasksGroupResponseDTO, formatting the submission date
     * to a string in "yyyy-MM-dd" format.
     *
     * @param task the TaskAnswer entity to convert (must not be null)
     * @return a TasksGroupResponseDTO representation of the TaskAnswer entity
     * @throws NullPointerException if task is null
     */
    public static TasksGroupResponseDTO fromTask(TaskAnswer task) {
        if (task == null) {
            return null;
        }

        long milliseconds = task.getSubmittedDate().getTime();
        Date date = new Date(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);

        return new TasksGroupResponseDTO(
                task.getAnswerUrl(),
                task.getIsPassed(),
                dateString,
                task.getUser().getName()
        );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * Two TasksGroupResponseDTO objects are considered equal if all their fields are equal.
     *
     * @param o the object to compare with
     * @return true if this object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksGroupResponseDTO that = (TasksGroupResponseDTO) o;

        return Objects.equals(answerUrl, that.answerUrl) &&
                Objects.equals(isPassed, that.isPassed) &&
                Objects.equals(submittedDate, that.submittedDate) &&
                Objects.equals(userName, that.userName)
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
                answerUrl,
                isPassed,
                submittedDate,
                userName
        );
    }

}
