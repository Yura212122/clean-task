package academy.prog.julia.model;

import jakarta.persistence.*;

/**
 * Represents an admin user entity in the database.
 *
 * This class extends the User class and is distinguished using a discriminator value.
 *
 * @Entity marks this class as a JPA entity to be managed by Hibernate
 *
 */
@Entity
@DiscriminatorValue("1")
public class AdminUser extends User {

    /**
     * Discriminator value for AdminUser.
     *
     * This constant is used to identify AdminUser entries in the superclass table.
     */
    public static final int DISCRIMINATOR_VALUE = 1;

    /**
     * Default constructor required by JPA.
     *
     * Initializes a new instance of the AdminUser class.
     */
    public AdminUser() { }

    /**
     * Constructor for creating an AdminUser with specified details.
     *
     * @param name The name of the admin user
     * @param surname The surname of the admin user
     * @param phone The phone number of the admin user
     * @param email The email address of the admin user
     * @param password The password of the admin user
     */
    public AdminUser(
            String name,
            String surname,
            String phone,
            String email,
            String password
    ) {
        super(name, surname, phone, email, password);
    }

}
