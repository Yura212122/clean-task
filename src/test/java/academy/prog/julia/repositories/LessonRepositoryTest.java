package academy.prog.julia.repositories;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.LessonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LessonRepositoryTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private GroupRepository groupRepository;

    private static final String TEST_GROUP_NAME = "TestGroupName";
    private static final String TEST_SPREADSHEET_ID = "testSheetId";

    
    @BeforeEach
    public void setUp() {
        Group group = createGroup(TEST_GROUP_NAME);
        createAndSaveLessons(group);
    }



    @Test
    void testFindBySpreadsheetIdAndSheetNumber() {
        List<Lesson> result = lessonRepository.findBySpreadsheetIDAndSheetNumber(TEST_SPREADSHEET_ID, 2);
        assertEquals(2, result.size(), "Expected two lessons for the given spreadsheet ID and sheet number");
    }

    @Test
    void testFindCourseLessons() {
        Optional<Group> group = groupRepository.findByName(TEST_GROUP_NAME);
        assertEquals(true, group.isPresent(), "Group should be present in the repository");

        List<Lesson> result = lessonRepository.findCourseLessons(group.get().getId());
        assertEquals(2, result.size(), "Expected two lessons associated with the group");
    }



    private Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);
        return groupRepository.save(group);
    }

    private void createAndSaveLessons(Group group) {
        Lesson lesson1 = createLesson(TEST_SPREADSHEET_ID, 1);
        Lesson lesson2 = createLesson(TEST_SPREADSHEET_ID, 2);
        Lesson lesson3 = createLesson(TEST_SPREADSHEET_ID, 2);

        lesson1.getGroups().add(group);
        lesson2.getGroups().add(group);

        lessonRepository.save(lesson1);
        lessonRepository.save(lesson2);
        lessonRepository.save(lesson3);

        group.getLessons().add(lesson1);
        group.getLessons().add(lesson2);
        groupRepository.save(group);
    }

    private Lesson createLesson(String spreadsheetId, int sheetNumber) {
        Lesson lesson = new Lesson();
        lesson.setSpreadsheetID(spreadsheetId);
        lesson.setSheetNumber(sheetNumber);
        return lesson;
    }
}
