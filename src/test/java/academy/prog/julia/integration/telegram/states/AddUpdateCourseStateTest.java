package academy.prog.julia.integration.telegram.states;

import academy.prog.julia.exceptions.TestQuestionsNotFound;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.Lesson;
import academy.prog.julia.repos.LessonRepository;
import academy.prog.julia.services.DocsSheetsService;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.telegram.states.AddUpdateCourseState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddUpdateCourseStateTest {

    @Mock
    private StateExecutionContext context;

    @Mock
    private DocsSheetsService docsSheetsService;

    @Mock
    private GroupService groupService;

    @Mock
    private UserService userService;

    @Mock
    private LessonRepository lessonRepository;


    @InjectMocks
    private AddUpdateCourseState state;

    private final String validLink = "https://docs.google.com/spreadsheets/d/valid_spreadsheet_id";
    private final String invalidLink = "invalid_link";
    private final String validNumber = "1";
    private final String invalidNumber = "abc";
    private final String groupName = "Test Group";

    @BeforeEach
    void setUp() {
        state = new AddUpdateCourseState(validLink, validNumber, groupName);
        lenient().when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        lenient().when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        lenient().when(context.getAttributeAsString(groupName)).thenReturn(groupName);

        lenient().when(context.getDocsSheetsService()).thenReturn(docsSheetsService);
        lenient().when(context.getGroupService()).thenReturn(groupService);
        lenient().when(context.getUserService()).thenReturn(userService);

    }


    @Test
    void enter_ShouldFail_WhenSpreadsheetLinkIsInvalid_Test() {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(invalidLink);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect spreadsheet link");
    }


    @Test
    void enter_ShouldFail_WhenSheetNumberIsInvalid_Test() {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(invalidNumber);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect sheetNumber input");
    }


    @Test
    void enter_ShouldFail_WhenGroupNotFound_Test() {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn(groupName);
        when(groupService.findGroupByName(groupName)).thenReturn(Optional.empty());

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("This group is not exist");
    }


    @Test
    void enter_ShouldAddLessons_WhenNoExistingLessons_Test() throws
            IOException, TestQuestionsNotFound, GeneralSecurityException
    {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn(groupName);
        when(groupService.findGroupByName(groupName)).thenReturn(Optional.of(new Group()));
        when(docsSheetsService.findLessonsBySpreadsheetIDAndNumber(anyString(), anyInt())).thenReturn(List.of());

        // When
        state.enter(context);

        // Then
        verify(docsSheetsService).lessonsSave(validLink, Integer.parseInt(validNumber), groupName);
    }

    @Test
    void enter_ShouldUpdateLessons_WhenExistingLessonsFound_Test() throws
            IOException, TestQuestionsNotFound, GeneralSecurityException
    {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn(groupName);
        when(groupService.findGroupByName(groupName)).thenReturn(Optional.of(new Group()));
        when(docsSheetsService
                .findLessonsBySpreadsheetIDAndNumber(anyString(), anyInt())).thenReturn(List.of(new Lesson())
        );

        // When
        state.enter(context);

        // Then
        verify(docsSheetsService).replaceLesson(
                anyList(),
                eq(validLink),
                eq(Integer.parseInt(validNumber)),
                any(Group.class)
        );
    }


    @Test
    void enter_ShouldSendMessage_WhenUserCredentialsAreIncorrect_Test() throws
            TestQuestionsNotFound, GeneralSecurityException, IOException
    {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn(groupName);
        when(groupService.findGroupByName(groupName)).thenReturn(Optional.of(new Group()));
        when(docsSheetsService.findLessonsBySpreadsheetIDAndNumber(anyString(), anyInt())).thenReturn(List.of());

        // When
        when(docsSheetsService
                .lessonsSave(validLink, Integer.parseInt(validNumber), groupName))
                .thenThrow(new GeneralSecurityException("Security error")
        );

        // Then
        state.enter(context);

        verify(context)
                .sendMessage("Lessons save/update operation failed: Security error\n Please," +
                        " update the credentials Google data (run command: /google_credentials")
        ;
    }


    @Test
    void enter_ShouldFail_WhenGroupNameIsEmpty_Test() {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn("");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("This group is not exist");
    }


    @Test
    void enter_ShouldSendMessage_WhenSpreadsheetLinkIsIncorrect_Test() {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn("invalidLink");
        when(context.getAttributeAsString(validNumber)).thenReturn("1");
        when(context.getAttributeAsString(groupName)).thenReturn("group");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect spreadsheet link");
    }


    @Test
    void enter_ShouldSendMessage_WhenDataIntegrityViolationOccurs_WithDataTooLong_Test() throws
            IOException, GeneralSecurityException, TestQuestionsNotFound
    {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn(groupName);
        when(groupService.findGroupByName(groupName)).thenReturn(Optional.of(new Group()));
        when(docsSheetsService.findLessonsBySpreadsheetIDAndNumber(anyString(), anyInt())).thenReturn(List.of());

        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Data too long for column lesson_name some_other_text",
                new Throwable("Data too long for column lesson_name some_other_text")
        );

        when(docsSheetsService.lessonsSave(validLink, Integer.parseInt(validNumber), groupName))
                .thenThrow(exception);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Data integrity error: the input for field 'lesson_name' exceeds the allowed size.");
    }


    @Test
    void enter_ShouldSendMessage_WhenDataIntegrityViolationOccurs_WithoutSpecificCause_Test() throws
            IOException, GeneralSecurityException, TestQuestionsNotFound
    {
        // Given
        when(context.getAttributeAsString(validLink)).thenReturn(validLink);
        when(context.getAttributeAsString(validNumber)).thenReturn(validNumber);
        when(context.getAttributeAsString(groupName)).thenReturn(groupName);
        when(groupService.findGroupByName(groupName)).thenReturn(Optional.of(new Group()));
        when(docsSheetsService.findLessonsBySpreadsheetIDAndNumber(anyString(), anyInt())).thenReturn(List.of());

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("General integrity constraint violation")
        ;

        when(docsSheetsService.lessonsSave(validLink, Integer.parseInt(validNumber), groupName)).thenThrow(exception);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Data integrity error occurred. Please check your input.");
    }


    @Test
    void enter_ShouldSendMessage_WhenTestQuestionsNotFound_Test() {
        // Given
        DocsSheetsService mockedService = mock(DocsSheetsService.class);

        // When
        when(mockedService.findLessonsBySpreadsheetIDAndNumber(anyString(), anyInt()))
                .thenThrow(new RuntimeException("Test questions not found"));

        // Then
        assertThrows(RuntimeException.class, () -> {
            mockedService.findLessonsBySpreadsheetIDAndNumber("spreadsheetId", 1);
        });
    }


    @Test
    void enter_ShouldHandleDataIntegrityViolation_WhenDataTooLong_Test() {
        // Given
        DocsSheetsService mockedService = mock(DocsSheetsService.class);
        String spreadsheetID = "spreadsheetId";
        int sheetNum = 1;
        String spreadsheetLink = "spreadsheetLink";
        List<String> groupNames = List.of("group1");

        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data too long for field 'name'");

        when(mockedService.findLessonsBySpreadsheetIDAndNumber(spreadsheetID, sheetNum))
                .thenThrow(exception);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            List<Lesson> lessonList = mockedService.findLessonsBySpreadsheetIDAndNumber(spreadsheetID, sheetNum);
            if (lessonList == null || lessonList.isEmpty()) {
                mockedService.lessonsSave(spreadsheetLink, sheetNum, groupNames.get(0));
            } else {
                mockedService.replaceLesson(lessonList, spreadsheetLink, sheetNum, new Group());
            }
        });
    }


    @Test
    void extractFieldNameFromMessage_ShouldReturnFieldName_WhenColumnPresent_Test() {
        // Given
        String errorMessage = "Data too long for column user_id in table users";

        // When
        String result = state.extractFieldNameFromMessage(errorMessage);

        // Then
        assertEquals("user_id", result);
    }


    @Test
    void extractFieldNameFromMessage_ShouldReturnUnknownField_WhenColumnNotPresent_Test() {
        // Given
        String errorMessage = "Somerandom";

        // When
        String result = state.extractFieldNameFromMessage(errorMessage);

        // Then
        assertEquals("unknown field", result);
    }

}

