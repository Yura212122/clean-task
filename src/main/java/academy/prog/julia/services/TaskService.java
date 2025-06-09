package academy.prog.julia.services;

import academy.prog.julia.dto.*;
import academy.prog.julia.exceptions.ResourceNotFoundException;
import academy.prog.julia.json_requests.TaskAnswerStartRequest;
import academy.prog.julia.json_responses.LessonProgressResponse;
import academy.prog.julia.json_responses.TaskAnswerResponse;
import academy.prog.julia.json_responses.TaskDetailsResponse;
import academy.prog.julia.json_responses.TaskProgressResponse;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing tasks and task answers, including fetching and submitting task details.
 */
@Service
public class TaskService {

    private static final Logger LOGGER = LogManager.getLogger(TaskService.class);

    private final TaskAnswerRepository taskAnswerRepository;
    private final TaskRepository taskRepository;
    private final LessonRepository lessonRepository;
    private final GroupRepository groupRepository;
    private final GroupService groupService;
    private final Environment environment;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private static final String KEY_STATUS = "status";
    private static final String VALUE_STATUS = "success";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TASK_ID = "taskId" ;
    private static final String KEY_PARAM_ID = "id";
    private static final String KEY_ANSWER_URL = "answerUrl";
    private static final String KEY_COURSE = "course";
    private static final String VALUE_TASK_ANSWER_CHANGED_SUCCESSFULLY = "Task answer status is changed successfully";
    private static final String VALUE_TASK_ANSWER_DOESNT_EXIST_YET = "Task answer doesn't exist yet";
    private static final String ANSWER_URL_CANT_BE_NULL_MESSAGE = "AnswerUrl cannot be null or empty";
    private static final String DONT_HAVE_PERMISSION_MESSAGE = "You don't have permission to do this";
    private static final String TASK_NOT_FOUND_MESSAGE = "Task with id %s not found";
    private static final String TASK_ANSWER_NOT_FOUND_MESSAGE = "TaskAnswer with id %s not found";
    private static final String VALUE_TASK_ANSWER_SUBMITTED_SUCCESSFULLY = "Task answer submitted successfully";
    private static final String VALUE_FAILED_STATUS = "failed";
    private static final String COURSE_NOT_FOUND_MESSAGE = "Courses not found with id: %s";
    private static final String LESSON_NOT_FOUND_MESSAGE = "Lesson not found with id: %s";

    /**
     * Constructs a new TaskService with the required repositories and services.
     *
     * @param taskAnswerRepository the repository for task answers
     * @param taskRepository the repository for tasks
     * @param lessonRepository the repository for lessons
     * @param groupRepository the repository for groups
     * @param groupService the service for managing groups
     * @param environment the environment for configuration properties
     * @param restTemplate the RestTemplate for external API calls
     * @param userService the service for managing users
     */
    public TaskService(
            TaskAnswerRepository taskAnswerRepository,
            TaskRepository taskRepository,
            LessonRepository lessonRepository,
            GroupRepository groupRepository,
            GroupService groupService,
            Environment environment,
            RestTemplate restTemplate,
            UserService userService
    ) {
        this.taskAnswerRepository = taskAnswerRepository;
        this.taskRepository = taskRepository;
        this.lessonRepository = lessonRepository;
        this.groupRepository = groupRepository;
        this.groupService = groupService;
        this.environment = environment;
        this.restTemplate = restTemplate;
        this.userService = userService;
    }


    @Lookup
    public TaskService getSelf() {
        return null;
    }

