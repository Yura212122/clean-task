package academy.prog.julia.services;

import academy.prog.julia.dto.*;
import academy.prog.julia.exceptions.ResourceNotFoundException;
import academy.prog.julia.json_requests.TaskAnswerStartRequest;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.LessonRepository;
import academy.prog.julia.repos.TaskAnswerRepository;
import academy.prog.julia.repos.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private TaskAnswerRepository taskAnswerRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private GroupService groupService;
    @Mock
    private Environment environment;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private TaskService taskService;

    @Test
    @Transactional(readOnly = true)
    void testGetTaskDetails_returnedTasksDetailedDTO() {
        Task task = getTestTask(1L);

        TaskAnswer answer1 = getTestTaskAnswer(1L);
        TaskAnswer answer2 = getTestTaskAnswer(2L);
        answer1.setTask(task);
        answer2.setTask(task);
        task.setTaskAnswers(Set.of(answer1, answer2));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TasksDetailedDTO expected = taskService.getTaskDetails(1L);
        TasksDetailedDTO actual = TasksDetailedDTO.fromTask(task);

        verify(taskRepository, times(1)).findById(1L);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDeadline(), actual.getDeadline());
        assertEquals(expected.getDescriptionUrl().length(), actual.getDescriptionUrl().length());
        assertEquals(expected.getDescriptionUrl(), actual.getDescriptionUrl());

    }

    @Test
    @Transactional(readOnly = true)
    void testGetTaskDetails_taskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskDetails(1L));
    }

    @Test
    @Transactional(readOnly = true)
    void testSubmitTaskAnswer_whenTaskAnswerNotNull_returnedVoid() {
        Long taskId = 1L;
        Long courseId = 1L;
        Long lessonId = 1L;
        Integer lessonNum = 1;
        User studentId = new User();
        studentId.setId(1L);
        Task task = new Task();
        Optional<Task> optionalTask = Optional.of(task);
        TaskAnswer previousTaskAnswer = new TaskAnswer();

        when(taskRepository.findById(taskId)).thenReturn(optionalTask);
        when(taskAnswerRepository.findByTaskIdAndUserId(taskId, studentId.getId())).thenReturn(previousTaskAnswer);
        when(taskAnswerRepository.save(any(TaskAnswer.class))).thenReturn(previousTaskAnswer);

        taskService.submitTaskAnswer(taskId, "someAnswerUrl", courseId, "someCourse", lessonId, lessonNum, studentId);

        verify(taskAnswerRepository, times(1)).save(previousTaskAnswer);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(taskId, studentId.getId());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @Transactional
    @Rollback
    void testSubmitTaskAnswer_whenTaskAnswerNull_returnedVoid() {
        Long taskId = 1L;
        Long courseId = 1L;
        Long lessonId = 1L;
        Integer lessonNum = 1;
        String answerUrl = "someAnswerUrl";
        String course = "someCourse";
        User student = new User();
        student.setId(1L);
        Task task = new Task();
        Optional<Task> optionalTask = Optional.of(task);
        TaskAnswer taskAnswer = new TaskAnswer(student, task, answerUrl, courseId, lessonId, lessonNum, course, false, false, "", false, new Date());

        when(taskRepository.findById(taskId)).thenReturn(optionalTask);
        when(taskAnswerRepository.findByTaskIdAndUserId(taskId, student.getId())).thenReturn(null);
        when(taskAnswerRepository.save(any(TaskAnswer.class))).thenReturn(taskAnswer);

        taskService.submitTaskAnswer(taskId, answerUrl, courseId, course, lessonId, lessonNum, student);

        verify(taskAnswerRepository, times(1)).save(any(TaskAnswer.class));
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(taskId, student.getId());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @Transactional
    @Rollback
    void testSubmitTaskAnswer_whenTaskNotFound() {
        Long taskId = 1L;
        User user = getTestUser(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> taskService.submitTaskAnswer(taskId, null, null, null, null, null, user));

        assertEquals("Task with id " + taskId + " not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindByUserId_whenTaskAnswersPresented() {
        TaskAnswer taskAnswer1 = getTestTaskAnswer(1L);
        TaskAnswer taskAnswer2 = getTestTaskAnswer(2L);
        Task testTask = getTestTask(1L);
        taskAnswer1.setTask(testTask);
        taskAnswer2.setTask(testTask);

        List<TaskAnswer> taskAnswers = List.of(taskAnswer1, taskAnswer2);

        when(taskAnswerRepository.findByUserId(1L)).thenReturn(taskAnswers);

        List<TaskAnswerDTO> actual = taskService.findByUserId(1L);

        assertEquals(actual.size(), taskAnswers.size());
        verify(taskAnswerRepository, times(1)).findByUserId(1L);
    }

    @Test
    @Transactional
    @Rollback
    void testFindByUserId_whenTasksNotFound() {
        when(taskAnswerRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<TaskAnswerDTO> actual = taskService.findByUserId(1L);
        assertEquals(0, actual.size());
        verify(taskAnswerRepository, times(1)).findByUserId(1L);
    }


    @Test
    @Transactional(readOnly = true)
    void testFindPaginated_returnedListTasksGroupResponseDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TaskAnswer> taskAnswers = new ArrayList<>();
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        taskAnswers.add(taskAnswer);
        Page<TaskAnswer> pageTaskAnswer = new PageImpl<>(taskAnswers, pageable, 1);

        when(taskService.getSelf()).thenReturn(taskService);
        doReturn(pageTaskAnswer).when(taskService).findPendingTask(pageable);

        List<TasksGroupResponseDTO> result = taskService.findPaginated(pageable, null);


        verify(taskService, times(1)).findPendingTask(pageable);
        assertFalse(result.isEmpty());
    }

    @Test
    @Transactional(readOnly = true)
    void testFindPaginated_throwResourceNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskAnswer> emptyPage = Page.empty();

        when(taskService.findPendingTask(pageable)).thenReturn(emptyPage);
        when(taskService.getSelf()).thenReturn(taskService);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.findPaginated(pageable, null));

        assertEquals("No tasks found", exception.getMessage());
    }

    @Test
    @Transactional
    @Rollback
    void testMapToTaskAnswerDto_whenTaskExist() {
        Task task = getTestTask(1L);
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        taskAnswer.setTask(task);
        when(taskRepository.findById(taskAnswer.getTask().getId())).thenReturn(Optional.of(task));

        TaskAnswerDTO actual = taskService.mapToTaskAnswerDTO(taskAnswer);
        assertEquals(actual.getAnswerId(), taskAnswer.getId());
        assertEquals(actual.getTaskId(), task.getId());
        verify(taskRepository, times(1)).findById(task.getId());
    }

    @Test
    void testMapToTaskDto_whenTaskAnswerNull() {
        TaskAnswerDTO actual = taskService.mapToTaskAnswerDTO(null);
        assertNull(actual);
        verify(taskRepository, times(0)).findById(anyLong());
    }

    @Test
    @Transactional
    @Rollback
    void testMapToTaskDto_whenTaskNotFound() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        taskAnswer.setTask(getTestTask(1L));
        when(taskRepository.findById(taskAnswer.getId())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> taskService.mapToTaskAnswerDTO(taskAnswer));
        assertEquals("Task with id " + taskAnswer.getId() + " not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskAnswer.getId());
    }

    @Test
    @Transactional(readOnly = true)
    void testGetTaskSubmission_returnedTaskAnswerDTO() {
        TaskAnswer actual = new TaskAnswer();
        actual.setId(1L);
        when(taskAnswerRepository.findById(1L)).thenReturn(Optional.of(actual));

        TaskAnswerDTO expected = taskService.getTaskSubmission(1L);

        verify(taskAnswerRepository, times(1)).findById(1L);
        Assertions.assertThat(TaskAnswerDTO.fromTaskAnswer(actual)).isEqualTo(expected);
    }

    @Test
    @Transactional(readOnly = true)
    void testGetTaskSubmission_taskAnswerNotFound() {
        when(taskAnswerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskSubmission(1L));
        assertEquals("TaskAnswer with id " + 1L + " not found", exception.getMessage());
        verify(taskAnswerRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    void testGradeTaskSubmission_returnedVoid() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        taskAnswer.setIsCorrection(false);
        taskAnswer.setIsPassed(true);
        when(taskAnswerRepository.findById(1L)).thenReturn(Optional.of(taskAnswer));
        when(taskAnswerRepository.save(taskAnswer)).thenReturn(taskAnswer);
        when(environment.getProperty("github.automatic-download-zip.enabled", Boolean.class, false)).thenReturn(false);
        taskService.gradeTaskSubmission(1L, false, true, "", false);

        verify(taskAnswerRepository, times(1)).findById(1L);
        verify(taskAnswerRepository, times(1)).save(taskAnswer);
        verify(environment, times(1)).getProperty("github.automatic-download-zip.enabled", Boolean.class, false);
    }

    @Test
    @Transactional(readOnly = true)
    void testGradeTaskSubmission_taskNotFound() {
        when(taskAnswerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.gradeTaskSubmission(1L, false, true, "", false));
        verify(taskAnswerRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    void testIsStudentInTaskGroup_whenOptionalEmpty() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        boolean actual = taskService.isStudentInTaskGroup(1L, 1L);

        assertFalse(actual);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    void testIsStudentInTaskGroup_whenGroupsEmpty() {
        Task testTask = getTestTask(1L);
        Lesson lesson = new Lesson();
        lesson.setGroups(Set.of());
        testTask.setLesson(lesson);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        boolean actual = taskService.isStudentInTaskGroup(1L, 1L);

        verify(taskRepository, times(1)).findById(1L);
        assertFalse(actual);
    }

    @Test
    @Transactional(readOnly = true)
    void testIsStudentInTaskGroup_whenGroupsNotEmpty() {
        Task testTask = getTestTask(1L);
        Lesson lesson = new Lesson();
        Group group = new Group();
        group.setId(1L);
        lesson.setGroups(Set.of(group));
        testTask.setLesson(lesson);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(groupService.isStudentInGroup(1L, 1L)).thenReturn(true);

        boolean actual = taskService.isStudentInTaskGroup(1L, 1L);


        verify(taskRepository, times(1)).findById(1L);
        verify(groupService, times(1)).isStudentInGroup(1L, group.getId());
        assertTrue(actual);
    }

    @Test
    @Transactional
    @Rollback
    void testGetCourseProgress_whenCourseNotFound() {
        Long courseId = 1L;
        when(groupRepository.existsById(courseId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> taskService.getCourseProgress(1L, courseId));
        assertEquals("Courses not found with id: " + courseId, exception.getMessage());
        verify(groupRepository, times(1)).existsById(courseId);
    }

    @Test
    @Transactional
    @Rollback
    void testGetCourseProgress_whenLessonsEmpty() {
        Long courseId = 1L;
        Group testGroup = new Group();
        testGroup.setId(1L);

        when(groupRepository.existsById(courseId)).thenReturn(true);
        when(lessonRepository.findCourseLessons(courseId)).thenReturn(Collections.emptyList());

        TaskProgressDTO expected = new TaskProgressDTO(courseId, 0);
        TaskProgressDTO actual = taskService.getCourseProgress(1L, courseId);

        Assertions.assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
        verify(groupRepository, times(1)).existsById(courseId);
        verify(lessonRepository, times(1)).findCourseLessons(courseId);
    }

    @Test
    @Transactional
    @Rollback
    void testGetCourseProgress_whenLessonsPresented() {
        Long courseId = 1L;
        Group testGroup = new Group();
        testGroup.setId(1L);
        Lesson lesson1 = getTestLesson(1L);
        Lesson lesson2 = getTestLesson(2L);
        Task testTask = getTestTask(1L);
        Task testTask2 = getTestTask(2L);
        lesson1.setTasks(Set.of(testTask));
        lesson2.setTasks(Set.of(testTask2));
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        taskAnswer.setIsPassed(true);
        TaskAnswer taskAnswer2 = getTestTaskAnswer(2L);
        taskAnswer2.setIsPassed(false);
        taskAnswer.setTask(testTask);
        taskAnswer2.setTask(testTask2);


        when(groupRepository.existsById(courseId)).thenReturn(true);
        when(lessonRepository.findCourseLessons(courseId)).thenReturn(List.of(lesson1, lesson2));
        when(lessonRepository.findById(lesson1.getId())).thenReturn(Optional.of(lesson1));
        when(lessonRepository.findById(lesson2.getId())).thenReturn(Optional.of(lesson2));
        when(taskAnswerRepository.findByTaskIdAndUserId(testTask.getId(), 1L)).thenReturn(taskAnswer);
        when(taskAnswerRepository.findByTaskIdAndUserId(testTask2.getId(), 1L)).thenReturn(taskAnswer2);

        TaskProgressDTO expected = new TaskProgressDTO(courseId, 50);
        TaskProgressDTO actual = taskService.getCourseProgress(1L, courseId);

        Assertions.assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
        verify(groupRepository, times(1)).existsById(courseId);
        verify(lessonRepository, times(1)).findCourseLessons(courseId);
        verify(lessonRepository, times(1)).findById(lesson1.getId());
        verify(lessonRepository, times(1)).findById(lesson2.getId());
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(testTask.getId(), 1L);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(testTask2.getId(), 1L);
    }

    @Test
    @Transactional
    @Rollback
    void testGetLessonProgress_whenLessonEmpty() {
        Long lessonId = 1L;
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> taskService.getLessonProgress(1L, lessonId));
        assertEquals("Lesson not found with id: " + lessonId, exception.getMessage());
        verify(lessonRepository, times(1)).findById(lessonId);
    }

    @Test
    @Transactional
    @Rollback
    void testGetLessonProgress_whenLessonPresented() {
        Lesson lesson = getTestLesson(1L);
        Task testTask = getTestTask(1L);
        Task testTask2 = getTestTask(2L);
        lesson.setTasks(Set.of(testTask, testTask2));

        TaskAnswer taskAnswer = getTestTaskAnswer(testTask.getId());
        TaskAnswer taskAnswer2 = getTestTaskAnswer(testTask2.getId());
        taskAnswer.setIsPassed(true);
        taskAnswer2.setIsPassed(false);
        taskAnswer2.setIsCorrection(true);

        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));
        when(taskAnswerRepository.findByTaskIdAndUserId(testTask.getId(), 1L)).thenReturn(taskAnswer);
        when(taskAnswerRepository.findByTaskIdAndUserId(testTask2.getId(), 1L)).thenReturn(taskAnswer2);

        LessonProgressDTO expected = new LessonProgressDTO(lesson.getId(), "Need correction", 50);
        LessonProgressDTO actual = taskService.getLessonProgress(1L, lesson.getId());

        Assertions.assertThat(expected).usingRecursiveComparison().isEqualTo(actual);

        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(testTask.getId(), 1L);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(testTask2.getId(), 1L);
    }

    @Test
    @Transactional
    @Rollback
    void testFilterTasksForNotifyToEndDeadlineDate() {
        User user = getTestUser(1L);
        Task testTask1 = getTestTask(1L);
        Task testTask2 = getTestTask(2L);
        TaskAnswer taskAnswer1 = getTestTaskAnswer(testTask1.getId());
        taskAnswer1.setIsPassed(false);
        taskAnswer1.setIsCorrection(true);
        List<Task> tasks = List.of(testTask1, testTask2);

        when(taskAnswerRepository.findByTaskIdAndUserId(testTask1.getId(), 1L)).thenReturn(taskAnswer1);
        when(taskAnswerRepository.findByTaskIdAndUserId(testTask2.getId(), 1L)).thenReturn(null);
        Set<Task> actual = taskService.filterTasksForNotifyToEndDeadLineDate(user, tasks);
        Set<Task> expected = new HashSet<>(tasks);

        Assertions.assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(testTask1.getId(), 1L);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(testTask2.getId(), 1L);
    }

    @Test
    @Transactional
    @Rollback
    void testGenerateAndSaveTaskAnswerZip_whenResourceNotFound() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);

        when(restTemplate.getForObject(taskAnswer.getAnswerUrl() + "/archive/master.zip", byte[].class)).thenThrow(HttpClientErrorException.NotFound.class);
        doReturn(true).when(taskService).isValidGitHubUrl(taskAnswer.getAnswerUrl());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.generateAndSaveTaskAnswerZip(taskAnswer));
        assertEquals("Repository NOT FOUND or it is NOT PUBLIC: " + taskAnswer.getAnswerUrl(), exception.getMessage());
    }

    @Test
    void testIsValidGithubUrl() {
        assertTrue(taskService.isValidGitHubUrl("https://github.com/example1/exampleRepository"));
        assertFalse(taskService.isValidGitHubUrl("https://example.com"));
    }

    @Test
    void testSubmitTaskAnswerWithChecks_whenUnauthorized() {
        TaskAnswerStartRequest taskAnswerStartRequest = new TaskAnswerStartRequest();
        taskAnswerStartRequest.setTaskId(1L);
        MockHttpSession session = new MockHttpSession();
        ResponseEntity<Object> actual = taskService.submitTaskAnswerWithChecks(taskAnswerStartRequest, session);
        assertNotNull(actual);
        assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testSubmitTaskAnswerWithChecks_whenPreviousTaskAnswerNonNull() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        TaskAnswerStartRequest taskAnswerStartRequest = getTaskAnswerStartRequest(taskAnswer);
        taskAnswerStartRequest.setAnswerUrl("isRead");
        MockHttpSession session = new MockHttpSession();

        var expected = Map.of(
                "status", "success",
                "message", "Task answer status is changed successfully",
                "taskId", taskAnswerStartRequest.getTaskId(),
                "answerUrl", taskAnswerStartRequest.getAnswerUrl(),
                "course", taskAnswerStartRequest.getCourse()
        );

        when(taskAnswerRepository.findByTaskIdAndUserId(taskAnswer.getId(), taskAnswer.getUser().getId())).thenReturn(taskAnswer);

        ResponseEntity<Object> actual = taskService.submitTaskAnswerWithChecks(taskAnswerStartRequest, session);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertThat(actual.getBody()).usingRecursiveComparison().isEqualTo(expected);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(taskAnswer.getId(), taskAnswer.getUser().getId());
    }

    @Test
    @Transactional
    @Rollback
    void testSubmitTaskAnswerWithChecks_whenPreviousTaskAnswerIsNull() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        Task testTask = getTestTask(1L);
        taskAnswer.setTask(testTask);
        TaskAnswerStartRequest taskAnswerStartRequest = getTaskAnswerStartRequest(taskAnswer);
        taskAnswerStartRequest.setAnswerUrl("isRead");
        MockHttpSession session = new MockHttpSession();

        var expected = Map.of(
                "status", "success",
                "message", "Task answer doesn't exist yet",
                "taskId", taskAnswerStartRequest.getTaskId(),
                "answerUrl", taskAnswerStartRequest.getAnswerUrl(),
                "course", taskAnswerStartRequest.getCourse()
        );

        when(taskAnswerRepository.findByTaskIdAndUserId(taskAnswer.getId(), taskAnswer.getUser().getId())).thenReturn(null);

        ResponseEntity<Object> actual = taskService.submitTaskAnswerWithChecks(taskAnswerStartRequest, session);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertThat(actual.getBody()).usingRecursiveComparison().isEqualTo(expected);
        verify(taskAnswerRepository, times(1)).findByTaskIdAndUserId(taskAnswer.getId(), taskAnswer.getUser().getId());
    }

    @Test
    @Transactional
    @Rollback
    void testSubmitTaskAnswerWithChecks_whenStudentNotInTaskGroup() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        TaskAnswerStartRequest taskAnswerStartRequest = getTaskAnswerStartRequest(taskAnswer);
        MockHttpSession session = new MockHttpSession();

        when(taskService.getSelf()).thenReturn(taskService);

        ResponseEntity<Object> actual = taskService.submitTaskAnswerWithChecks(taskAnswerStartRequest, session);
        assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void testSubmitTaskAnswerWithChecks_whenSubmittedSuccessfully() {
        TaskAnswer taskAnswer = getTestTaskAnswer(1L);
        TaskAnswerStartRequest taskAnswerStartRequest = getTaskAnswerStartRequest(taskAnswer);
        MockHttpSession session = new MockHttpSession();

        var expected = Map.of(
                "status", "success",
                "message", "Task answer submitted successfully",
                "taskId", taskAnswerStartRequest.getTaskId(),
                "answerUrl", taskAnswerStartRequest.getAnswerUrl(),
                "course", taskAnswerStartRequest.getCourse()
        );
        when(taskService.getSelf()).thenReturn(taskService);
        doReturn(true).when(taskService).isStudentInTaskGroup(taskAnswerStartRequest.getUserId(), taskAnswerStartRequest.getTaskId());
        doNothing().when(taskService).submitTaskAnswer(
                taskAnswerStartRequest.getTaskId(), taskAnswerStartRequest.getAnswerUrl(),
                taskAnswerStartRequest.getCourseId(), taskAnswerStartRequest.getCourse(),
                taskAnswerStartRequest.getLessonId(), taskAnswerStartRequest.getLessonNum(), taskAnswer.getUser());

        when(userService.findById(taskAnswerStartRequest.getUserId())).thenReturn(Optional.of(taskAnswer.getUser()));

        ResponseEntity<Object> actual = taskService.submitTaskAnswerWithChecks(taskAnswerStartRequest, session);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertThat(actual.getBody()).usingRecursiveComparison().isEqualTo(expected);
        verify(taskService, times(1)).isStudentInTaskGroup(taskAnswerStartRequest.getUserId(), taskAnswerStartRequest.getTaskId());
        verify(userService, times(1)).findById(taskAnswerStartRequest.getUserId());
    }

    private static TaskAnswerStartRequest getTaskAnswerStartRequest(TaskAnswer taskAnswer) {
        TaskAnswerStartRequest taskAnswerStartRequest = new TaskAnswerStartRequest();
        taskAnswerStartRequest.setTaskId(taskAnswer.getId());
        taskAnswerStartRequest.setUserId(taskAnswer.getUser().getId());
        taskAnswerStartRequest.setAnswerUrl("https://github.com/yourGitHubName/nameOfRepository");
        taskAnswerStartRequest.setCourse(taskAnswer.getCourse());
        taskAnswerStartRequest.setCourseId(taskAnswer.getCourseId());
        taskAnswerStartRequest.setLessonId(1L);
        taskAnswerStartRequest.setLessonNum(1);
        return taskAnswerStartRequest;
    }

    @Test
    @Transactional(readOnly = true)
    @Rollback
    void testGetAllTaskSubmissions_returnedPageTaskSubmissionDTOByTeacherId() {
        int page = 0;
        int size = 2;
        Group group1 = new Group();
        group1.setName("mock1");
        Lesson lesson1 = getTestLesson(1L);
        lesson1.setGroups(Set.of(group1));
        Task task1 = getTestTask(1L);
        task1.setActive(true);
        task1.setLesson(lesson1);
        TaskAnswer taskAnswer1 = getTestTaskAnswer(1L);
        task1.setTaskAnswers(Set.of(taskAnswer1));
        taskAnswer1.setTask(task1);

        Lesson lesson2 = getTestLesson(2L);
        lesson2.setGroups(Set.of(group1));
        Task task2 = getTestTask(2L);
        task2.setActive(true);
        task2.setLesson(lesson2);
        TaskAnswer taskAnswer2 = getTestTaskAnswer(2L);
        task2.setTaskAnswers(Set.of(taskAnswer2));
        taskAnswer2.setTask(task2);

        Page<Task> fakeTaskPage = new PageImpl<>(List.of(task1, task2));

        when(taskRepository.findById(taskAnswer1.getTask().getId())).thenReturn(Optional.of(task1));
        when(taskRepository.findById(taskAnswer2.getTask().getId())).thenReturn(Optional.of(task2));
        when(taskRepository.findAll(PageRequest.of(page, size))).thenReturn(fakeTaskPage);

        Page<TaskSubmissionDTO> result = taskService.getAllTaskSubmissions(page, size);

        verify(taskRepository, times(1)).findAll(PageRequest.of(page, size));
        assertInstanceOf(Page.class, result);

        assertEquals(result.getTotalElements(), fakeTaskPage.getTotalElements());
    }

    private Lesson getTestLesson(Long id) {
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setName("TestLesson " + id);
        return lesson;
    }

    private static Task getTestTask(Long id) {
        Task task = new Task();
        task.setId(id);
        task.setName("Sample Task Name " + id);
        task.setDescriptionUrl("https://example.com/description");
        task.setDeadline(LocalDate.now().plusDays(7));
        task.setActive(true);
        return task;
    }

    private static TaskAnswer getTestTaskAnswer(Long id) {
        TaskAnswer answer = new TaskAnswer();
        User user = getTestUser(id);
        answer.setId(id);
        answer.setUser(user);
        answer.setAnswerUrl("https://example.com/answer" + id);
        answer.setSubmittedDate(new Date());
        answer.setCourseId(id);
        answer.setIsPassed(false);
        answer.setIsCorrection(false);
        answer.setMessageForCorrection("");
        answer.setIsRead(false);
        answer.setCourse("Test Course");
        answer.setLessonId(id);
        answer.setLessonNum(1);
        return answer;
    }

    static User getTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Sample User Name " + id);
        user.setEmail("sample@gmail.com");
        user.setPhone("123456789");
        user.setSurname("Sample Surname " + id);
        return user;
    }


}
