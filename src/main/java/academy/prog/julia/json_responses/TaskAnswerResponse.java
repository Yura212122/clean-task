package academy.prog.julia.json_responses;

import academy.prog.julia.dto.TaskAnswerDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;
import jakarta.persistence.Lob;

import java.util.Date;

/**
 * Represents the response object for a submitted task answer.
 *
 * This class holds details about the task answer, its status, associated task/lesson, and student information.
 *
 */
public class TaskAnswerResponse {

    private Long answerId;
    private String answerUrl;
    private Long courseId;
    private Long lessonId;
    private Integer lessonNum;
    private Long taskId;
    private String taskName;
    private String course;
    private String description;
    private Boolean isPassed;
    private Boolean isCorrection;
    @Lob
    private String messageForCorrection;
    private Boolean isRead;
    private Date submittedDate;
    private UserFromAnswerTaskDTO student;

    /**
     * Constructor for {@link TaskAnswerResponse}.
     *
     * @param answerId           ID of the answer
     * @param answerUrl          URL of the submitted answer
     * @param courseId           ID of the course
     * @param lessonId           ID of the lesson
     * @param lessonNum          Number of the lesson
     * @param taskId             ID of the task
     * @param taskName           Name of the task
     * @param course             Course name
     * @param description        Description of the task answer
     * @param isPassed           Whether the task has been passed
     * @param isCorrection       Whether the task requires correction
     * @param messageForCorrection Feedback message for correction
     * @param isRead             Whether the submission has been read/reviewed
     * @param submittedDate      Date of submission
     * @param student            Information about the student
     */
    public TaskAnswerResponse(
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
        this.answerId = answerId;
        this.answerUrl = answerUrl;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.lessonNum = lessonNum;
        this.taskId = taskId;
        this.taskName = taskName;
        this.course = course;
        this.description = description;
        this.isPassed = isPassed;
        this.isCorrection = isCorrection;
        this.messageForCorrection = messageForCorrection;
        this.isRead = isRead;
        this.submittedDate = submittedDate;
        this.student = student;
    }

    /**
     * Gets the answer ID.
     *
     * @return the ID of the answer
     */
    public Long getAnswerId() {
        return answerId;
    }

    /**
     * Sets the answer ID.
     *
     * @param answerId the ID to set for the answer
     */
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    /**
     * Gets the URL of the answer.
     *
     * @return the URL where the answer is stored
     */
    public String getAnswerUrl() {
        return answerUrl;
    }

    /**
     * Sets the URL of the answer.
     *
     * @param answerUrl the URL to set for the answer
     */
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    /**
     * Gets the course ID.
     *
     * @return the ID of the course
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID.
     *
     * @param courseId the ID to set for the course
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the lesson ID.
     *
     * @return the ID of the lesson
     */
    public Long getLessonId() {
        return lessonId;
    }

    /**
     * Sets the lesson ID.
     *
     * @param lessonId the ID to set for the lesson
     */
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    /**
     * Gets the lesson number.
     *
     * @return the number of the lesson
     */
    public Integer getLessonNum() {
        return lessonNum;
    }

    /**
     * Sets the lesson number.
     *
     * @param lessonNum the number to set for the lesson
     */
    public void setLessonNum(Integer lessonNum) {
        this.lessonNum = lessonNum;
    }

    /**
     * Gets the task ID.
     *
     * @return the ID of the task
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Sets the task ID.
     *
     * @param taskId the ID to set for the task
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the task name.
     *
     * @return the name of the task
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets the task name.
     *
     * @param taskName the name to set for the task
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets the course name.
     *
     * @return the name of the course
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the course name.
     *
     * @param course the name to set for the course
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Gets the task description.
     *
     * @return the description of the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the task description.
     *
     * @param description the description to set for the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the task passed status.
     *
     * @return true if the task is passed, false otherwise
     */
    public Boolean getPassed() {
        return isPassed;
    }

    /**
     * Sets the task passed status.
     *
     * @param passed the status to set for the task (true if passed, false otherwise)
     */
    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    /**
     * Gets the correction status.
     *
     * @return true if the task requires correction, false otherwise
     */
    public Boolean getIsCorrection() {
        return isCorrection;
    }

    /**
     * Sets the correction status.
     *
     * @param forCorrection the correction status to set (true if correction is needed)
     */
    public void setIsCorrection(Boolean forCorrection) {
        isCorrection = forCorrection;
    }

    /**
     * Gets the message for correction.
     *
     * @return the message provided for correction
     */
    public String getMessageForCorrection() {
        return messageForCorrection;
    }

    /**
     * Sets the message for correction.
     *
     * @param messageForCorrection the message to set for correction
     */
    public void setMessageForCorrection(String messageForCorrection) {
        this.messageForCorrection = messageForCorrection;
    }

    /**
     * Gets the read status of the answer.
     *
     * @return true if the answer has been read, false otherwise
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * Sets the read status of the answer.
     *
     * @param read the status to set (true if the answer has been read)
     */
    public void setIsRead(Boolean read) {
        isRead = read;
    }

    /**
     * Gets the submitted date of the answer.
     *
     * @return the date when the answer was submitted
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }

    /**
     * Sets the submitted date of the answer.
     *
     * @param submittedDate the date to set for when the answer was submitted
     */
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    /**
     * Gets the student information.
     *
     * @return the student who submitted the answer
     */
    public UserFromAnswerTaskDTO getStudent() {
        return student;
    }

    /**
     * Sets the student information.
     *
     * @param student the student to set for the answer
     */
    public void setStudent(UserFromAnswerTaskDTO student) {
        this.student = student;
    }

    /**
     * Static method to convert a {@link TaskAnswerDTO} object to a {@link TaskAnswerResponse} object.
     *
     * @param taskAnswerDTO the {@link TaskAnswerDTO} object to be converted
     * @return a new {@link TaskAnswerResponse} object, or null if the input is null
     */
    public static TaskAnswerResponse fromDTO(TaskAnswerDTO taskAnswerDTO) {
        if (taskAnswerDTO == null) {
            return null;
        }

        return new TaskAnswerResponse(
                taskAnswerDTO.getAnswerId(),
                taskAnswerDTO.getAnswerUrl(),
                taskAnswerDTO.getCourseId(),
                taskAnswerDTO.getLessonId(),
                taskAnswerDTO.getLessonNum(),
                taskAnswerDTO.getTaskId(),
                taskAnswerDTO.getTaskName(),
                taskAnswerDTO.getCourse(),
                taskAnswerDTO.getDescription(),
                taskAnswerDTO.getIsPassed(),
                taskAnswerDTO.getIsCorrection(),
                taskAnswerDTO.getMessageForCorrection(),
                taskAnswerDTO.getIsRead(),
                taskAnswerDTO.getSubmittedDate(),
                taskAnswerDTO.getStudent()
        );
    }

}
