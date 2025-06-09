package academy.prog.julia.telegram.states;

import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrintTextStateTest {

    private StateExecutionContext context;
    private PrintTextState printTextState;
    private static final String ATTRIBUTE_NAME = "testMessage";
    private static final String MESSAGE_CONTENT = "Hello, world!";

    @BeforeEach
    void setUp() {
        context = mock(StateExecutionContext.class);
        printTextState = new PrintTextState(ATTRIBUTE_NAME);
    }

    
    @Test
    void testEnter_SendsCorrectMessage() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn(MESSAGE_CONTENT);

        // When
        printTextState.enter(context);

        // Then
        verify(context, times(1)).sendMessage(MESSAGE_CONTENT);
    }

    
    @Test
    void testEnter_WithNullMessage() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn(null);

        // When
        printTextState.enter(context);

        // Thn
        verify(context, times(1)).sendMessage(null);
    }

    
    @Test
    void testEnter_WithEmptyMessage() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn("");

        // When
        printTextState.enter(context);

        // Then
        verify(context, times(1)).sendMessage("");
    }


    @Test
    void testEnter_WithWhitespaceMessage() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn("   ");

        // When
        printTextState.enter(context);

        // Then
        verify(context, times(1)).sendMessage("   ");
    }


    @Test
    void testEnter_MultipleCalls() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn(MESSAGE_CONTENT);

        // When
        printTextState.enter(context);
        printTextState.enter(context);

        // Then
        verify(context, times(2)).sendMessage(MESSAGE_CONTENT);
    }


    @Test
    void testEnter_AttributeNotFound() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn(null);

        // When
        printTextState.enter(context);

        // Then
        verify(context, times(1)).sendMessage(null);
    }


    @Test
    void testEnter_WithSendMessageException() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn(MESSAGE_CONTENT);
        doThrow(new RuntimeException("Error sending message")).when(context).sendMessage(MESSAGE_CONTENT);

        // When & Then
        assertThrows(RuntimeException.class, () -> printTextState.enter(context));

        verify(context, times(1)).sendMessage(MESSAGE_CONTENT);
    }


    @Test
    void testConstructor_SavesTextAttributeName() {
        // When & Then
        assertEquals(ATTRIBUTE_NAME, printTextState.textAttributeName);
    }


    @Test
    void testEnter_WithNumericMessage() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn("12345");

        // When
        printTextState.enter(context);

        // Then
        verify(context, times(1)).sendMessage("12345");
    }


    @Test
    void testEnter_WithSpecialCharactersMessage() {
        // Given
        when(context.getAttributeAsString(ATTRIBUTE_NAME)).thenReturn("@!#$%^&*()");

        // When
        printTextState.enter(context);

        // Then
        verify(context, times(1)).sendMessage("@!#$%^&*()");
    }

}