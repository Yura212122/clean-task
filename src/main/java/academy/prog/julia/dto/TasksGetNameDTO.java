package academy.prog.julia.dto;

import academy.prog.julia.model.Task;

import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) for retrieving the name of a task.
 *
 * This class represents a lightweight, immutable DTO that encapsulates the name of a task.
 * It is used for transferring task name information between different layers of the application.
 */
public class TasksGetNameDTO {

    private final String name;

    /**
     * Constructs a TasksGetNameDTO with the specified task name.
     *
     * @param name the name of the task (must not be null)
     * @throws NullPointerException if name is null
     */
    public TasksGetNameDTO(String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
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
     * Converts a Task entity to a TasksGetNameDTO.
     *
     * This method creates a new instance of TasksGetNameDTO using the name of the provided Task entity.
     *
     * @param task the Task entity to convert (must not be null)
     * @return a TasksGetNameDTO representation of the Task entity
     * @throws NullPointerException if task is null
     */
    public static TasksGetNameDTO fromTask(Task task) {
        Objects.requireNonNull(task, "task cannot be null");
        return new TasksGetNameDTO(task.getName());
    }

    /**
     * Compares this TasksGetNameDTO to another object.
     *
     * This method compares two TasksGetNameDTO objects for equality by checking if their task names are equal.
     *
     * @param o the object to compare to
     * @return true if the objects are of the same class and have the same task name, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksGetNameDTO that = (TasksGetNameDTO) o;

        return Objects.equals(name, that.name);
    }

    /**
     * Generates a hash code for this TasksGetNameDTO.
     *
     * The hash code is based on the task's name, ensuring that two equal objects (via equals) will return
     * the same hash code.
     *
     * @return the hash code for this TasksGetNameDTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
