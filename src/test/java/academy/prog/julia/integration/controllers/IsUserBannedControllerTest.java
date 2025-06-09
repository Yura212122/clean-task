package academy.prog.julia.integration.controllers;

import academy.prog.julia.exceptions.UserNotFoundException;
import academy.prog.julia.services.UserService;
import org.junit.jupiter.api.Test;
import academy.prog.julia.controllers.IsUserBannedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(IsUserBannedController.class)
public class IsUserBannedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "Test", roles = "ADMIN_ROLE", password = "111111")
    void testCheckUserStatus_userIsBlocked_shouldReturnTrue() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.isUserBlocked(userId)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/user/status/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
        ;
    }


    @Test
    @WithMockUser(username = "Test", roles = "ADMIN_ROLE", password = "111111")
    void testCheckUserStatus_userIsNotBlocked_shouldReturnFalse() throws Exception {
        // Given
        Long userId = 2L;
        when(userService.isUserBlocked(userId)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/user/status/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false))
        ;
    }


    @Test
    @WithMockUser(username = "Test", roles = "ADMIN_ROLE", password = "111111")
    void testCheckUserStatus_userNotFound_shouldReturnNotFound() throws Exception {
        // Given
        Long userId = 3L;
        doThrow(new UserNotFoundException("User not found with id: " + userId))
                .when(userService).isUserBlocked(userId)
        ;

        // When & Then
        mockMvc.perform(get("/api/user/status/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: User not found with id: " + userId))
        ;
    }

}
