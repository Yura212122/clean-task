package academy.prog.julia.dto;

import java.util.List;
import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) for lesson information.
 *
 * This class represents a lesson with its associated tasks.
 * It is designed as an immutable object to ensure thread-safety and avoid accidental data modification.
 */
public class LessonsDTO {

    private final Long id;
    private final String name;
    private final List<TasksGetNameDTO> tasks;

    /**
     * Constructs a new immutable LessonsDTO.
     *
     * @param id    the unique identifier of the lesson (must not be null)
     * @param name  the name of the lesson (must not be null)
     * @param tasks the list of tasks associated with the lesson (must not be null, copied to ensure immutability)
     *
     * If tasks is null, it is handled gracefully by assigning null. Otherwise, a copy of the list is created
     * to ensure that the original list cannot be modified externally.
     *
     * Additionally, this method performs null checks to ensure that neither the ID nor the name are null
     * (if the constraints of your application require them to be non-null).
     *
     * @throws NullPointerException if any of the provided arguments are null (if required by your application constraints)
     */
    public LessonsDTO(
            Long id,
            String name,
            List<TasksGetNameDTO> tasks
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.tasks = tasks != null ? List.copyOf(tasks) : null;
    }

    /**
     * Gets the unique identifier of the lesson.
     *
     * @return the unique identifier of the lesson (can be null)
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the lesson.
     *
     * @return the name of the lesson (can be null)
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of tasks associated with the lesson.
     * Returns a copy of the list to maintain immutability.
     *
     * @return a copy of the list of tasks (can be null)
     */
    public List<TasksGetNameDTO> getTasks() {
        return tasks != null ? List.copyOf(tasks) : null;
    }

    /**
     * Determines if this object is equal to another object.
     * The comparison is based on the lesson's ID, name, and the list of tasks.
     *
     * To avoid potential issues with cyclic dependencies in the tasks list,
     * the comparison is performed by checking the content of the tasks list and ensuring
     * that no deep object comparison that could cause infinite recursion is performed.
     *
     * @param o the object to compare this instance against
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonsDTO that = (LessonsDTO) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                areTasksEqual(this.tasks, that.tasks)
        ;
    }

    /**
     * Compares two lists of tasks by their NAMEs.
     * This method avoids deep object comparison and reduces the risk of cyclic dependencies.
     *
     * By comparing the NAMEs of tasks instead of the full objects, we avoid potential issues
     * with cyclic relationships that could lead to infinite recursion.
     *
     * @param tasks1 the first list of tasks
     * @param tasks2 the second list of tasks
     * @return true if both lists contain the same tasks, false otherwise
     */
    private boolean areTasksEqual(
            List<TasksGetNameDTO> tasks1,
            List<TasksGetNameDTO> tasks2
    ) {
        if (tasks1 == tasks2) return true;
        if (tasks1 == null || tasks2 == null || tasks1.size() != tasks2.size()) return false;

        for (TasksGetNameDTO task1 : tasks1) {
            boolean found = false;
            for (TasksGetNameDTO task2 : tasks2) {
                if (Objects.equals(task1.getName(), task2.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return true;
    }

    /**
     * Computes the hash code for this object.
     * The hash code is computed based on the lesson's ID, name, and the IDs of the tasks.
     *
     * By using task IDs instead of full task objects, we avoid potential issues
     * with cyclic dependencies when computing the hash code.
     *
     * @return the hash code of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                extractTaskNames(tasks)
        );
    }

    /**
     * Extracts the IDs from the list of TasksGetNameDTO objects for comparison purposes.
     * This prevents the risk of cyclic dependencies and reduces the comparison to a simple ID-based check.
     *
     * @param tasks the list of TasksGetNameDTO objects
     * @return a list of task IDs
     */
    private List<String> extractTaskNames(List<TasksGetNameDTO> tasks) {
        return tasks.stream()
                .map(TasksGetNameDTO::getName)
                .collect(java.util.stream.Collectors.toList())
        ;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string containing the lesson's ID, name, and tasks
     */
    @Override
    public String toString() {
        return "LessonsDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tasks=" + tasks +
                '}'
        ;
    }

}
