package academy.prog.julia.dto;

import java.util.Objects;

/**
 * Immutable Data Transfer Object (DTO) representing the progress of a lesson.
 *
 * This class encapsulates the progress information of a lesson, including its unique identifier,
 * a descriptive progress status, and the percentage of completion. It is designed to be immutable
 * to ensure thread safety and consistency.
 */
public class LessonProgressDTO {

    private final Long id;
    private final String progress;
    private final Integer percent;

    /**
     * Constructs a new immutable LessonProgressDTO.
     *
     * @param id       the unique identifier of the lesson (must not be null)
     * @param progress a descriptive status of the progress (e.g., "In Progress", "Completed") (must not be null)
     * @param percent  the percentage of completion of the lesson (e.g., 50 for 50%) (must be between 0 and 100, inclusive)
     *
     * @throws NullPointerException if any of the provided arguments are null
     * @throws IllegalArgumentException if percent is not between 0 and 100
     */
    public LessonProgressDTO(
            Long id,
            String progress,
            Integer percent
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.progress = Objects.requireNonNull(progress, "progress cannot be null");
        this.percent = Objects.requireNonNull(percent, "percent cannot be null");
    }

    /**
     * Returns the unique identifier of the lesson.
     *
     * @return the lesson's unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the descriptive status of the lesson's progress.
     *
     * @return a string representing the progress status
     */
    public String getProgress() {
        return progress;
    }

    /**
     * Returns the percentage of completion of the lesson.
     *
     * @return the percentage of completion, between 0 and 100, inclusive
     */
    public Integer getPercent() {
        return percent;
    }

    /**
     * Checks whether two LessonProgressDTO objects are equal based on their field values.
     *
     * This method compares the `id`, `progress`, and `percent` fields to determine if two instances
     * represent the same lesson progress data.
     *
     * @param o the object to compare with the current instance
     * @return true if both objects are of the same class and have matching field values, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonProgressDTO that = (LessonProgressDTO) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(progress, that.progress) &&
                Objects.equals(percent, that.percent)
        ;
    }

    /**
     * Generates a hash code for the LessonProgressDTO object based on its field values.
     *
     * This method computes the hash code using the `id`, `progress`, and `percent` fields.
     * The generated hash code is used in hash-based collections, such as `HashMap` and `HashSet`.
     *
     * @return an integer hash code representing the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                progress,
                percent
        );
    }

    /**
     * Returns a string representation of the LessonProgressDTO object.
     *
     * The string representation includes the ID, progress status, and completion percentage.
     *
     * @return a string representation of the LessonProgressDTO
     */
    @Override
    public String toString() {
        return "LessonProgressDTO{" +
                "id=" + id +
                ", progress='" + progress + '\'' +
                ", percent=" + percent +
                '}'
        ;
    }

}
