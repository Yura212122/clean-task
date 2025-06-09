package academy.prog.julia.services;

import academy.prog.julia.dto.UserDTO;
import academy.prog.julia.json_responses.EmployeesResponse;
import academy.prog.julia.model.AdminUser;
import academy.prog.julia.model.TeacherUser;
import academy.prog.julia.model.User;
import academy.prog.julia.telegram.states.ListUsersWebState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class UserListServiceTest {

    @InjectMocks
    private UserListService userListService;

    @Mock
    private UserService userService;

    @Test
    void getUsersForRandomURLFromUserListService_InvalidURL() {
        String randomURL = "invalid-url";
        int page = 0;
        ResponseEntity<Map<String, List<UserDTO>>> response =
                userListService.getUsersForRandomURLFromUserListService(randomURL, page);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Error"));
    }

    @Test
    void getUsersForRandomURLFromUserListService_ValidURL() {
        String randomURL = "valid-url";
        int page = 0;
        ListUsersWebState.generatedUrlForUserList.add(randomURL);
        Map<String, List<UserDTO>> mockResult = Map.of("users", new ArrayList<>());
        Mockito.when(userService.findAllByAllGroups(PageRequest.of(page, 20))).thenReturn(mockResult);

        ResponseEntity<Map<String, List<UserDTO>>> response =
                userListService.getUsersForRandomURLFromUserListService(randomURL, page);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllEmployeesResponse() {
        AdminUser adminUser = new AdminUser();
        adminUser.setName("Nameadmin");
        adminUser.setSurname("Surnameadmin");
        adminUser.setEmail("Emailadmin");
        adminUser.setPhone("000000000000");
        adminUser.setEmail("Passwordadmin");

        TeacherUser teacherUser = new TeacherUser();
        teacherUser.setName("Nameteacher");
        teacherUser.setSurname("Surnameteacher");
        teacherUser.setEmail("Emailteacher");
        teacherUser.setPhone("000000000000");
        teacherUser.setEmail("Passwordteacher");
        User user1 = createUser(1);
        User user2 = createUser(2);

        List<User> mockUsers = new ArrayList<>(List.of(adminUser,
                user1,
                user2,
                teacherUser));

        Mockito.when(userService.findAll()).thenReturn(mockUsers);

        ResponseEntity<List<EmployeesResponse>> response = userListService.getAllEmployeesResponse();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<EmployeesResponse> employees = response.getBody();

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("Nameadmin", employees.get(0).getName());
        assertEquals("Nameteacher", employees.get(1).getName());
    }





    static User createUser(Integer userId){
        User user = new User();
        user.setId(userId.longValue());
        user.setName("Name" + userId);
        user.setSurname("Surname");
        user.setEmail("email" + userId + "@example.com");
        user.setPhone("1234567899");
        user.setActive(true);
        user.setPassword("Password");

        user.setRegisterDate(LocalDateTime.now());
        user.setUniqueId("unique" + userId);
        return user;
    }
}
