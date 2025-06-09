package academy.prog.julia;

import academy.prog.julia.controllers.RegistrationController;
import academy.prog.julia.json_requests.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class RegistrationControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationController registrationController;

    @Test
    public void testNotNull() throws Exception {
        assertThat(registrationController).isNotNull();
    }

    @Test
    public void testRegistrationSuccess() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("John");
        registrationRequest.setSurname("Doe");
        registrationRequest.setPhone("123456789");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setPasswordConfirm("password");
        registrationRequest.setClientInvite("validInviteCode");
        registrationRequest.setCaptchaResponse("validCaptchaResponse");

        this.mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"message\":\"Registration successful\"}"));
    }

    @Test
    public void testRegistrationWithInvalidCaptcha() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("John");
        registrationRequest.setSurname("Doe");
        registrationRequest.setPhone("123456789");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setPasswordConfirm("password");
        registrationRequest.setClientInvite("validInviteCode");
        registrationRequest.setCaptchaResponse("invalidCaptchaResponse");

        this.mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistrationWithExistingEmail() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("John");
        registrationRequest.setSurname("Doe");
        registrationRequest.setPhone("123456789");
        registrationRequest.setEmail("existing.email@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setPasswordConfirm("password");
        registrationRequest.setClientInvite("validInviteCode");
        registrationRequest.setCaptchaResponse("validCaptchaResponse");

        this.mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().json("{\"message\":\"Email already in use\"}"));
    }

    @Test
    public void testRegistrationWithInvalidPassword() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("John");
        registrationRequest.setSurname("Doe");
        registrationRequest.setPhone("123456789");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("pass");
        registrationRequest.setPasswordConfirm("pass");
        registrationRequest.setClientInvite("validInviteCode");
        registrationRequest.setCaptchaResponse("validCaptchaResponse");

        this.mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Password too short\"}"));
    }

    @Test
    public void testRegistrationWithMismatchedPasswords() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("John");
        registrationRequest.setSurname("Doe");
        registrationRequest.setPhone("123456789");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setPasswordConfirm("differentpassword");
        registrationRequest.setClientInvite("validInviteCode");
        registrationRequest.setCaptchaResponse("validCaptchaResponse");

        this.mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Passwords do not match\"}"));
    }

    @Test
    public void testNullInviteCode() throws Exception {
        mockMvc.perform(post("/api/invite")
                        .param("inviteCode", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testValidInviteCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/invite")
                        .param("inviteCode", "validInviteCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void testInvalidInviteCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/invite")
                        .param("inviteCode", "invalidInviteCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("failed"));
    }
}
