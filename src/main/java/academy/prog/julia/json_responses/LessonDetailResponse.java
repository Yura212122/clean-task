package academy.prog.julia.json_responses;

import academy.prog.julia.dto.LessonDetailDTO;
import academy.prog.julia.dto.TestDTOForLessonDetailResponseWithTestAnswers;
import academy.prog.julia.dto.TestQuestionDTOForTestDTOForLessonDetailResponse;
import academy.prog.julia.model.Task;
import academy.prog.julia.model.Test;
import academy.prog.julia.model.TestQuestionFromGoogleDocs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the response object containing detailed information about a lesson.
 */
public class LessonDetailResponse {

    private String name;
    private String descriptionUrl;
    private String videoUrl;
    private List<Task> tasks;
    private List<TestDTOForLessonDetailResponseWithTestAnswers> tests;

    /**
     * Default constructor for LessonDetailResponse.
     *
     * This constructor creates an empty LessonDetailResponse object.
     *
     */
    public LessonDetailResponse() {
    }

    /**
     * Parameterized constructor to initialize LessonDetailResponse with given values.
     *
     * @param name             the name of the lesson
     * @param descriptionUrl   the URL for the lesson description
     * @param videoUrl         the URL for the lesson video
     * @param tasks            the list of tasks for the lesson
     * @param tests            the list of tests for the lesson
     */
    public LessonDetailResponse(
            String name,
            String descriptionUrl,
            String videoUrl,
            List<Task> tasks,
            List<TestDTOForLessonDetailResponseWithTestAnswers> tests
    ) {
        this.name = name;
        this.descriptionUrl = descriptionUrl;
        this.videoUrl = videoUrl;
        this.tasks = tasks;
        this.tests = tests;
    }

    /**
     * Retrieves the name of the lesson.
     *
     * @return the name of the lesson.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the URL for the lesson description.
     *
     * @return the URL for the lesson description.
     */
    public String getDescriptionUrl() {
        return descriptionUrl;
    }

    /**
     * Retrieves the URL for the lesson video.
     *
     * @return the URL for the lesson video.
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * Retrieves the list of tasks associated with the lesson.
     *
     * @return a {@link List} of {@link Task} objects representing the tasks for the lesson
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Retrieves the list of tests associated with the lesson.
     *
     * @return a {@link List} of {@link TestDTOForLessonDetailResponseWithTestAnswers} objects
     *         representing the tests for the lesson
     */
    public List<TestDTOForLessonDetailResponseWithTestAnswers> getTests() {
        return tests;
    }

    /**
     * Converts a LessonDetailDTO object to a LessonDetailResponse object.
     *
     * @param lessonDetailDTO the DTO containing lesson details
     * @return a LessonDetailResponse object
     */
    public static LessonDetailResponse fromDTO(LessonDetailDTO lessonDetailDTO) {
        if (lessonDetailDTO == null) {
            return null;
        }
        Set<TestDTOForLessonDetailResponseWithTestAnswers> testDtoWithAnswers =
                converterTestDTOForLessonDetailResponseFromTest(lessonDetailDTO.getTests());

        return new LessonDetailResponse(
                lessonDetailDTO.getName(),
                lessonDetailDTO.getDescriptionUrl(),
                lessonDetailDTO.getVideoUrl(),
                List.copyOf(lessonDetailDTO.getTasks()),
                List.copyOf(testDtoWithAnswers)
        );
    }

    /**
     * Converts a set of Test objects to a set of TestDTOForLessonDetailResponseWithTestAnswers.
     *
     * @param testSet the set of Test objects
     * @return a set of TestDTOForLessonDetailResponseWithTestAnswers
     */
    public static Set<TestDTOForLessonDetailResponseWithTestAnswers>
    converterTestDTOForLessonDetailResponseFromTest(Set<Test> testSet) {

        Set<TestDTOForLessonDetailResponseWithTestAnswers> convertedTests = new HashSet<>();

        for (Test test : testSet) {
            Set<TestQuestionDTOForTestDTOForLessonDetailResponse> convertedTestQuestion =
                    convertTestQuestionDTOForTestDTOForLessonDetailResponseFromTestAnswers(
                            test.getTestQuestionFromGoogleDocs()
                    );

            TestDTOForLessonDetailResponseWithTestAnswers convertedTest =
                    new TestDTOForLessonDetailResponseWithTestAnswers(
                        test.getId(),
                        test.getName(),
                        test.getMandatory(),
                        test.getDeadline(),
                        convertedTestQuestion
                    );

            convertedTests.add(convertedTest);
        }

        return convertedTests;
    }

    /**
     * Converts a set of TestQuestionFromGoogleDocs objects to a set of TestQuestionDTOForTestDTOForLessonDetailResponse.
     *
     * @param testQuestionFromGoogleDocs the set of TestQuestionFromGoogleDocs objects
     * @return a set of TestQuestionDTOForTestDTOForLessonDetailResponse
     */
    public static Set<TestQuestionDTOForTestDTOForLessonDetailResponse>
    convertTestQuestionDTOForTestDTOForLessonDetailResponseFromTestAnswers(
            Set<TestQuestionFromGoogleDocs> testQuestionFromGoogleDocs
    ) {
        Set<TestQuestionDTOForTestDTOForLessonDetailResponse> convertedSet = new HashSet<>();
        for (TestQuestionFromGoogleDocs question : testQuestionFromGoogleDocs) {
            TestQuestionDTOForTestDTOForLessonDetailResponse newTest =
                    new TestQuestionDTOForTestDTOForLessonDetailResponse(
                        question.getId(), question.getQuestion(),
                        question.getOptions(), question.getCorrectAnswers()
                    );

            convertedSet.add(newTest);
        }

        return convertedSet;
    }

}
