package academy.prog.julia.controllers;

import academy.prog.julia.dto.StudentDTO;
import academy.prog.julia.dto.UserDTO;
import academy.prog.julia.json_responses.EmployeesResponse;
import academy.prog.julia.services.UserListService;
import academy.prog.julia.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller to handle requests related to user and employee lists.
 * Provides endpoints to retrieve lists of users, students, and employees based on specific criteria.
 */
@RestController
@RequestMapping("/api")
public class UserListController {

    private final UserListService userListService;
    private final UserService userService;

    /**
     * Constructor for injecting required service dependencies.
     *
     * @param userListService - Service handling the logic related to users, namely their display in the web view.
     * @param userService - Service handling the logic related to users.
     */
    public UserListController(
            UserListService userListService,
            UserService userService
    ) {
        this.userListService = userListService;
        this.userService = userService;
    }

    /**
     * Fetches a paginated list of users based on a valid random URL.
     *
     * @param randomURL - Randomly generated URL that must be valid for user retrieval.
     * @param page      - The page number for pagination (default is 0).
     * @return A response containing the list of users or an error if the URL is invalid.
     */
    @GetMapping("/{randomURL}/userlist")
    public ResponseEntity<Map<String, List<UserDTO>>> showUsers(
            @PathVariable String randomURL,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        return userListService.getUsersForRandomURLFromUserListService(randomURL, page);
    }

    /**
     * Retrieves a list of all students.
     *
     * @return A response containing the list of students.
     */
    @GetMapping("/studentslist")
    public ResponseEntity<List<StudentDTO>> showAllStudents() {
        List<StudentDTO> result = userService.findAllStudents();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Retrieves a list of all employees that have specific roles such as ADMIN, TEACHER, MANAGER, or MENTOR.
     *
     * @return A response containing the list of employees filtered by roles.
     */
    @GetMapping("/allEmployeesList")
    public ResponseEntity<List<EmployeesResponse>> getAllEmployees() {
        return userListService.getAllEmployeesResponse();
    }

}
