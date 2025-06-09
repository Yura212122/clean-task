package academy.prog.julia.json_responses;

import academy.prog.julia.dto.TaskProgressDTO;

/**
 * Class representing the progress of a task.
 *
 * This class is mutable, allowing modifications to its fields.
 */
public class TaskProgressResponse {

    private Long id;
    private Integer progress;

    /**
     * Constructor to initialize the TaskProgressResponse object.
     *
     * @param id the ID of the task
     * @param progress the progress percentage of the task
     */
    public TaskProgressResponse(
            Long id,
            Integer progress
    ) {
        this.id = id;
        this.progress = progress;
    }

    /**
     * Gets the ID of the task.
     *
     * @return the task ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the task.
     *
     * @param id the new task ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the progress percentage of the task.
     *
     * @return the task progress as an Integer
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * Sets the progress percentage of the task.
     *
     * @param progress the new progress value
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * Converts a TaskProgressDTO object into a TaskProgressResponse object.
     * Useful for transforming DTOs from the service layer into response objects for the client.
     *
     * @param taskProgressDTO the DTO object containing task progress information
     * @return a new TaskProgressResponse object created from the provided DTO, or null if the DTO is null
     */
    public static TaskProgressResponse fromDTO(TaskProgressDTO taskProgressDTO) {
        if (taskProgressDTO == null) {
            return null;
        }

        return new TaskProgressResponse(
                taskProgressDTO.getId(),
                taskProgressDTO.getProgress()
        );
    }

}
