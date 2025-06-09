package academy.prog.julia.services;

import academy.prog.julia.json_requests.LoginRequest;
import academy.prog.julia.json_responses.LoginResponse;
import academy.prog.julia.model.User;
import academy.prog.julia.telegram.BotCredentials;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class LoginServiceTest {
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private BotCredentials botCredentials;

    @Autowired
    private LoginService loginService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession httpSession;

    @Mock
    private Authentication authentication;

    private LoginRequest loginRequest;
    private User testUser;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Initialize User
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("securePassword");
        testUser.setUniqueId("123123123");
        testUser.setSurname("TestUser");

        // Initialize Login Request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("securePassword");

        // Mock HttpSession and BotCredentials
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(botCredentials.getBotName()).thenReturn("TestBot");
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testLogin_Success() {
        // Mock Authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(httpSession.getId()).thenReturn("session123");

        // Perform login
        LoginResponse response = loginService.login(loginRequest, httpServletRequest);

        // Assertions
        assertEquals("Login is successful", response.getMessage());
        assertEquals("session123", response.getJSessionId());
        assertEquals(testUser, response.getPrincipal());
        assertEquals("https://t.me/TestBot?start=" + testUser.getUniqueId(), response.getTelegramUrl());

        // Verify interactions
        verify(httpServletRequest, times(1)).getSession();
        verify(httpSession, times(1)).setAttribute(eq("SPRING_SECURITY_CONTEXT"), eq(SecurityContextHolder.getContext()));
    }

    @Test
    public void testLogin_Failure() {
        // Mock Authentication failure
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        RuntimeException exception = null;

        try {
            loginService.login(loginRequest, httpServletRequest);
        } catch (RuntimeException e) {
            exception = e;
        }

        // Assertions
        assertNotNull(exception, "Exception should not be null");
        assertEquals("Authentication failed", exception.getMessage());
        verify(httpServletRequest, never()).getSession();
    }
}
