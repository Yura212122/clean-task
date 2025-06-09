package academy.prog.julia.json_responses;

import academy.prog.julia.dto.TasksDetailedDTO;
import academy.prog.julia.dto.TaskAnswerDTO;

import java.time.LocalDate;
import java.util.Set;

/**
 * Immutable class representing the response details of a task.
 *
 * Once an instance is created, its fields cannot be modified.
 */
public class TaskDetailsResponse {

    private final String name;
    private final String descriptionUrl;
    private final Set<TaskAnswerDTO> answers;
    private final LocalDate deadline;
    private final Boolean isActive;

    /**
     * Constructor to initialize all fields of the immutable TaskDetailsResponse class.
     *
     * @param name the name of the task
     * @param descriptionUrl the URL of the task description
     * @param answers the set of answers for the task
     * @param deadline the deadline for the task
     * @param isActive the active status of the task
     */
    public TaskDetailsResponse(
            String name,
            String descriptionUrl,
            Set<TaskAnswerDTO> answers,
            LocalDate deadline,
            Boolean isActive
    ) {
        this.name = name;
        this.descriptionUrl = descriptionUrl;
        this.answers = answers;
        this.deadline = deadline;
        this.isActive = isActive;
    }

    /**
     * Gets the name of the task.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the URL of the task description.
     *
     * @return the URL of the task description
     */
    public String getDescriptionUrl() {
        return descriptionUrl;
    }

    /**
     * Gets the set of answers related to the task.
     *
     * @return a set of TaskAnswerDTO representing the answers
     */
    public Set<TaskAnswerDTO> getAnswers() {
        return answers;
    }

    /**
     * Gets the deadline for the task.
     *
     * @return the deadline as a LocalDate
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Checks if the task is active.
     *
     * @return true if the task is active, false otherwise
     */
    public Boolean isActive() {
        return isActive;
    }

    /**
     * Converts a TasksDetailedDTO object into a TaskDetailsResponse.
     * This method is useful for transforming DTOs from the service layer into immutable response objects.
     *
     * @param tasksDetailedDTO the DTO object containing detailed task information
     * @return a new TaskDetailsResponse object created from the provided DTO, or null if the DTO is null
     */
    public static TaskDetailsResponse fromDTO(TasksDetailedDTO tasksDetailedDTO) {
        if (tasksDetailedDTO == null) {
            return null;
        }

        return new TaskDetailsResponse(
                tasksDetailedDTO.getName(),
                tasksDetailedDTO.getDescriptionUrl(),
                tasksDetailedDTO.getAnswerUrls(),
                tasksDetailedDTO.getDeadline(),
                tasksDetailedDTO.isActive()
        );
    }

}
