package academy.prog.julia.dto;

import academy.prog.julia.model.Task;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Immutable Data Transfer Object (DTO) for representing detailed information about a task.
 *
 * This class provides a detailed view of a task, including its name, description URL, associated answers,
 * deadline, and active status. It is used for transferring task-related data between different layers of an application.
 */
public class TasksDetailedDTO {

    private final String name;
    private final String descriptionUrl;
    private final Set<TaskAnswerDTO> answerUrls;
    private final LocalDate deadline;
    private final Boolean isActive;

    /**
     * Constructs a TasksDetailedDTO with the specified values.
     *
     * @param name the name of the task (must not be null)
     * @param descriptionUrl the URL link to the task description (must not be null)
     * @param answerUrls the set of TaskAnswerDTO associated with the task (must not be null)
     * @param deadline the deadline for the task
     * @param isActive the status indicating if the task is active
     *
     * @throws NullPointerException if any required fields are null
     */
    public TasksDetailedDTO(
            String name,
            String descriptionUrl,
            Set<TaskAnswerDTO> answerUrls,
            LocalDate deadline,
            Boolean isActive
    ) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.descriptionUrl = Objects.requireNonNull(descriptionUrl, "descriptionUrl cannot be null");
        this.answerUrls = Objects.requireNonNull(answerUrls, "answerUrls cannot be null");
        this.deadline = deadline;
        this.isActive = isActive;
    }

    /**
     * Returns the name of the task.
     *
     * @return the name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the URL link to the task description.
     *
     * @return the URL link to the task description.
     */
    public String getDescriptionUrl() {
        return descriptionUrl;
    }

    /**
     * Returns the set of TaskAnswerDTO associated with the task.
     *
     * @return the set of TaskAnswerDTO.
     */
    public Set<TaskAnswerDTO> getAnswerUrls() {
        return answerUrls;
    }

    /**
     * Returns the deadline for the task.
     *
     * @return the deadline of the task.
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the status indicating if the task is active.
     *
     * @return true if the task is active, false otherwise.
     */
    public Boolean isActive() {
        return isActive;
    }

    /**
     * Converts a Task entity to a TasksDetailedDTO.
     *
     * @param task the Task entity to convert (may be null)
     * @return a TasksDetailedDTO representation of the entity, or null if the entity is null
     */
    public static TasksDetailedDTO fromTask(Task task) {
        if (task == null) {
            return null;
        }

        Set<TaskAnswerDTO> answerDTOs = task.getTaskAnswers().stream()
                .map(TaskAnswerDTO::fromTaskAnswer)
                .collect(Collectors.toSet());

        return new TasksDetailedDTO(
                task.getName(),
                task.getDescriptionUrl(),
                answerDTOs,
                task.getDeadline(),
                task.getActive()
        );
    }

    /**
     * Compares this TasksDetailedDTO to another object for equality.
     *
     * Two TasksDetailedDTO instances are considered equal if all their fields are equal. The comparison
     * of TaskAnswerDTO sets is performed by comparing their IDs.
     *
     * @param o the object to compare this TasksDetailedDTO against
     * @return true if the given object is equal to this TasksDetailedDTO, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksDetailedDTO that = (TasksDetailedDTO) o;

        return Objects.equals(name, that.name) &&
                Objects.equals(descriptionUrl, that.descriptionUrl) &&
                areTaskAnswerDTOEqual(answerUrls, that.answerUrls) &&
                Objects.equals(deadline, that.deadline) &&
                Objects.equals(isActive, that.isActive)
        ;
    }

    /**
     * Returns a hash code value for this TasksDetailedDTO.
     *
     * The hash code is computed based on the values of all fields, including a set of IDs extracted from
     * TaskAnswerDTO instances. This ensures that two equal objects have the same hash code.
     *
     * @return a hash code value for this TasksDetailedDTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                descriptionUrl,
                extractAnswerDTOIds(answerUrls),
                deadline,
                isActive
        );
    }

    /**
     * Compares two sets of TaskAnswerDTO based on their IDs.
     * Handles null values gracefully by considering null sets unequal to any non-null set.
     *
     * @param answerDTOs1 the first set of TaskAnswerDTO to compare
     * @param answerDTOs2 the second set of TaskAnswerDTO to compare
     * @return true if both sets are equal based on their IDs, false otherwise
     */
    private boolean areTaskAnswerDTOEqual(
            Set<TaskAnswerDTO> answerDTOs1,
            Set<TaskAnswerDTO> answerDTOs2
    ) {
        if (answerDTOs1 == null && answerDTOs2 == null) {
            return true;
        }

        if (answerDTOs1 == null || answerDTOs2 == null) {
            return false;
        }

        return extractAnswerDTOIds(answerDTOs1).equals(extractAnswerDTOIds(answerDTOs2));
    }

    /**
     * Extracts the IDs from a set of TaskAnswerDTO.
     * Handles null values gracefully by returning an empty set if the input is null.
     *
     * @param answerDTOs the set of TaskAnswerDTO from which to extract IDs
     * @return a set of IDs extracted from the TaskAnswerDTO, or an empty set if the input is null
     */
    private Set<Long> extractAnswerDTOIds(Set<TaskAnswerDTO> answerDTOs) {
        if (answerDTOs == null) {
            return Set.of();
        }

        return answerDTOs.stream()
                .map(TaskAnswerDTO::getAnswerId)
                .collect(Collectors.toSet())
        ;
    }

}
