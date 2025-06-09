package academy.prog.julia.services;

import academy.prog.julia.dto.TestAnswerDTO;
import academy.prog.julia.dto.TestSubmissionDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.Test;
import academy.prog.julia.model.TestAnswer;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.TestAnswerRepository;
import academy.prog.julia.repos.TestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling business logic related to Test entities.
 */
@Service
public class TestService {

    private final TestAnswerRepository testAnswerRepository;
    private final TestRepository testRepository;

    /**
     * Constructor for TestService. Uses constructor-based dependency injection
     * to initialize repositories needed for handling tests and answers.
     *
     * @param testAnswerRepository the repository for managing test answer data
     * @param testRepository the repository for managing test data
     */
    public TestService(
            TestAnswerRepository testAnswerRepository,
            TestRepository testRepository
    ) {
        this.testAnswerRepository = testAnswerRepository;
        this.testRepository = testRepository;

    }

    /**
     * Retrieves test submissions by a specific teacher's ID, paginated.
     * This method allows filtering submissions based on the teacher's ID.
     *
     * @param page the current page number
     * @param size the size of each page
     * @param teacherId the ID of the teacher to filter submissions by
     * @return a Page object containing a list of TestSubmissionDTOs
     */
    @Transactional(readOnly = true)
    public Page<TestSubmissionDTO> getAllTestSubmissionsByTeacherId(
            int page,
            int size,
            Long teacherId
    ) {
        Page<Test> testPage = testRepository.findTestByTeacherId(teacherId, PageRequest.of(page, size));

        List<TestSubmissionDTO> testSubmissionDTOList = testPage.getContent().stream()
                .map(test -> {
                    Set<TestAnswerDTO> studentAnswers = test.getTestAnswers().stream()
                            .map(this::mapToTestAnswerDTO)
                            .collect(Collectors.toSet())
                    ;

                    Set<Group> groups = test.getLesson() != null ? test.getLesson().getGroups() : Collections.emptySet();
                    Set<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toSet());

                    return new TestSubmissionDTO(test.getId(), groupNames, studentAnswers);
                })
                .collect(Collectors.toCollection(ArrayList::new))
        ;

