package academy.prog.julia.controllers;

import academy.prog.julia.exception_handlers.LoginExceptionHandler;
import academy.prog.julia.json_requests.LoginRequest;
import academy.prog.julia.json_responses.LoginResponse;
import academy.prog.julia.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final Logger LOGGER = LogManager.getLogger(LoginExceptionHandler.class);

    private final LoginService loginService;

    public LoginController(
            LoginService loginService
    ) {
        this.loginService = loginService;
    }

    @PostMapping("/formLogin")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        LoginResponse loginResponse = loginService.login(loginRequest, request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> handleLogout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            LOGGER.info("Logout is successful.");
            return ResponseEntity.ok("Logout successful");
        } else {
            LOGGER.info("Not authenticated from logout.");
            return ResponseEntity.status(401).body("Not authenticated");
        }
    }

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "Hello from the backend!");
        return responseData;
    }
}