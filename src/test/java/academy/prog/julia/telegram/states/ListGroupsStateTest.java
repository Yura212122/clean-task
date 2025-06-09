package academy.prog.julia.telegram.states;

import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListGroupsStateTest {

    private ListGroupsState state;
    private StateExecutionContext context;
    private UserService userService;


    @BeforeEach
    void setUp() {
        state = new ListGroupsState();
        context = Mockito.mock(StateExecutionContext.class);
        userService = Mockito.mock(UserService.class);
        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_NoGroupsDefined() {
        // Given
        when(userService.fillAllGroupNames()).thenReturn(Collections.emptyList());

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("No groups defined");
    }

    @Test
    void testEnter_SingleGroup() {
        // Given
        List<String> groupNames = Collections.singletonList("Group1");
        when(userService.fillAllGroupNames()).thenReturn(groupNames);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Group1\r\n");
    }

    @Test
    void testEnter_MultipleGroups() {
        // Given
        List<String> groupNames = Arrays.asList("Group1", "Group2", "Group3");
        when(userService.fillAllGroupNames()).thenReturn(groupNames);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Group1\r\nGroup2\r\nGroup3\r\n");
    }

    @Test
    void testEnter_GroupWithSpecialCharacters() {
        // Given
        List<String> groupNames = Collections.singletonList("Group@123");
        when(userService.fillAllGroupNames()).thenReturn(groupNames);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Group@123\r\n");
    }

    @Test
    void testEnter_GroupWithNumbers() {
        // Given
        List<String> groupNames = Collections.singletonList("Group123");
        when(userService.fillAllGroupNames()).thenReturn(groupNames);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Group123\r\n");
    }

    @Test
    void testEnter_GroupWithOnlyNumbers() {
        // Given
        List<String> groupNames = Collections.singletonList("12345");
        when(userService.fillAllGroupNames()).thenReturn(groupNames);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("12345\r\n");
    }

    @Test
    void testEnter_GroupWithOnlySpecialCharacters() {
        // Given
        List<String> groupNames = Collections.singletonList("@#$%");
        when(userService.fillAllGroupNames()).thenReturn(groupNames);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("@#$%\r\n");
    }

}