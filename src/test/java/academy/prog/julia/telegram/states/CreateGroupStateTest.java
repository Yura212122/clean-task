package academy.prog.julia.telegram.states;

import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateGroupStateTest {

    private CreateGroupState state;
    private StateExecutionContext context;
    private UserService userService;
    private final String groupNameAttribute = "testGroupName";


    @BeforeEach
    void setUp() {
        state = new CreateGroupState(groupNameAttribute);
        context = Mockito.mock(StateExecutionContext.class);
        userService = Mockito.mock(UserService.class);
        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_ValidGroupName_Success() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("NewGroup");
        when(userService.groupAdd("NewGroup")).thenReturn(true);

        state.enter(context);

        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_NullGroupName() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(null);

        state.enter(context);

        verify(context).sendMessage("Wrong group name");
    }


    @Test
    void testEnter_EmptyGroupName() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("");

        state.enter(context);

        verify(context).sendMessage("Wrong group name");
    }


    @Test
    void testEnter_WhitespaceGroupName() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("   ");

        state.enter(context);

        verify(context).sendMessage("Wrong group name");
    }


    @Test
    void testEnter_ReservedGroupName() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("ProgAcademy");

        state.enter(context);

        verify(context).sendMessage("This group name is reserved and cannot be used");
    }


    @Test
    void testEnter_GroupAlreadyExists() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("ExistingGroup");
        when(userService.groupAdd("ExistingGroup")).thenReturn(false);

        state.enter(context);

        verify(context).sendMessage("Group already exists");
    }


    @Test
    void testEnter_ValidLongGroupName() {
        String longGroupName = "A".repeat(50);
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(longGroupName);
        when(userService.groupAdd(longGroupName)).thenReturn(true);

        state.enter(context);

        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_GroupNameWithSpecialCharacters() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("Group@123");
        when(userService.groupAdd("Group@123")).thenReturn(true);

        state.enter(context);

        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_GroupNameWithNumbers() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("Group123");
        when(userService.groupAdd("Group123")).thenReturn(true);

        state.enter(context);

        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_GroupNameWithInvalidCharacters() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("Group!@#");
        when(userService.groupAdd("Group!@#")).thenReturn(false);

        state.enter(context);

        verify(context).sendMessage("Group already exists");
    }


    @Test
    void testEnter_GroupNameWithOnlyNumbers() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("12345");
        when(userService.groupAdd("12345")).thenReturn(true);

        state.enter(context);

        verify(context, never()).sendMessage(anyString());
    }


    @Test
    void testEnter_GroupNameWithOnlySpecialCharacters() {
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("@#$%");
        when(userService.groupAdd("@#$%")).thenReturn(true);

        state.enter(context);

        verify(context, never()).sendMessage(anyString());
    }

}