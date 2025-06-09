package academy.prog.julia.services;

import academy.prog.julia.dto.StudentDTO;
import academy.prog.julia.dto.UserDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import academy.prog.julia.exceptions.UserNotFoundException;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.InviteRepository;
import academy.prog.julia.repos.SessionRepository2;
import academy.prog.julia.repos.UserRepository;

import academy.prog.julia.telegram.BotCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BotCredentials botCredentials;

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private SessionRepository2 sessionRepository;

    @InjectMocks
    private UserService userService;


    @Test
    void getSessionIdByPrincipalName() {
        String principalName = "testUser";
        String expectedSessionId = "session123";

        when(sessionRepository.getSessionIdByPrincipalName(principalName))
                .thenReturn(expectedSessionId);

        String actualSessionId = userService.getSessionIdByPrincipalName(principalName);


        verify(sessionRepository, times(1)).getSessionIdByPrincipalName(principalName);
        assertEquals(expectedSessionId, actualSessionId);
    }

    @Test
    void getPrincipalNameBySessionId() {

        String expectedPrincipalName = "testUser";
        String sessionId = "session123";

        when(sessionRepository.getPrincipalNameBySessionId(sessionId))
                .thenReturn(expectedPrincipalName);

        String actualPrincipalname = userService.getPrincipalNameBySessionId(sessionId);


        verify(sessionRepository, times(1)).getPrincipalNameBySessionId(sessionId);
        assertEquals(expectedPrincipalName, actualPrincipalname);
    }


    @Test
    void findAll() {
        List<User> expected = new ArrayList<>(Arrays.asList(createUser(1), createUser(2)));

        when(userRepository.findAll()).thenReturn(expected);

        List<User> actualUsers = userService.findAll();

        verify(userRepository, times(1)).findAll();

        assertEquals(expected, actualUsers);
    }

    @Test
    void saveUser() {
        User userToSave = createUser(1);

        userService.saveUser(userToSave);

        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void groupAdd_AlreadyExists() {
        String groupName = "existingGroup";
        when(groupRepository.existsByName(groupName)).thenReturn(true);

        boolean result = userService.groupAdd(groupName);

        assertFalse(result);
        verify(groupRepository, times(1)).existsByName(groupName);
        verifyNoMoreInteractions(groupRepository);
    }

    @Test
    void groupAdd_Success(){
        String groupName = "Group";
        when(groupRepository.existsByName(groupName)).thenReturn(false);

        boolean result = userService.groupAdd(groupName);

        assertTrue(result);

        verify(groupRepository, times(1)).existsByName(groupName);
        verify(groupRepository, times(1)).save(any());
        verifyNoMoreInteractions(groupRepository);

    }

    @Test
    void findAllGroupNames() {
        List<String> groupNamesExpected = Arrays.asList("Java 1", "Java 2");
        when(groupRepository.findAllNames()).thenReturn(groupNamesExpected);

        List<String> result = userService.fillAllGroupNames();

        assertEquals(groupNamesExpected, result);
    }



    @Test
    void isPhoneNumberExists() {
        String phoneNumber = "Phone";

        //return true
        when(userRepository.existsByPhone(phoneNumber)).thenReturn(true);
        boolean result = userService.isPhoneNumberExists(phoneNumber);
        assertTrue(result);

        //return false
        when(userRepository.existsByPhone(phoneNumber)).thenReturn(false);
        boolean result2 = userService.isPhoneNumberExists(phoneNumber);
        assertFalse(result2);
    }



    @Test
    void isEmailExists() {
        String email = "Email@gmail.com";

        //return true
        when(userRepository.existsByEmail(email)).thenReturn(true);
        boolean result = userService.isEmailExists(email);
        assertTrue( result);

        //return false
        when(userRepository.existsByEmail(email)).thenReturn(false);
        boolean result2 = userService.isEmailExists(email);
        assertFalse(result2);
    }


    @Test
    void findByGroupName_Page() {
        Pageable pageable = PageRequest.of(0, 10);
        String groupName = "Java";
        PageImpl<User> usersExpected = new PageImpl<>
                (Arrays.asList(createUser(1), createUser(2)));
        when(userRepository.findByGroupName(groupName, pageable))
                .thenReturn(usersExpected);
        Set<User> result = userService.findByGroupName(groupName, pageable);


        assertEquals(usersExpected.toSet(), result);
        verify(userRepository).findByGroupName(groupName, pageable);
    }


    @Test
    void findByGroupName_List(){
        String groupName = "Java1";
        List<User> expectedUsers = Arrays.asList(createUser(1), createUser(2));
        when(userRepository.findAllUsersByGroups(groupName)).thenReturn(expectedUsers);

        List<User> result = userService.findByGroupName(groupName);

        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAllUsersByGroups(groupName);

    }

    @Test
    void findUserByEmail_UserFound() {
        User expectedUser = createUser(1);
        String email = expectedUser.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        User actualUser = userService.findUserByEmail(email);

        assertEquals(expectedUser, actualUser);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findUserByEmail_UserNotFound(){
        String email = "doesntExist@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> userService.findUserByEmail(email));

    }

    @Test
    void findByPhoneOrEmailLike_ByPhone(){
        List<User> usersExpected = Arrays.asList(createUser(1), createUser(2));
        String phone = "380";

        when(userRepository.findByPhoneLike(phone)).thenReturn(usersExpected);

        List<User> result = userService.findByPhoneOrEmailLike(phone, null);


        assertEquals(usersExpected, result);
        verify(userRepository, times(1)).findByPhoneLike(phone);
        verify(userRepository, never()).findByEmailLike(phone);

    }

    @Test
    void findByPhoneOrEmailLike_ByEmail(){
        List<User> usersExpected = Arrays.asList(createUser(1), createUser(2));
        String email = "email@gmail.com";

        when(userRepository.findByEmailLike(email)).thenReturn(usersExpected);

        List<User> result = userService.findByPhoneOrEmailLike(null, email);


        assertEquals(usersExpected, result);
        verify(userRepository, times(1)).findByEmailLike(email);
    }

    @Test
    void updateUserChatId_UserNotFound(){
        String uniqueId = "uniqueId";
        when(userRepository.findByUniqueId(uniqueId)).thenReturn(Optional.empty());

        assertThrows(JuliaRuntimeException.class, ()-> userService.updateUserChatId(uniqueId, null));
    }

    @Test
    void updateUserChatId_UserFound(){
        User user = createUser(1);

        String uniqueId = user.getUniqueId();
        String newUniqueId = "newUniqueId";
        when(userRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(user));

        userService.updateUserChatId(uniqueId, newUniqueId);
        assertEquals("newUniqueId", user.getTelegramChatId());


        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void addUserToGroup() {
        User user = createUser(1);
        Group group = new Group();
        group.setName("Group1");

        // Test case: User and Group exist (Success)
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(groupRepository.findByName(group.getName())).thenReturn(Optional.of(group));

        userService.addUserToGroup(user.getId(), group.getName());

        assertTrue(user.getGroups().contains(group));
        verify(userRepository).findById(user.getId());
        verify(groupRepository).findByName(group.getName());


        // Test case: User not found
        reset(userRepository, groupRepository);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.addUserToGroup(user.getId(), group.getName()));
        verify(userRepository).findById(user.getId());
        verify(groupRepository, never()).findByName(anyString());


        // Test case: Group not found
        reset(userRepository, groupRepository);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(groupRepository.findByName(group.getName())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.addUserToGroup(user.getId(), group.getName()));
        verify(userRepository).findById(user.getId());
        verify(groupRepository).findByName(group.getName());
    }

    @Test
    public void removeUserFromGroup() {
        long userId = 1L;
        User user = createUser((int) userId);

        String groupName = "TestGroup";
        Group group = new Group(groupName, new HashSet<>());

        // Test case: User and Group exist
        user.addGroup(group);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findByName(groupName)).thenReturn(Optional.of(group));

        userService.removeUserFromGroup(userId, groupName);
        assertFalse(user.getGroups().contains(group));



        // Test case: User not found
        reset(userRepository, groupRepository);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.removeUserFromGroup(userId, groupName));
        verify(userRepository).findById(userId);
        verify(groupRepository, never()).findByName(anyString());



        // Test case: Group not found
        reset(userRepository, groupRepository);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findByName(groupName)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.removeUserFromGroup(userId, groupName));
        verify(userRepository).findById(userId);
        verify(groupRepository).findByName(groupName);
    }

    @Test
    public void registerUser_UserAlreadyExists() {
        String phone = "1234567890";
        String email = "test@example.com";
        when(userRepository.existsByPhoneOrEmail(phone, email)).thenReturn(true);

        JuliaRuntimeException exception = assertThrows(JuliaRuntimeException.class, () ->
                userService.registerUser("User", "Test", phone, email,
                        "password", "inviteCode"));
        assertEquals("user already exists", exception.getMessage());
    }

    @Test
    public void registerUser_InvalidInviteCode() {
        String phone = "1234567890";
        String email = "test@example.com";
        when(userRepository.existsByPhoneOrEmail(phone, email)).thenReturn(false);
        when(inviteRepository.findByCode("inviteCode")).thenReturn(Optional.empty());

        JuliaRuntimeException exception = assertThrows(JuliaRuntimeException.class, () ->
                userService.registerUser("User", "Test", phone, email, "password",
                        "inviteCode"));
        assertEquals("wrong invite code", exception.getMessage());
    }

    @Test
    public void registerUser_ExpiredInviteCode() {
        String phone = "1234567890";
        String email = "test@example.com";
        Invite invite = Mockito.mock(Invite.class);
        Mockito.when(inviteRepository.findByCode("inviteCode")).thenReturn(Optional.of(invite));
        Mockito.when(invite.checkValidity()).thenReturn(false);
        Mockito.when(userRepository.existsByPhoneOrEmail(phone, email)).thenReturn(false);

        JuliaRuntimeException exception = assertThrows(JuliaRuntimeException.class, () ->
                userService.registerUser("User", "Test", phone, email, "password",
                        "inviteCode"));
        assertEquals("invite code is expired", exception.getMessage());
    }

    @Test
    public void registerUser_GroupDestinationPart_GroupNotFound() {
        User user = createUser(1);

        String inviteCode = "validInviteCode";
        Invite invite = Invite.createNewOf(UserRole.STUDENT, 10, 20);
        invite.setExpirationDate(LocalDateTime.now().plusDays(20));
        invite.setDestinationType("group");
        invite.setDestination("Group1");



        when(userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail())).thenReturn(false);
        when(inviteRepository.findByCode(inviteCode)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);
        when(groupRepository.findByName("Group1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

        try (MockedStatic<UserFactory> utilities = Mockito.mockStatic(UserFactory.class)) {
            utilities.when(() -> UserFactory.createUser(UserRole.STUDENT, user.getName(), user.getSurname(),
                    user.getPhone(), user.getEmail(), user.getPassword())).thenReturn(user);


            var error = assertThrows(JuliaRuntimeException.class,  () -> userService.registerUser(user.getName(),
                    user.getSurname(), user.getPhone(), user.getEmail(),
                    user.getPassword(), inviteCode));


            assertEquals("unknown group name specified in invite for students", error.getMessage());
        }
    }

    @Test
    public void registerUser_GroupDestinationPart_GroupFound() {
        User user = createUser(1);

        String inviteCode = "validInviteCode";
        Invite invite = Invite.createNewOf(UserRole.STUDENT, 10, 20);
        invite.setExpirationDate(LocalDateTime.now().plusDays(20));
        invite.setDestinationType("group");
        invite.setDestination("Group1");

        Group group = new Group("Group1", new HashSet<>());

        when(userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail())).thenReturn(false);
        when(inviteRepository.findByCode(inviteCode)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);
        when(groupRepository.findByName("Group1")).thenReturn(Optional.of(group));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

        try (MockedStatic<UserFactory> utilities = Mockito.mockStatic(UserFactory.class)) {
            utilities.when(() -> UserFactory.createUser(UserRole.STUDENT, user.getName(), user.getSurname(),
                    user.getPhone(), user.getEmail(), user.getPassword())).thenReturn(user);


            userService.registerUser(user.getName(), user.getSurname(), user.getPhone(), user.getEmail(),
                    user.getPassword(), inviteCode);

            verify(groupRepository).findByName("Group1");
            assertThat(user.getGroups()).contains(group);

        }
    }

    @Test
    public void registerUser_CoworkerDestinationPart_GroupFound() {
        User user = createUser(1);
        Group group = new Group("ProgAcademy", new HashSet<>());

        String inviteCode = "validInviteCode";
        Invite invite = Invite.createNewOf(UserRole.TEACHER, 10, 20);
        invite.setExpirationDate(LocalDateTime.now().plusDays(20));
        invite.setDestinationType("coworker");
        invite.setDestination(group.getName());

        // Test case: Coworker destination found
        when(userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail())).thenReturn(false);
        when(inviteRepository.findByCode(inviteCode)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);
        when(groupRepository.findByName(group.getName())).thenReturn(Optional.of(group));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

        try (MockedStatic<UserFactory> utilities = Mockito.mockStatic(UserFactory.class)) {
            utilities.when(() -> UserFactory.createUser(UserRole.TEACHER, user.getName(), user.getSurname(),
                    user.getPhone(), user.getEmail(), user.getPassword())).thenReturn(user);

            userService.registerUser(user.getName(), user.getSurname(), user.getPhone(), user.getEmail(),
                    user.getPassword(), inviteCode);

            verify(groupRepository).findByName("ProgAcademy");
            assertThat(user.getGroups()).extracting(Group::getName).contains("ProgAcademy");

        }

    }

    @Test
    public void registerUser_CoworkerDestinationPart_CreateNewProgAcademyGroup() {
        User user = createUser(1);
        Group group = new Group("ProgAcademy", new HashSet<>());

        String inviteCode = "validInviteCode";
        Invite invite = Invite.createNewOf(UserRole.TEACHER, 10, 20);
        invite.setExpirationDate(LocalDateTime.now().plusDays(20));
        invite.setDestinationType("coworker");
        invite.setDestination(group.getName());


        // Test case: Coworker destination not found and invite destination = ProgAcademy
        reset(userRepository, inviteRepository, groupRepository, passwordEncoder);

        invite.setDestination("ProgAcademy");
        when(userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail())).thenReturn(false);
        when(inviteRepository.findByCode(inviteCode)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(groupRepository.findByName(invite.getDestination())).thenReturn(Optional.empty());

        Group newGroup = new Group();
        newGroup.setName(invite.getDestination());
        when(groupRepository.save(newGroup)).thenReturn(newGroup);

        try (MockedStatic<UserFactory> utilities = Mockito.mockStatic(UserFactory.class)) {
            utilities.when(() -> UserFactory.createUser(UserRole.TEACHER, user.getName(), user.getSurname(),
                    user.getPhone(), user.getEmail(), user.getPassword())).thenReturn(user);

            String result = userService.registerUser(user.getName(),
                    user.getSurname(), user.getPhone(), user.getEmail(),
                    user.getPassword(), inviteCode);

            assertEquals("unique1", result);
            verify(groupRepository, times(1)).save(newGroup);

        }


    }

    @Test
    void registerUser_CoworkerDestinationPart_WrondDestinationAndNoGroup() {
        // Test case: Coworker destination group not found and invite destination != ProgAcademy
        User user = createUser(1);
        Group group = new Group("ProgAcademy", new HashSet<>());

        String inviteCode = "validInviteCode";
        Invite invite = Invite.createNewOf(UserRole.TEACHER, 10, 20);
        invite.setExpirationDate(LocalDateTime.now().plusDays(20));
        invite.setDestinationType("coworker");
        invite.setDestination(group.getName());


        invite.setDestination("NotProgAcademy");
        when(userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail())).thenReturn(false);
        when(inviteRepository.findByCode(inviteCode)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(groupRepository.findByName(invite.getDestination())).thenReturn(Optional.empty());


        try (MockedStatic<UserFactory> utilities = Mockito.mockStatic(UserFactory.class)) {
            utilities.when(() -> UserFactory.createUser(UserRole.TEACHER, user.getName(), user.getSurname(),
                    user.getPhone(), user.getEmail(), user.getPassword())).thenReturn(user);

            var error = assertThrows(JuliaRuntimeException.class, () -> userService.registerUser(user.getName(),
                    user.getSurname(), user.getPhone(), user.getEmail(),
                    user.getPassword(), inviteCode));

            assertEquals("unknown group name specified in invite for coworkers", error.getMessage());
        }


    }



    @Test
    void registerUser_CoworkerDestinationPart_UserIsAdminOrManager() {
        User userCreated = createUser(1);
        AdminUser  user = new AdminUser(userCreated.getName(), userCreated.getSurname(),
                userCreated.getPhone(), userCreated.getEmail(), userCreated.getPassword());

        Group group = new Group("ProgAcademy", new HashSet<>());

        Group group2 = new Group("Group2", new HashSet<>());
        Group group3 = new Group("Group3", new HashSet<>());


        String inviteCode = "validInviteCode";
        Invite invite = Invite.createNewOf(UserRole.ADMIN, 10, 20);
        invite.setExpirationDate(LocalDateTime.now().plusDays(20));
        invite.setDestinationType("coworker");
        invite.setDestination(group.getName());


        when(userRepository.existsByPhoneOrEmail(user.getPhone(), user.getEmail())).thenReturn(false);
        when(inviteRepository.findByCode(inviteCode)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(invite)).thenReturn(invite);
        when(groupRepository.findByName("ProgAcademy")).thenReturn(Optional.of(group));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group, group2, group3));
        when(userRepository.save(user)).thenReturn(user);

        try (MockedStatic<UserFactory> utilities = Mockito.mockStatic(UserFactory.class)) {
            utilities.when(() -> UserFactory.createUser(invite.getRole(), user.getName(), user.getSurname(),
                    user.getPhone(), user.getEmail(), user.getPassword())).thenReturn(user);


            userService.registerUser(user.getName(), user.getSurname(), user.getPhone(), user.getEmail(),
                    user.getPassword(), inviteCode);


            assertThat(user.getGroups()).hasSize(3);
            assertThat(user.getGroups()).contains(group, group2, group3);
            assertTrue(user.getActive());
            assertFalse(user.getBannedStatus());
            verify(userRepository).save(user);

            }
        }


    @Test
    void sendMessage() {
        try {
            User user = createUser(1);
            Method method = UserService.class.getDeclaredMethod("sendMessage", User.class);
            method.setAccessible(true);

            method.invoke(userService, user);
            verify(mailSenderService).sendFromProgAcademy(eq(user.getEmail()), anyString(), anyString());

        }catch (NoSuchMethodException err){
            fail("Signature of 'sendMessage(User user)' was changed");
        }catch (Exception err){
            fail("Error");
        }


    }


    @Test
    void sendMessageTelBot() {
        try {
            String chatId = "testChatId";
            String botToken = "botToken";
            String course = "Java";
            User user = createUser(1);

            Method method = UserService.class.getDeclaredMethod("sendMessageTelBot",
                    String.class, String.class, String.class, String.class);
            method.setAccessible(true);

            method.invoke(userService, chatId, botToken, user.getName(), course);
            verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
        }catch (NoSuchMethodException err){
            fail("Signature of 'sendMessage(User user, String discoursePassword)' was changed");
        }catch (Exception err){
            fail("Error");
        }
    }


    @Test
    void sendMessageAboutCorrection() {
        String course = "Java";
        User user = createUser(1);
        UserFromAnswerTaskDTO dto = new UserFromAnswerTaskDTO(user.getId(), user.getName(),
                user.getSurname(), user.getPhone(), user.getEmail(), user.getTelegramChatId(), new HashSet<>());
        userService.sendMessageAboutCorrection(dto, course);

        verify(mailSenderService).sendFromProgAcademy(eq(dto.getEmail()), anyString(), anyString());
        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void sendMessageAboutCorrection_DtoIsNull() {
        String course = "Java";
        UserFromAnswerTaskDTO dto = null;

        assertThrows(NullPointerException.class, () -> userService.sendMessageAboutCorrection(dto, course));


    }
    @Test
    void userHasAnyRole() {
        AdminUser  admin = new AdminUser("Admin", "Admin", "1234567890",
                "admin@gmail.com", "Admin" );
        MentorUser mentor = new MentorUser("Mentor", "Mentor", "1234567890",
                "mentor@gmail.com", "Mentor");

        UserRole[] roles = { UserRole.MENTOR, UserRole.TEACHER };
        try{
            Method method = UserService.class.getDeclaredMethod("userHasAnyRole",
                    User.class, UserRole[].class);

            method.setAccessible(true);

            boolean testTrue = (boolean) method.invoke(userService, mentor , roles);
            boolean testFalse = (boolean) method.invoke(userService, admin , roles);
            assertTrue(testTrue);
            assertFalse(testFalse);


        }catch(NoSuchMethodException exception){
            fail("Signature of 'userHasAnyRole(User user, UserRole ... roles)' was changed");

        }catch (Exception exception){
            fail( "Error");
        }
    }

    @Test
    void blockUser() {
        User user = createUser(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //verify user is unblocked before method and blocked after
        assertFalse(user.getBannedStatus(), "User is blocked before test");
        userService.blockUser(1L);
        assertTrue(user.getBannedStatus());

        verify(userRepository).findById(1L);
    }
    @Test
    void unblockUser() {
        User user = createUser(1);
        user.setBannedStatus(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // verify user is blocked before method and unblocked after
        assertTrue(user.getBannedStatus(), "User isn`t blocked before test");
        userService.unblockUser(1L);
        assertFalse(user.getBannedStatus());

        verify(userRepository).findById(1L);
    }





    @Test
    void findAllByAllGroups() {
        User user1 = createUser(1);
        User user2 = createUser(2);
        User user3 = createUser(3);
        User user4 = createUser(4);




        List<String> groupNames = Arrays.asList("Group1", "Group2");
        when(groupRepository.findAllNames()).thenReturn(groupNames);

        when(userRepository.findByGroupName("Group1", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(user1, user2)));
        when(userRepository.findByGroupName("Group2", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(user2, user3, user4)));

        Map<String, List<UserDTO>> result = userService.findAllByAllGroups(PageRequest.of(0, 10));

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Group1"));
        assertTrue(result.containsKey("Group2"));
        assertThat(result.get("Group1")).hasSize(2);
        assertThat(result.get("Group2")).hasSize(3);

        verify(groupRepository).findAllNames();
        verify(userRepository).findByGroupName("Group1", PageRequest.of(0, 10));
        verify(userRepository).findByGroupName("Group2", PageRequest.of(0, 10));
    }

    @Test
    void findAllStudents() {
        Group group = new Group("Java1", new HashSet<>());
        group.setRegisterDate(LocalDateTime.now());
        group.setId(1L);

        Group group2 = new Group("Java2", new HashSet<>());
        group2.setRegisterDate(LocalDateTime.now());
        group2.setId(2L);


        AdminUser admin = new AdminUser( "Admin", "Admin",  "111222333","admin@example.com", "admin");
        TeacherUser teacher = new TeacherUser( "Teacher", "Teacher",  "444555666","teacher@example.com", "teacher");
        User student1 = createUser(1);
        User student2 = createUser(2);
        student2.addGroup(group);
        student2.addGroup(group2);
        student1.addGroup(group);

        when(userRepository.findAll()).thenReturn(Arrays.asList(student1, student2, admin, teacher));


        List<StudentDTO> result = userService.findAllStudents();

        result.forEach(i -> System.out.println(i.getName()));
        //student2 has 2 groups(courses), so he counts as 2 different users
        assertEquals(3, result.size());

        assertThat(result).extracting(StudentDTO::getName).containsAll(Arrays.asList("Name1", "Name2"));

    }

    @Test
    void registerDateToString() {
        assertEquals("November 2023", userService.registerDateToString(LocalDateTime.of(2023, 11, 1, 0, 0)));
        assertEquals("February 2020", userService.registerDateToString(LocalDateTime.of(2020, 2, 29, 0, 0)));

        assertThrows(NullPointerException.class, () -> {
            userService.registerDateToString(null);
        });
    }

    @Test
    void isUserBlocked() {
        User user = createUser(1);
        User user3 = createUser(3);
        user3.setBannedStatus(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean notBlocked = userService.isUserBlocked(1L);
        assertFalse(notBlocked);

        reset(userRepository);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.isUserBlocked(2L));

        reset(userRepository);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));
        boolean isBlocked = userService.isUserBlocked(3L);
        assertTrue(isBlocked);



    }

    @Test
    void findByEmail() {
        User user = createUser(1);
        when(userRepository.findByEmail("email1@example.com")).thenReturn(user);
        when(userRepository.findByEmail("nonexist@example.com")).thenReturn(null);

        User foundUser = userService.findByEmail("email1@example.com");
        assertNotNull(foundUser);
        assertEquals("email1@example.com", foundUser.getEmail());

        User notFoundUser = userService.findByEmail("nonexist@example.com");
        assertNull(notFoundUser);
    }

    @Test
    void loadUserByUsername() {
        User user = createUser(1);
        when(userRepository.findByEmail("email1@example.com")).thenReturn(user);
        when(userRepository.findByEmail("nonexist@example.com")).thenReturn(null);

        UserDetails userDetails = userService.loadUserByUsername("email1@example.com");
        assertNotNull(userDetails);
        assertEquals("email1@example.com", userDetails.getUsername());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexist@example.com");
        });
    }

    @Test
    void findByPhone() {
        User user = createUser(1);
        when(userRepository.findByPhoneLike("123")).thenReturn(List.of(user));

        List<User> result = userService.findByPhone("123");
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));


        when(userRepository.findByPhoneLike("nonexist")).thenReturn(Collections.emptyList());
        result = userService.findByPhone("nonexist");
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    @Test
    void testCreateInviteCodeByRole_Admin() {
        String userRoleName = "ADMIN";
        int expireAfterDaysCount = 30;
        int numUsers = 5;

        String inviteCode = userService.createInviteCodeByRole(userRoleName, expireAfterDaysCount, numUsers);

        assertNotNull(inviteCode);
        verify(inviteRepository, times(1)).save(any(Invite.class));


    }
    @Test
    void testCreateInviteCodeByRole_Student() {
        String userRoleName = "ADMIN";
        int expireAfterDaysCount = 30;
        int numUsers = 5;

        String inviteCode = userService.createInviteCodeByRole(userRoleName, expireAfterDaysCount, numUsers);

        assertNotNull(inviteCode);
        verify(inviteRepository, times(1)).save(any(Invite.class));


    }

    @Test
    void countAllByIsBannedAndIsActive() {

        long expectedCount = 5L;

        when(userRepository.countAllByIsBannedAndIsActive(true, false)).thenReturn(expectedCount);

        long actualCount = userService.countAllByIsBannedAndIsActive(true, false);

        assertEquals(expectedCount, actualCount);
        verify(userRepository).countAllByIsBannedAndIsActive(true, false);
    }

    @Test
    void testGetReferenceById() {
        User expectedUser = createUser(1);
        expectedUser.setId(expectedUser.getId());

        when(userRepository.getReferenceById(expectedUser.getId())).thenReturn(expectedUser);

        User actualUser = userService.getReferenceById(expectedUser.getId());

        assertEquals(expectedUser, actualUser);
        verify(userRepository).getReferenceById(expectedUser.getId());
    }

    @Test
    void isValidUser() {
        String password = "password";
        User user = createUser(1);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        boolean isValid = userService.isValidUser(user.getEmail(), password);

        assertTrue(isValid);
        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).matches(password, user.getPassword());
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
