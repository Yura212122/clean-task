package academy.prog.julia.dto;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.Task;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for representing task submission details.
 *
 * This class encapsulates the task ID, associated group names, and the task answers for submission purposes.
 */
public class TaskSubmissionDTO {

    private final Long taskId;
    private final Set<String> groupNames;
    private final Set<TaskAnswerDTO> taskAnswers;

    /**
     * Constructs a TaskSubmissionDTO with the specified details.
     *
     * @param taskId the ID of the task (must not be null)
     * @param groupNames the names of the groups associated with the task (must not be null)
     * @param taskAnswers the answers to the task (must not be null)
     * @throws NullPointerException if any of the required parameters are null
     */
    public TaskSubmissionDTO(
            Long taskId,
            Set<String> groupNames,
            Set<TaskAnswerDTO> taskAnswers
    ) {
        this.taskId = Objects.requireNonNull(taskId, "taskId cannot be null");
        this.groupNames = Objects.requireNonNull(groupNames, "groupNames cannot be null");
        this.taskAnswers = Objects.requireNonNull(taskAnswers, "answerUrl cannot be null");
    }

    /**
     * Returns the ID of the task.
     *
     * @return the ID of the task
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Returns the set of task answers.
     *
     * @return the set of TaskAnswerDTO instances
     */
    public Set<TaskAnswerDTO> getTaskAnswers() {
        return taskAnswers;
    }

    /**
     * Returns the set of group names associated with the task.
     *
     * @return the set of group names
     */
    public Set<String> getGroupNames() {
        return groupNames;
    }

    /**
     * Creates a TaskSubmissionDTO from a Task entity.
     *
     * This method converts a Task entity into a TaskSubmissionDTO, filtering out answers that are marked as passed
     * and extracting group names from the associated groups.
     *
     * @param task the Task entity to convert (must not be null)
     * @return a TaskSubmissionDTO representation of the Task entity
     * @throws NullPointerException if task is null
     */
    public static TaskSubmissionDTO fromTask(Task task) {
        if (task == null) {
            return null;
        }

        Set<TaskAnswerDTO> taskAnswerDTOs = task.getTaskAnswers().stream()
                .filter(answer -> !Boolean.TRUE.equals(answer.getIsPassed()))
                .map(TaskAnswerDTO::fromTaskAnswer)
                .collect(Collectors.toSet());

        Set<Group> groups = task.getLesson() != null ? task.getLesson().getGroups() : Collections.emptySet();
        Set<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toSet());

        return new TaskSubmissionDTO(
                task.getId(),
                groupNames,
                taskAnswerDTOs
        );
    }

    /**
     * Compares this TaskSubmissionDTO with another object for equality.
     *
     * This method considers two TaskSubmissionDTO instances equal if they have the same task ID, task answers, and group names.
     *
     * @param o the object to compare with
     * @return true if this TaskSubmissionDTO is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskSubmissionDTO that = (TaskSubmissionDTO) o;

        return Objects.equals(taskId, that.taskId) &&
                areTaskAnswerDTOEqual(taskAnswers, that.taskAnswers) &&
                Objects.equals(groupNames, that.groupNames);
    }

    /**
     * Returns a hash code value for this TaskSubmissionDTO.
     *
     * The hash code is computed based on the task ID, task answers, and group names.
     *
     * @return the hash code value for this TaskSubmissionDTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                taskId,
                extractTaskAnswerDTOIds(taskAnswers),
                groupNames
        );
    }

    /**
     * Checks if two sets of TaskAnswerDTO are equal.
     *
     * This method ensures that two sets of TaskAnswerDTO are equal by comparing their IDs.
     *
     * @param set1 the first set of TaskAnswerDTO
     * @param set2 the second set of TaskAnswerDTO
     * @return true if the sets are equal, false otherwise
     */
    private boolean areTaskAnswerDTOEqual(
            Set<TaskAnswerDTO> set1,
            Set<TaskAnswerDTO> set2
    ) {
        Set<Long> ids1 = set1.stream().map(TaskAnswerDTO::getAnswerId).collect(Collectors.toSet());
        Set<Long> ids2 = set2.stream().map(TaskAnswerDTO::getAnswerId).collect(Collectors.toSet());
        return ids1.equals(ids2);
    }

    /**
     * Extracts the IDs from a set of TaskAnswerDTO.
     *
     * This method is used to generate a consistent hash code by extracting IDs from TaskAnswerDTO instances.
     *
     * @param set the set of TaskAnswerDTO
     * @return a set of IDs extracted from TaskAnswerDTO instances
     */
    private Set<Long> extractTaskAnswerDTOIds(Set<TaskAnswerDTO> set) {
        return set
                .stream()
                .map(TaskAnswerDTO::getAnswerId).collect(Collectors.toSet())
        ;
    }
}
