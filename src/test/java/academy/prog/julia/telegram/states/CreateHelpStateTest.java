package academy.prog.julia.telegram.states;

import static org.junit.jupiter.api.Assertions.*;

import academy.prog.julia.model.User;
import academy.prog.julia.telegram.executor.CommandRegistry;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.telegram.commands.Command;
import academy.prog.julia.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;


class CreateHelpStateTest {

    private CreateHelpState state;

    @Mock
    private StateExecutionContext context;

    @Mock
    private CommandRegistry commandRegistry;

    @Mock
    private Command command1, command2, restrictedCommand;

    @Mock
    private User user;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new CreateHelpState("helpResult");

        when(context.getCommandRegistry()).thenReturn(commandRegistry);
        when(context.getUser()).thenReturn(user);

        when(command1.getName()).thenReturn("start");
        when(command1.getDescription()).thenReturn("Starts the bot");
        when(command1.getAllowedRoles()).thenReturn(Collections.emptySet());

        when(command2.getName()).thenReturn("help");
        when(command2.getDescription()).thenReturn("Shows this help");
        when(command2.getAllowedRoles()).thenReturn(Collections.emptySet());

        when(restrictedCommand.getName()).thenReturn("admin");
        when(restrictedCommand.getDescription()).thenReturn("Admin command");
        when(restrictedCommand.getAllowedRoles()).thenReturn(Set.of(UserRole.ADMIN));
    }


    @Test
    void testEnter_AllCommandsForRegularUser() {
        // Given
        when(commandRegistry.getAll()).thenReturn(List.of(command1, command2, restrictedCommand));
        when(context.getUser().getRole()).thenReturn(UserRole.STUDENT);

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        String result = captor.getValue();

        assertAll(
                () -> assertTrue(result.contains("start: Starts the bot")),
                () -> assertTrue(result.contains("help: Shows this help")),
                () -> assertFalse(result.contains("admin: Admin command"))
        );
    }


    @Test
    void testEnter_AllCommandsForAdminUser() {
        // Given
        when(commandRegistry.getAll()).thenReturn(List.of(command1, command2, restrictedCommand));
        when(context.getUser().getRole()).thenReturn(UserRole.ADMIN);

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        String result = captor.getValue();

        assertAll(
                () -> assertTrue(result.contains("start: Starts the bot")),
                () -> assertTrue(result.contains("help: Shows this help")),
                () -> assertTrue(result.contains("admin: Admin command"))
        );
    }


    @Test
    void testEnter_WrongCommandsForStudentUser() {
        // Given
        when(context.getUser().getRole()).thenReturn(UserRole.ADMIN);

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        String result = captor.getValue();
        assertFalse(result.contains("admin: Admin command"));
    }


    @Test
    void testEnter_NoCommandsAvailable() {
        // Given
        when(context.getCommandRegistry().getAll()).thenReturn(Collections.emptyList());

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        assertEquals("", captor.getValue());
    }


    @Test
    void testEnter_CommandWithEmptyDescription() {
        // Given
        Command emptyDescCommand = mock(Command.class);
        when(emptyDescCommand.getName()).thenReturn("empty");
        when(emptyDescCommand.getDescription()).thenReturn("");
        when(emptyDescCommand.getAllowedRoles()).thenReturn(Collections.emptySet());

        when(context.getCommandRegistry().getAll()).thenReturn(List.of(emptyDescCommand));

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        assertEquals("empty: \r\n", captor.getValue());
    }


    @Test
    void testEnter_CommandWithNullDescription() {
        // Given
        Command nullDescCommand = mock(Command.class);
        when(nullDescCommand.getName()).thenReturn("nullcmd");
        when(nullDescCommand.getDescription()).thenReturn(null);
        when(nullDescCommand.getAllowedRoles()).thenReturn(Collections.emptySet());

        when(context.getCommandRegistry().getAll()).thenReturn(List.of(nullDescCommand));

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        assertEquals("nullcmd: null\r\n", captor.getValue());
    }


    @Test
    void testEnter_NullContext() {
        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(null));
    }


    @Test
    void testEnter_NullCommandRegistry() {
        // Given
        when(context.getCommandRegistry()).thenReturn(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(context));
    }


    @Test
    void testEnter_CommandWithSpecialCharacters() {
        // Given
        Command specialCommand = mock(Command.class);
        when(specialCommand.getName()).thenReturn("cmd@123");
        when(specialCommand.getDescription()).thenReturn("Description with spéciäl chäråcters");
        when(specialCommand.getAllowedRoles()).thenReturn(Collections.emptySet());

        when(context.getCommandRegistry().getAll()).thenReturn(List.of(specialCommand));

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        assertEquals("cmd@123: Description with spéciäl chäråcters\r\n", captor.getValue());
    }


    @Test
    void testEnter_MultipleRolesRestriction() {
        // Given
        Command multiRoleCommand = mock(Command.class);
        when(multiRoleCommand.getName()).thenReturn("multirole");
        when(multiRoleCommand.getDescription()).thenReturn("Multiple roles");
        when(multiRoleCommand.getAllowedRoles()).thenReturn(Set.of(UserRole.ADMIN, UserRole.MENTOR));

        when(context.getCommandRegistry().getAll()).thenReturn(List.of(multiRoleCommand));

        // Test for user with one of required roles
        when(context.getUser().getRole()).thenReturn(UserRole.MENTOR);

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        assertTrue(captor.getValue().contains("multirole: Multiple roles"));
    }


    @Test
    void testEnter_FormattingWithMultipleCommands() {
        // Given
        when(context.getCommandRegistry().getAll()).thenReturn(List.of(command1, command2));

        // When
        state.enter(context);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).putAttribute(eq("helpResult"), captor.capture());

        String result = captor.getValue();
        assertTrue(result.startsWith("start: Starts the bot\r\nhelp: Shows this help") ||
                result.startsWith("help: Shows this help\r\nstart: Starts the bot")
        );
    }

}