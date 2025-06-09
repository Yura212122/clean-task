package academy.prog.julia.integration.telegram.validators;

import academy.prog.julia.telegram.validators.StateInputValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StateInputValidationExceptionTest {

    @Test
    void testStateInputValidationException_Message() {
        // Given
        String errorMessage = "Invalid input provided";

        // When
        StateInputValidationException exception = new StateInputValidationException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testStateInputValidationException_EmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        StateInputValidationException exception = new StateInputValidationException(emptyMessage);

        // Then
        assertEquals("", exception.getMessage());
    }

    @Test
    void testStateInputValidationException_NullMessage() {
        // Given
        String nullMessage = null;

        // When
        StateInputValidationException exception = new StateInputValidationException(nullMessage);

        // Then
        assertNull(exception.getMessage());
    }

}

