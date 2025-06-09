package academy.prog.julia.controllers;

import academy.prog.julia.dto.TasksGroupResponseDTO;
import academy.prog.julia.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskGroupController.class)
public class TaskGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private List<TasksGroupResponseDTO> objectList;


    @BeforeEach
    public void generateTestObject() {
        objectList = List.of(
                new TasksGroupResponseDTO("TestUrl1", false, "2023-11-20", "User1"),
                new TasksGroupResponseDTO("TestUrl2", false, "2022-11-20", "User2")
        );
    }

    @Test
    @WithMockUser
    public void testFindPendingTasks_groupNull_returnedListTasksGroupResponseDTO() throws Exception {
        when(taskService.findPaginated(any(Pageable.class), isNull())).thenReturn(objectList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/sorted/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].answerUrl").value("TestUrl1"))
                .andExpect(jsonPath("$[1].answerUrl").value("TestUrl2"));
    }

    @Test
    @WithMockUser
    public void testFindPendingTasksByGroupId_groupNull_returnedNotFoundException() throws Exception {
        when(taskService.findPaginated(any(Pageable.class), isNull())).thenThrow(new EntityNotFoundException("testError"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/sorted/pending"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    public void testFindPendingTasksByGroupId_groupNotNull_returnedListTasksGroupResponseDTO() throws Exception {
        String groupName = "TestGroup";
        when(taskService.findPaginated(any(Pageable.class), anyString())).thenReturn(objectList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/sorted/pending/{groupName}", groupName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].answerUrl").value("TestUrl1"))
                .andExpect(jsonPath("$[1].answerUrl").value("TestUrl2"));
    }

    @Test
    @WithMockUser
    public void testFindPendingTasks_groupNull_returnedNotFoundException() throws Exception {
        String groupName = "TestGroup";
        when(taskService.findPaginated(any(Pageable.class),anyString())).thenThrow(new EntityNotFoundException("testError"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/sorted/pending/{groupName}", groupName))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
    }
}
