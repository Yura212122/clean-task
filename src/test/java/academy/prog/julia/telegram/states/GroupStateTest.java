package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class GroupStateTest {

    private GroupState state;

    @Mock
    private StateExecutionContext context;
    @Mock
    private UserService userService;
    @Mock
    private GroupService groupService;
    @Mock
    private User user;

    private final String groupDataAttribute = "groupData";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new GroupState(groupDataAttribute);
        when(context.getUserService()).thenReturn(userService);
        when(context.getGroupService()).thenReturn(groupService);
    }


    @Test
    void testEnter_NullInput_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn(null);

        // When
        state.enter(context);

        //Then
        verify(context).sendMessage("Incorrect input: at least one \"+\" or \"-\" is required!");
        verifyNoInteractions(userService);
        verifyNoInteractions(groupService);
    }


    @Test
    void testEnter_EmptyInput_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input: at least one \"+\" or \"-\" is required!");
        verifyNoInteractions(userService);
        verifyNoInteractions(groupService);
    }


    @Test
    void testEnter_NoOperationSign_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("123group");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input: at least one \"+\" or \"-\" is required!");
        verifyNoInteractions(userService);
        verifyNoInteractions(groupService);
    }


    @Test
    void testEnter_SingleAddOperation_Success() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("123+group1");
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of("group1"));
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(userService).addUserToGroup(123L, "group1");
        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_SingleRemoveOperation_Success() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("123-group1");
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of("group1"));
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(userService).removeUserFromGroup(123L, "group1");
        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_MultipleOperations_Success() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("123+group1;456-group2");
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of("group1", "group2"));
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findById(456L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(userService).addUserToGroup(123L, "group1");
        verify(userService).removeUserFromGroup(456L, "group2");
        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_UserNotFound_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("999+group1");
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of("group1"));
        when(userService.findById(999L)).thenReturn(Optional.empty());

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("User not found!");
        verify(userService, never()).addUserToGroup(anyLong(), anyString());
    }


    @Test
    void testEnter_InvalidUserId_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("abc+group1");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input of user's id!");
        verify(userService, never()).addUserToGroup(anyLong(), anyString());
    }


    @Test
    void testEnter_GroupNotFound_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("123+unknown");
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of("group1"));
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input: group \"unknown\" doesn't exist!");
        verify(userService, never()).addUserToGroup(anyLong(), anyString());
    }


    @Test
    void testEnter_InvalidFormat_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupDataAttribute)).thenReturn("123+");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input");
        verify(userService, never()).addUserToGroup(anyLong(), anyString());
    }


    @Test
    void testParser_SingleOperation_CorrectParsing() throws Exception {
        // Given
        GroupState state = new GroupState("test");
        Method method = GroupState.class.getDeclaredMethod("parser", String.class);
        method.setAccessible(true);

        // When
        List<String> result = (List<String>) method.invoke(state, "123+group1");

        // Then
        assertEquals(List.of("123", "group1", "+"), result);
    }


    @Test
    void testParser_SingleOperationWithWhitespace_CorrectParsing() throws Exception {
        // Given
        GroupState state = new GroupState("test");
        Method method = GroupState.class.getDeclaredMethod("parser", String.class);
        method.setAccessible(true);

        // When
        List<String> result = (List<String>) method.invoke(state, " 123 + group1 ");

        // Then
        assertEquals(List.of(" 123 ", " group1 ", "+"), result);
    }


    @Test
    void testParser_InvalidFormat_ThrowsException() throws Exception {
        // Given
        GroupState state = new GroupState("test");
        Method method = GroupState.class.getDeclaredMethod("parser", String.class);
        method.setAccessible(true);

        // When & Then
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            try {
                method.invoke(state, "123+");
            } catch (Exception e) {

                throw e.getCause();
            }
        });
    }

}