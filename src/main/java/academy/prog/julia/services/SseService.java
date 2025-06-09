package academy.prog.julia.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling Server-Sent Events (SSE).
 * Manages SSE emitters and handles sending messages to the frontend.
 */
@Service
public class SseService {

    @Value("${allowed_cross_origin}")
    private String crossOrigin;

    // List of active SSE emitters to push events to clients
    private final List<SseEmitter> emitters = new ArrayList<>();

    /**
     * Sets up a new SseEmitter and adds it to the list of active emitters.
     *
     * @param response HttpServletResponse to set the Access-Control-Allow-Origin header
     * @return the initialized SseEmitter
     */
    public SseEmitter createEmitter(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", crossOrigin);
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        // Remove emitter on completion or timeout
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    /**
     * Sends a message to all active SSE clients.
     *
     * @param message the message to be sent to all clients
     */
    public void notifyFrontend(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                deadEmitters.add(emitter);  // Collect dead emitters for removal
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
