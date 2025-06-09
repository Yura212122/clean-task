package academy.prog.julia.json_responses;

import academy.prog.julia.dto.LessonsDTO;
import academy.prog.julia.dto.TasksGetNameDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Immutable class.
 * <p>
 * Represents a response object for lessons that will be sent to the React frontend.
 */
public class LessonsResponse {

    private final Long id;
    private final String name;
    private final List<TasksGetNameDTO> tasks;

    /**
     * Constructs a new {@link LessonsResponse} with the specified parameters.
     *
     * @param id    the unique identifier for the lesson
     * @param name  the name of the lesson
     * @param tasks a list of tasks associated with the lesson
     */
    public LessonsResponse(
            Long id,
            String name,
            List<TasksGetNameDTO> tasks
    ) {
        this.id = id;
        this.name = name;
        this.tasks = tasks != null ? List.copyOf(tasks) : null;
    }

    /**
     * Retrieves the unique identifier for the lesson.
     *
     * @return the lesson ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Retrieves the name of the lesson.
     *
     * @return the lesson name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of tasks associated with the lesson.
     *
     * @return the list of {@link TasksGetNameDTO} representing the tasks, or {@code null} if no tasks exist
     */
    public List<TasksGetNameDTO> getTasks() {
        return tasks != null ? List.copyOf(tasks) : null;
    }

    /**
     * Converts a list of {@link LessonsDTO} into a list of {@link LessonsResponse} objects.
     *
     * @param lessonsDTOList the list of lessons in {@link LessonsDTO} format
     * @return a list of {@link LessonsResponse} objects to be sent to the frontend
     */
    public static List<LessonsResponse> fromDTO(List<LessonsDTO> lessonsDTOList) {
        return lessonsDTOList.stream()
                .map(lesson ->
                        new LessonsResponse(
                                lesson.getId(),
                                lesson.getName(),
                                lesson.getTasks() != null ?
                                        lesson.getTasks()
                                                .stream()
                                                .map(task -> new TasksGetNameDTO(task.getName()))
                                                .collect(Collectors.toList()) : null
                        )
                )
                .collect(Collectors.toList())
        ;
    }

}
