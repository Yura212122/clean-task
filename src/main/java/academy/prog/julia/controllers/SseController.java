package academy.prog.julia.controllers;

import academy.prog.julia.services.SseService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * REST controller for handling Server-Sent Events (SSE).
 * Uses SseService to manage emitters and send notifications to the frontend.
 */
@RestController
public class SseController {

    private final SseService sseService;

    /**
     * Constructor to inject the SseService.
     *
     * @param sseService service for managing SSE emitters and notifications
     */
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    /**
     * Endpoint for establishing an SSE connection.
     * Returns an SseEmitter that allows the frontend to receive real-time updates.
     *
     * @param response HttpServletResponse to set headers for cross-origin requests
     * @return SseEmitter for the client connection
     */
    @GetMapping(value = "/sse", produces = "text/event-stream")
    public SseEmitter streamSseMvc(HttpServletResponse response) {
        return sseService.createEmitter(response);
    }

}
