package academy.prog.julia.telegram.states;


import academy.prog.julia.model.User;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


class UnblockStateTest {

    private UnblockState state;

    @Mock
    private StateExecutionContext context;
    @Mock
    private UserService userService;
    @Mock
    private User user;

    private final String unblockDataAttribute = "unblockData";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new UnblockState(unblockDataAttribute);
        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_NullInput_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input: phone number, email, or user ID is required!");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_EmptyInput_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn("");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Incorrect input: phone number, email, or user ID is required!");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_ValidPhoneNumber_UserFoundAndUnblocked() {
        // Given
        String phone = "+380501234567";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(phone);
        when(userService.findByPhone(phone)).thenReturn(List.of(user));
        when(user.getBannedStatus()).thenReturn(true);

        // When
        state.enter(context);

        // Then
        verify(userService).unblockUser(user.getId());
        verify(context).sendMessage("User unblocked successfully.");
    }


    @Test
    void testEnter_ValidPhoneNumber_UserFoundButNotBlocked() {
        // Given
        String phone = "+380501234567";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(phone);
        when(userService.findByPhone(phone)).thenReturn(List.of(user));
        when(user.getBannedStatus()).thenReturn(false);

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User is not blocked.");
    }


    @Test
    void testEnter_ValidPhoneNumber_UserNotFound() {
        // Given
        String phone = "+380501234567";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(phone);
        when(userService.findByPhone(phone)).thenReturn(List.of());

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_ValidUserId_UserFoundAndUnblocked() {
        // Given
        String phone = "+380501234567";
        long expectedUserId = 123L;

        User realUser = new User();
        realUser.setId(expectedUserId);
        realUser.setBannedStatus(true);

        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(phone);
        when(userService.findByPhone(phone)).thenReturn(List.of(realUser));

        // When
        state.enter(context);

        // Then
        verify(userService).unblockUser(expectedUserId);
        verify(context).sendMessage("User unblocked successfully.");
    }


    @Test
    void testEnter_ValidUserId_UserFoundButNotBlocked() {
        // Given
        String userId = "123";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(userId);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(user.getBannedStatus()).thenReturn(false);

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User is not blocked.");
    }


    @Test
    void testEnter_ValidUserId_UserNotFound() {
        // Given
        String userId = "123";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(userId);
        when(userService.findById(123L)).thenReturn(Optional.empty());

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_InvalidUserId_ErrorMessage() {
        // Given
        String invalidId = "abc";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(invalidId);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("User not found!");
        verify(userService, never()).unblockUser(anyLong());
    }


    @Test
    void testEnter_ValidEmail_UserFoundAndUnblocked() {
        // Given
        String email = "test@example.com";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(user);
        when(user.getBannedStatus()).thenReturn(true);

        // When
        state.enter(context);

        // Then
        verify(userService).unblockUser(user.getId());
        verify(context).sendMessage("User unblocked successfully.");
    }


    @Test
    void testEnter_ValidEmail_UserFoundButNotBlocked() {
        // Given
        String email = "test@example.com";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(user);
        when(user.getBannedStatus()).thenReturn(false);

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User is not blocked.");
    }


    @Test
    void testEnter_ValidEmail_UserNotFound() {
        // Given
        String email = "test@example.com";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_InvalidInputFormat_UserNotFound() {
        // Given
        String invalidInput = "invalid_format";
        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(invalidInput);

        // When
        state.enter(context);

        // Then
        verify(userService, never()).unblockUser(anyLong());
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_MultipleUsersWithSamePhone_UnblocksFirst() {
        // Given
        String phone = "+380501234567";
        User user1 = new User();
        user1.setId(1L);
        user1.setBannedStatus(true);

        User user2 = new User();
        user2.setId(2L);
        user2.setBannedStatus(true);

        when(context.getAttributeAsString(unblockDataAttribute)).thenReturn(phone);
        when(userService.findByPhone(phone)).thenReturn(List.of(user1, user2));

        // When
        state.enter(context);

        // Then
        verify(userService).unblockUser(1L);
        verify(context).sendMessage("User unblocked successfully.");
    }

}