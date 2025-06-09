package academy.prog.julia.services;

import academy.prog.julia.dto.StudentDTO;
import academy.prog.julia.dto.UserDTO;
import academy.prog.julia.dto.UserFromAnswerTaskDTO;
import academy.prog.julia.exceptions.BadRequestException;
import academy.prog.julia.exceptions.UserNotFoundException;
import academy.prog.julia.model.*;
import academy.prog.julia.repos.*;
import academy.prog.julia.telegram.BotCredentials;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import academy.prog.julia.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static academy.prog.julia.dto.UserDTO.userToDTO;

/**
 * Service class for managing user-related operations and interactions.
 * This service provides methods for user management, including user retrieval,
 * group management, and session handling.
 */
@Service
public class UserService implements UserDetailsService {

    @Value("${allowed_cross_origin}")
    private String crossOrigin;

    @Value("${telegram.api.url}")
    private String telegramUrl;

    @Value("${test.internet.available.url}")
    private String testInternetAvailableUrl;

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final InviteRepository inviteRepository;
    private final MailSenderService mailSenderService;
    private final BotCredentials botCredentials;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    private final SessionRepository2 sessionRepository;

    /**
     * Constructor for UserService. Uses constructor-based dependency injection
     * for repositories and services required for handling user operations.
     *
     * @param userRepository    the repository for managing user data
     * @param groupRepository   the repository for managing group data
     * @param inviteRepository  the repository for managing invite data
     * @param mailSenderService the service for sending emails
     * @param botCredentials    the credentials for bot integration
     * @param passwordEncoder   the utility for encoding passwords
     * @param restTemplate      the utility for making HTTP requests
     * @param sessionRepository the repository for managing session data
     */
    public UserService(
            UserRepository userRepository,
            GroupRepository groupRepository,
            InviteRepository inviteRepository,
            MailSenderService mailSenderService,
            BotCredentials botCredentials,
            PasswordEncoder passwordEncoder,
            RestTemplate restTemplate,
            SessionRepository2 sessionRepository
    ) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.inviteRepository = inviteRepository;
        this.mailSenderService = mailSenderService;
        this.botCredentials = botCredentials;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Retrieves the session ID for a given principal name (user's username).
     *
     * @param principalName the username of the principal
     * @return the session ID associated with the given principal name
     */
    @Transactional(readOnly = true)
    public String getSessionIdByPrincipalName(String principalName) {
        return sessionRepository.getSessionIdByPrincipalName(principalName);
    }

    /**
     * Retrieves the principal name for a given session ID.
     *
     * @param sessionId the session ID to look up
     * @return the principal name associated with the given session ID
     */
    @Transactional(readOnly = true)
    public String getPrincipalNameBySessionId(String sessionId) {
        return sessionRepository.getPrincipalNameBySessionId(sessionId);
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Saves or updates a user in the repository.
     *
     * @param user the user to save or update
     */
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Adds a new group with the given name if it does not already exist.
     *
     * @param name the name of the group to add
     * @return true if the group was added successfully, false if the group already exists
     */
    @Transactional
    public boolean groupAdd(String name) {
        if (groupRepository.existsByName(name)) {
            return false;
        }

        var group = new Group();
        group.setName(name);

        groupRepository.save(group);

        return true;
    }

    /**
     * Retrieves all group names from the repository.
     *
     * @return a list of all group names
     */
    @Transactional(readOnly = true)
    public List<String> fillAllGroupNames() {
        return groupRepository.findAllNames();
    }

    /**
     * Retrieves a set of users belonging to a specified group.
     *
     * @param groupName the name of the group
     * @param pageable  pagination information
     * @return a set of users belonging to the specified group
     */
    @Transactional(readOnly = true)
    public Set<User> findByGroupName(
            String groupName,
            Pageable pageable
    ) {
        return userRepository.findByGroupName(groupName, pageable).toSet();
    }

    /**
     * Checks if a phone number already exists in the repository.
     *
     * @param phoneNumber the phone number to check
     * @return true if the phone number exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhone(phoneNumber);
    }

    /**
     * Checks if an email address already exists in the repository.
     *
     * @param email the email address to check
     * @return true if the email address exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Retrieves a list of users belonging to a specified group.
     *
     * @param groupName the name of the group
     * @return a list of users belonging to the specified group
     */
    @Transactional(readOnly = true)
    public List<User> findByGroupName(String groupName) {
        return userRepository.findAllUsersByGroups(groupName);
    }

    /**
     * Counts the number of users in a specified group.
     *
     * @param groupName the name of the group
     * @return the number of users in the specified group
     */
    @Transactional(readOnly = true)
    public long countUsersByGroupName(String groupName) {
        return userRepository.countByGroupName(groupName);
    }

    /**
     * Finds a user by their Telegram chat ID.
     *
     * @param chatId the Telegram chat ID to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByChatId(String chatId) {
        return userRepository.findByTelegramChatId(chatId);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return an Optional containing the user if found, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Finds a user by their email address.
     * Throws a UserNotFoundException if the user is not found.
     *
     * @param email the email address of the user to find
     * @return the user with the given email address
     */
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            LOGGER.error("User with email <{}> NOT FOUND!", email);
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        return user;
    }

    /**
     * Finds users by phone number or email, based on which parameter is provided.
     *
     * @param phone the phone number to search for (or null if not searching by phone)
     * @param email the email to search for (or null if not searching by email)
     * @return a list of users matching the given phone number or email
     */
    @Transactional(readOnly = true)
    public List<User> findByPhoneOrEmailLike(
            String phone,
            String email
    ) {
        if (phone != null) {
            return userRepository.findByPhoneLike(phone);
        } else {
            return userRepository.findByEmailLike(email);
        }
    }

    /**
     * Updates the Telegram chat ID of a user based on their unique ID.
     *
     * @param userUniqueId the unique identifier of the user
     * @param chatId       the new Telegram chat ID to set for the user
     * @throws JuliaRuntimeException if the user is not found
     */
    @Transactional
    public void updateUserChatId(
            String userUniqueId,
            String chatId
    ) {
        var user = userRepository.findByUniqueId(userUniqueId);

        if (user.isEmpty()) {
            throw new JuliaRuntimeException("user not found");
        }

        user.get().setTelegramChatId(chatId);
        userRepository.save(user.get());
    }

    /**
     * Creates a new invite code with specified parameters and saves it to the repository.
     *
     * @param userRole             the role associated with the invite
     * @param expireAfterDaysCount the number of days after which the invite will expire
     * @param maxUsageCount        the maximum number of times the invite can be used
     * @param inviteDestination    the destination group for the invite (for students)
     * @return the generated invite code
     */
    @Transactional
    public String createInviteCode(
            UserRole userRole,
            int expireAfterDaysCount,
            int maxUsageCount,
            String inviteDestination
    ) {
        var invite = Invite.createNewOf(userRole, expireAfterDaysCount, maxUsageCount);
        inviteRepository.save(invite);

        if (userRole == UserRole.STUDENT && inviteDestination != null && inviteDestination.length() > 0) {
            invite.setDestinationType(Invite.DESTINATION_GROUP);
            invite.setDestination(inviteDestination);
        }

        return invite.getCode();
    }

    /**
     * Adds a user to a specified group.
     *
     * @param userId    the ID of the user to add
     * @param groupName the name of the group to add the user to
     */
    @Transactional
    public void addUserToGroup(
            long userId,
            String groupName
    ) {
        User user = userRepository.findById(userId).orElseThrow();
        Group group = groupRepository.findByName(groupName).orElseThrow();

        user.addGroup(group);
    }

    /**
     * Removes a user from a specified group.
     *
     * @param userId    the ID of the user to remove
     * @param groupName the name of the group to remove the user from
     */
    @Transactional
    public void removeUserFromGroup(
            long userId,
            String groupName
    ) {
        User user = userRepository.findById(userId).orElseThrow();
        Group group = groupRepository.findByName(groupName).orElseThrow();

        user.removeGroup(group);
    }

    /**
     * Registers a new user with the provided details and invite code.
     *
     * @param name       the name of the user
     * @param surname    the surname of the user
     * @param phone      the phone number of the user
     * @param email      the email of the user
     * @param password   the password for the user
     * @param inviteCode the invite code used for registration
     * @return the unique ID of the newly registered user
     * @throws JuliaRuntimeException if there are any errors during registration
     */
    @Transactional
    public String registerUser(
            String name,
            String surname,
            String phone,
            String email,
            String password,
            String inviteCode
    ) {
        try {
            if (userRepository.existsByPhoneOrEmail(phone, email)) {
                throw new JuliaRuntimeException("user already exists");
            }

            var inviteOpt = inviteRepository.findByCode(inviteCode);

            if (inviteOpt.isEmpty()) {
                throw new JuliaRuntimeException("wrong invite code");
            }

            var invite = inviteOpt.get();

            if (!invite.checkValidity()) {
                throw new JuliaRuntimeException("invite code is expired");
            }

            invite.decrementUsageCounter();

            inviteRepository.save(invite);

            String passwordEncoded = passwordEncoder.encode(password);

            var user = UserFactory.createUser(invite.getRole(), name, surname, phone, email, passwordEncoded);

            if (invite.hasGroupDestination()) {
                var groupOpt = groupRepository.findByName(invite.getDestination());

                if (groupOpt.isPresent()) {
                    user.addGroup(groupOpt.get());
                } else {
                    throw new JuliaRuntimeException("unknown group name specified in invite for students");
                }
            }

            if (invite.hasCoworkerDestination()) {
                var groupName = invite.getDestination();
                var groupOpt = groupRepository.findByName(groupName);

                if (groupOpt.isPresent()) {
                    user.addGroup(groupOpt.get());
                } else {
                    if ("ProgAcademy".equals(groupName)) {
                        var newGroup = new Group();
                        newGroup.setName("ProgAcademy");
                        groupRepository.save(newGroup);

                        user.addGroup(newGroup);
                    } else {
                        throw new JuliaRuntimeException("unknown group name specified in invite for coworkers");
                    }
                }

                var userGroups = groupRepository.findAll();

                if (userHasAnyRole(user, UserRole.ADMIN, UserRole.MANAGER)) {
                    user.getGroups().addAll(userGroups);
                }
            }

            List<User> userFromDb = userRepository.findByEmailLike(user.getEmail());

            if (userFromDb != null) {
                LOGGER.info("This user is exists!");
            }

            user.setBannedStatus(false);
            user.setActive(true);

            userRepository.save(user);

            sendMessage(user);

            return user.getUniqueId();

        } catch (JuliaRuntimeException e) {
            LOGGER.error("Error registration!", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unknown error!", e);
            throw new JuliaRuntimeException("Unknown error!");
        }
    }

    /**
     * Checks if a user has any of the specified roles.
     *
     * @param user  the user to check
     * @param roles the roles to check for
     * @return true if the user has any of the specified roles, otherwise false
     */
    private boolean userHasAnyRole(User user, UserRole... roles) {
        for (UserRole role : roles) {
            if (user.getAuthorities().contains(new SimpleGrantedAuthority(role.toString()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sends a message with a Telegram activation link to the user's email.
     *
     * @param user the user to send the message to
     */
    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            var telegramUrl = String.format(Utils.TELEGRAM_URL, botCredentials.getBotName(), user.getUniqueId());

            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to our big family ProgACADEMY. Please visit to next links: \n" +
                            "Telegram bot: " + telegramUrl +
                            "\nOur study application: " + crossOrigin,
                    user.getName()
            );

            mailSenderService.sendFromProgAcademy(user.getEmail(), "Activation link", message);
        }
    }

    /**
     * Sends a message to a user's Telegram bot with a notification about task correction.
     *
     * @param chatId   the user's Telegram chat ID
     * @param botToken the Telegram bot token
     * @param name     the name of the user
     * @param course   the course for which the task needs to be corrected
     */
    private void sendMessageTelBot(
            String chatId,
            String botToken,
            String name,
            String course
    ) {

        String text = String.format(
                "Hello, %s! \n" +
                        "You received a message from the teacher of " + course +
                        " course about the need to correct the task. \n" +
                        "Please visit the ProgAcademy's website: ",
                name
        );

        String htmlText = text + " <a href='" + crossOrigin + "'>" + crossOrigin + "</a>";
        String url = telegramUrl + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + htmlText;

        try {
            JSONObject message = new JSONObject();
            message.put("chat_id", chatId);
            message.put("text", htmlText);
            message.put("parse_mode", "HTML");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(message.toString(), headers);
            restTemplate.postForEntity(url, entity, String.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("Telegram API error: {} - {}", e.getRawStatusCode(), e.getStatusText());
            LOGGER.error(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            LOGGER.error("Rest client error: {}", e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email and a Telegram notification to the user about the need to correct a task.
     *
     * @param user   the user receiving the notification
     * @param course the course for which the correction is needed
     */
    @Transactional
    public void sendMessageAboutCorrection(
            @NonNull UserFromAnswerTaskDTO user,
            String course
    ) {
        if (!StringUtils.isEmpty(user.getEmail())) {

            String message = String.format(
                    "Hello, %s! \n" +
                            "You received a message from the teacher of " + course +
                            " course about the need to correct the task. \n" +
                            "Please visit the ProgAcademy's website: " + crossOrigin,

                    user.getName()
            );

            mailSenderService.sendFromProgAcademy(user.getEmail(), "The message from a teacher ", message);

            sendMessageTelBot(user.getTelegramChatId(), botCredentials.getBotToken(), user.getName(), course);
        }

    }

    /**
     * Blocks a user by setting their banned status to true.
     *
     * @param userId the ID of the user to block
     */
    @Transactional
    public void blockUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBannedStatus(true);
    }

    /**
     * Unblocks a user by setting their banned status to false.
     *
     * @param userId the ID of the user to unblock
     */
    @Transactional
    public void unblockUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBannedStatus(false);
    }

    /**
     * Checks if the user has an active internet connection.
     *
     * @return true if the internet is available, false otherwise
     */
    public boolean isUserInternetAvailable() {
        try {
            URL url = new URL(testInternetAvailableUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Retrieves all users grouped by their group names, converts them to DTOs,
     * and returns them in a map where the key is the group name and the value is a list of UserDTOs.
     *
     * @param pageRequest Pagination information
     * @return Map of group names with corresponding lists of UserDTOs
     */
    @Transactional(readOnly = true)
    public Map<String, List<UserDTO>> findAllByAllGroups(PageRequest pageRequest) {
        Map<String, List<UserDTO>> result = new HashMap<>();

        List<String> allGroupNames = groupRepository.findAllNames();
        List<User> users = new ArrayList<>();

        for (String groupName : allGroupNames) {
            users.addAll(findByGroupName(groupName, pageRequest));
            List<UserDTO> userDTOs = new ArrayList<>();

            for (User user : users) {
                userDTOs.add(userToDTO(user));
            }

            result.put(groupName, userDTOs);
            users = new ArrayList<>();
        }

        return result;
    }

    /**
     * Retrieves all students by filtering users who are not admins, teachers, managers, or mentors.
     * The users are grouped by their group names, and their details are converted into StudentDTOs.
     * The result is sorted by group.
     *
     * @return A list of StudentDTOs sorted by group
     */
    @Transactional(readOnly = true)
    public List<StudentDTO> findAllStudents() {
        List<StudentDTO> result = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (!user.getRole().toString().equals("ROLE_ADMIN") &&
                    !user.getRole().toString().equals("ROLE_TEACHER") &&
                    !user.getRole().toString().equals("ROLE_MANAGER") &&
                    !user.getRole().toString().equals("ROLE_MENTOR")
            ) {
                for (Group group : user.getGroups()) {
                    StudentDTO temp = new StudentDTO();
                    temp.setUniqueId(group.getName() + user.getId());
                    temp.setGroup(group.getName());
                    temp.setRegisterDate(registerDateToString(group.getRegisterDate()));
                    temp.setId(user.getId());
                    temp.setName(user.getName());
                    temp.setSurname(user.getSurname());
                    temp.setEmail(user.getEmail());
                    temp.setPhone(user.getPhone());

                    result.add(temp);
                }
            }
        }
        result.sort(Comparator.comparing(StudentDTO::getGroup));

        return result;
    }

    /**
     * Converts a LocalDateTime object into a formatted string representing the registration date.
     * The format is "Month(LLLL) Year(yyyy)" and uses the English locale.
     *
     * @param dateTime The LocalDateTime to format
     * @return The formatted date as a string
     */
    public String registerDateToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy", new Locale("en"));

        return dateTime.format(formatter);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email of the user
     * @return The User object, or null if no user is found
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findByEmailLike(String emailStarting) {
        if (emailStarting == null || emailStarting.isEmpty()) {
            throw new UserNotFoundException("Email should not be empty");
        }
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = userRepository.findByEmailLike(emailStarting, pageable);
        if (users.isEmpty()) {
            throw new UserNotFoundException("Users with this email is not found.");
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> findAllByRole(String role) throws BadRequestException {
        Class<? extends User> userClass = switch (role.toUpperCase()) {
            case "STUDENT" -> User.class;
            case "ADMIN" -> AdminUser.class;
            case "TEACHER" -> TeacherUser.class;
            case "MANAGER" -> ManagerUser.class;
            case "MENTOR" -> MentorUser.class;
            default -> null;
        };
        if (userClass == null) {
            throw new BadRequestException("Invalid role: " + role);
        }
        return userRepository.findAllByRole(userClass);
    }


    /**
     * Loads a user by their email address for authentication purposes.
     * Throws a UsernameNotFoundException if the user is not found.
     *
     * @param email The email of the user
     * @return The UserDetails object containing user data
     * @throws UsernameNotFoundException if the user is not found
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User with Email - " + email + " not found!");
        }

        return user;
    }

    /**
     * Checks if the user with the given ID is blocked (banned).
     *
     * @param userId The ID of the user
     * @return true if the user is banned, false otherwise
     * @throws UserNotFoundException if the user with the given ID is not found
     */
    @Transactional(readOnly = true)
    public boolean isUserBlocked(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return user.getBannedStatus();
    }

    /**
     * Finds users whose phone number matches the given phone number.
     *
     * @param phone The phone number to search for
     * @return A list of users whose phone number matches the provided value
     */
    @Transactional(readOnly = true)
    public List<User> findByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByPhoneLike(phone);
    }

    /**
     * Creates a new invite code for users based on their role.
     * The invite code has an expiration date and a limit on the number of users.
     * Specific invite configurations are applied for certain roles like ADMIN, MANAGER, etc.
     *
     * @param userRoleName         The name of the user role
     * @param expireAfterDaysCount The number of days the invite will be valid
     * @param numUsers             The number of users that can use the invite
     * @return The generated invite code
     */
    @Transactional
    public String createInviteCodeByRole(
            String userRoleName,
            int expireAfterDaysCount,
            int numUsers
    ) {
        UserRole userRole = UserRole.valueOf(userRoleName.toUpperCase());

        var invite = Invite.createNewOf(userRole, expireAfterDaysCount, numUsers);

        if (userRole == UserRole.ADMIN || userRole == UserRole.MANAGER ||
                userRole == UserRole.MENTOR || userRole == UserRole.TEACHER
        ) {
            invite.setDestinationType(Invite.DESTINATION_COWORKER);
            invite.setDestination("ProgAcademy");
        }

        inviteRepository.save(invite);

        return invite.getCode();
    }

    /**
     * Retrieves users who are either banned or active, based on the provided parameters,
     * with pagination.
     *
     * @param isBanned Whether to search for banned users
     * @param isActive Whether to search for active users
     * @param pageable The pagination information
     * @return A list of users matching the provided parameters
     */
    @Transactional(readOnly = true)
    public List<User> findAllByIsBannedAndIsActive(
            boolean isBanned,
            boolean isActive,
            Pageable pageable
    ) {
        return userRepository.findAllByIsBannedAndIsActive(isBanned, isActive, pageable);
    }

    /**
     * Counts the number of users who are either banned or active, based on the provided parameters.
     *
     * @param isBanned Whether to count banned users
     * @param isActive Whether to count active users
     * @return The number of users matching the provided parameters
     */
    @Transactional(readOnly = true)
    public long countAllByIsBannedAndIsActive(
            boolean isBanned,
            boolean isActive
    ) {
        return userRepository.countAllByIsBannedAndIsActive(isBanned, isActive);
    }


    // CURRENTLY NOT IN USE


    /**
     * Retrieves a reference to a user by their ID.
     *
     * @param id the ID of the user
     * @return the user reference
     */
    @Transactional(readOnly = true)
    public User getReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }

    /**
     * Validates if a user exists with the provided email and if the password matches the stored password.
     *
     * @param email    The email of the user
     * @param password The password to validate
     * @return true if the password matches, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isValidUser(
            String email,
            String password
    ) {
        User user = userRepository.findByEmail(email);
        String userPassword = user.getPassword();

        return passwordEncoder.matches(password, userPassword);
    }
}
