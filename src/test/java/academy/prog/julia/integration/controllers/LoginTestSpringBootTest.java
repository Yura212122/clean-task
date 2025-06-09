package academy.prog.julia.integration.controllers;

import academy.prog.julia.controllers.LoginController;
import academy.prog.julia.json_requests.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class LoginTestSpringBootTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginController loginController;


    @Test
    public void testNotNull() throws Exception {
        assertThat(loginController).isNotNull();
    }

    @Test
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/api/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello from the backend!")))
        ;
    }

    @Test
    @Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-task-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-task-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void accessToTaskTest() throws Exception {
        this.mockMvc.perform(get("/api/tasks/2"))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void accessDeniedWrongUriTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("111111");

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/auth/formLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
        ;
    }


    @Test
    @Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void correctLoginTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("111111");

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/formLogin") // Перевірте шлях
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Login is successful\"}"))
        ;
    }

    @Test
    @WithMockUser(username = "Test", roles = "ADMIN_ROLE", password = "111111")
    public void authorizedUserShouldAccessEndpointTest() throws Exception {
        this.mockMvc.perform(get("/api/studentslist"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void badCredentialsTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("donot@know.ua");
        loginRequest.setPassword("123456");

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/formLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    public void successLogoutTest() throws Exception {
        this.mockMvc.perform(post("/api/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Logout successful")))
        ;
    }

    @Test
    public void accessDeniedLoginTest() throws Exception {
        String email = "test@example.com";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest(email, password);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/formLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
        ;
    }

}