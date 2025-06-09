package academy.prog.julia.services;

import academy.prog.julia.components.DocsSheets;
import academy.prog.julia.exceptions.TestQuestionsNotFound;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.Task;
import academy.prog.julia.repos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DocsSheetsServiceTest {

    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private TestRepository testRepository;
    @MockBean
    private LessonRepository lessonRepository;
    @MockBean
    private GroupRepository groupRepository;
    @MockBean
    private DocsSheets docsSheets;
    @MockBean
    private TestQuestionFromGoogleDocsRepository testQuestionFromGoogleDocsRepository; // Добавлено
    @InjectMocks
    private DocsSheetsService docsSheetsService;

    private Group group;
    private Lesson lesson1;
    private Task task;
    private academy.prog.julia.model.Test test;
    private String spreadsheetURL = "https://docs.google.com/spreadsheets/d/1CYgjOW_ExqdT8W16rN61CPPf0NEJcRk2GX6MXYzVcdo/edit?gid=0#gid=0";
    private int sheetNumber = 0;;
    private String groupName;
    private List<Lesson> lessonsReaderLessons;

    @Autowired
    public DocsSheetsServiceTest(TaskRepository taskRepository, TestRepository testRepository,
                                 LessonRepository lessonRepository, GroupRepository groupRepository,
                                 DocsSheets docsSheets) {
        this.taskRepository = taskRepository;
        this.testRepository = testRepository;
        this.lessonRepository = lessonRepository;
        this.groupRepository = groupRepository;
        this.docsSheets = docsSheets;
    }

    @BeforeEach
    public void setup() {
        spreadsheetURL = "https://docs.google.com/spreadsheets/d/1CYgjOW_ExqdT8W16rN61CPPf0NEJcRk2GX6MXYzVcdo/edit?gid=0#gid=0";
        sheetNumber = 0;
        groupName = "Po";
        task = new Task();
        task.setName("task1");
        test = new academy.prog.julia.model.Test();
        test.setName("test1");

        lesson1 = new Lesson();
        lesson1.setTasks(Set.of(task));
        lesson1.setTests(Set.of(test));
        lesson1.setId(1L);

        lessonsReaderLessons = new ArrayList<>();
        lessonsReaderLessons.add(lesson1);

        group = new Group();
        group.setName(groupName);
        group.setLessons(new HashSet<>(lessonsReaderLessons));

        lesson1.setGroups(new HashSet<>(Set.of(group)));
    }

    @Test
    @Transactional
    @Rollback
    @DirtiesContext
    public void testLessonsSave() throws GeneralSecurityException, IOException, TestQuestionsNotFound {
        when(docsSheets.lessonReader(spreadsheetURL, sheetNumber)).thenReturn(lessonsReaderLessons);
        when(groupRepository.findByName(groupName)).thenReturn(Optional.of(group));

        List<Lesson> result = docsSheetsService.lessonsSave(spreadsheetURL, sheetNumber, groupName);

        verify(docsSheets, times(1)).lessonReader(spreadsheetURL, sheetNumber);
        verify(taskRepository, times(1)).save(task);
        verify(testRepository, times(1)).save(test);
        verify(lessonRepository, times(1)).saveAll(lessonsReaderLessons);
        verify(groupRepository, times(1)).save(group);

        assertEquals(lessonsReaderLessons, result);
        assertEquals(sheetNumber, lesson1.getSheetNumber());
        assertTrue(group.getLessons().contains(lesson1));
        assertTrue(lesson1.getGroups().contains(group));
    }

    @Test
    @Transactional
    @Rollback
    @DirtiesContext
    public void testFindLessonsBySpreadsheetIDAndNumber() {
        String spreadsheetID = "your_spreadsheet_id";
        Lesson lesson1 = new Lesson();
        lesson1.setName("Введення в Java 3");
        Lesson lesson2 = new Lesson();
        lesson2.setName("Типи даних та змінні3");
        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);

        when(lessonRepository.findBySpreadsheetIDAndSheetNumber(spreadsheetID, sheetNumber)).thenReturn(lessons);

        List<Lesson> foundLessons = docsSheetsService.findLessonsBySpreadsheetIDAndNumber(spreadsheetID, sheetNumber);

        assertEquals(2, foundLessons.size());
        assertEquals("Введення в Java 3", foundLessons.get(0).getName());
        assertEquals("Типи даних та змінні3", foundLessons.get(1).getName());
    }

    @Test
    void testReplaceLesson() throws GeneralSecurityException, IOException, TestQuestionsNotFound {
        // Mock input data
        Group group = new Group();
        group.setName("Test Group");

        Lesson existingLesson = new Lesson();
        existingLesson.setId(1L);
        existingLesson.setName("Existing Lesson");
        existingLesson.setGroups(new HashSet<>(List.of(group)));
        existingLesson.setTasks(new HashSet<>());
        existingLesson.setTests(new HashSet<>());

        Lesson newLesson = new Lesson();
        newLesson.setName("New Lesson");
        newLesson.setTasks(new HashSet<>());
        newLesson.setTests(new HashSet<>());

        String spreadsheetURL = "https://docs.google.com/spreadsheets/d/sheet12345";
        String spreadsheetID = "sheet12345";
        Integer sheetNumber = 1;

        // Mock repository and DocsSheets behavior
        when(docsSheets.lessonReader(spreadsheetURL, sheetNumber)).thenReturn(List.of(newLesson));
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existingLesson));

        // Call the method
        docsSheetsService.replaceLesson(
                List.of(existingLesson),
                spreadsheetURL,
                sheetNumber,
                group
        );

        // Verify repository interactions
        verify(lessonRepository).findById(1L);
        verify(lessonRepository).save(existingLesson);
        verify(docsSheets).lessonReader(spreadsheetURL, sheetNumber);

        // Verify changes to the existing lesson
        assertEquals(sheetNumber, existingLesson.getSheetNumber());
        assertEquals(spreadsheetID, existingLesson.getSpreadsheetID());
        assertTrue(existingLesson.getGroups().contains(group));
        assertEquals("New Lesson", existingLesson.getName());
    }
}
