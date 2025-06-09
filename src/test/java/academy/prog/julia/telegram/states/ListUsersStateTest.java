package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.repos.UserRepository;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListUsersStateTest {

    private ListUsersState state;
    private StateExecutionContext context;
    private UserRepository userRepository;
    private User currentUser;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        state = new ListUsersState(false, userRepository);
        context = Mockito.mock(StateExecutionContext.class);

        currentUser = new User();
        currentUser.setTelegramChatId("12345");
        when(context.getUser()).thenReturn(currentUser);
    }


    @Test
    void testEnter_NoUsersInDatabase() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("There are no users, except you");
        verify(context, never()).sendMessage(anyString(), anyString());
    }


    @Test
    void testEnter_SingleUserInDatabase() {
        // Given
        User user = createTestUser(
                1L, "Name", "Surname", "Name@example.com", "+123456789"
        );
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name: Name, surname: Surname, email: Name@example.com, phone: +123456789\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_MultipleUsersInDatabase() {
        // Given
        User user1 = createTestUser(
                1L, "Name", "Surname", "Name@example.com", "+123456789"
        );
        User user2 = createTestUser(
                2L, "Jane", "Smith", "jane@example.com", "+987654321"
        );
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name: Name, surname: Surname, email: Name@example.com, phone: +123456789\r\n" +
                "Id: 2, name: Jane, surname: Smith, email: jane@example.com, phone: +987654321\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_UserWithNullFields() {
        // Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name: null, surname: null, email: null, phone: null\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_UserWithEmptyFields() {
        // Given
        User user = createTestUser(1L, "", "", "", "+123456789");
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name: , surname: , email: , phone: +123456789\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_UserWithMaxLengthFields() {
        // Given
        String longName = "A".repeat(255);
        String longSurname = "B".repeat(255);
        String longEmail = "C".repeat(255) + "@example.com";
        String longPhone = "+" + "1".repeat(9);

        User user = createTestUser(1L, longName, longSurname, longEmail, longPhone);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name: " + longName + ", surname: " + longSurname +
                ", email: " + longEmail + ", phone: " + longPhone + "\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_UserWithSpecialCharacters() {
        // Given
        User user = createTestUser(
                1L, "Jöhn", "Döe", "Name+test@exämple.com", "+123456789"
        );
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name: Jöhn, surname: Döe, email: Name+test@exämple.com, phone: +123456789\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_NullRepository() {
        // Given
        ListUsersState stateWithNullRepo = new ListUsersState(false, null);

        // When
        assertThrows(NullPointerException.class, () -> stateWithNullRepo.enter(context));

        // Then
        verifyNoInteractions(context);
    }


    @Test
    void testEnter_NullContext() {
        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(null));
    }


    @Test
    void testEnter_NullChatId() {
        // Given
        currentUser.setTelegramChatId(null);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("There are no users, except you");
    }


    @Test
    void testEnter_VerifyReadOnlyTransaction() {
        // Given
        User user = createTestUser(
                1L, "Name", "Surname", "Name@example.com", "+123456789"
        );
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        verify(userRepository, times(1)).findAll();
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).delete(any());
    }


    @Test
    void testEnter_UserWithDifferentSpacing() {
        // Given
        User user = createTestUser(
                1L, " Name ", "  Surname  ", "  Name@example.com  ", "+123456789"
        );
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // When
        state.enter(context);

        // Then
        String expectedMessage = "All user list:\r\n" +
                "Id: 1, name:  Name , surname:   Surname  , email:   Name@example.com  , phone: +123456789\r\n";
        verify(context).sendMessage("12345", expectedMessage);
    }


    @Test
    void testEnter_LargeNumberOfUsers() {
        // Given
        List<User> users = IntStream.range(1, 101)
                .mapToObj(i -> createTestUser(
                        (long)i,
                        "User" + i,
                        "Surname" + i,
                        "user" + i + "@test.com",
                        "+123456789")
                )
                .toList();
        when(userRepository.findAll()).thenReturn(users);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage(eq("12345"), anyString());
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).sendMessage(eq("12345"), messageCaptor.capture());

        String actualMessage = messageCaptor.getValue();
        assertEquals(100, actualMessage.split("\r\n").length - 1); // -1 for title
    }

    
    private User createTestUser(Long id, String name, String surname, String email, String phone) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }

}