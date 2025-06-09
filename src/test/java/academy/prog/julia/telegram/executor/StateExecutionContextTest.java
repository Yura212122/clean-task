package academy.prog.julia.telegram.executor;

import academy.prog.julia.model.User;
import academy.prog.julia.services.*;
import academy.prog.julia.telegram.MainBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StateExecutionContextTest {

    private MainBot mainBotMock;
    private UserService userServiceMock;
    private CertificateService certificateServiceMock;
    private GroupService groupServiceMock;
    private DocsSheetsService docsSheetsServiceMock;
    private TaskService taskServiceMock;
    private User userMock;
    private Update updateMock;

    private StateExecutionContext context;

    @BeforeEach
    void setUp() {
        mainBotMock = mock(MainBot.class);
        userServiceMock = mock(UserService.class);
        certificateServiceMock = mock(CertificateService.class);
        groupServiceMock = mock(GroupService.class);
        docsSheetsServiceMock = mock(DocsSheetsService.class);
        taskServiceMock = mock(TaskService.class);
        userMock = mock(User.class);
        updateMock = mock(Update.class);

        context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );
    }


    @Test
    void testConstructorInitialization_Positive() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertEquals(12345L, context.getChatId());
        assertEquals("test message", context.getMessage());
        assertEquals(userMock, context.getUser());
        assertEquals(updateMock, context.getUpdate());
    }


    @Test
    void testSendMessage_Positive() {
        context.sendMessage("Hello, World!");
        verify(mainBotMock).sendMessage(12345L, "Hello, World!");
    }


    @Test
    void testSendMessageWithChatId_Positive() {
        context.sendMessage(67890L, "Hello, Another Chat!");
        verify(mainBotMock).sendMessage(67890L, "Hello, Another Chat!");
    }


    @Test
    void testSendMessageWithStringChatId_Positive() {
        context.sendMessage("67890", "Hello, Another Chat!");
        verify(mainBotMock).sendMessage(67890L, "Hello, Another Chat!");
    }

    @Test
    void testSendMessageWithStringChatId_Negative_InvalidChatId() {
        assertThrows(NumberFormatException.class, () -> {
            context.sendMessage("invalidChatId", "Invalid Chat ID");
        });
    }


    @Test
    void testGetArgCount_Positive() {
        assertEquals(2, context.getArgCount());
    }

    @Test
    void testGetArgCount_Negative_NoArgs() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{},
                updateMock
        );

        assertEquals(-1, context.getArgCount());
    }


    @Test
    void testGetCommand_Positive() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertEquals("command", context.getCommand());
    }

    @Test
    void testGetCommand_Negative_NoArgs() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{},
                updateMock
        );

        assertNull(context.getCommand());
    }

    @Test
    void testGetCommand_Negative_NullArgs() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                null,
                updateMock
        );

        assertNull(context.getCommand());
    }

    @Test
    void testGetCommand_Negative_EmptyArgs() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{},
                updateMock
        );

        assertNull(context.getCommand());
    }

    @Test
    void testGetCommand_Negative_NullFirstArg() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{null, "arg1", "arg2"},
                updateMock
        );

        assertNull(context.getCommand());
    }


    @Test
    void testGetArg_Positive() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertEquals("arg1", context.getArg(1));
    }

    @Test
    void testGetArg_Negative_NullArgs() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                null,
                updateMock
        );

        assertThrows(IndexOutOfBoundsException.class, () -> {
            context.getArg(0);
        });
    }

    @Test
    void testGetArg_Negative_InvalidIndex_LessThanZero() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertThrows(IndexOutOfBoundsException.class, () -> {
            context.getArg(-1);
        });
    }

    @Test
    void testGetArg_Negative_InvalidIndex_GreaterThanLength() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertThrows(IndexOutOfBoundsException.class, () -> {
            context.getArg(10);
        });
    }

    @Test
    void testGetArg_Positive_ValidIndex() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertEquals("arg1", context.getArg(1));
    }

    @Test
    void testGetArg_Positive_SingleElementArray() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command"},
                updateMock
        );

        assertEquals("command", context.getArg(0));
    }

    @Test
    void testGetArg_Negative_InvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            context.getArg(10);
        });
    }

    @Test
    void testGetArg_Negative_NullElement() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                userServiceMock,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", null, "arg2"},
                updateMock
        );

        assertNull(context.getArg(1));
    }


    @Test
    void testAttributes_Positive() {
        ActiveCommand activeCommandMock = mock(ActiveCommand.class);
        Map<String, Object> attributes = new HashMap<>();
        when(activeCommandMock.getAttributes()).thenReturn(attributes);

        context.setActiveCommand(activeCommandMock);

        context.putAttribute("key1", "value1");
        context.putAttribute("key2", 123);

        assertEquals("value1", context.getAttributeAsString("key1"));
        assertEquals(123, context.getAttributeAsInt("key2"));
        assertEquals("value1", context.getAttribute("key1"));
        assertEquals(Optional.of(123), Optional.ofNullable(context.getAttribute("key2")));
    }

    @Test
    void testAttributes_Negative_InvalidKey() {
        ActiveCommand activeCommandMock = mock(ActiveCommand.class);
        Map<String, Object> attributes = new HashMap<>();
        when(activeCommandMock.getAttributes()).thenReturn(attributes);

        context.setActiveCommand(activeCommandMock);

        context.putAttribute("key1", "value1");

        assertNull(context.getAttributeAsString("nonExistentKey"));
        assertNull(context.getAttributeAsInt("nonExistentKey"));
        assertNull(context.getAttribute("nonExistentKey"));
    }


    @Test
    void testGetServices_Positive() {
        assertEquals(userServiceMock, context.getUserService());
        assertEquals(certificateServiceMock, context.getCertificateService());
        assertEquals(groupServiceMock, context.getGroupService());
        assertEquals(docsSheetsServiceMock, context.getDocsSheetsService());
        assertEquals(taskServiceMock, context.getTaskService());
    }

    @Test
    void testGetServices_Negative_NullServices() {
        StateExecutionContext context = new StateExecutionContext(
                mainBotMock,
                null,
                certificateServiceMock,
                groupServiceMock,
                docsSheetsServiceMock,
                taskServiceMock,
                userMock,
                12345L,
                "test message",
                new String[]{"command", "arg1", "arg2"},
                updateMock
        );

        assertNull(context.getUserService());
    }


    @Test
    void testActiveCommandAndRegistry_Positive() {
        ActiveCommand activeCommandMock = mock(ActiveCommand.class);
        CommandRegistry commandRegistryMock = mock(CommandRegistry.class);

        context.setActiveCommand(activeCommandMock);
        context.setCommandRegistry(commandRegistryMock);

        assertEquals(activeCommandMock, context.getActiveCommand());
        assertEquals(commandRegistryMock, context.getCommandRegistry());
    }

    @Test
    void testActiveCommandAndRegistry_Negative_NullValues() {
        context.setActiveCommand(null);
        context.setCommandRegistry(null);

        assertNull(context.getActiveCommand());
        assertNull(context.getCommandRegistry());
    }


    @Test
    void testIsFinished_Positive() {
        assertFalse(context.isFinished());
    }

    @Test
    void testIsFinished_Negative_AlreadyFinished() {
        context.setFinished(true);
        assertTrue(context.isFinished());
    }


    @Test
    void testSetFinished_Positive() {
        context.setFinished(true);
        assertTrue(context.isFinished());

        context.setFinished(false);
        assertFalse(context.isFinished());
    }


    @Test
    void testSendTextWithMarkup_Positive() throws TelegramApiException {
        ReplyKeyboardMarkup replyKeyboardMarkupMock = mock(ReplyKeyboardMarkup.class);

        context.sendTextWithMarkup(
                "Test message",
                "https://example.com",
                "Markdown",
                replyKeyboardMarkupMock
        );

        verify(mainBotMock).execute(any(SendMessage.class));
    }

    @Test
    void testSendTextWithMarkup_Negative_TelegramApiException() throws TelegramApiException {
        doThrow(new TelegramApiException("Simulated exception")).when(mainBotMock).execute(any(SendMessage.class));

        assertDoesNotThrow(() -> {
            context.sendTextWithMarkup(
                    "Test message",
                    "https://example.com",
                    "Markdown",
                    null
            );
        });

        verify(mainBotMock).execute(any(SendMessage.class));
    }

    @Test
    void testSendTextWithMarkup_Negative_NullParameters() {
        assertThrows(NullPointerException.class, () -> {
            context.sendTextWithMarkup(
                    null,
                    "https://example.com",
                    "Markdown",
                    null
            );
        });
    }

    @Test
    void testSendTextWithMarkup_WithoutReplyKeyboard() throws TelegramApiException {
        context.sendTextWithMarkup(
                "Test message",
                "https://example.com",
                "Markdown",
                null
        );

        verify(mainBotMock).execute(any(SendMessage.class));
    }

}