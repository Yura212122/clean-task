package academy.prog.julia.services;

import academy.prog.julia.dto.UserDTO;
import academy.prog.julia.json_responses.EmployeesResponse;
import academy.prog.julia.model.User;
import academy.prog.julia.model.UserRole;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static academy.prog.julia.telegram.states.ListUsersWebState.generatedUrlForUserList;

/**
 * Service class for managing user lists and handling user-related queries.
 * This service provides methods for retrieving users based on URL validation and fetching
 * employee details with specific roles.
 */
@Service
public class UserListService {

    private static final Integer COUNT_FOR_PAGE = 20;

    private final UserService userService;

    /**
     * Constructor for UserListService. Uses constructor-based dependency injection
     * for the userService required for handling user-related operations.
     *
     * @param userService the service for managing user-related operations
     */
    public UserListService(
            UserService userService
    ) {
        this.userService = userService;
    }

    /**
     * Retrieves a paginated list of users based on a random URL.
     * Validates the provided URL against a predefined list. If the URL is valid,
     * it fetches users from all groups and returns the result.
     *
     * @param randomURL the URL to validate
     * @param page the page number for pagination
     * @return a ResponseEntity containing a map of user DTOs if the URL is valid,
     *         otherwise an error response with HTTP status NOT_FOUND
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, List<UserDTO>>> getUsersForRandomURLFromUserListService(
            String randomURL,
            int page
    ) {
        if (generatedUrlForUserList.contains(randomURL)) {
            Map<String, List<UserDTO>> result = userService.findAllByAllGroups(PageRequest.of(page, COUNT_FOR_PAGE));

            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "URL doesn't exist");

            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all employees with specific roles (ADMIN, TEACHER, MANAGER, MENTOR)
     * and returns their details in a list of EmployeesResponse objects.
     *
     * @return a ResponseEntity containing a list of EmployeesResponse objects
     *         representing users with the specified roles
     */
    @Transactional
    public ResponseEntity<List<EmployeesResponse>> getAllEmployeesResponse() {
        List<User> users = userService.findAll();
        List<EmployeesResponse> employeesResponses = new ArrayList<>();

        for (User user : users) {
            if (userHasAnyRole(user, UserRole.ADMIN, UserRole.TEACHER, UserRole.MANAGER, UserRole.MENTOR)) {
                EmployeesResponse employee = new EmployeesResponse();
                employee.setId(user.getId());
                employee.setName(user.getName());
                employee.setSurname(user.getSurname());
                employee.setRole(user.getRole().toString().substring(5));
                employeesResponses.add(employee);
            }
        }

        return ResponseEntity.ok(employeesResponses);
    }

    private boolean userHasAnyRole(
            User user,
            UserRole... roles
    ) {
        for (UserRole role : roles) {
            if (user.getAuthorities().contains(new SimpleGrantedAuthority(role.toString()))) {
                return true;
            }
        }

        return false;
    }

}
