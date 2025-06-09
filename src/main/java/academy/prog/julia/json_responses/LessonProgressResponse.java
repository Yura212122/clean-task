package academy.prog.julia.json_responses;

import academy.prog.julia.dto.LessonProgressDTO;

/**
 * Represents a response containing lesson progress information.
 */
public class LessonProgressResponse {

    private Long id;
    private String progress;
    private Integer percent;

    /**
     * Constructs a new {@link LessonProgressResponse} with the specified parameters.
     *
     * @param id the unique identifier for the lesson progress record
     * @param progress the progress description or status
     * @param percent the progress percentage
     */
    public LessonProgressResponse(
            Long id,
            String progress,
            Integer percent
    ) {
        this.id = id;
        this.progress = progress;
        this.percent = percent;
    }

    /**
     * Retrieves the unique identifier for the lesson progress record.
     *
     * @return the unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the lesson progress record.
     *
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the progress description or status.
     *
     * @return the progress description or status.
     */
    public String getProgress() {
        return progress;
    }

    /**
     * Sets the progress description or status.
     *
     * @param progress the progress description or status to set.
     */
    public void setProgress(String progress) {
        this.progress = progress;
    }

    /**
     * Retrieves the progress percentage.
     *
     * @return the progress percentage
     */
    public Integer getPercent() {
        return percent;
    }

    /**
     * Sets the progress percentage.
     *
     * @param percent the progress percentage to set.
     */
    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    /**
     * Converts a {@link LessonProgressDTO} to a {@link LessonProgressResponse}.
     *
     * @param lessonProgressDTO the DTO to convert
     * @return a {@link LessonProgressResponse} representing the DTO, or {@code null} if the DTO is {@code null}
     */
    public static LessonProgressResponse fromDTO(LessonProgressDTO lessonProgressDTO) {
        if (lessonProgressDTO == null) {
            return null;
        }

        return new LessonProgressResponse(
            lessonProgressDTO.getId(),
            lessonProgressDTO.getProgress(),
            lessonProgressDTO.getPercent()
        );
    }

}
