package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.model.UserRole;
import academy.prog.julia.services.UserService;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BlockStateTest {

    @Mock
    private UserService userService;

    @Mock
    private StateExecutionContext context;

    private BlockState blockState;

    @BeforeEach
    void setup() {
        blockState = new BlockState("userInput");
        lenient().when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_InvalidInput_Null() {
        // Arrange
        lenient().when(context.getUserService()).thenReturn(userService);
        lenient().when(context.getAttributeAsString("userInput")).thenReturn(null);

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("Incorrect input: phone number, email, or user ID is required!");
    }


    @Test
    void testEnter_InvalidInput_IsEmpty() {
        // Arrange
        lenient().when(context.getUserService()).thenReturn(userService);
        lenient().when(context.getAttributeAsString("userInput")).thenReturn("");

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("Incorrect input: phone number, email, or user ID is required!");
    }


    @Test
    void testEnter_UserFoundByPhone() {
        // Arrange
        User user = new User();
        user.setId(8L);
        String phone = "+380991234567";

        when(context.getAttributeAsString("userInput")).thenReturn(phone);
        when(userService.findByPhone(phone)).thenReturn(List.of(user));

        // Act
        blockState.enter(context);

        // Assert
        verify(userService).blockUser(8L);
        verify(context).sendMessage("User blocked successfully.");
    }


    @Test
    void testEnter_EmptyPhoneNumber() {
        // Arrange
        String phone = "";

        when(context.getAttributeAsString("userInput")).thenReturn(phone);

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("Incorrect input: phone number, email, or user ID is required!");
    }


    @Test
    void testEnter_UserNotFound() {
        // Arrange
        when(context.getAttributeAsString("userInput")).thenReturn("12345");
        when(userService.findById(12345L)).thenReturn(Optional.empty());

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_BlockRegularUser() {
        // Arrange
        User user = new User();
        user.setId(2L);

        when(context.getAttributeAsString("userInput")).thenReturn("2");
        when(userService.findById(2L)).thenReturn(Optional.of(user));

        // Act
        blockState.enter(context);

        // Assert
        verify(userService).blockUser(2L);
        verify(context).sendMessage("User blocked successfully.");
    }


    @Test
    void testEnter_BlockUserByPhone() {
        // Arrange
        User user = new User();
        user.setId(3L);

        when(context.getAttributeAsString("userInput")).thenReturn("+380991234567");
        when(userService.findByPhone("+380991234567")).thenReturn(List.of(user));

        // Act
        blockState.enter(context);

        // Assert
        verify(userService).blockUser(3L);
        verify(context).sendMessage("User blocked successfully.");
    }


    @Test
    void testEnter_BlockUserByEmail() {
        // Arrange
        User user = new User();
        user.setId(4L);

        when(context.getAttributeAsString("userInput")).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(user);

        // Act
        blockState.enter(context);

        // Assert
        verify(userService).blockUser(4L);
        verify(context).sendMessage("User blocked successfully.");
    }


    @Test
    @WithMockUser(username = "BlockedUser", roles = "STUDENT")
    void testEnter_BlockAdministrator() {
        // Arrange
        User admin = mock(User.class);

        when(admin.getId()).thenReturn(1L);
        when(admin.getRole()).thenReturn(UserRole.ADMIN);

        when(context.getAttributeAsString("userInput")).thenReturn("1");
        when(userService.findById(1L)).thenReturn(Optional.of(admin));

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("You can't block an administrator with Id " + admin.getId());
    }


    @Test
    void testEnter_InvalidUserIdFormat() {
        // Arrange
        when(context.getAttributeAsString("userInput")).thenReturn("invalidId");

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_UserNotFoundByEmail() {
        // Arrange
        when(context.getAttributeAsString("userInput")).thenReturn("nonexistentuser@example.com");
        when(userService.findByEmail("nonexistentuser@example.com")).thenReturn(null);

        // Act
        blockState.enter(context);

        // Assert
        verify(context).sendMessage("User not found!");
    }


    @Test
    void testEnter_BlockUserByEmailFound() {
        // Arrange
        User user = new User();
        user.setId(5L);

        when(context.getAttributeAsString("userInput")).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(user);

        // Act
        blockState.enter(context);

        // Assert
        verify(userService).blockUser(5L);
        verify(context).sendMessage("User blocked successfully.");
    }

}
