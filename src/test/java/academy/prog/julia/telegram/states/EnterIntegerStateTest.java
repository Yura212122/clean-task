package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.telegram.validators.StateInputValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnterIntegerStateTest {

    private EnterIntegerState state;
    private StateExecutionContext context;
    private final String inputAttributeName = "testInteger";

    @BeforeEach
    void setUp() {
        state = new EnterIntegerState("Enter an integer:", inputAttributeName);
        context = Mockito.mock(StateExecutionContext.class);
    }


    @Test
    void testValid_IntegerInput() {
        Mockito.when(context.getMessage()).thenReturn("42");
        assertDoesNotThrow(() -> state.handleInput(context));
        Mockito.verify(context).putAttribute(inputAttributeName, 42);
    }


    @Test
    void testValid_NegativeIntegerInput() {
        Mockito.when(context.getMessage()).thenReturn("-17");
        assertDoesNotThrow(() -> state.handleInput(context));
        Mockito.verify(context).putAttribute(inputAttributeName, -17);
    }


    @Test
    void testValid_ZeroInput() {
        Mockito.when(context.getMessage()).thenReturn("0");
        assertDoesNotThrow(() -> state.handleInput(context));
        Mockito.verify(context).putAttribute(inputAttributeName, 0);
    }


    @Test
    void testInvalid_NonNumericInput() {
        Mockito.when(context.getMessage()).thenReturn("abc");
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }


    @Test
    void testInvalid_DecimalInput() {
        Mockito.when(context.getMessage()).thenReturn("3.33");
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }


    @Test
    void testInvalid_SpecialCharactersInput() {
        Mockito.when(context.getMessage()).thenReturn("@#$%");
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }


    @Test
    void testEmptyInput() {
        Mockito.when(context.getMessage()).thenReturn("");
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }


    @Test
    void testNullInput() {
        Mockito.when(context.getMessage()).thenReturn(null);
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }


    @Test
    void testWhitespaceInput() {
        Mockito.when(context.getMessage()).thenReturn("   ");
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }


    @Test
    void testExtremelyLargeNumber() {
        Mockito.when(context.getMessage()).thenReturn("999999999999999999999999");
        assertThrows(StateInputValidationException.class, () -> state.handleInput(context));
    }

}
