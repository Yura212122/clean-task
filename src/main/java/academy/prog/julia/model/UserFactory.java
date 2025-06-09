package academy.prog.julia.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class for creating User instances based on user roles.
 * This class uses a switch statement to create different types of User objects
 * depending on the specified role.
 */
public class UserFactory {

    private static final Logger LOGGER = LogManager.getLogger(UserFactory.class);

    /**
     * Creates a User instance based on the provided user role.
     *
     * @param userRole The role of the user to be created
     * @param name The name of the user
     * @param surname The surname of the user
     * @param phone The phone number of the user
     * @param email The email address of the user
     * @param password The password of the user
     * @return The created User object based on the specified role
     */
    public static User createUser(
            UserRole userRole,
            String name,
            String surname,
            String phone,
            String email,
            String password
    ) {
        User result = null;

        LOGGER.info("User role: {}", userRole);

        /**
         * Create different types of User objects based on the userRole
         */
        switch (userRole) {
            case STUDENT -> result = new User(name, surname, phone, email, password);
            case ADMIN -> result = new AdminUser(name, surname, phone, email, password);
            case TEACHER -> result = new TeacherUser(name, surname, phone, email, password);
            case MENTOR -> result = new MentorUser(name, surname, phone, email, password);
            case MANAGER -> result = new ManagerUser(name, surname, phone, email, password);
        }

        return result;
    }

}
