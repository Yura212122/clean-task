package academy.prog.julia.controllers;

import academy.prog.julia.dto.TasksGroupResponseDTO;
import academy.prog.julia.services.TaskGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing and retrieving tasks.
 *
 * This controller provides endpoints for fetching pending tasks, either globally or filtered by
 * task group name.
 * The results are paginated and can be customized via page and size parameters.
 *
 * This controller is not currently in use.
 */
@RestController
@RequestMapping("/api/tasks/sorted")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    /**
     * Constructor for TaskGroupController.
     *
     * @param taskGroupService Service for handling task-related operations.
     */
    public TaskGroupController(TaskGroupService taskGroupService) {
        this.taskGroupService = taskGroupService;
    }

    /**
     * Retrieves a list of pending tasks without filtering by group name.
     * The result is paginated, with the ability to specify the page and size of the results.
     *
     * @param page The page number (optional).
     * @param size The number of items per page (optional).
     * @return A paginated list of pending tasks.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<TasksGroupResponseDTO>> findPendingTasks(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size)
    {
        return ResponseEntity.ok(taskGroupService.findTasksByGroupName(null, page, size));
    }

    /**
     * Retrieves a list of pending tasks filtered by the specified group name.
     * The result is paginated, with the ability to specify the page and size of the results.
     *
     * @param groupName The name of the task group to filter by.
     * @param page The page number (optional).
     * @param size The number of items per page (optional).
     * @return A paginated list of pending tasks for the specified group.
     */
    @GetMapping("/pending/{groupName}")
    public ResponseEntity<List<TasksGroupResponseDTO>> findPendingTasksByGroupId(
            @PathVariable String groupName,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size)
    {
        return ResponseEntity.ok(taskGroupService.findTasksByGroupName(groupName, page, size));
    }

}
