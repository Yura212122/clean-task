package academy.prog.julia.dto;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.Test;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Immutable Data Transfer Object (DTO) for representing test submission details.
 *
 * This class encapsulates the test ID, group names, and test answers to be submitted.
 */
public class TestSubmissionDTO {

    private final Long testId;
    private final Set<String> groupNames;
    private final Set<TestAnswerDTO> testAnswers;

    /**
     * Constructs a TestSubmissionDTO with the specified parameters.
     *
     * @param testId the test ID (must not be null)
     * @param groupNames the names of groups associated with the test (must not be null)
     * @param testAnswers the answers to the test (must not be null)
     * @throws NullPointerException if any of the parameters are null
     */
    public TestSubmissionDTO(
            Long testId,
            Set<String> groupNames,
            Set<TestAnswerDTO> testAnswers
    ) {
        this.testId = Objects.requireNonNull(testId, "testId cannot be null");
        this.groupNames = Objects.requireNonNull(groupNames, "groupNames cannot be null");
        this.testAnswers = Objects.requireNonNull(testAnswers, "testAnswers cannot be null");
    }

    /**
     * Returns the test ID.
     *
     * @return the test ID
     */
    public Long getTestId() {
        return testId;
    }

    /**
     * Returns the set of group names.
     *
     * @return the set of group names
     */
    public Set<String> getGroupNames() {
        return groupNames;
    }

    /**
     * Returns the set of test answers.
     *
     * @return the set of test answers as TestAnswerDTOs
     */
    public Set<TestAnswerDTO> getTestAnswers() {
        return testAnswers;
    }

    /**
     * Converts a Test object to a TestSubmissionDTO.
     *
     * This method transforms a Test object into a TestSubmissionDTO, filtering out answers
     * that have already been passed, and retrieves the names of groups associated with the test.
     *
     * @param test the Test object to convert
     * @return a TestSubmissionDTO instance
     */
    public static TestSubmissionDTO fromTest(Test test) {
        if (test == null) {
            return null;
        }

        Set<TestAnswerDTO> testAnswerDTOs = test.getTestAnswers().stream()
                .filter(answer -> !Boolean.TRUE.equals(answer.getIsPassed()))
                .map(TestAnswerDTO::fromTestAnswer)
                .collect(Collectors.toSet());

        Set<Group> groups = test.getLesson() != null ? test.getLesson().getGroups() : Collections.emptySet();
        Set<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toSet());

        return new TestSubmissionDTO(
                test.getId(),
                groupNames,
                testAnswerDTOs
        );
    }

    /**
     * Checks if this TestSubmissionDTO is equal to another object.
     *
     * Two TestSubmissionDTOs are considered equal if their testId, groupNames, and testAnswers are identical.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestSubmissionDTO that = (TestSubmissionDTO) o;

        return Objects.equals(testId, that.testId) &&
                Objects.equals(groupNames, that.groupNames) &&
                areTestAnswerDTOsEqual(testAnswers, that.testAnswers)
        ;
    }

    /**
     * Returns the hash code for this TestSubmissionDTO.
     *
     * The hash code is computed based on testId, groupNames, and testAnswers.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                testId,
                groupNames,
                extractTestAnswerDTOIds(testAnswers)
        );
    }

    /**
     * Checks if two sets of TestAnswerDTOs are equal by comparing their IDs.
     *
     * This ensures that cyclic references are avoided during comparison.
     *
     * @param set1 the first set of TestAnswerDTOs
     * @param set2 the second set of TestAnswerDTOs
     * @return true if the sets are equal, otherwise false
     */
    private boolean areTestAnswerDTOsEqual(
            Set<TestAnswerDTO> set1,
            Set<TestAnswerDTO> set2
    ) {
        Set<Long> ids1 = set1.stream().map(TestAnswerDTO::getAnswerId).collect(Collectors.toSet());
        Set<Long> ids2 = set2.stream().map(TestAnswerDTO::getAnswerId).collect(Collectors.toSet());

        return ids1.equals(ids2);
    }

    /**
     * Extracts the IDs from a set of TestAnswerDTOs.
     *
     * This method is used to ensure consistent hash code computation.
     *
     * @param set the set of TestAnswerDTOs
     * @return the set of TestAnswerDTO IDs
     */
    private Set<Long> extractTestAnswerDTOIds(Set<TestAnswerDTO> set) {
        return set
                .stream()
                .map(TestAnswerDTO::getAnswerId).collect(Collectors.toSet())
        ;
    }

}
