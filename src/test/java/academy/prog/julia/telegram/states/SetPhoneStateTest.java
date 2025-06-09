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


class SetPhoneStateTest {

    private SetPhoneState state;

    @Mock
    private StateExecutionContext context;
    @Mock
    private UserService userService;
    @Mock
    private User user;

    private final String userIdAttribute = "userId";
    private final String userPhonesAttribute = "phones";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new SetPhoneState(userIdAttribute, userPhonesAttribute);

        when(context.getUserService()).thenReturn(userService);
    }


    @Test
    void testEnter_ValidSinglePhone_Success() {
        // Given
        String userId = "123";
        String phone = "380501234567";

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findByPhone(phone)).thenReturn(Collections.emptyList());

        Set<String> phonesSet = new HashSet<>();
        when(user.getPhones()).thenReturn(phonesSet);

        // When
        state.enter(context);

        // Then
        verify(user).setPhone("380501234567");

        assertEquals(1, phonesSet.size());
        assertTrue(phonesSet.contains("+380501234567"));

        verify(userService).saveUser(user);
        verify(context).sendMessage("User phone(s) successfully changed");
        verify(user, atLeastOnce()).getPhones();
    }


    @Test
    void testEnter_ValidMultiplePhones_Success() {
        // Given
        String userId = "123";
        String phones = "380501234567,380671234567";

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phones);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findByPhone("380501234567")).thenReturn(Collections.emptyList());
        when(userService.findByPhone("380671234567")).thenReturn(Collections.emptyList());

        Set<String> phonesSet = new HashSet<>();
        when(user.getPhones()).thenReturn(phonesSet);

        // When
        state.enter(context);

        // Then
        verify(user).setPhone("380501234567");

        assertEquals(2, phonesSet.size());
        assertTrue(phonesSet.contains("+380501234567"));
        assertTrue(phonesSet.contains("+380671234567"));

        verify(userService).saveUser(user);
        verify(context).sendMessage("User phone(s) successfully changed");
        verify(user, atLeastOnce()).getPhones();
    }


    @Test
    void testEnter_InvalidUserId_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("abc");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn("380501234567");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong user id");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NullUserId_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userIdAttribute)).thenReturn(null);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn("380501234567");

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong user id");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_NullPhones_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(null);

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Wrong user phones");
        verifyNoInteractions(userService);
    }


    @Test
    void testEnter_UserNotFound_ErrorMessage() {
        // Given
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn("380501234567");
        when(userService.findById(123L)).thenReturn(Optional.empty());

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("user id not exists");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_InvalidCountryCode_ErrorMessage() {
        // Given
        String invalidPhone = "9991234567";
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(invalidPhone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invalid country code in the phone number: " + invalidPhone + ".");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_PhoneTooShort_ErrorMessage() {
        // Given
        String shortPhone = "38012345"; // 8 digits
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(shortPhone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invalid quantity of digits in the phone number: " + shortPhone +
                ". The length of the number should be from 9 to 15 digits.")
        ;
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_PhoneTooLong_ErrorMessage() {
        // Given
        String longPhone = "380123456789012345"; // 18 digits
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(longPhone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invalid quantity of digits in the phone number: " + longPhone +
                ". The length of the number should be from 9 to 15 digits.")
        ;
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_PhoneWithPlusSign_NormalizedCorrectly() {
        // Given
        String phoneWithPlus = "+380501234567";

        Set<String> phonesSet = new HashSet<>();
        when(user.getPhones()).thenReturn(phonesSet);

        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phoneWithPlus);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findByPhone("380501234567")).thenReturn(Collections.emptyList());

        // When
        state.enter(context);

        // Then
        verify(user).setPhone("380501234567");

        assertEquals(1, phonesSet.size());
        assertTrue(phonesSet.contains("+380501234567"));

        verify(userService).saveUser(user);
        verify(context).sendMessage("User phone(s) successfully changed");
        verify(user, atLeastOnce()).getPhones();
    }


    @Test
    void testEnter_PhoneWithWhitespace_NormalizedCorrectly() {
        // Given
        String phoneWithSpaces = " +380501234567 ";
        String normalizedPhone = "380501234567";

        Set<String> phonesSet = new HashSet<>();
        when(user.getPhones()).thenReturn(phonesSet);

        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phoneWithSpaces);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findByPhone(normalizedPhone)).thenReturn(Collections.emptyList());

        // When
        state.enter(context);

        // Then
        verify(user).setPhone(normalizedPhone);

        assertEquals(1, phonesSet.size());
        assertTrue(phonesSet.contains("+" + normalizedPhone));

        verify(userService).saveUser(user);
        verify(context).sendMessage("User phone(s) successfully changed");
        verify(user, atLeastOnce()).getPhones();
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
        when(context.getAttributeAsString(userIdAttribute)).thenReturn("123");
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn("380501234567");

        // When & Then
        assertThrows(NullPointerException.class, () -> state.enter(context));
    }


    @Test
    void testEnter_PhoneAlreadyInUse_ErrorMessage() {
        // Given
        String userId = "123";
        String phone = "380501234567";
        User otherUser = new User();
        otherUser.setId(456L);

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findByPhone(phone)).thenReturn(List.of(otherUser));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Phone number " + phone + " is already in use by another user.");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_InvalidPhoneFormat_ErrorMessage() {
        // Given
        String userId = "123";
        String invalidPhone = "invalid_phone";

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(invalidPhone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invalid country code in the phone number: " + invalidPhone + ".");
        verify(userService, never()).saveUser(any());
        verify(user, never()).setPhone(anyString());
    }


    @Test
    void testEnter_PhoneUsedByOtherUser_ErrorMessage() {
        // Given
        String userId = "123";
        String phone = "380501234567";
        User currentUser = new User();
        currentUser.setId(123L);
        User otherUser = new User();
        otherUser.setId(456L);

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phone);
        when(userService.findById(123L)).thenReturn(Optional.of(currentUser));
        when(userService.findByPhone(phone)).thenReturn(List.of(otherUser));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Phone number " + phone + " is already in use by another user.");
        verify(userService, never()).saveUser(any());
    }


    @Test
    void testEnter_PhoneUsedBySameUser_Success() {
        // Given
        String userId = "123";
        String phone = "380501234567";
        User currentUser = new User();
        currentUser.setId(123L);
        currentUser.setPhone("+380501234567");

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(phone);
        when(userService.findById(123L)).thenReturn(Optional.of(currentUser));
        when(userService.findByPhone(phone)).thenReturn(List.of(currentUser));

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("User phone(s) successfully changed");
        verify(userService).saveUser(currentUser);
    }


    @Test
    void testEnter_InvalidPhoneNumberFormat_ThrowsException() {
        // Given
        String userId = "123";
        String invalidPhone = "380invalid123";

        when(context.getAttributeAsString(userIdAttribute)).thenReturn(userId);
        when(context.getAttributeAsString(userPhonesAttribute)).thenReturn(invalidPhone);
        when(userService.findById(123L)).thenReturn(Optional.of(user));
        when(userService.findByPhone("380invalid123")).thenReturn(Collections.emptyList());

        doThrow(new IllegalArgumentException("Invalid phone number format"))
                .when(user).setPhone("380invalid123")
        ;

        // When
        state.enter(context);

        // Then
        verify(context).sendMessage("Invalid phone number format: 380invalid123. Please use a valid format.");
        verify(userService, never()).saveUser(user);
    }

}