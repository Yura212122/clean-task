package academy.prog.julia.integration.telegram.validators;

import academy.prog.julia.telegram.validators.CommandStateInputValidator;
import academy.prog.julia.telegram.validators.StateInputValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CommandStateInputValidatorTest {

    private CommandStateInputValidator createValidator() {
        return new CommandStateInputValidator() {
            @Override
            public void validate(String input) throws StateInputValidationException {
                if (input == null || input.isEmpty()) {
                    throw new StateInputValidationException("Input cannot be empty");
                }
            }
        };
    }


    @Test
    void testValidate_ValidInput() {
        // Given
        CommandStateInputValidator validator = createValidator();

        // When
        try {
            validator.validate("Valid Input");

        // Then
        } catch (StateInputValidationException e) {
            fail("Validation failed for valid input");
        }
    }

    @Test
    void testValidate_EmptyInput() {
        // Given
        CommandStateInputValidator validator = createValidator();

        // When
        StateInputValidationException thrown = assertThrows(StateInputValidationException.class, () -> {
            validator.validate("");
        });

        // Then
        assertEquals("Input cannot be empty", thrown.getMessage());
    }

    @Test
    void testValidate_NullInput() {
        // Given
        CommandStateInputValidator validator = createValidator();

        // When
        StateInputValidationException thrown = assertThrows(StateInputValidationException.class, () -> {
            validator.validate(null);
        });

        // Then
        assertEquals("Input cannot be empty", thrown.getMessage());
    }

}
