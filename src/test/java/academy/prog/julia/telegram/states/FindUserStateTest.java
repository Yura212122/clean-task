package academy.prog.julia.telegram.states;


import academy.prog.julia.model.User;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;


class FindUserStateTest {

    private FindUserState state;

    @Mock
    private StateExecutionContext context;
    @Mock
    private UserService userService;

    private final String userDataAttribute = "userData";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new FindUserState(userDataAttribute);
        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_NullInput_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userDataAttribute)).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong user search pattern");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_EmptyInput_NoUsersFound() {
        // Given
        when(context.getAttributeAsString(userDataAttribute)).thenReturn("");
        when(userService.findByPhoneOrEmailLike("", null))
                .thenReturn(Collections.emptyList())
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("No users found");
        verify(userService).findByPhoneOrEmailLike("", null);
    }


    @Test
    void testEnter_PhoneOnly_Success() {
        // Given
        String phone = "380501234567";
        User user = createTestUser(1L, "John Doe");

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(phone);
        when(userService.findByPhoneOrEmailLike(phone, null))
                .thenReturn(Collections.singletonList(user))
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(contains("The person you are looking for"));
        verify(context).sendMessage(contains("ID: 1"));
        verify(context).sendMessage(contains("Name: John Doe"));
    }


    @Test
    void testEnter_EmailOnly_Success() {
        // Given
        String email = "test@example.com";
        User user = createTestUser(2L, "Jane Smith");

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(email);
        when(userService.findByPhoneOrEmailLike(null, email))
                .thenReturn(Collections.singletonList(user))
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(contains("The person you are looking for"));
        verify(context).sendMessage(contains("ID: 2"));
        verify(context).sendMessage(contains("Name: Jane Smith"));
    }


    @Test
    void testEnter_PhoneAndEmail_Success() {
        // Given
        String search = "380501234567;test@example.com";
        User user = createTestUser(3L, "Bob Johnson");

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(search);
        when(userService.findByPhoneOrEmailLike("380501234567", "test@example.com"))
                .thenReturn(Collections.singletonList(user))
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(contains("The person you are looking for"));
        verify(context).sendMessage(contains("ID: 3"));
        verify(context).sendMessage(contains("Name: Bob Johnson"));
    }


    @Test
    void testEnter_PhoneWithWhitespace_Normalized() {
        // Given
        String phone = " 380501234567 ";
        User user = createTestUser(4L, "Alice Brown");

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(phone);
        when(userService.findByPhoneOrEmailLike("380501234567", null))
                .thenReturn(Collections.singletonList(user))
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(contains("The person you are looking for"));
        verify(context).sendMessage(contains("ID: 4"));
    }


    @Test
    void testEnter_EmailWithWhitespace_Normalized() {
        // Given
        String email = " test@example.com ";
        User user = createTestUser(5L, "Charlie Green");

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(email);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com"))
                .thenReturn(Collections.singletonList(user))
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(contains("The person you are looking for"));
        verify(context).sendMessage(contains("ID: 5"));
    }


    @Test
    void testEnter_MultipleUsersFound_Success() {
        // Given
        String search = "test@example.com";
        User user1 = createTestUser(6L, "User One");
        User user2 = createTestUser(7L, "User Two");

        when(context.getAttributeAsString(userDataAttribute)).thenReturn(search);
        when(userService.findByPhoneOrEmailLike(null, "test@example.com"))
                .thenReturn(Arrays.asList(user1, user2))
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(contains("ID: 6"));
        verify(context).sendMessage(contains("ID: 7"));
        verify(context, times(1)).sendMessage(anyString());
    }


    @Test
    void testEnter_NoUsersFound_ErrorMessage() {
        // Given
        String search = "nonexistent@example.com";
        when(context.getAttributeAsString(userDataAttribute)).thenReturn(search);
        when(userService.findByPhoneOrEmailLike(null, "nonexistent@example.com"))
                .thenReturn(Collections.emptyList())
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("No users found");
    }


    @Test
    void testEnter_InvalidFormat_NoUsersFound() {
        // Given
        String search = "invalid_format";
        when(context.getAttributeAsString(userDataAttribute)).thenReturn(search);
        when(userService.findByPhoneOrEmailLike("invalid_format", null))
                .thenReturn(Collections.emptyList())
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("No users found");
    }


    private User createTestUser(long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

}