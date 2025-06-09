package academy.prog.julia.controllers;

import academy.prog.julia.dto.UserForAdminFindingDTO;
import academy.prog.julia.exceptions.BadRequestException;
import academy.prog.julia.exceptions.UserNotFoundException;
import academy.prog.julia.model.*;
import academy.prog.julia.services.AdminService;
import academy.prog.julia.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/findByEmail")
    public ResponseEntity<?> findUserByEmailLike(@RequestParam(required = false) String email) {
        try {
            List<User> users = userService.findByEmailLike(email);
            return ResponseEntity.ok(users.stream()
                    .map(UserForAdminFindingDTO::userToDTO)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/messageByEmail")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> massageForUserByEmail(@RequestBody Map<String, String> payload) {
        try {
            //check authentication
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()
//                    || authentication instanceof AnonymousAuthenticationToken) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authorized.");
//            }
            String email = payload.get("email");
            String message = payload.get("message");
            if (email == null || message == null || message.isEmpty()) {
                return ResponseEntity.badRequest().body("Email or message are required.");
            }

            User user = userService.findByEmail(email);

            adminService.sendMessageByUserEmail(user, message);
            return ResponseEntity.ok("Message sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getAllRole")
    public ResponseEntity<?> getAllRole() {
        try {
            return ResponseEntity.ok(List.of("STUDENT", "ADMIN", "TEACHER", "MANAGER", "MENTOR"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/messageByRole")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> massageForUsersByRole(@RequestBody Map<String, String> payload) {
        try {
            //check authentication
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()
//                    || authentication instanceof AnonymousAuthenticationToken) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authorized.");
//            }

            String role = payload.get("role");
            String message = payload.get("message");
            if (role == null || message == null || message.isEmpty()) {
                return ResponseEntity.badRequest().body("role or message are required.");
            }

            List<User> users = userService.findAllByRole(role);

            StringBuilder sb = adminService.sendMessageByUserEmail(users, message);
            sb.append("emails has no attached telegram.");

            return ResponseEntity.ok(sb.length() == 48 ?
                    "Message sent successfully." :
                    "Message sent successfully, but " + sb.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}