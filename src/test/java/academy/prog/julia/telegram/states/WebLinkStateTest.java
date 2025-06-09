package academy.prog.julia.telegram.states;

import academy.prog.julia.configurations.ApiConfigProperties;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class WebLinkStateTest {

    private ApiConfigProperties apiConfigProperties;
    private StateExecutionContext context;
    private WebLinkState webLinkState;

    @BeforeEach
    void setUp() {
        apiConfigProperties = Mockito.mock(ApiConfigProperties.class);
        context = Mockito.mock(StateExecutionContext.class);
        webLinkState = new WebLinkState(false, apiConfigProperties);
    }


    @Test
    void givenValidApiConfig_whenEnter_thenSendMessageWithShortenedUrlWebLinkState_Test() {
        // Given
        String frontendUrl = "https://example.com";
        String originalUrl = frontendUrl + "/login";
        String shortenedUrl = "https://short.url";
        when(apiConfigProperties.getFrontendUrl()).thenReturn(frontendUrl);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getShortenedUrl(originalUrl)).thenReturn(shortenedUrl);

            // When
            webLinkState.enter(context);

            // Then
            String expectedMessage = "Link to log into your account: " + shortenedUrl;
            verify(context).sendMessage(expectedMessage);
        }
    }


    @Test
    void testEnter_SuccessfulExecution_Test() {
        // Given
        String frontendUrl = "https://example.com";
        String expectedUrl = frontendUrl + "/login";
        String shortenedUrl = "https://short.ly/abc123";

        when(apiConfigProperties.getFrontendUrl()).thenReturn(frontendUrl);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getShortenedUrl(expectedUrl)).thenReturn(shortenedUrl);

            // When
            webLinkState.enter(context);

            // Then
            verify(context).sendMessage("Link to log into your account: " + shortenedUrl);
        }
    }


    @Test
    void givenNoFrontendUrl_whenEnter_thenNoMessageSent_Test() {
        // Given
        when(apiConfigProperties.getFrontendUrl()).thenReturn(null);

        // When
        webLinkState.enter(context);

        // Then
        verify(context).sendMessage("Link to log into your account: null/login");
    }


    @Test
    void givenUrlShorteningFails_whenEnter_thenNoMessageSent_Test() {
        // Given
        String frontendUrl = "https://example.com";
        String originalUrl = frontendUrl + "/login";
        when(apiConfigProperties.getFrontendUrl()).thenReturn(frontendUrl);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils
                    .getShortenedUrl(originalUrl))
                    .thenThrow(new RuntimeException("URL shortening failed")
            );

            // When & Then
            assertThrows(RuntimeException.class, () -> webLinkState.enter(context));

            verify(context, never()).sendMessage(anyString());
        }
    }


    @Test
    void givenInputNeeded_whenEnter_thenInputIsExpected_Test() {
        // Given
        WebLinkState webLinkStateWithInput = new WebLinkState(true, apiConfigProperties);

        // When
        webLinkStateWithInput.enter(context);

        // Then
        assertTrue(webLinkStateWithInput.isInputNeeded());
    }


    @Test
    void givenNullContext_whenEnter_thenThrowNullPointerException_Test() {
        // Given
        StateExecutionContext nullContext = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> webLinkState.enter(nullContext));
    }


    @Test
    void givenValidApiConfig_whenEnter_thenMessageContainsCorrectShortenedUrl_Test() {
        // Given
        String frontendUrl = "https://example.com";
        String originalUrl = frontendUrl + "/login";
        String shortenedUrl = "https://short.url";
        when(apiConfigProperties.getFrontendUrl()).thenReturn(frontendUrl);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getShortenedUrl(originalUrl)).thenReturn(shortenedUrl);

            // When
            webLinkState.enter(context);

            // Then
            String expectedMessage = "Link to log into your account: " + shortenedUrl;
            verify(context).sendMessage(expectedMessage);
        }
    }

}