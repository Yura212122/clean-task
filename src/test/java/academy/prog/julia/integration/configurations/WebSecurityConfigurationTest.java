package academy.prog.julia.integration.configurations;

import academy.prog.julia.configurations.CookieClearFilter;
import academy.prog.julia.configurations.EncodersConfiguration;
import academy.prog.julia.configurations.WebSecurityConfiguration;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@EnableWebSecurity
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class WebSecurityConfigurationTest {

    @Mock
    private EncodersConfiguration passwordEncoder;

    @Mock
    private CookieClearFilter cookieClearFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Autowired
    private MockMvc mockMvc;

    private WebSecurityConfiguration webSecurityConfiguration;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        webSecurityConfiguration = new WebSecurityConfiguration(passwordEncoder, cookieClearFilter);
    }


    @Test
    @WithMockUser
    public void testSecurityFilterChainWithAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/studentslist"))
                .andExpect(status().isOk())
        ;
    }


    @Test
    void givenProtectedApi_whenGetRequestWithoutAuthentication_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/allEmployeesList"))
                .andExpect(status().isOk())
        ;
    }


    @Test
    void givenInvalidCredentials_whenPostLogin_thenNotFound() throws Exception {
        mockMvc.perform(post("/api/formLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Test2\", \"password\":\"12211221\"}"))
                .andExpect(status().isNotFound())
        ;
    }


    @Test
    public void testSecurityFilterChainWithPostRequest() throws Exception {
        mockMvc.perform(get("/api/login"))
                .andExpect(status().isForbidden())
        ;
    }


    @Test
    public void testAuthenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);

        when(passwordEncoder.userPasswordEncoder())
                .thenReturn(mock(org.springframework.security.crypto.password.PasswordEncoder.class));

        AuthenticationManager authenticationManager =
                webSecurityConfiguration.authenticationManager(userDetailsService);

        assertNotNull(authenticationManager);
        assertTrue(authenticationManager instanceof AuthenticationManager);
    }


    @Test
    public void testHttpSessionIdResolver() {
        HttpSessionIdResolver sessionIdResolver = webSecurityConfiguration.httpSessionIdResolver();
        assertNotNull(sessionIdResolver);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "12345")});

        List<String> cookieValues = sessionIdResolver.resolveSessionIds(request);

        assertFalse(cookieValues.isEmpty());
        assertEquals("12345", cookieValues.get(0));

        sessionIdResolver.setSessionId(request, response, "");
        verify(response, times(2)).addCookie(any(Cookie.class));
    }


    @Test
    public void testCookieClear() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "12345")});
        webSecurityConfiguration.clearCookie(request, response);

        verify(response, times(1)).addCookie(any(Cookie.class));
    }


    @Test
    public void testJsonConverter() {
        MappingJackson2HttpMessageConverter converter = webSecurityConfiguration.jsonConverter();
        assertNotNull(converter);
        assertEquals("application/json", converter.getSupportedMediaTypes().get(0).toString());
    }

}
