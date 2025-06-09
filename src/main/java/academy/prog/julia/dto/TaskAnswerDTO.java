package academy.prog.julia.dto;

import academy.prog.julia.model.TaskAnswer;
import jakarta.persistence.Lob;

import java.util.Date;
import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) for representing a task answer.
 *
 * This class serves as a data container for transferring information about a task answer between
 * different layers of an application. It includes details about the task, the answer provided by
 * a student, and various metadata related to the answer.
 */
public class TaskAnswerDTO {

    private final Long answerId;
    private final String answerUrl;
    private final Long courseId;
    private final Long lessonId;
    private final Integer lessonNum;
    private final Long taskId;
    private final String taskName;
    private final String course;
    private final String description;
    private final Boolean isPassed;
    private final Boolean isCorrection;
    @Lob
    private final String messageForCorrection;
    private final Boolean isRead;
    private final Date submittedDate;
    private final UserFromAnswerTaskDTO student;

    /**
     * Constructs a TaskAnswerDTO with the specified values.
     *
     * @param answerId the unique identifier for the answer (must not be null)
     * @param answerUrl the URL of the answer (must not be null)
     * @param courseId the ID of the course associated with the answer (must not be null)
     * @param lessonId the ID of the lesson associated with the answer (must not be null)
     * @param lessonNum the number of the lesson (must not be null)
     * @param taskId the ID of the task associated with the answer (must not be null)
     * @param taskName the name of the task (must not be null)
     * @param course the name of the course (must not be null)
     * @param description the description of the task (must not be null)
     * @param isPassed indicates if the answer has been passed
     * @param isCorrection indicates if the answer is for correction
     * @param messageForCorrection the message provided for correction
     * @param isRead indicates if the answer has been read
     * @param submittedDate the date when the answer was submitted
     * @param student the student who submitted the answer (must not be null)
     *
     * @throws NullPointerException if any required fields are null
     */
    public TaskAnswerDTO(
            Long answerId,
            String answerUrl,
            Long courseId,
            Long lessonId,
            Integer lessonNum,
            Long taskId,
            String taskName,
            String course,
            String description,
            Boolean isPassed,
            Boolean isCorrection,
            String messageForCorrection,
            Boolean isRead,
            Date submittedDate,
            UserFromAnswerTaskDTO student
    ) {
        this.answerId = Objects.requireNonNull(answerId, "answerId cannot be null");
        this.answerUrl = Objects.requireNonNull(answerUrl, "answerUrl cannot be null");
        this.courseId = Objects.requireNonNull(courseId, "courseId cannot be null");
        this.lessonId = Objects.requireNonNull(lessonId, "lessonId cannot be null");
        this.lessonNum = Objects.requireNonNull(lessonNum, "lessonNum cannot be null");
        this.taskId = Objects.requireNonNull(taskId, "taskId cannot be null");
        this.taskName = Objects.requireNonNull(taskName, "taskName cannot be null");
        this.course = Objects.requireNonNull(course, "course cannot be null");
        this.description = Objects.requireNonNull(description, "description cannot be null");
        this.isPassed = isPassed;
        this.isCorrection = isCorrection;
        this.messageForCorrection = messageForCorrection;
        this.isRead = isRead;
        this.submittedDate = Objects.requireNonNull(submittedDate, "submittedDate cannot be null");
        this.student = Objects.requireNonNull(student, "student cannot be null");
    }

    /**
     * Returns the unique identifier of the task answer.
     *
     * @return the unique identifier of the answer.
     */
    public Long getAnswerId() {
        return answerId;
    }

    /**
     * Returns the URL link to the task answer.
     *
     * @return the URL link where the task answer can be accessed.
     */
    public String getAnswerUrl() {
        return answerUrl;
    }

    /**
     * Returns the ID of the course associated with the task answer.
     *
     * @return the unique identifier of the course.
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Returns the ID of the lesson associated with the task answer.
     *
     * @return the unique identifier of the lesson.
     */
    public Long getLessonId() {
        return lessonId;
    }

    /**
     * Returns the number of the lesson within the course.
     *
     * @return the number representing the position of the lesson within the course.
     */
    public Integer getLessonNum() {
        return lessonNum;
    }

    /**
     * Returns the ID of the task associated with the answer.
     *
     * @return the unique identifier of the task.
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Returns the name of the task.
     *
     * @return the name or title of the task.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Returns the name of the course associated with the task answer.
     *
     * @return the name of the course.
     */
    public String getCourse() {
        return course;
    }

