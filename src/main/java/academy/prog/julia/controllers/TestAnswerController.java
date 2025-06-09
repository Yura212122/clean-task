package academy.prog.julia.controllers;

import academy.prog.julia.json_responses.TestAnswerResponse;
import academy.prog.julia.services.TestAnswerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling test-related operations such as submitting test answers
 * and retrieving test information for a specific user.
 */
@RestController
@RequestMapping("/api/tests")
public class TestAnswerController {

    private final TestAnswerService testAnswerService;

    /**
     * Constructor for dependency injection of services.
     *
     * @param testAnswerService - Service responsible for test-related operations.
     */
    public TestAnswerController(
            TestAnswerService testAnswerService
    ) {
        this.testAnswerService = testAnswerService;
    }

    /**
     * Submits the answer for a test by a specific user.
     *
     * @param userId - ID of the user submitting the test.
     * @param testId - ID of the test being submitted.
     * @param passed - Boolean indicating whether the test was passed.
     * @param totalScore - String representing the total score of the test.
     * @param course - String representing the course the test belongs to.
     * @param session - HttpSession object for session management.
     * @return ResponseEntity containing the result of the submission process (success or error).
     */
    @PostMapping("/{userId}/{testId}/submit")
    public ResponseEntity<Object> submitTestAnswer(
            @RequestBody
            @PathVariable("userId") Long userId,
            @PathVariable("testId") Long testId,
            @RequestParam Boolean passed,
            @RequestParam String totalScore,
            @RequestParam String course,
            HttpSession session
    ) {
        return testAnswerService.handleTestSubmission(userId, testId, passed, totalScore, course, session);
    }

    /**
     * Retrieves the test answer submitted by a specific user for a specific test.
     *
     * @param userId - ID of the user whose test result is being retrieved.
     * @param testId - ID of the test result to retrieve.
     * @return ResponseEntity containing the TestAnswerResponse object with the test details.
     */
    @GetMapping("/{userId}/{testId}")
    public ResponseEntity<TestAnswerResponse> viewTestByUser(
            @PathVariable("userId") Long userId,
            @PathVariable("testId") Long testId
    ) {
        return testAnswerService.getTestAnswerResponse(userId, testId);
    }

}
