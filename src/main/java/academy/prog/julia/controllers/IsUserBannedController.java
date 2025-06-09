package academy.prog.julia.controllers;

import academy.prog.julia.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for checking the blocked status of users.
 * This controller exposes an endpoint to verify whether a specific user is banned (blocked) in the system.
 */
@RestController
@RequestMapping("/api/user")
public class IsUserBannedController {
    private final UserService userService;

    /**
     * Constructor that injects the UserService dependency.
     *
     * @param userService the service responsible for user-related operations
     */
    public IsUserBannedController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to check if a user is blocked.
     * This method retrieves the blocked status of a user by their ID.
     *
     * @param userId the ID of the user whose blocked status is being checked
     * @return ResponseEntity containing a boolean value indicating if the user is blocked
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> checkUserStatus(@PathVariable Long userId) {
        boolean isBlocked = userService.isUserBlocked(userId);
        return ResponseEntity.ok(isBlocked);
    }

}
