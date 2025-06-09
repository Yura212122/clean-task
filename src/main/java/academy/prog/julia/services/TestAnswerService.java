package academy.prog.julia.services;

import academy.prog.julia.dto.TestAnswerDTO;
import academy.prog.julia.json_responses.TestAnswerResponse;
import academy.prog.julia.model.Test;
import academy.prog.julia.model.TestAnswer;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.TestAnswerRepository;
import academy.prog.julia.repos.TestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for handling business logic related to TestAnswer entities.
 */
@Service
public class TestAnswerService {


    private final TestAnswerRepository testAnswerRepository;
    private final UserService userService;
    private final TestRepository testRepository;
    private final GroupService groupService;
    private  final  MailSenderService mailSenderService;


    /**
     * Constructor for TestAnswerService. Uses constructor-based dependency injection
     * for repositories and services required for handling test answers.
     *
     * @param testAnswerRepository the repository for managing test answer data
     * @param userService the service for managing user-related operations
     * @param testRepository the repository for retrieving test data
     * @param groupService the service for managing group-related operations
     */
    public TestAnswerService(
            TestAnswerRepository testAnswerRepository,
            UserService userService,
            TestRepository testRepository,
            GroupService groupService,
            MailSenderService mailSenderService
    ) {
        this.testAnswerRepository = testAnswerRepository;
        this.userService = userService;
        this.testRepository = testRepository;
        this.groupService = groupService;
        this.mailSenderService = mailSenderService;
    }

    /**
     * Retrieves a test answer based on the provided test ID and user ID.
     * The method is marked as read-only to ensure the transaction is not modifiable.
     *
     * @param testId the ID of the test
     * @param userId the ID of the user
     * @return a DTO representation of the test answer
     */
    @Transactional(readOnly = true)
    public TestAnswerDTO getTestAnswer(
            Long testId,
            Long userId
    ) {
        TestAnswer testAnswer = testAnswerRepository.findByTestIdAndUserId(testId, userId);

        return TestAnswerDTO.fromTestAnswer(testAnswer);
    }

    /**
     * Retrieves a test answer wrapped in a ResponseEntity object, useful for sending HTTP responses.
     * Uses read-only transactions as it's a retrieval operation.
     *
     * @param userId the ID of the user
     * @param testId the ID of the test
     * @return a ResponseEntity containing the test answer response
     */
    @Transactional(readOnly = true)
    public ResponseEntity<TestAnswerResponse> getTestAnswerResponse(
            Long userId,
            Long testId
    ) {
        TestAnswerDTO testAnswerDTO = getTestAnswer(testId, userId);

        TestAnswerResponse testAnswerResponse = TestAnswerResponse.fromDTO(testAnswerDTO);

        return ResponseEntity.ok(testAnswerResponse);
    }

    /**
     * Handles test submission by validating user and group membership, and then saving the test answer.
     *
     * @param userId the ID of the user submitting the test
     * @param testId the ID of the test being submitted
     * @param passed boolean indicating whether the user passed the test
     * @param totalScore the total score for the test
     * @param course the name of the course
     * @param session the HTTP session to store relevant data like testId
     * @return a ResponseEntity indicating success or failure of the operation
     */
    @Transactional
    public ResponseEntity<Object> handleTestSubmission(
            Long userId,
            Long testId,
            Boolean passed,
            String totalScore,
            String course,
            HttpSession session
    ) {
        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated", testId))
            ;
        }

        User currentUser = userService.findById(userId).orElseThrow();

        if (!isStudentInTestGroup(userId, testId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(
                            "You do not have permission to submit test answer for this group",
                            testId)
                    )
            ;
        }

        submitTestAnswer(testId, course, passed, totalScore, currentUser);
        session.setAttribute("testId", testId);
        // If successful submission
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Test answer submitted successfully",
                "taskId", testId,
                "course", course
        ));
    }

    /**
     * Creates a response map for error handling in the test submission process.
     *
     * @param message the error message to return
     * @param testId the ID of the test
     * @return a map containing the error details
     */
    private Object createErrorResponse(
            String message,
            Long testId
    ) {
        return Map.of(
                "status", "failed",
                "message", message,
                "testId", testId,
                "answerUrl", "",
                "course", ""
        );
    }

    /**
     * Checks whether a user belongs to the group that is allowed to submit a test answer.
     * Uses a read-only transaction for better performance.
     *
     * @param userId the ID of the user
     * @param testId the ID of the test
     * @return true if the user belongs to the test's group, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isStudentInTestGroup(
            Long userId,
            Long testId
    ) {
        Optional<Test> testOptional = testRepository.findById(testId);

        if (testOptional.isPresent()) {
            Test test = testOptional.get();

            if (!test.getLesson().getGroups().isEmpty()) {

                return test.getLesson().getGroups().stream()
                        .anyMatch(group -> groupService.isStudentInGroup(userId, group.getId()))
                ;
            }
        }

        return false;
    }

    /**
     * Saves or updates the test answer for a user. If the user has already submitted a test answer,
     * it will update the existing entry; otherwise, it creates a new one.
     *
     * @param testId the ID of the test
     * @param course the name of the course
     * @param passed boolean indicating whether the user passed the test
     * @param totalScore the score obtained in the test
     * @param student the User entity representing the student submitting the test answer
     */
    @Transactional
    public void submitTestAnswer(
            Long testId,
            String course,
            Boolean passed,
            String totalScore,
            User student
    ) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test with id " + testId + " not found"));

        TestAnswer previousTestAnswer = testAnswerRepository.findByTestIdAndUserId(testId, student.getId());

        if (previousTestAnswer != null) {
            previousTestAnswer.setAttempt(previousTestAnswer.getAttempt() + 1);
            previousTestAnswer.setIsPassed(passed);
            previousTestAnswer.setTotalScore(totalScore);
            previousTestAnswer.setSubmittedDate(new Date());

            testAnswerRepository.save(previousTestAnswer);
        } else {
            TestAnswer testAnswer = new TestAnswer(course, 1, passed, totalScore, new Date(), student, test);
            String studentEmail = student.getEmail();
            String message = "Thank you very much, " + student.getName() +
                    ". You have passed the test with a score of " + totalScore + ".";

            // Отправляем письмо
            mailSenderService.sendFromProgAcademy(studentEmail, "ProgAcademy", message);


            testAnswerRepository.save(testAnswer);

        }
    }


                        // CURRENTLY NOT IN USE

    /**
     * Saves a test answer to the database.
     *
     * @param testAnswer the TestAnswer entity to be saved
     */
    @Transactional
    public void saveTestAnswer(TestAnswer testAnswer) {
        testAnswerRepository.save(testAnswer);
    }
}
