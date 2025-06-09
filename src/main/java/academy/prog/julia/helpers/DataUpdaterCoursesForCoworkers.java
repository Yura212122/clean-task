package academy.prog.julia.helpers;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import academy.prog.julia.model.UserRole;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.UserRepository;
import academy.prog.julia.services.MailSenderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Component for updating course data for coworkers and sending reminders about empty groups.
 *
 * This component periodically updates the groups associated with users who have specific roles
 * and sends reminder emails to administrators about empty groups.
 *
 */
@Component
public class DataUpdaterCoursesForCoworkers {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MailSenderService mailSenderService;

    /**
     * Constructs a new DataUpdaterCoursesForCoworkers with the provided repositories and mail sender service.
     *
     * @param userRepository       the repository for user data access
     * @param groupRepository      the repository for group data access
     * @param mailSenderService    the service for sending emails
     */
    public DataUpdaterCoursesForCoworkers(
            UserRepository userRepository,
            GroupRepository groupRepository,
            MailSenderService mailSenderService
    ) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.mailSenderService = mailSenderService;
    }

    /**
     * Updates group memberships for users with specific roles.
     *
     * This method is scheduled to run every 120 seconds (2 minutes). It iterates over all users,
     * and for users with roles ADMIN or MANAGER, it adds all available groups to their list of groups.
     *
     */
    @Scheduled(fixedRate = 120000)
    @Transactional
    public void updateData() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (userHasAnyRole(user, UserRole.ADMIN, UserRole.MANAGER)) {
                List<Group> userGroups = groupRepository.findAll();
                user.getGroups().addAll(userGroups);
                userRepository.save(user);
            }
        }
    }

    /**
     * Checks if a user has any of the specified roles.
     *
     * @param user  the user to check
     * @param roles the roles to check against
     * @return true if the user has at least one of the specified roles, false otherwise
     */
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

    /**
     * Sends reminders to administrators about empty groups.
     *
     * This method is scheduled to run every 2 hours. It iterates over all groups and checks if
     * any group (excluding "ProgAcademy") is empty. If an empty group is found, it sends an email
     * reminder to all administrators to add lessons to the group.
     *
     */
    @Scheduled(fixedRate = 7200000)
    @Transactional(readOnly = true)
    public void groupFillingReminder() {
        List<Group> groups = groupRepository.findAll();
        for (Group group : groups) {
            if (group.getLessons().isEmpty() && !group.getName().equals("ProgAcademy")) {
                List<User> users = userRepository.findAll();
                for (User user : users) {
                    if (user.getRole() == UserRole.ADMIN) {
                        String massage = "Group " + group.getName() + " is empty, please add lessons to it.";
                        mailSenderService.sendFromProgAcademy(
                                user.getEmail(),
                                "Reminder: You have an empty group.",
                                massage
                        );
                    }
                }
            }
        }
    }

}
