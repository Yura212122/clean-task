package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class SetEmailStateTest {

    private SetEmailState state;

    @Mock
    private StateExecutionContext context;
    @Mock
    private UserService userService;
    @Mock
    private User user;

    private final String userDataAttribute = "userData";
    private final String emailsListAttribute = "emails";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new SetEmailState(userDataAttribute, emailsListAttribute);

        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_EmailWithWhitespace_NormalizedCorrectly() {
        // Given
        String userData = " test@example.com ";
        String emails = " test@example.com ";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);

        User realUser = new User();
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(realUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        assertEquals("test@example.com", realUser.getEmail());
        assertEquals(1, realUser.getEmails().size());
        assertTrue(realUser.getEmails().contains("test@example.com"));

        verify(userService).saveUser(realUser);
        verify(context, times(3)).sendMessage(anyString());
    }


    @Test
    void testEnter_ValidSingleEmail_Success() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);

        User realUser = new User();
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(realUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        assertEquals("test@example.com", realUser.getEmail());
        assertEquals(1, realUser.getEmails().size());
        assertTrue(realUser.getEmails().contains("test@example.com"));

        verify(userService).saveUser(realUser);
        verify(context).sendMessage("User emails successfully updated");
        verify(context).sendMessage("Now user has the following emails:");
        verify(context).sendMessage(realUser.getEmails().toString());
    }


    @Test
    void testEnter_ValidPhoneSearch_Success() {
        // Given
        String userData = "380501234567";
        String emails = "test@example.com";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);

        User realUser = new User();
        when(userService.findByPhoneOrEmailLike("380501234567", null)).thenReturn(List.of(realUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        assertEquals("test@example.com", realUser.getEmail());
        assertEquals(1, realUser.getEmails().size());
        assertTrue(realUser.getEmails().contains("test@example.com"));

        verify(userService).saveUser(realUser);
        verify(context, times(3)).sendMessage(anyString());
    }


    @Test
    void testEnter_PhoneAndEmailSearch_Success() {
        // Given
        String userData = "380501234567;test@example.com";
        String emails = "test@example.com";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);

        User realUser = new User();
        when(userService.findByPhoneOrEmailLike("380501234567", "test@example.com"))
                .thenReturn(List.of(realUser))
        ;
        when(userService.findByEmail("test@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        assertEquals("test@example.com", realUser.getEmail());
        assertEquals(1, realUser.getEmails().size());
        assertTrue(realUser.getEmails().contains("test@example.com"));

        verify(userService).saveUser(realUser);
        verify(context, times(3)).sendMessage(anyString());
    }


    @Test
    void testEnter_NullUserData_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userDataAttribute)).thenReturn(null);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn("test@example.com");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong user search pattern");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NullEmails_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userDataAttribute)).thenReturn("test@example.com");
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong user search pattern");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_UserNotFound_ErrorMessage() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com"))
                .thenReturn(Collections.emptyList())
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("No users found");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_EmailAlreadyInUse_ErrorMessage() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com";
        User otherUser = new User();
        otherUser.setId(2L);

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(user));
        when(userService.findByEmail("test@example.com")).thenReturn(otherUser);
        when(user.getId()).thenReturn(1L);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Email address test@example.com is already in use by another user.");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_InvalidEmail_ErrorMessage() {
        // Given
        String userData = "test@example.com";
        String emails = "invalid-email";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(user));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Something went wrong with additional emails. Main email was stored");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_ExceptionDuringSave_ErrorMessage() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(user));
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        when(user.getId()).thenReturn(1L);

        doThrow(new IllegalArgumentException("Test exception")).when(userService).saveUser(user);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Something went wrong");
    }


    @Test
    void testEnter_ValidMultipleEmails_Success() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com, test2@example.com";

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);

        User realUser = new User();
        realUser.setId(1L);

        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(realUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        when(userService.findByEmail("test2@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        assertEquals("test@example.com", realUser.getEmail());
        assertEquals(2, realUser.getEmails().size());
        assertTrue(realUser.getEmails().contains("test@example.com"));
        assertTrue(realUser.getEmails().contains("test2@example.com"));

        verify(userService).saveUser(realUser);
        verify(context, times(3)).sendMessage(anyString());
    }


    @Test
    void testEnter_EmailUsedByOtherUser_ErrorMessage() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com,existing@example.com";

        User currentUser = new User();
        currentUser.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(currentUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        when(userService.findByEmail("existing@example.com")).thenReturn(otherUser);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Email address existing@example.com is already in use by another user.");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_EmailUsedBySameUser_Success() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com,existing@example.com";

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");
        currentUser.setEmails(new HashSet<>(Set.of("existing@example.com")));

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(currentUser));
        when(userService.findByEmail("test@example.com")).thenReturn(currentUser);
        when(userService.findByEmail("existing@example.com")).thenReturn(currentUser);

        // When
        state.enter(context);

        // Then
        verify(context, never()).sendMessage(contains("is already in use by another user"));
        verify(userService).saveUser(currentUser);
        assertEquals(2, currentUser.getEmails().size());
    }


    @Test
    void testEnter_FirstEmailUsedByOtherUser_StopsProcessing() {
        // Given
        String userData = "test@example.com";
        String emails = "existing@example.com,test@example.com";

        User currentUser = new User();
        currentUser.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(currentUser));
        when(userService.findByEmail("existing@example.com")).thenReturn(otherUser);
        when(userService.findByEmail("test@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Email address existing@example.com is already in use by another user.");
        verify(userService, never()).saveUser(any());
        assertTrue(currentUser.getEmails() == null || currentUser.getEmails().isEmpty());
    }


    @Test
    void testEnter_InvalidEmailAmongMultiple_StopsProcessing() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com,invalid-email";

        User currentUser = new User();
        currentUser.setId(1L);

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(currentUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Something went wrong with additional emails. Main email was stored");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_DuplicateEmails_StoresOnlyUnique() {
        // Given
        String userData = "test@example.com";
        String emails = "test@example.com,test@example.com,test2@example.com";

        User currentUser = new User();
        currentUser.setId(1L);

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(userData);
        when(context.getAttributeAsString(emailsListAttribute)).thenReturn(emails);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com")).thenReturn(List.of(currentUser));
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        when(userService.findByEmail("test2@example.com")).thenReturn(null);

        // When
        state.enter(context);

        // Then
        assertEquals(2, currentUser.getEmails().size());
        verify(userService).saveUser(currentUser);
    }

}