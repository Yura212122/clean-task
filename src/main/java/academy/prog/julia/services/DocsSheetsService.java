package academy.prog.julia.services;

import academy.prog.julia.components.DocsSheets;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import academy.prog.julia.exceptions.TestQuestionsNotFound;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.*;
import com.mysql.cj.log.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static academy.prog.julia.components.DocsSheets.extractSpreadsheetID;

/**
 * Service class responsible for handling operations related to lessons, tasks, and tests
 * using data from Google Sheets. This class interacts with various repositories
 * to persist and retrieve data from the database, and integrates Google Sheets
 * data into the application's domain models (lessons, tasks, tests, and groups).
 *
 * Main responsibilities:
 * - Save lessons from Google Sheets and associate them with groups.
 * - Replace existing lessons with new ones based on Google Sheets data.
 * - Update tasks and tests associated with lessons.
 *
 * This service ensures that all data modifications are transactional and
 * properly manage relationships between lessons, tasks, tests, and groups.
 */
@Service
public class   DocsSheetsService {

    private final LessonRepository lessonRepository;
    private final TaskRepository taskRepository;
    private final TestRepository testRepository;
    private final GroupRepository groupRepository;
    private final DocsSheets docsSheets;
    private final TestQuestionFromGoogleDocsRepository testQuestionFromGoogleDocsRepository;

    private static final Logger LOGGER = LogManager.getLogger(DocsSheetsService.class);

    /**
     * Constructor to inject the dependencies (repositories and DocsSheets component).
     *
     * @param lessonRepository Repository for lessons.
     * @param taskRepository Repository for tasks.
     * @param testRepository Repository for tests.
     * @param groupRepository Repository for groups.
     * @param docsSheets Component responsible for Google Sheets data extraction.
     * @param testQuestionFromGoogleDocsRepository Repository for managing test questions extracted from Google Docs.
     */
    public DocsSheetsService(
            LessonRepository lessonRepository,
            TaskRepository taskRepository,
            TestRepository testRepository,
            GroupRepository groupRepository,
            DocsSheets docsSheets,
            TestQuestionFromGoogleDocsRepository testQuestionFromGoogleDocsRepository
    ) {
        this.lessonRepository = lessonRepository;
        this.taskRepository = taskRepository;
        this.testRepository = testRepository;
        this.groupRepository = groupRepository;
        this.docsSheets = docsSheets;
        this.testQuestionFromGoogleDocsRepository = testQuestionFromGoogleDocsRepository;
    }

    /**
     * Saves lessons fetched from a Google Sheets document to the database.
     * Links tasks and tests to their respective lessons, and associates the lessons with a group.
     *
     * @param spreadsheetURL The URL of the Google Sheets document.
     * @param sheetNumber The number of the sheet within the document.
     * @param groupName The name of the group to associate the lessons with.
     * @return The list of saved lessons.
     * @throws GeneralSecurityException If there's a security issue accessing the document.
     * @throws IOException If an I/O error occurs during the document reading.
     * @throws TestQuestionsNotFound If test questions are not found in the document.
     */
    @Transactional
    public List<Lesson> lessonsSave(
            String spreadsheetURL,
            int sheetNumber,
            String groupName
    ) throws GeneralSecurityException, IOException, TestQuestionsNotFound {

        // Find the group by name, throw an exception if not found
        LOGGER.info("Looking for group with name: {}", groupName);
        Group group = groupRepository.findByName(groupName).orElseThrow(() -> {
            LOGGER.error("Group not found with name: {}", groupName);
            return new RuntimeException("Group not found");
        });

        // Read lessons from the provided Google Sheets URL
        LOGGER.info("Reading lessons from Google Sheets URL: {}, Sheet number: {}", spreadsheetURL, sheetNumber);
        List<Lesson> lessons = docsSheets.lessonReader(spreadsheetURL, sheetNumber);

        // Process each lesson
        lessons.forEach(lesson -> {
            // Save tasks and associate them with the lesson
            lesson.getTasks().stream()
                    .peek(task -> {
                        task.setLesson(lesson);  // Set lesson for the task
                        LOGGER.debug("Associating task with lesson: {}, Task: {}", lesson.getName(), task.getName());
                    })
                    .forEach(task -> {
                        taskRepository.save(task);  // Save the task
                        LOGGER.debug("Task saved: {}", task.getName());
                    });

            // Save tests and associate them with the lesson
            lesson.getTests().stream()
                    .peek(test -> {
                        test.setLesson(lesson);  // Set lesson for the test
                        LOGGER.debug("Associating test with lesson: {}, Test: {}", lesson.getName(), test.getName());
                    })
                    .forEach(test -> {
                        testRepository.save(test);  // Save the test
                        LOGGER.debug("Test saved: {}", test.getName());
                    });

            // Associate lesson with the group
            group.getLessons().add(lesson);
            lesson.getGroups().add(group);
            lesson.setSheetNumber(sheetNumber);  // Set the sheet number

        });

        // Save all lessons at once
        LOGGER.info("Saving all lessons...");
        lessonRepository.saveAll(lessons);

        // Save the group with the associated lessons
        LOGGER.info("Saving group: {}", group.getName());
        groupRepository.save(group);
        LOGGER.info("Lessons saved successfully for group: {}", group.getName());

        return lessons;
    }



