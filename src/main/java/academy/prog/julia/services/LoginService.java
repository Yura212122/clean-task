package academy.prog.julia.services;

import academy.prog.julia.json_requests.LoginRequest;
import academy.prog.julia.json_responses.LoginResponse;
import academy.prog.julia.model.User;
import academy.prog.julia.telegram.BotCredentials;
import academy.prog.julia.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class that handles user login functionality.
 * It authenticates the user, sets up session attributes, and generates a login response.
 */
@Service
public class LoginService {
    private static final Logger LOGGER = LogManager.getLogger(LoginService.class);

    /**
     * Authentication manager to handle authentication process
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Bot credentials used to generate Telegram-related URL
     */
    private final BotCredentials botCredentials;

    /**
     * Constructor for LoginService, initializes required dependencies.
     *
     * @param authenticationManager the authentication manager for handling user authentication
     * @param botCredentials credentials for the Telegram bot
     */
    public LoginService(
            AuthenticationManager authenticationManager,
            BotCredentials botCredentials
    ) {
        this.authenticationManager = authenticationManager;
        this.botCredentials = botCredentials;
    }

    /**
     * Handles the login process. Authenticates the user and sets up the session.
     *
     * @param loginRequest the login request containing the user's email and password
     * @param request the HTTP request used to manage the session
     * @return a LoginResponse object containing login details and session information
     */
    @Transactional
    public LoginResponse login(
            LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

        HttpSession session = request.getSession();

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        Object principal = authenticationResponse.getPrincipal();
        String uniqueId = ((User) principal).getUniqueId();
        String telegramUrl = String.format(Utils.TELEGRAM_URL, botCredentials.getBotName(), uniqueId);

        LOGGER.info("Login is successful: {}", ((User) principal).getUsername());

        LoginResponse loginResponse = new LoginResponse("Login is successful", principal, telegramUrl);
        loginResponse.setJSessionId(session.getId());

        return loginResponse;
    }

}
