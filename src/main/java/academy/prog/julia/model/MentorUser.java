package academy.prog.julia.model;

import jakarta.persistence.*;

/**
 * Represents a mentor user in the system.
 * This class extends the User class and is mapped to the 'user' table in the database.
 * It is used to differentiate mentor users from other types of users.
 *
 * @Entity marks this class as a JPA entity to be managed by Hibernate.
 * @DiscriminatorValue specifies the discriminator value for this specific subclass. "4" discriminator value
 * used to identify MentorUser in the superclass table.
 */
@Entity
@DiscriminatorValue("4")
public class MentorUser extends User {
    /**
     * Discriminator value for MentorUser. This constant is used to identify
     * mentor users in the database, ensuring data consistency and integration
     * with other classes or enums.
     */
    public static final int DISCRIMINATOR_VALUE = 4;

    /**
     * Default constructor required by JPA. Initializes a new instance of the MentorUser class.
     */
    public MentorUser() { }

    /**
     * Constructor to initialize a MentorUser with specified parameters.
     *
     * @param name The name of the mentor
     * @param surname The surname of the mentor
     * @param phone The phone number of the mentor
     * @param email The email address of the mentor
     * @param password The password of the mentor
     */
    public MentorUser(
            String name,
            String surname,
            String phone,
            String email,
            String password
    ) {
        super(name, surname, phone, email, password);
    }

}