    /**
     * Retrieves detailed information about a specific task.
     *
     * @param taskId the ID of the task to retrieve
     * @return a TasksDetailedDTO containing detailed task information
     * @throws EntityNotFoundException if no task with the given ID is found
     */
    @Transactional(readOnly = true)
    public TasksDetailedDTO getTaskDetails(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, taskId)));

        return TasksDetailedDTO.fromTask(task);
    }

    /**
     * Submits an answer for a specific task, creating or updating the task answer as necessary.
     *
     * @param taskId the ID of the task to which the answer is submitted
     * @param answerUrl the URL of the submitted answer
     * @param courseId the ID of the course related to the task
     * @param course the name of the course related to the task
     * @param lessonId the ID of the lesson related to the task
     * @param lessonNum the number of the lesson related to the task
     * @param student the student submitting the answer
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void submitTaskAnswer(
            Long taskId,
            String answerUrl,
            Long courseId,
            String course,
            Long lessonId,
            Integer lessonNum,
            User student
    ) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, taskId)));

        TaskAnswer previousTaskAnswer = taskAnswerRepository.findByTaskIdAndUserId(taskId, student.getId());

        if (Objects.nonNull(previousTaskAnswer)) {

            previousTaskAnswer.setAnswerUrl(answerUrl);
            previousTaskAnswer.setSubmittedDate(new Date());
            previousTaskAnswer.setIsCorrection(false);
            previousTaskAnswer.setIsRead(false);

            taskAnswerRepository.save(previousTaskAnswer);
        } else {
            TaskAnswer taskAnswer = new TaskAnswer(
                    student,
                    task,
                    answerUrl,
                    courseId,
                    lessonId,
                    lessonNum,
                    course,
                    false,
                    false,
                    "",
                    false,
                    new Date()
            );

            taskAnswerRepository.save(taskAnswer);
        }
    }

    /**
     * Retrieves the answer for a specific task submitted by a particular user.
     *
     * @param taskId the ID of the task for which the answer is requested
     * @param userId the ID of the user who submitted the answer
     * @return a TaskAnswerDTO containing the details of the task answer
     */
    @Transactional(readOnly = true)
    public TaskAnswerDTO getTaskAnswer(
            Long taskId,
            Long userId
    ) {
        TaskAnswer taskAnswer = taskAnswerRepository.findByTaskIdAndUserId(taskId, userId);

        return TaskAnswerDTO.fromTaskAnswer(taskAnswer);
    }

    /**
     * Finds all task answers submitted by a specific user.
     *
     * @param userId the ID of the user whose task answers are to be retrieved
     * @return a list of TaskAnswerDTO objects representing the user's task answers
     */
    @Transactional(readOnly = true)
    public List<TaskAnswerDTO> findByUserId(Long userId) {
        List<TaskAnswer> taskAnswers = taskAnswerRepository.findByUserId(userId);
        List<TaskAnswerDTO> taskAnswerDTOS = new ArrayList<>();

        try {
            for (TaskAnswer taskAnswer : taskAnswers) {
                taskAnswerDTOS.add(TaskAnswerDTO.fromTaskAnswer(taskAnswer));
            }

        } catch (NullPointerException e) {
            LOGGER.error("Task answer has null fields!");
        }

        return taskAnswerDTOS;
    }

    /**
     * Retrieves all task submissions for a specific teacher with pagination support.
     *
     * @param page the page number (0-based index)
     * @param size the size of the page (number of tasks per page)
     * @param teacherId the ID of the teacher whose tasks are to be retrieved
     * @return a Page of TaskSubmissionDTO objects representing the teacher's task submissions
     */
    @Transactional(readOnly = true)
    public Page<TaskSubmissionDTO> getAllTaskSubmissionsByTeacherId(
            int page,
            int size,
            Long teacherId
    ) {
        Page<Task> taskPage = taskRepository.findTaskByTeacherId(teacherId, PageRequest.of(page, size));

        return getTaskSubmissionDTOS(page, size, taskPage);
    }

    private Page<TaskSubmissionDTO> getTaskSubmissionDTOS(int page, int size, Page<Task> taskPage) {
        List<TaskSubmissionDTO> taskSubmissionDTOList = taskPage.getContent().stream()
                .map(task -> {
                    Set<TaskAnswerDTO> studentAnswers = task.getTaskAnswers().stream()
                            .map(this::mapToTaskAnswerDTO)
                            .collect(Collectors.toSet());

                    Set<Group> groups = Objects.nonNull(task.getLesson()) ? task.getLesson().getGroups() : Collections.emptySet();
                    Set<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toSet());

                    return new TaskSubmissionDTO(task.getId(), groupNames, studentAnswers);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        return new PageImpl<>(taskSubmissionDTOList, PageRequest.of(page, size), taskPage.getTotalElements());
    }

    /**
     * Retrieves pending task answers with pagination support.
     *
     * @param pageable the pagination information
     * @return a Page of TaskAnswer objects representing pending task answers
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Page<TaskAnswer> findPendingTask(Pageable pageable) {
        return taskAnswerRepository.findPendingTask(pageable);
    }

    /**
     * Retrieves pending task answers for a specific group with pagination support.
     *
     * @param pageable the pagination information
     * @param groupName the name of the group for which to retrieve pending task answers
     * @return a Page of TaskAnswer objects representing pending task answers for the specified group
     */
    @Transactional(readOnly = true)
    public Page<TaskAnswer> findPendingTaskByGroup(
            Pageable pageable,
            String groupName
    ) {
        return taskAnswerRepository.findPendingTaskByGroup(pageable, groupName);
    }

    /**
     * Retrieves a paginated list of task answers grouped by tasks, with optional filtering by group name.
     *
     * @param pageable the pagination information
     * @param groupName the name of the group to filter tasks (can be null for no filtering)
     * @return a List of TasksGroupResponseDTO objects representing the tasks
     * @throws EntityNotFoundException if no tasks are found
     */
    @Transactional(readOnly = true)
    public List<TasksGroupResponseDTO> findPaginated(
            Pageable pageable,
            String groupName
    ) throws EntityNotFoundException {
        TaskService self = getSelf();

        Page<TaskAnswer> tasksDB = (Objects.isNull(groupName)) ? self.findPendingTask(pageable) : self.findPendingTaskByGroup(pageable, groupName);

        List<TasksGroupResponseDTO> tasks = tasksDB.stream()
                .map(TasksGroupResponseDTO::fromTask)
                .toList();

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found");
        }

        return tasks;
    }

    /**
     * Maps a TaskAnswer entity to a TaskAnswerDTO.
     *
     * @param taskAnswer the TaskAnswer entity to map
     * @return a TaskAnswerDTO representing the mapped task answer, or null if the task answer or user is null
     */
    @Transactional(readOnly = true)
    public TaskAnswerDTO mapToTaskAnswerDTO(TaskAnswer taskAnswer) {
        if (Objects.isNull(taskAnswer) || Objects.isNull(taskAnswer.getUser())) {
            return null;
        }

        Task task = taskRepository.findById(taskAnswer.getTask().getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, taskAnswer.getTask().getId())));

        UserFromAnswerTaskDTO userFromAnswerTaskDTO = UserFromAnswerTaskDTO.fromUser(taskAnswer.getUser());

        return new TaskAnswerDTO(
                taskAnswer.getId(),
                taskAnswer.getAnswerUrl(),
                taskAnswer.getCourseId(),
                taskAnswer.getLessonId(),
                taskAnswer.getLessonNum(),
                task.getId(),
                task.getName(),
                taskAnswer.getCourse(),
                task.getDescriptionUrl(),
                taskAnswer.getIsPassed(),
                taskAnswer.getIsCorrection(),
                taskAnswer.getMessageForCorrection(),
                taskAnswer.getIsRead(),
                taskAnswer.getSubmittedDate(),
                userFromAnswerTaskDTO
        );
    }

    /**
     * Retrieves a specific task answer by its ID.
     *
     * @param taskAnswerId the ID of the task answer to retrieve
     * @return a TaskAnswerDTO representing the task answer
     * @throws ResourceNotFoundException if the task answer with the given ID is not found
     */
    @Transactional(readOnly = true)
    public TaskAnswerDTO getTaskSubmission(Long taskAnswerId) {
        TaskAnswer taskAnswer = taskAnswerRepository.findById(taskAnswerId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_ANSWER_NOT_FOUND_MESSAGE, taskAnswerId)));

        return TaskAnswerDTO.fromTaskAnswer(taskAnswer);
    }

    /**
     * Grades a task submission, updating its correction status, pass/fail status, correction message, and read status.
     * Optionally generates a ZIP file of the task answer if certain conditions are met.
     *
     * @param taskId the ID of the task associated with the submission
     * @param isCorrection the correction status of the task answer
     * @param isPassed the pass/fail status of the task answer
     * @param messageForCorrection the message for correction
     * @param isRead the read status of the task answer
     */
    @Transactional
    public void gradeTaskSubmission(
            Long taskId,
            boolean isCorrection,
            boolean isPassed,
            String messageForCorrection,
            boolean isRead
    ) {
        TaskService self = getSelf();
        TaskAnswer taskAnswer = taskAnswerRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TASK_NOT_FOUND_MESSAGE, taskId)));

        taskAnswer.setIsCorrection(isCorrection);
        taskAnswer.setIsPassed(isPassed);
        taskAnswer.setMessageForCorrection(messageForCorrection);
        taskAnswer.setIsRead(isRead);

        // Here we have flag to download repositories from GitHub
        boolean automaticDownloadZipEnabled = environment
                .getProperty("github.automatic-download-zip.enabled", Boolean.class, false);

        if (Boolean.TRUE.equals(taskAnswer.getIsPassed()) && automaticDownloadZipEnabled) {
            self.generateAndSaveTaskAnswerZip(taskAnswer);
        }

        taskAnswerRepository.save(taskAnswer);
    }

    /**
     * Checks if a given student (user) is a member of the group associated with a specific task.
     *
     * @param userId The ID of the student.
     * @param taskId The ID of the task.
     * @return True if the student is in the group for the given task, false otherwise.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public boolean isStudentInTaskGroup(
            Long userId,
            Long taskId
    ) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            if (!task.getLesson().getGroups().isEmpty()) {

                return task.getLesson()
                        .getGroups()
                        .stream()
                        .anyMatch(group -> groupService.isStudentInGroup(userId, group.getId()))
                ;
            }
        }

        return false;
    }

    /**
     * Retrieves the progress of a user for a specific course.
     *
     * @param userId The ID of the user.
     * @param courseId The ID of the course.
     * @return A DTO containing the course progress for the user.
     * @throws EntityNotFoundException if the course or its lessons are not found.
     */
    @Transactional(readOnly = true)
    public TaskProgressDTO getCourseProgress(
            Long userId,
            Long courseId
    ) {

        if (!groupRepository.existsById(courseId)) throw new EntityNotFoundException(String.format(COURSE_NOT_FOUND_MESSAGE, courseId));
        List<Lesson> lessons = lessonRepository.findCourseLessons(courseId);

        if (lessons.isEmpty()) {
            return new TaskProgressDTO(courseId, 0);
        }

        ArrayList<Task> tasks = new ArrayList<>();

        lessons.forEach(lesson -> {
            Lesson lessonL = lessonRepository.findById(lesson.getId()).orElseThrow(() -> new EntityNotFoundException(String.format(LESSON_NOT_FOUND_MESSAGE, lesson.getId())));
            tasks.addAll(lessonL.getTasks());
        });

        ArrayList<TaskAnswer> taskAnswers = new ArrayList<>();

        Integer taskProgress = null;

        for (Task task : tasks) {
            TaskAnswer taskAnswer = taskAnswerRepository.findByTaskIdAndUserId(task.getId(), userId);
            if (Objects.nonNull(taskAnswer) && Boolean.TRUE.equals(taskAnswer.getIsPassed())) {
                    taskAnswers.add(taskAnswer);
                }

            taskProgress = taskAnswers.size() * 100 / tasks.size();
        }

        return new TaskProgressDTO(courseId, taskProgress);
    }

    /**
     * Calculates the progress of a user for a specific lesson.
     *
     * @param userId The ID of the user.
     * @param lessonId The ID of the lesson.
     * @return A DTO containing the lesson progress and percentage for the user.
     * @throws EntityNotFoundException if the lesson is not found.
     */
    @Transactional(readOnly = true)
    public LessonProgressDTO getLessonProgress(
            Long userId,
            Long lessonId
    ) {

        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new EntityNotFoundException(String.format(LESSON_NOT_FOUND_MESSAGE, lessonId)));

        ArrayList<Task> tasks = new ArrayList<>(lesson.getTasks());

        ArrayList<TaskAnswer> taskAnswersNull = new ArrayList<>();
        ArrayList<TaskAnswer> taskAnswersSubmit = new ArrayList<>();
        ArrayList<TaskAnswer> taskAnswersCorrection = new ArrayList<>();
        ArrayList<TaskAnswer> taskAnswersPassed = new ArrayList<>();

        String lessonProgress = null;
        int lessonPercent = 0;

        tasks.forEach(task -> {
            TaskAnswer taskAnswer = taskAnswerRepository.findByTaskIdAndUserId(task.getId(), userId);
            if (taskAnswer != null) {
                if (Boolean.TRUE.equals(taskAnswer.getIsPassed())) {
                    taskAnswersPassed.add(taskAnswer);
                } else if (Boolean.TRUE.equals(taskAnswer.getIsCorrection())) {
                    taskAnswersCorrection.add(taskAnswer);
                } else if (Boolean.FALSE.equals(taskAnswer.getIsPassed()) && Boolean.FALSE.equals(taskAnswer.getIsCorrection())) {
                    taskAnswersSubmit.add(taskAnswer);
                }
            } else {
                taskAnswersNull.add(null);
            }
        });

        if (tasks.isEmpty()) {
            lessonProgress = "No tasks";
        } else if (taskAnswersPassed.size() == tasks.size()) {
            lessonProgress = "All passed";
        } else if (taskAnswersNull.size() == tasks.size()) {
            lessonProgress = "Not started";
        } else if (!taskAnswersCorrection.isEmpty()) {
            lessonProgress = "Need correction";
        } else if (!taskAnswersSubmit.isEmpty() || !taskAnswersPassed.isEmpty()) {
            lessonProgress = "In progress";
        }

        if (!tasks.isEmpty()) {
            lessonPercent = taskAnswersPassed.size() * 100 / tasks.size();
        }

        return new LessonProgressDTO(lessonId, lessonProgress, lessonPercent);
    }

    /**
     * Finds all active tasks for a specific user that have a deadline on or before a given date.
     *
     * @param userId The ID of the user.
     * @param deadLine The deadline date.
     * @return A list of tasks that are active for the user and have a deadline on or before the specified date.
     */
    @Transactional(readOnly = true)
    public List<Task> findAllActiveTaskByUserIdWithDeadLine(
            Long userId,
            LocalDate deadLine
    ) {
        return taskRepository.findAllActiveTaskByUserIdWithDeadLine(userId, deadLine);
    }

    /**
     * Filters tasks for notification purposes based on their status and user answers.
     *
     * @param user The user for whom the tasks are filtered.
     * @param tasks The list of tasks to filter.
     * @return A set of tasks that either have no answer from the user or have an answer that is not yet corrected.
     */
    @Transactional(readOnly = true)
    public Set<Task> filterTasksForNotifyToEndDeadLineDate(
            User user,
            List<Task> tasks
    ) {
        Set<Task> filteredTasks = new HashSet<>();
        for (Task task : tasks) {
            // Add task into set who don't have answers on the task
            TaskAnswer taskAnswer = taskAnswerRepository.findByTaskIdAndUserId(task.getId(), user.getId());
            if (Objects.isNull(taskAnswer) || (Boolean.FALSE.equals(taskAnswer.getIsPassed()) && Boolean.TRUE.equals(taskAnswer.getIsCorrection()))) {
                filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }

    /**
     * Generates and saves a ZIP archive of the task answer's repository if the task is marked as passed and
     * automatic download is enabled.
     *
     * @param taskAnswer The task answer object containing the repository URL.
     */
    @Transactional
    public void generateAndSaveTaskAnswerZip(TaskAnswer taskAnswer) {
        try {
            String repoUrl = taskAnswer.getAnswerUrl();

            if (repoUrl == null || repoUrl.isEmpty()) {
                LOGGER.error("Repo URL is null or empty");
                return;
            }

            if (!isValidGitHubUrl(repoUrl)) {
                LOGGER.error("Invalid GitHub URL: {}", repoUrl);
                return;
            }

            String zipArchiveUrl = repoUrl + "/archive/master.zip";
            byte[] answerFile = restTemplate.getForObject(zipArchiveUrl, byte[].class);

            // If we'll need to save taskAnswer to root folder
//            saveByteArrayToFile(answerFile, "master.zip");

            taskAnswer.setZipAnswerFile(answerFile);

            taskAnswerRepository.save(taskAnswer);

        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.error(
                    "From TeacherController. Repository NOT FOUND or it is NOT PUBLIC: {}. Error: {}",
                    taskAnswer.getAnswerUrl(),
                    e.getMessage()
            );

            throw new ResourceNotFoundException(
                    "Repository NOT FOUND or it is NOT PUBLIC: " +
                            taskAnswer.getAnswerUrl()
            );

        } catch (Exception exception) {
            LOGGER.error(
                    "Failed to generate and save task answer ZIP file: {}",
                    exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Retrieves the ZIP file associated with a specific task answer.
     * Throws an exception if the task answer is not marked as passed.
     *
     * @param taskAnswerId the ID of the task answer
     * @return the ZIP file byte array
     * @throws EntityNotFoundException if the task answer with the given ID is not found
     */
    @Transactional
    public byte[] getZipAnswerFile(Long taskAnswerId) {
        TaskAnswer taskAnswer = taskAnswerRepository.findById(taskAnswerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TASK_ANSWER_NOT_FOUND_MESSAGE, taskAnswerId)));

        if (Boolean.FALSE.equals(taskAnswer.getIsPassed())) {
            LOGGER.error("TaskAnswer with id {} is not passed!", taskAnswerId);
        }

        return taskAnswer.getZipAnswerFile();
    }

    /**
     * Validates if the provided URL is a valid GitHub repository URL.
     *
     * @param url the URL to be validated
     * @return true if the URL is a valid GitHub repository URL, false otherwise
     */
    public boolean isValidGitHubUrl(String url) {
        String regex = "^https://github\\.com(?:/[\\w-]+){2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        return url.startsWith("https://github.com/") && matcher.matches();
    }

    /**
     * Retrieves the task details and returns them as a TaskDetailsResponse object.
     *
     * @param taskId the ID of the task
     * @return TaskDetailsResponse object containing the task details
     */
    @Transactional(readOnly = true)
    public TaskDetailsResponse getTaskDetailsAsTaskDetailsResponse(Long taskId) {
        TaskService self = getSelf();
        TasksDetailedDTO tasksDetailedDTO = self.getTaskDetails(taskId);

        return TaskDetailsResponse.fromDTO(tasksDetailedDTO);
    }

    /**
     * Handles the submission of a task answer with various checks in place.
     * This includes authentication, URL validation, and permissions checks.
     *
     * @param taskAnswerStartRequest the request object containing task answer details
     * @param session the HTTP session
     * @return ResponseEntity with the result of the submission
     */
    @Transactional
    public ResponseEntity<Object> submitTaskAnswerWithChecks(
            TaskAnswerStartRequest taskAnswerStartRequest,
            HttpSession session
    ) {
        TaskService self = getSelf();

        if (taskAnswerStartRequest.getUserId() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated", taskAnswerStartRequest.getTaskId()))
            ;
        }

        if (taskAnswerStartRequest.getTaskId() == null || taskAnswerStartRequest.getTaskId() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid task ID provided.", taskAnswerStartRequest.getTaskId()))
            ;
        }

        if (taskAnswerStartRequest.getCourseId() == null || taskAnswerStartRequest.getCourseId() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid course ID provided.", taskAnswerStartRequest.getCourseId()))
            ;
        }

        if (taskAnswerStartRequest.getLessonId() == null || taskAnswerStartRequest.getLessonId() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid lesson ID provided.", taskAnswerStartRequest.getLessonId()))
            ;
        }

        if (taskAnswerStartRequest.getAnswerUrl().equals("isRead")) {
            TaskAnswer previousTaskAnswer = taskAnswerRepository
                    .findByTaskIdAndUserId(taskAnswerStartRequest.getTaskId(), taskAnswerStartRequest.getUserId())
            ;

            if (previousTaskAnswer != null) {
                previousTaskAnswer.setIsRead(false);
                taskAnswerRepository.save(previousTaskAnswer);
                return ResponseEntity.ok(Map.of(
                        KEY_STATUS, VALUE_STATUS,

                        KEY_MESSAGE, VALUE_TASK_ANSWER_CHANGED_SUCCESSFULLY,
                        KEY_TASK_ID, taskAnswerStartRequest.getTaskId(),
                        KEY_ANSWER_URL, taskAnswerStartRequest.getAnswerUrl(),
                        KEY_COURSE, taskAnswerStartRequest.getCourse()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        KEY_STATUS, VALUE_STATUS,
                        KEY_MESSAGE, VALUE_TASK_ANSWER_DOESNT_EXIST_YET,
                        KEY_TASK_ID, taskAnswerStartRequest.getTaskId(),
                        KEY_ANSWER_URL, taskAnswerStartRequest.getAnswerUrl(),
                        KEY_COURSE, taskAnswerStartRequest.getCourse()
                ));
            }
        }

        if (!self.isStudentInTaskGroup(taskAnswerStartRequest.getUserId(), taskAnswerStartRequest.getTaskId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(
                            DONT_HAVE_PERMISSION_MESSAGE,
                            taskAnswerStartRequest.getTaskId())
                    )
            ;
        }

        String answerUrl = taskAnswerStartRequest.getAnswerUrl();

        if (answerUrl.endsWith(".git")) {
            answerUrl = taskAnswerStartRequest
                    .getAnswerUrl()
                    .substring(0, taskAnswerStartRequest.getAnswerUrl().length() - 4)
            ;
        }

        if (answerUrl.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(
                            ANSWER_URL_CANT_BE_NULL_MESSAGE,
                            taskAnswerStartRequest.getTaskId())
                    )
            ;
        } else if (!answerUrl.equals("isRead") && !isValidGitHubUrl(answerUrl)) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(
                            "AnswerUrl is not valid. " +
                                    "The URL must contain only 4 forward slashes. " +
                                    "Example: https://github.com/yourGitHubName/nameOfRepository",
                            taskAnswerStartRequest.getTaskId())
                    )
            ;
        }

        self.submitTaskAnswer(
                taskAnswerStartRequest.getTaskId(), answerUrl,
                taskAnswerStartRequest.getCourseId(), taskAnswerStartRequest.getCourse(),
                taskAnswerStartRequest.getLessonId(), taskAnswerStartRequest.getLessonNum(),
                userService.findById(taskAnswerStartRequest.getUserId()).orElseThrow()
        );

        session.setAttribute(KEY_PARAM_ID, taskAnswerStartRequest.getTaskId());

        return ResponseEntity.ok(
                Map.of(
                        KEY_STATUS, VALUE_STATUS,
                        KEY_MESSAGE, VALUE_TASK_ANSWER_SUBMITTED_SUCCESSFULLY,
                        KEY_TASK_ID, taskAnswerStartRequest.getTaskId(),
                        KEY_ANSWER_URL, answerUrl,
                        KEY_COURSE, taskAnswerStartRequest.getCourse()
                )
        );
    }

    /**
     * Creates a standardized error response with the provided message and task ID.
     *
     * @param message the error message
     * @param id the ID of the parameter
     * @return a Map containing error details
     */
    private Object createErrorResponse(
            String message,
            Long id
    ) {
        if (message == null) {
            message = "An error occurred.";
        }

        if (id == null) {
            id = -1L;
        }

        return Map.of(
                KEY_STATUS, VALUE_FAILED_STATUS,
                KEY_MESSAGE, message,
                KEY_TASK_ID, id,
                KEY_ANSWER_URL, "",
                KEY_COURSE, ""
        );
    }

    /**
     * Retrieves a task answer for a specific user and converts it to a TaskAnswerResponse object.
     *
     * @param taskId the ID of the task
     * @param userId the ID of the user
     * @return TaskAnswerResponse object containing the task answer details
     */
    @Transactional(readOnly = true)
    public TaskAnswerResponse getTaskAnswerAsTaskAnswerResponse(
            Long taskId,
            Long userId
    ) {
        TaskService self = getSelf();
        TaskAnswerDTO taskAnswerDTO = self.getTaskAnswer(taskId, userId);

        return TaskAnswerResponse.fromDTO(taskAnswerDTO);
    }

    /**
     * Retrieves a list of task answers for a specific user and converts them to TaskAnswerResponse objects.
     *
     * @param userId the ID of the user
     * @return a List of TaskAnswerResponse objects
     */
    @Transactional(readOnly = true)
    public List<TaskAnswerResponse> getTaskAnswersByUserId(Long userId) {

        TaskService self = getSelf();
        List<TaskAnswerDTO> taskAnswerDTOS = self.findByUserId(userId);
        List<TaskAnswerResponse> taskAnswerResponses = new ArrayList<>();

        for (TaskAnswerDTO taskAnswerDTO : taskAnswerDTOS) {
            taskAnswerResponses.add(TaskAnswerResponse.fromDTO(taskAnswerDTO));
        }

        return taskAnswerResponses;
    }

    /**
     * Retrieves the task progress for a specific user and course, and converts it to a TaskProgressResponse object.
     *
     * @param userId the ID of the user
     * @param courseId the ID of the course
     * @return TaskProgressResponse object containing the progress details
     */
    @Transactional(readOnly = true)
    public TaskProgressResponse getTaskProgress(
            Long userId,
            Long courseId
    ) {
        TaskService self = getSelf();
        TaskProgressDTO taskProgressDTO = self.getCourseProgress(userId, courseId);

        return TaskProgressResponse.fromDTO(taskProgressDTO);
    }

    /**
     * Retrieves the lesson progress for a specific user and lesson, and converts it to a LessonProgressResponse object.
     *
     * @param userId the ID of the user
     * @param lessonId the ID of the lesson
     * @return LessonProgressResponse object containing the progress details
     */
    @Transactional(readOnly = true)
    public LessonProgressResponse getLessonProgressAdLessonProgressResponse(
            Long userId,
            Long lessonId
    ) {
        TaskService self = getSelf();
        LessonProgressDTO lessonProgressDTO = self.getLessonProgress(userId, lessonId);

        return LessonProgressResponse.fromDTO(lessonProgressDTO);
    }


                // CURRENTLY NOT IN USE

    /**
     * Retrieves all task submissions with pagination support.
     *
     * @param page the page number (0-based index)
     * @param size the size of the page (number of tasks per page)
     * @return a Page of TaskSubmissionDTO objects representing the task submissions
     *
     * INFO     Currently not in use.
     */
    @Transactional(readOnly = true)
    public Page<TaskSubmissionDTO> getAllTaskSubmissions(
            int page,
            int size
    ) {
        Page<Task> taskPage = taskRepository.findAll(PageRequest.of(page, size));

        return getTaskSubmissionDTOS(page, size, taskPage);
    }

    /**
     * Saves a byte array to a file with the specified name.
     * This method is intended for saving ZIP files if needed.
     *
     * @param byteArray the byte array to be saved
     * @param fileName the name of the file
     * @throws IOException if an I/O error occurs
     */
    private void saveByteArrayToFile(
            byte[] byteArray,
            String fileName
    ) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(byteArray);
        }
    }

}

