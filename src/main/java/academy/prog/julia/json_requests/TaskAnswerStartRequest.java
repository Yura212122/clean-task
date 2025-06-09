package academy.prog.julia.json_requests;

/**
 * Represents a request to initiate the start of a task answer submission,
 * including details about the user, course, lesson, and task.
 */
public class TaskAnswerStartRequest {

    private String answerUrl;
    private Long userId;
    private Long courseId;
    private Long lessonId;
    private Integer lessonNum;
    private Long taskId;
    private String course;

    /**
     * Default constructor for TaskAnswerStartRequest.
     *
     * This constructor is used for creating an instance of {@code TaskAnswerStartRequest}
     * without initializing its fields.
     *
     */
    public TaskAnswerStartRequest() {}

    /**
     * Constructs a new TaskAnswerStartRequest with the specified details.
     *
     * @param answerUrl  the URL where the user's answer is located
     * @param userId     the unique identifier of the user
     * @param courseId   the unique identifier of the course
     * @param lessonId   the unique identifier of the lesson
     * @param lessonNum  the number of the lesson within the course
     * @param taskId     the unique identifier of the task
     * @param course     the name or identifier of the course
     */
    public TaskAnswerStartRequest(
            String answerUrl,
            Long userId,
            Long courseId,
            Long lessonId,
            Integer lessonNum,
            Long taskId,
            String course
    ) {
        this.answerUrl = answerUrl;
        this.userId = userId;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.lessonNum = lessonNum;
        this.taskId = taskId;
        this.course = course;
    }

    /**
     * Gets the URL where the answer is located.
     *
     * @return the answer URL
     */
    public String getAnswerUrl() {
        return answerUrl;
    }

    /**
     * Sets the URL where the answer is located.
     *
     * @param answerUrl the answer URL
     */
    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    /**
     * Gets the ID of the user starting the task.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user starting the task.
     *
     * @param userId the user ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the course associated with the task.
     *
     * @return the course ID
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Sets the ID of the course associated with the task.
     *
     * @param courseId the course ID
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the ID of the lesson associated with the task.
     *
     * @return the lesson ID
     */
    public Long getLessonId() {
        return lessonId;
    }

    /**
     * Sets the ID of the lesson associated with the task.
     *
     * @param lessonId the lesson ID
     */
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    /**
     * Gets the number of the lesson within the course.
     *
     * @return the lesson number
     */
    public Integer getLessonNum() {
        return lessonNum;
    }

    /**
     * Sets the number of the lesson within the course.
     *
     * @param lessonNum the lesson number
     */
    public void setLessonNum(Integer lessonNum) {
        this.lessonNum = lessonNum;
    }

    /**
     * Gets the ID of the task being answered.
     *
     * @return the task ID
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Sets the ID of the task being answered.
     *
     * @param taskId the task ID
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the name or identifier of the course.
     *
     * @return the course name or identifier
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the name or identifier of the course.
     *
     * @param course the course name or identifier
     */
    public void setCourse(String course) {
        this.course = course;
    }

}
