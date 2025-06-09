package academy.prog.julia.dto;

import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) for representing the progress of a task.
 *
 * This class is used to transfer information about the progress of a specific task,
 * including its unique identifier and the current progress status.
 */
public class TaskProgressDTO {

    private final Long id;
    private final Integer progress;

    /**
     * Constructs a TaskProgressDTO with the specified values.
     *
     * @param id the unique identifier for the task (must not be null)
     * @param progress the current progress of the task
     */
    public TaskProgressDTO(
            Long id,
            Integer progress
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.progress = progress;
    }

    /**
     * Returns the unique identifier of the task.
     *
     * @return the unique identifier of the task.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the current progress of the task.
     *
     * @return the current progress value of the task.
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * Compares this TaskProgressDTO to the specified object.
     *
     * The comparison is based on the task's unique identifier and progress value.
     *
     * @param o the object to compare with
     * @return true if both objects are of the same class and have the same id and progress, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskProgressDTO that = (TaskProgressDTO) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(progress, that.progress)
        ;
    }

    /**
     * Generates a hash code for this TaskProgressDTO based on its id and progress.
     *
     * This method ensures that two TaskProgressDTO objects that are considered equal
     * (via the equals method) will have the same hash code, making this class suitable
     * for use in hash-based collections such as HashMap or HashSet.
     *
     * @return the hash code for this TaskProgressDTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                progress
        );
    }

}
