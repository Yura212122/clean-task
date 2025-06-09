package academy.prog.julia.integration.controllers;

import academy.prog.julia.controllers.SseController;
import academy.prog.julia.services.SseService;
import academy.prog.julia.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SseController.class)
public class SseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SseService sseService;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(username = "BlockedUser", roles = "ADMIN")
    void testSseConnection_UserSuccessAccessSse() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.isUserBlocked(userId)).thenReturn(true);

        // When
        mockMvc.perform(get("/sse"))
                .andExpect(status().isOk());

        // Then
    }


    @Test
    void testSseConnection_BlockedUserCannotAccessSse() throws Exception {
        // Given

        // When
        mockMvc.perform(get("/sse"))
                .andExpect(status().isUnauthorized())
        ;

        //Then
    }


    @Test
    @WithMockUser(username = "BlockedUser", roles = "ADMIN")
    void testSseConnection_Success() throws Exception {
        // Given
        Long userId = 1L;
        SseEmitter mockEmitter = new SseEmitter();
        when(userService.isUserBlocked(userId)).thenReturn(true);
        when(sseService.createEmitter(any(HttpServletResponse.class))).thenReturn(mockEmitter);

        // When
        MvcResult result = mockMvc.perform(get("/sse")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andReturn()
        ;

        // Then
        String content = result.getResponse().getContentAsString();
        System.out.println("Response content: " + content); // will be empty(because connection is opening)
    }


    @Test
    @WithMockUser(username = "BlockedUser", roles = "ADMIN")
    void testSseConnection_ReturnsEmitter() throws Exception {
        // Given
        SseEmitter mockEmitter = new SseEmitter();
        Long userId = 1L;
        when(userService.isUserBlocked(userId)).thenReturn(true);
        when(sseService.createEmitter(any(HttpServletResponse.class))).thenReturn(mockEmitter);

        // When
        mockMvc.perform(get("/sse")
                        .header("Origin", "http://localhost:3000")) // imitation cors-request
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
        ;

        // Then
        verify(sseService, times(1)).createEmitter(any(HttpServletResponse.class));
    }


    @Test
    @WithMockUser(username = "BlockedUser", roles = "ADMIN")
    void testSseEmitter_CompletesOnTimeout() throws Exception {
        // Given
        AtomicBoolean isTimedOut = new AtomicBoolean(false);

        SseEmitter emitter = new SseEmitter(500L);
        emitter.onTimeout(() -> isTimedOut.set(true));

        Long userId = 1L;
        when(userService.isUserBlocked(userId)).thenReturn(true);
        when(sseService.createEmitter(any(HttpServletResponse.class))).thenReturn(emitter);

        // When
        mockMvc.perform(get("/sse"))
                .andExpect(status().isOk())
        ;

        // Then
        Thread.sleep(600);
        assertThat(isTimedOut.get()).isFalse();
    }


    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void testSseEmitter_IsConfiguredNotCorrectly() throws Exception {
        // Given
        SseEmitter mockEmitter = new SseEmitter();
        ArgumentCaptor<HttpServletResponse> captor = ArgumentCaptor.forClass(HttpServletResponse.class);
        when(sseService.createEmitter(captor.capture())).thenReturn(mockEmitter);

        // When
        mockMvc.perform(get("/sse"))
                .andExpect(status().isOk())
        ;

        // Then
        HttpServletResponse capturedResponse = captor.getValue();
        assertThat(capturedResponse).isNotNull();
    }


    @Test
    @WithMockUser(username = "BlockedUser", roles = "ADMIN")
    void testSseEmitter_RemovesOnCompletion() throws Exception {
        // Given
        SseEmitter emitter = new SseEmitter();
        Long userId = 1L;
        when(userService.isUserBlocked(userId)).thenReturn(true);
        when(sseService.createEmitter(any(HttpServletResponse.class))).thenReturn(emitter);

        // When
        mockMvc.perform(get("/sse"))
                .andExpect(status().isOk())
        ;

        // Then
        emitter.complete();
        verify(sseService, times(1)).createEmitter(any(HttpServletResponse.class));
    }

}
