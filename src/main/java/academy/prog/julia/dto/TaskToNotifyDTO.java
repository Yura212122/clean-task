package academy.prog.julia.dto;

import academy.prog.julia.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for representing a task to notify users about.
 *
 * 09/09/2024 - current DTO is not used.
 */
public class TaskToNotifyDTO {

    private String taskName;
    private String lessonName;
    private List<String> groupNames = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    /**
     * Default constructor for creating an empty TaskForNotifyDTO.
     */
    public TaskToNotifyDTO() {
    }

    /**
     * Constructs a TaskToNotifyDTO with the specified values.
     *
     * @param taskName the name of the task
     * @param lessonName the name of the lesson associated with the task
     * @param groupNames the list of group names related to the task
     * @param users the list of users to be notified about the task
     */
    public TaskToNotifyDTO(
            String taskName,
            String lessonName,
            List<String> groupNames,
            List<User> users
    ) {
        this.taskName = taskName;
        this.lessonName = lessonName;
        this.groupNames = groupNames;
        this.users = users;
    }

    /**
     * Gets the name of the task.
     *
     * @return the name of the task
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets the name of the task.
     *
     * @param taskName the new name to be set for the task
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets the name of the lesson associated with the task.
     *
     * @return the name of the lesson
     */
    public String getLessonName() {
        return lessonName;
    }

    /**
     * Sets the name of the lesson associated with the task.
     *
     * @param lessonName the new name to be set for the lesson
     */
    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    /**
     * Gets the list of group names related to the task.
     *
     * @return the list of group names
     */
    public List<String> getGroupNames() {
        return groupNames;
    }

    /**
     * Sets the list of group names related to the task.
     *
     * @param groupNames the new list of group names to be set
     */
    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    /**
     * Gets the list of users to be notified about the task.
     *
     * @return the list of users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users to be notified about the task.
     *
     * @param users the new list of users to be set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * Two TaskToNotifyDTO objects are considered equal if all their fields are equal.
     *
     * @param o the object to compare with
     * @return true if this object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskToNotifyDTO that = (TaskToNotifyDTO) o;

        return Objects.equals(taskName, that.taskName) &&
                Objects.equals(lessonName, that.lessonName) &&
                Objects.equals(groupNames, that.groupNames) &&
                Objects.equals(users, that.users)
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
                taskName,
                lessonName,
                groupNames,
                users
        );
    }
}
