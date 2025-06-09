package academy.prog.julia.telegram.states;

import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CreateInviteByRoleStateTest {

    private CreateInviteByRoleState state;

    @Mock
    private StateExecutionContext context;
    
    @Mock
    private UserService userService;

    private final String roleNameAttribute = "roleName";
    private final String maxParticipantsAttribute = "maxParticipants";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new CreateInviteByRoleState(roleNameAttribute, maxParticipantsAttribute);

        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_ValidAdminRole_Success() {
        // Given
        String roleName = "ADMIN";
        int maxUsers = 10;
        String expectedInviteCode = "admin123";

        when(context.getAttributeAsString(roleNameAttribute)).thenReturn(roleName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(userService.createInviteCodeByRole(roleName, 30, maxUsers))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("New invite code for ADMIN(s) is: admin123");
    }


    @Test
    void testEnter_ValidTeacherRole_Success() {
        // Given
        String roleName = "TEACHER";
        int maxUsers = 5;
        String expectedInviteCode = "teacher123";

        when(context.getAttributeAsString(roleNameAttribute)).thenReturn(roleName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(userService.createInviteCodeByRole(roleName, 30, maxUsers))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("New invite code for TEACHER(s) is: teacher123");
    }


    @Test
    void testEnter_InvalidRole_ErrorMessage() {
        // Given
        String roleName = "INVALID";
        when(context.getAttributeAsString(roleNameAttribute)).thenReturn(roleName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(5);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong role name! Please select from ADMIN, TEACHER, MANAGER, MENTOR.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NullRole_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(roleNameAttribute)).thenReturn(null);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(5);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong role name! Please select from ADMIN, TEACHER, MANAGER, MENTOR.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_EmptyRole_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(roleNameAttribute)).thenReturn("");
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(5);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong role name! Please select from ADMIN, TEACHER, MANAGER, MENTOR.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_ZeroMaxUsers_ErrorMessage() {
        // Given
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(0);
        when(context.getAttributeAsString(roleNameAttribute)).thenReturn("ADMIN");

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
        when(context.getAttributeAsString(roleNameAttribute)).thenReturn("TEACHER");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Error: Max users must be a positive integer.");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_MaxIntegerValue_Success() {
        // Given
        String roleName = "MANAGER";
        int maxUsers = Integer.MAX_VALUE;
        String expectedInviteCode = "manager123";

        when(context.getAttributeAsString(roleNameAttribute)).thenReturn(roleName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(userService.createInviteCodeByRole(roleName, 30, maxUsers))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("New invite code for MANAGER(s) is: manager123");
    }


    @Test
    void testEnter_LowercaseRoleName_Success() {
        // Given
        String roleName = "mentor";
        int maxUsers = 3;
        String expectedInviteCode = "mentor123";

        when(context.getAttributeAsString(roleNameAttribute)).thenReturn(roleName);
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(maxUsers);
        when(userService.createInviteCodeByRole(roleName, 30, maxUsers))
                .thenReturn(expectedInviteCode)
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("New invite code for MENTOR(s) is: mentor123");
    }


    @Test
    void testEnter_NullContext_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(null));
    }


    @Test
    void testEnter_NullUserService_ThrowsException() {
        // Given
        when(context.getUserService()).thenReturn(null);
        when(context.getAttributeAsString(roleNameAttribute)).thenReturn("ADMIN");
        when(context.getAttributeAsInt(maxParticipantsAttribute)).thenReturn(5);

        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(context));
    }

}