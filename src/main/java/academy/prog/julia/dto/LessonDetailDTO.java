package academy.prog.julia.dto;

import academy.prog.julia.model.Task;
import academy.prog.julia.model.Test;

import java.util.Objects;
import java.util.Set;

/**
 * Immutable Data Transfer Object (DTO) for lesson's details.
 *
 * This class represents detailed information about a lesson.
 * It is designed as an immutable object to ensure thread-safety and avoid accidental data modification.
 */
public class LessonDetailDTO {

    private final String name;
    private final String descriptionUrl;
    private final String videoUrl;
    private final Set<Task> tasks;
    private final Set<Test> tests;

    /**
     * Constructs a new immutable LessonDetailDTO.
     *
     * @param name          the name of the lesson (must not be null)
     * @param descriptionUrl the URL of the lesson description (must not be null)
     * @param videoUrl       the URL of the lesson video (must not be null)
     * @param tasks          the set of tasks associated with the lesson (must not be null, copied to ensure immutability)
     * @param tests          the set of tests associated with the lesson (must not be null, copied to ensure immutability)
     *
     * @throws NullPointerException if any of the provided arguments are null
     */
    public LessonDetailDTO(
            String name,
            String descriptionUrl,
            String videoUrl,
            Set<Task> tasks,
            Set<Test> tests
    ) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.descriptionUrl = Objects.requireNonNull(descriptionUrl, "descriptionUrl cannot be null");
        this.videoUrl = Objects.requireNonNull(videoUrl, "videoUrl cannot be null");
        this.tasks = Set.copyOf(Objects.requireNonNull(tasks, "tasks cannot be null"));
        this.tests = Set.copyOf(Objects.requireNonNull(tests, "tests cannot be null"));
    }

    /**
     * Retrieves the name of the lesson.
     *
     * @return the name of the lesson
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the URL of the lesson's description.
     *
     * @return the description URL
     */
    public String getDescriptionUrl() {
        return descriptionUrl;
    }

    /**
     * Retrieves the URL of the lesson's video.
     *
     * @return the video URL
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * Retrieves the set of tasks associated with this lesson.
     *
     * @return a set of tasks
     */
    public Set<Task> getTasks() {
        return tasks;
    }

    /**
     * Retrieves the set of tests associated with this lesson.
     *
     * @return a set of tests
     */
    public Set<Test> getTests() {
        return tests;
    }

    /**
     * Determines if this object is equal to another object.
     * The comparison is based on the lesson's name, descriptionUrl, videoUrl, and the sets of tasks and tests.
     *
     * To avoid potential issues with cyclic dependencies between Task and Test objects,
     * this method uses only the identifiers (e.g., taskId, testId) of the tasks and tests rather than the full objects.
     *
     * @param o the object to compare this instance against
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonDetailDTO that = (LessonDetailDTO) o;

        return Objects.equals(name, that.name) &&
                Objects.equals(descriptionUrl, that.descriptionUrl) &&
                Objects.equals(videoUrl, that.videoUrl) &&
                areTaskIdsEqual(this.tasks, that.tasks) &&
                areTestIdsEqual(this.tests, that.tests)
        ;
    }

    /**
     * Helper method to compare two sets of entities by their IDs.
     * This avoids deep object comparison and reduces the risk of cyclic dependencies.
     *
     * Instead of comparing full Task objects (which may have their own cyclic relationships),
     * we compare only the IDs of the tasks.
     *
     * @param tasks1 the first set
     * @param tasks2 the second set
     * @return true if both sets contain the same IDs, false otherwise
     */
    private boolean areTaskIdsEqual(
            Set<Task> tasks1,
            Set<Task> tasks2
    ) {
        if (tasks1 == tasks2) return true;
        if (tasks1 == null || tasks2 == null || tasks1.size() != tasks2.size()) return false;

        for (Task task1 : tasks1) {
            boolean found = false;
            for (Task task2 : tasks2) {
                if (Objects.equals(task1.getId(), task2.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return true;
    }

    /**
     * Helper method to compare two sets of entities by their IDs.
     * This avoids deep object comparison and reduces the risk of cyclic dependencies.
     *
     * Similar to the task comparison, we compare the IDs of the Test objects to avoid
     * potential issues with cyclic relationships between entities.
     *
     * @param tests1 the first set
     * @param tests2 the second set
     * @return true if both sets contain the same IDs, false otherwise
     */
    private boolean areTestIdsEqual(
            Set<Test> tests1,
            Set<Test> tests2
    ) {
        if (tests1 == tests2) return true;
        if (tests1 == null || tests2 == null || tests1.size() != tests2.size()) return false;

        for (Test test1 : tests1) {
            boolean found = false;
            for (Test test2 : tests2) {
                if (Objects.equals(test1.getId(), test2.getId())) {
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
     * The hash code is computed based on the lesson's name, descriptionUrl, videoUrl, and the IDs of the tasks and tests.
     *
     * By using the IDs of the tasks and tests instead of the full objects, we avoid potential issues
     * with cyclic relationships when computing the hash code.
     *
     * @return the hash code of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                descriptionUrl,
                videoUrl,
                extractTaskIds(tasks),
                extractTestIds(tests)
        );
    }

    /**
     * Extracts the IDs from the set of Task objects for comparison purposes.
     * This prevents the risk of cyclic dependencies and reduces the comparison to a simple ID-based check.
     *
     * @param tasks the set of Task objects
     * @return a set of Task IDs
     */
    private Set<Long> extractTaskIds(Set<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .collect(java.util.stream.Collectors.toSet())
        ;
    }

    /**
     * Extracts the IDs from the set of Test objects for comparison purposes.
     * This prevents the risk of cyclic dependencies and reduces the comparison to a simple ID-based check.
     *
     * @param tests the set of Test objects
     * @return a set of Test IDs
     */
    private Set<Long> extractTestIds(Set<Test> tests) {
        return tests.stream()
                .map(Test::getId)
                .collect(java.util.stream.Collectors.toSet())
        ;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string containing the lesson's name, descriptionUrl, videoUrl, tasks, and tests
     */
    @Override
    public String toString() {
        return "LessonDetailDTO{" +
                "name='" + name + '\'' +
                ", descriptionUrl='" + descriptionUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", tasks=" + tasks +
                ", tests=" + tests +
                '}'
        ;
    }

}
