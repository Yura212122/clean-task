package academy.prog.julia.integration.telegram.validators;

import academy.prog.julia.telegram.validators.IntegerValidator;
import academy.prog.julia.telegram.validators.StateInputValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class IntegerValidatorTest {

    private final IntegerValidator integerValidator = new IntegerValidator();

    @Test
    void testValidate_ValidInteger() {
        // Given
        String validInput = "123";

        // When
        try {
            integerValidator.validate(validInput);

        // Then
        } catch (StateInputValidationException e) {
            fail("Validation failed for valid input");
        }
    }

    @Test
    void testValidate_InvalidInteger() {
        // Given
        String invalidInput = "abc";

        // When
        StateInputValidationException thrown = assertThrows(StateInputValidationException.class, () -> {
            integerValidator.validate(invalidInput);
        });

        // Then
        assertTrue(thrown.getMessage().contains("is not the integer"));
    }

    @Test
    void testValidate_EmptyString() {
        // Given
        String emptyInput = "";

        // When
        StateInputValidationException thrown = assertThrows(StateInputValidationException.class, () -> {
            integerValidator.validate(emptyInput);
        });

        // Then
        assertTrue(thrown.getMessage().contains("is not the integer"));
    }

    @Test
    void testValidate_NullInput() {
        // Given (input with value = null)
        // When (call validate() method)
        // Then (throws exception0
        assertThrows(StateInputValidationException.class, () -> {
            integerValidator.validate(null);
        });
    }

}

