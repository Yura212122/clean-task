package academy.prog.julia.controllers;


import academy.prog.julia.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // <-- правильный импорт

@WebMvcTest(IsUserBannedController.class)
public class BannedTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldReturnTrueIfUserIsBanned() throws Exception {
        Long userId = 1L;

        Mockito.when(userService.isUserBlocked(userId)).thenReturn(true);

        mockMvc.perform(get("/api/user/status/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void ShouldReturnFalseIfUserIsNotBanned() throws Exception {
        Long userId = 1L;
        Mockito.when(userService.isUserBlocked(userId)).thenReturn(false);
        mockMvc.perform(get("/api/user/status/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

}


