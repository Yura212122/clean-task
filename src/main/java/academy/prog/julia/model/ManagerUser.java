package academy.prog.julia.model;

import jakarta.persistence.*;

/**
 * Represents a manager user in the system.
 * This class extends the User class and is mapped to the 'user' table in the database.
 * It is used to differentiate manager users from other types of users.
 *
 * @Entity marks this class as a JPA entity to be managed by Hibernate.
 * @DiscriminatorValue specifies the discriminator value for this specific subclass. "3" discriminator value
 * used to identify ManagerUser in the superclass table
 */
@Entity
@DiscriminatorValue("3")
public class ManagerUser extends User {
    /**
     * Discriminator value for ManagerUser. This constant is used to identify
     * manager users in the database, ensuring data consistency and integration
     * with other classes or enums.
     */
    public static final int DISCRIMINATOR_VALUE = 3;

    /**
     * Default constructor required by JPA.
     * Initializes a new instance of the ManagerUser class.
     */
    public ManagerUser() { }

    /**
     * Constructor to initialize a ManagerUser with specified parameters.
     *
     * @param name The name of the manager
     * @param surname The surname of the manager
     * @param phone The phone number of the manager
     * @param email The email address of the manager
     * @param password The password of the manager
     */
    public ManagerUser(
            String name,
            String surname,
            String phone,
            String email,
            String password
    ) {
        super(name, surname, phone, email, password);
    }

}