    /**
     * Finds lessons by their spreadsheet ID and sheet number.
     *
     * @param spreadsheetID The ID of the Google Sheets document.
     * @param sheetNum The sheet number in the document.
     * @return A list of lessons matching the provided spreadsheet ID and sheet number.
     */
    @Transactional(readOnly = true)
    public List<Lesson> findLessonsBySpreadsheetIDAndNumber(
            String spreadsheetID,
            Integer sheetNum
    ) {
        return lessonRepository.findBySpreadsheetIDAndSheetNumber(spreadsheetID, sheetNum);
    }

    /**
     * Replaces existing lessons with new lessons fetched from the provided Google Sheets document.
     * Updates tasks and tests in the lessons where necessary, and deletes lessons that need replacement.
     *
     * @param lessonsToReplace The list of lessons to be replaced.
     * @param spreadsheetURL The URL of the Google Sheets document.
     * @param sheetNumber The number of the sheet within the document.
     * @param group The group associated with the lessons.
     * @throws GeneralSecurityException If there's a security issue accessing the document.
     * @throws IOException If an I/O error occurs during the document reading.
     * @throws TestQuestionsNotFound If test questions are not found in the document.
     */
    @Transactional
    public void replaceLesson(
            List<Lesson> lessonsToReplace,
            String spreadsheetURL,
            Integer sheetNumber,
            Group group
    ) throws GeneralSecurityException, IOException, TestQuestionsNotFound {

        // Logging initial parameters
        LOGGER.info("Starting replaceLesson with spreadsheetURL: {}, sheetNumber: {}, group: {}",
                spreadsheetURL, sheetNumber, group.getName());

        // Validate lessonsToReplace
        if (lessonsToReplace == null || lessonsToReplace.isEmpty()) {
            LOGGER.error("Error: lessonsToReplace is null or empty.");
            throw new TestQuestionsNotFound("No lessons found to replace.");
        }
        LOGGER.debug("Number of lessons to replace: {}", lessonsToReplace.size());

        // Validate spreadsheetURL
        if (spreadsheetURL == null || spreadsheetURL.isEmpty()) {
            LOGGER.error("Error: spreadsheetURL is null or empty.");
            throw new IllegalArgumentException("spreadsheetURL cannot be null or empty.");
        }

        // Extract spreadsheet ID with logging
        LOGGER.debug("Extracting spreadsheet ID from URL: {}", spreadsheetURL);
        String spreadsheetID = extractSpreadsheetID(spreadsheetURL);
        LOGGER.debug("Extracted spreadsheet ID: {}", spreadsheetID);

        if (spreadsheetID == null || spreadsheetID.isEmpty()) {
            LOGGER.error("Failed to extract Spreadsheet ID from the URL: {}", spreadsheetURL);
            throw new JuliaRuntimeException("Spreadsheet ID cannot be null or empty.");
        }

        // Retrieve new lessons from Google Sheets
        LOGGER.info("Reading lessons from Google Sheets for sheet number: {}", sheetNumber);
        List<Lesson> newLessons = docsSheets.lessonReader(spreadsheetURL, sheetNumber);
        if (newLessons == null || newLessons.isEmpty()) {
            LOGGER.error("Error: No lessons found in Google Sheets document.");
            throw new TestQuestionsNotFound("No lessons found in Google Sheets.");
        }
        LOGGER.info("Number of new lessons retrieved: {}", newLessons.size());

        // Replace or update existing lessons
        LOGGER.info("Replacing or updating existing lessons");
        for (int i = 0; i < Math.min(lessonsToReplace.size(), newLessons.size()); i++) {
            Lesson existingLesson = lessonRepository.findById(lessonsToReplace.get(i).getId()).orElseThrow();
            Lesson newLesson = newLessons.get(i);

            LOGGER.debug("Updating lesson: {} with new data", existingLesson.getName());
            existingLesson.setSheetNumber(sheetNumber);
            existingLesson.setSpreadsheetID(spreadsheetID);

            // Remove existing lesson from old groups and add to new group
            LOGGER.debug("Updating group associations for lesson: {}", existingLesson.getName());
            existingLesson.getGroups().forEach(g -> g.getLessons().remove(existingLesson));
            group.getLessons().add(existingLesson);
            existingLesson.getGroups().add(group);

            // Update lesson details
            existingLesson.setName(newLesson.getName());
            existingLesson.setVideoUrl(newLesson.getVideoUrl());
            existingLesson.setDescriptionUrl(newLesson.getDescriptionUrl());
            LOGGER.debug("Updated details for lesson: {}", existingLesson.getName());

            // Update tasks if count matches
            if (newLesson.getTasks().size() == existingLesson.getTasks().size()) {
                LOGGER.debug("Updating tasks for lesson: {}", existingLesson.getName());
                List<Task> existingTasks = new ArrayList<>(existingLesson.getTasks());
                List<Task> newTasks = new ArrayList<>(newLesson.getTasks());

                for (int j = 0; j < existingTasks.size(); j++) {
                    Task existingTask = existingTasks.get(j);
                    Task newTask = newTasks.get(j);

                    if (existingTask != null) {
                        existingTask.setDeadline(newTask.getDeadline());
                        existingTask.setDescriptionUrl(newTask.getDescriptionUrl());
                        existingTask.setExpectedResult(newTask.getExpectedResult());
                        existingTask.setName(newTask.getName());
                        taskRepository.save(existingTask);
                        LOGGER.debug("Task updated: {}", existingTask.getName());
                    }
                }
            }

            // Update tests
            LOGGER.debug("Updating tests for lesson: {}", existingLesson.getName());
            for (Test newTest : newLesson.getTests()) {
                Optional<Test> optionalTest = existingLesson.getTests().stream()
                        .filter(test -> test.getName().equals(newTest.getName()) &&
                                test.getTestUrl().equals(newTest.getTestUrl()))
                        .findAny();

                if (optionalTest.isPresent()) {
                    Test existingTest = optionalTest.get();
                    existingTest.setDeadline(newTest.getDeadline());
                    existingTest.setMandatory(newTest.getMandatory());
                    existingTest.setPassed(newTest.getPassed());

                    testQuestionFromGoogleDocsRepository.deleteAll(existingTest.getTestQuestionFromGoogleDocs());
                    existingTest.setTestQuestionFromGoogleDocs(newTest.getTestQuestionFromGoogleDocs());
                    testRepository.save(existingTest);
                    LOGGER.debug("Test updated: {}", existingTest.getName());
                }
            }

            lessonRepository.save(existingLesson);
            LOGGER.info("Lesson saved: {}", existingLesson.getName());
        }

        // Remove old lessons if the list has shrunk
        LOGGER.info("Removing old lessons if the list size has decreased");
        for (int i = newLessons.size(); i < lessonsToReplace.size(); i++) {
            Lesson lesson = lessonRepository.findById(lessonsToReplace.get(i).getId()).orElseThrow();
            lesson.getGroups().forEach(g -> g.getLessons().remove(lesson));
            taskRepository.deleteAll(lesson.getTasks());
            testRepository.deleteAll(lesson.getTests());
            lessonRepository.deleteById(lesson.getId());
            LOGGER.info("Removed lesson: {}", lesson.getName());
        }

        // Add new lessons if the list has grown
        LOGGER.info("Adding new lessons if the list size has increased");
        for (int i = lessonsToReplace.size(); i < newLessons.size(); i++) {
            Lesson newLesson = newLessons.get(i);
            newLesson.setSheetNumber(sheetNumber);
            newLesson.setSpreadsheetID(spreadsheetID);
            newLesson.getGroups().add(group);
            group.getLessons().add(newLesson);

            for (var task : newLesson.getTasks()) {
                task.setLesson(newLesson);
                taskRepository.save(task);
                LOGGER.debug("Task saved for new lesson: {}", task.getName());
            }

            for (var test : newLesson.getTests()) {
                test.setLesson(newLesson);
                testRepository.save(test);
                LOGGER.debug("Test saved for new lesson: {}", test.getName());
            }

            lessonRepository.save(newLesson);
            LOGGER.info("New lesson saved: {}", newLesson.getName());
        }

        LOGGER.info("Completed replaceLesson process for group: {}", group.getName());
    }




}