    /**
     * Returns the description of the task.
     *
     * @return a description or additional information about the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status indicating if the task answer has been passed.
     *
     * @return true if the task has been passed, false otherwise.
     */
    public Boolean getIsPassed() {
        return isPassed;
    }

    /**
     * Returns the status indicating if the task answer is under correction.
     *
     * @return true if the task is under correction, false otherwise.
     */
    public Boolean getIsCorrection() {
        return isCorrection;
    }

    /**
     * Returns the message or feedback related to corrections for the task.
     *
     * @return the message or feedback provided for corrections.
     */
    public String getMessageForCorrection() {
        return messageForCorrection;
    }

    /**
     * Returns the status indicating if the task answer has been read.
     *
     * @return true if the task answer has been read, false otherwise.
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * Returns the date when the task answer was submitted.
     *
     * @return the date of submission.
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
     * Returns the information about the student who submitted the task answer.
     *
     * @return a UserFromAnswerTaskDTO representing the student.
     */
    public UserFromAnswerTaskDTO getStudent() {
        return student;
    }

    /**
     * Converts a TaskAnswer entity to a TaskAnswerDTO.
     *
     * @param taskAnswer the TaskAnswer entity to convert (may be null)
     * @return a TaskAnswerDTO representation of the entity, or null if the entity is null
     */
    public static TaskAnswerDTO fromTaskAnswer(TaskAnswer taskAnswer) {
        if (taskAnswer == null || taskAnswer.getUser() == null) {
            return null;
        }

        UserFromAnswerTaskDTO userFromAnswerTaskDTO = UserFromAnswerTaskDTO.fromUser(taskAnswer.getUser());

        return new TaskAnswerDTO(
                taskAnswer.getId(),
                taskAnswer.getAnswerUrl(),
                taskAnswer.getCourseId(),
                taskAnswer.getLessonId(),
                taskAnswer.getLessonNum(),
                taskAnswer.getTask().getId(),
                taskAnswer.getTask().getName(),
                taskAnswer.getCourse(),
                taskAnswer.getTask().getDescriptionUrl(),
                taskAnswer.getIsPassed(),
                taskAnswer.getIsCorrection(),
                taskAnswer.getMessageForCorrection(),
                taskAnswer.getIsRead(),
                taskAnswer.getSubmittedDate(),
                userFromAnswerTaskDTO
        );
    }

    /**
     * Determines if two TaskAnswerDTO objects are equal by comparing their fields.
     *
     * @param o the object to compare against
     * @return true if the objects are considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAnswerDTO that = (TaskAnswerDTO) o;

        return Objects.equals(answerId, that.answerId) &&
                Objects.equals(answerUrl, that.answerUrl) &&
                Objects.equals(courseId, that.courseId) &&
                Objects.equals(lessonId, that.lessonId) &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(taskName, that.taskName) &&
                Objects.equals(course, that.course) &&
                Objects.equals(description, that.description) &&
                Objects.equals(isPassed, that.isPassed) &&
                Objects.equals(isCorrection, that.isCorrection) &&
                Objects.equals(messageForCorrection, that.messageForCorrection) &&
                Objects.equals(isRead, that.isRead) &&
                Objects.equals(submittedDate, that.submittedDate) &&
                areStudentEqual(student, that.student)
        ;
    }

    /**
     * Generates a hash code for the TaskAnswerDTO.
     * This method uses the Objects.hash function to generate a hash code based on the fields of the DTO.
     *
     * @return the hash code representing this TaskAnswerDTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                answerId,
                answerUrl,
                courseId,
                lessonId,
                taskId,
                taskName,
                course,
                description,
                isPassed,
                isCorrection,
                messageForCorrection,
                isRead,
                submittedDate,
                extractUserId(student)
        );
    }

    /**
     * Compares two students based on their user IDs.
     * Handles null values gracefully by considering null students unequal to any non-null student.
     *
     * @param student1 the first student to compare
     * @param student2 the second student to compare
     * @return true if both students are equal based on their IDs, false otherwise
     */
    private boolean areStudentEqual(
            UserFromAnswerTaskDTO student1,
            UserFromAnswerTaskDTO student2
    ) {
        if (student1 == null && student2 == null) {
            return true;
        }

        if (student1 == null || student2 == null) {
            return false;
        }

        return Objects.equals(extractUserId(student1), extractUserId(student2));
    }

    /**
     * Extracts the user ID from a UserFromAnswerTaskDTO.
     * If the user is null, returns null.
     *
     * @param user the user from whom to extract the ID
     * @return the ID of the user, or null if the user is null
     */
    private Long extractUserId(UserFromAnswerTaskDTO user) {
        if (user == null) {
            return null;
        }

        return user.getId();
    }

}
