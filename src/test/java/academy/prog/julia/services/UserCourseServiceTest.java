package academy.prog.julia.services;

import academy.prog.julia.dto.UserCoursesDTO;
import academy.prog.julia.json_responses.UserCoursesResponse;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.UserCourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCourseServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private UserCourseRepository userCourseRepository;


    @InjectMocks
    private UserCourseService userCourseService;

    @Test
    void getUserCourses_Success() {
        Group course1 = new Group("Java1", new HashSet<>());
        Group course2 = new Group("Java2", new HashSet<>());

        User user = createUser(1);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(userCourseRepository.findUserCourses(1L)).thenReturn(Arrays.asList(course1, course2));

        UserCoursesDTO result = userCourseService.getUserCourses(1L);

        assertThat(result.getCourses()).hasSize(2);
        assertEquals("Java1", result.getCourses().get(0).getName());
        assertEquals("Java2", result.getCourses().get(1).getName());

    }

    @Test
    void getUserCourses_UserNotFound(){
        when(userService.findById(1L)).thenReturn(Optional.empty());

        Exception exception =
                assertThrows(EntityNotFoundException.class, ()-> userCourseService.getUserCourses(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
        
        verify(userCourseRepository, times(0)).findUserCourses(1L);
    }

    @Test
    void getUserCourses_CoursesNotFound(){
        User user = createUser(1);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(userCourseRepository.findUserCourses(1L)).thenReturn(new ArrayList<>());


        Exception exception =
                assertThrows(EmptyResultDataAccessException.class, ()-> userCourseService.getUserCourses(1L));

        assertEquals("No courses found for user with id: 1", exception.getMessage());

    }


    @Test
    void getUserCoursesWithSessionValidation_Unauthorized() {
        User user = createUser(1);
        Long userId = user.getId();
        String userEmail = user.getEmail();
        String sessionId = "invalidSessionId";
        MockHttpSession session = new MockHttpSession();

        when(userService.findById(userId)).thenReturn(Optional.of(createUser(1)));
        when(userService.getSessionIdByPrincipalName(userEmail)).thenReturn("validSessionId");

        ResponseEntity<List<UserCoursesResponse>> response = userCourseService.getUserCoursesWithSessionValidation(
                userId, sessionId, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You unauthorized from UserCourseController", response.getBody().get(0).getName());
    }

    @Test
    void getUserCoursesWithSessionValidation_Authorized() {
        User user = createUser(1);
        Long userId = user.getId();
        String userEmail = user.getEmail();

        Group course1 = new Group("Java1", new HashSet<>());
        Group course2 = new Group("Java2", new HashSet<>());

        UserCoursesDTO userCoursesDTO = new UserCoursesDTO(List.of(course1, course2));


        String sessionId = "validSessionId";
        MockHttpSession session = new MockHttpSession();

        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getSessionIdByPrincipalName(userEmail)).thenReturn("validSessionId");
        when(userCourseRepository.findUserCourses(userId)).thenReturn(List.of(course1, course2));

        ResponseEntity<List<UserCoursesResponse>> response = userCourseService.getUserCoursesWithSessionValidation(
                userId, sessionId, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Java1", response.getBody().get(0).getName());
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
