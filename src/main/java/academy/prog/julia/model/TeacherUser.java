package academy.prog.julia.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Represents a teacher user in the system.
 * This class extends the User class and is mapped to the 'user' table in the database.
 * It is used to differentiate teacher users from other types of users.
 *
 * @Entity marks this class as a JPA entity to be managed by Hibernate.
 * @DiscriminatorValue specifies the discriminator value for this specific subclass. "2" discriminator
 * value used to identify TeacherUser in the superclass table.
 */
@Entity
@DiscriminatorValue("2")
public class TeacherUser extends User {

    /**
     * Discriminator value for TeacherUser. This constant is used to identify
     * teacher users in the database, ensuring data consistency and integration
     * with other classes or enums.
     */
    public static final int DISCRIMINATOR_VALUE = 2;

    /**
     * Default constructor required by JPA. Initializes a new instance of the TeacherUser class.
     */
    public TeacherUser() { }

    /**
     * Constructor to initialize a TeacherUser with specified parameters.
     *
     * @param name The name of the teacher
     * @param surname The surname of the teacher
     * @param phone The phone number of the teacher
     * @param email The email address of the teacher
     * @param password The password of the teacher
     */
    public TeacherUser(
            String name,
            String surname,
            String phone,
            String email,
            String password
    ) {
        super(name, surname, phone, email, password);
    }

}
