package academy.prog.julia.services;

import academy.prog.julia.dto.TasksGroupResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for handling operations related to task groups.
 */
@Service
public class TaskGroupService {

    private final TaskService taskService;

    // Default values for pagination
    private final static Integer CURRENT_PAGE_DEFAULT = 1;
    private final static Integer PAGE_SIZE_DEFAULT = 6;

    /**
     * Constructs a new TaskGroupService with the given TaskService.
     *
     * @param taskService the TaskService to be used by this service
     */
    public TaskGroupService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Finds tasks by group name with pagination support.
     *
     * @param groupName the name of the task group to search for
     * @param page an Optional representing the current page number (1-based index)
     * @param size an Optional representing the page size (number of tasks per page)
     * @return a list of TasksGroupResponseDTO representing the tasks in the specified group
     */
    @Transactional(readOnly = true)
    public List<TasksGroupResponseDTO> findTasksByGroupName(
            String groupName,
            Optional<Integer> page,
            Optional<Integer> size
    ) {
        Integer currentPage = page.orElse(CURRENT_PAGE_DEFAULT);
        Integer pageSize = size.orElse(PAGE_SIZE_DEFAULT);

        return taskService.findPaginated(
                PageRequest.of(currentPage - 1, pageSize), groupName
        );
    }

}
