package academy.prog.julia.model;

/**
 * Enum representing different user roles in the system.
 * Each role is associated with a specific discriminator value used for database identification.
 */
public enum UserRole {

    /**
     * Enum constants representing different user roles with their discriminator values
     */
    STUDENT(User.DISCRIMINATOR_VALUE),
    ADMIN(AdminUser.DISCRIMINATOR_VALUE),
    TEACHER(TeacherUser.DISCRIMINATOR_VALUE),
    MANAGER(ManagerUser.DISCRIMINATOR_VALUE),
    MENTOR(MentorUser.DISCRIMINATOR_VALUE);

    /**
     * Discriminator value for each role
     */
    private final int discriminatorValue;

    /**
     * Constructor to initialize the UserRole with a specific discriminator value.
     *
     * @param discriminatorValue The discriminator value for this role
     */
    UserRole(int discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    /**
     * Gets the discriminator value for this role.
     *
     * @return The discriminator value
     */
    public int getDiscriminatorValue() {
        return discriminatorValue;
    }

    /**
     * Returns a string representation of the role in the format "ROLE_<role_name>".
     *
     * @return The string representation of the role
     */
    @Override
    public String toString() {
        return String.format("ROLE_%s", name());
    }

}
