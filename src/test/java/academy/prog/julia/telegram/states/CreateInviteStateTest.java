package academy.prog.julia.telegram.states;

import academy.prog.julia.model.UserRole;
import academy.prog.julia.services.GroupService;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CreateInviteStateTest {

    private CreateInviteState state;

    @Mock
    private StateExecutionContext context;
    @Mock
    private UserService userService;
    @Mock
    private GroupService groupService;

    private final String groupNameAttribute = "groupName";
    private final String maxParticipantsAttribute = "maxParticipants";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new CreateInviteState(groupNameAttribute, maxParticipantsAttribute);

        when(context.getUserService()).thenReturn(userService);
        when(context.getGroupService()).thenReturn(groupService);
    }


    @Test
    void testEnter_ValidInput_Success() {
        // Given
        String groupName = "TestGroup";
        int maxUsers = 10;
        String expectedInviteCode = "test123";
        String expectedUrl = String.format(Utils.REGISTER_URL, expectedInviteCode);

        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(groupName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of(groupName));
        when(userService.createInviteCode(UserRole.STUDENT, 30, maxUsers, groupName))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invite URL is: " + expectedUrl);
    }


    @Test
    void testEnter_ZeroMaxUsers_ErrorMessage() {
        // Given
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(0);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Error: Max users must be a positive integer.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NegativeMaxUsers_ErrorMessage() {
        // Given
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(-5);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Error: Max users must be a positive integer.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NonExistingGroup_ErrorMessage() {
        // Given
        String groupName = "NonExistingGroup";
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(groupName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(10);
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of("ExistingGroup"));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Error: Group with name " + groupName + " does not exist.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NullGroupName_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(null);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(10);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Error: Group with name null does not exist.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_EmptyGroupName_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("");
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(10);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Error: Group with name  does not exist.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_MaxIntegerValue_Success() {
        // Given
        String groupName = "TestGroup";
        int maxUsers = Integer.MAX_VALUE;
        String expectedInviteCode = "test123";

        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(groupName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of(groupName));
        when(userService.createInviteCode(UserRole.STUDENT, 30, maxUsers, groupName))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invite URL is: " + String.format(Utils.REGISTER_URL, expectedInviteCode));
    }


    @Test
    void testEnter_SpecialCharactersInGroupName_Success() {
        // Given
        String groupName = "Group@123#";
        int maxUsers = 5;
        String expectedInviteCode = "special123";

        when(context.getAttributeAsString(groupNameAttribute)).thenReturn(groupName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(groupService.findAllNamesOfGroups()).thenReturn(List.of(groupName));
        when(userService.createInviteCode(UserRole.STUDENT, 30, maxUsers, groupName))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invite URL is: " + String.format(Utils.REGISTER_URL, expectedInviteCode));
    }


    @Test
    void testEnter_NullContext_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(null));
    }


    @Test
    void testEnter_NullGroupService_ThrowsException() {
        // Given
        when(context.getGroupService()).thenReturn(null);
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("test");
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(5);

        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(context));
    }


    @Test
    void testEnter_NullUserService_ThrowsException() {
        // Given
        when(context.getUserService()).thenReturn(null);
        when(context.getAttributeAsString(groupNameAttribute)).thenReturn("test");
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(5);
        when(context.getGroupService().findAllNamesOfGroups()).thenReturn(List.of("test"));

        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(context));
    }

}