        return new PageImpl<>(testSubmissionDTOList, PageRequest.of(page, size), testPage.getTotalElements());
    }

    /**
     * Maps a TestAnswer entity to a TestAnswerDTO. This is a utility function used to
     * convert entities into DTOs.
     *
     * @param testAnswer the TestAnswer entity to map
     * @return a TestAnswerDTO containing the relevant fields of TestAnswer
     */
    private TestAnswerDTO mapToTestAnswerDTO(TestAnswer testAnswer) {
        if (testAnswer == null || testAnswer.getUser() == null) {
            return null;
        }

        UserFromAnswerTaskDTO userFromAnswerTaskDTO = UserFromAnswerTaskDTO.fromUser(testAnswer.getUser());

        return new TestAnswerDTO(
                testAnswer.getId(),
                testAnswer.getCourse(),
                testAnswer.getAttempt(),
                testAnswer.getIsPassed(),
                testAnswer.getTotalScore(),
                testAnswer.getSubmittedDate(),
                userFromAnswerTaskDTO
        );
    }

    /**
     * Retrieves all tests associated with a specific user, filtered by deadline and mandatory flag.
     * Used primarily for user notifications.
     *
     * @param userId the ID of the user
     * @param deadLine the deadline date to filter tests
     * @param mandatory a flag indicating whether the test is mandatory
     * @return a list of Test entities that match the criteria
     */
    @Transactional(readOnly = true)
    public List<Test> findAllTestsByUserIdWithDeadLineAndMandatory(
            Long userId,
            LocalDate deadLine,
            boolean mandatory
    ) {
        return testRepository.findAllTestsByUserIdWithDeadLineAndMandatory(userId, deadLine, mandatory);
    }

    /**
     * Filters a list of tests for a user, only returning those that the user hasn't passed or hasn't taken.
     * Used for user notifications about pending or failed tests.
     *
     * @param user the user to filter tests for
     * @param tests a list of tests to filter
     * @return a set of Test entities filtered for pending or failed tests
     */
    @Transactional(readOnly = true)
    public Set<Test> filterTestsForNotifyToEndDeadLineDate(
            User user,
            List<Test> tests
    ) {
        Set<Test> filteredTasks = new HashSet<>();

        for (Test test : tests) {
            if (testAnswerRepository.findByTestIdAndUserId(test.getId(), user.getId()) == null) {
                filteredTasks.add(test);
            } else if (!testAnswerRepository.findByTestIdAndUserId(test.getId(), user.getId()).getIsPassed()) {
                filteredTasks.add(test);
            }
        }

        return filteredTasks;
    }

    /**
     * Checks if a user has taken a specific test and whether they passed or not.
     * Used for checking the user's history with a test.
     *
     * @param user the user to check
     * @param test the test to check against
     * @return true if the test was taken and not passed, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean checkToTestHasBeenTakenBefore(
            User user,
            Test test
    ) {
        if (testAnswerRepository.findByTestIdAndUserId(test.getId(), user.getId()) == null) {
            return false;
        } else if (!testAnswerRepository.findByTestIdAndUserId(test.getId(), user.getId()).getIsPassed()) {
            return true;
        }

        return false;
    }

    /**
     * Sorts a list of tests for a user, first returning untouched tests (those not taken),
     * followed by failed tests (those taken but not passed).
     *
     * @param user the user to sort tests for
     * @param tests a set of tests to sort
     * @return a list of sorted tests, first untouched, then failed
     */
    @Transactional(readOnly = true)
    public List<Test> sortingTestsFirstUntouchedThenFailed(
            User user,
            Set<Test> tests
    ) {
        List<Test> sortedTests = new ArrayList<>();
        boolean tempoLever = false;
        boolean tempoLeverToAddTakenTest = false;

        for (int i = 0; i < 2; i++) {
            // intermediate zero to separate sorted parts and turn lever
            if (i == 1) {
                sortedTests.add(null);
                tempoLever = true;
            }

            for (Test test : tests) {
               if (checkToTestHasBeenTakenBefore(user,test) == tempoLever) {
                   sortedTests.add(test);
                   tempoLeverToAddTakenTest = true;
               }
            }
        }

        if (!tempoLeverToAddTakenTest) {
            sortedTests.remove(sortedTests.size() - 1);
        }

        return sortedTests;
    }



                                // CURRENTLY NOT IN USE

    /**
     * Retrieves all test submissions, paginated, with details like student answers and group names.
     * This method is marked as deprecated, and its use is discouraged.
     *
     * @param page the current page number
     * @param size the size of each page
     * @return a Page object containing a list of TestSubmissionDTOs
     */
    @Transactional(readOnly = true)
    public Page<TestSubmissionDTO> getAllTestSubmissions(
            int page,
            int size
    ) {
        Page<Test> testPage = testRepository.findAll(PageRequest.of(page, size));

        List<TestSubmissionDTO> testSubmissionDTOList = testPage.getContent().stream()
                .map(test -> {
                    Set<TestAnswerDTO> studentAnswers = test.getTestAnswers().stream()
                            .map(this::mapToTestAnswerDTO)
                            .collect(Collectors.toSet())
                    ;

                    Set<Group> groups = test.getLesson() != null ? test.getLesson().getGroups() : Collections.emptySet();
                    Set<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toSet());

                    return new TestSubmissionDTO(test.getId(), groupNames, studentAnswers);
                })
                .collect(Collectors.toCollection(ArrayList::new))
        ;

        return new PageImpl<>(testSubmissionDTOList, PageRequest.of(page, size), testPage.getTotalElements());
    }

    /**
     * Saves a Test entity to the repository. Used to persist or update test data.
     *
     * @param test the Test entity to save
     */
    @Transactional
    public void saveTest(Test test) {
        testRepository.save(test);
    }

}
