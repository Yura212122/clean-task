package academy.prog.julia.dto;

import academy.prog.julia.model.User;

import java.util.Objects;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for the User entity.
 *
 * This class is used to transfer user data between different layers of the application
 * while ensuring immutability and encapsulation.
 */
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private Set<String> emails;
    private Set<String> phones;

    /**
     * Converts a User entity to a UserDTO object.
     *
     * This static method is responsible for creating a UserDTO from a User entity,
     * mapping all the necessary fields.
     *
     * @param user the User entity to be converted to DTO
     * @return a UserDTO representing the given User entity
     */
    public static UserDTO userToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setEmail(user.getEmail());
        userDTO.setEmails(user.getEmails());
        userDTO.setPhones(user.getPhones());
        return userDTO;
    }

    /**
     * Constructs a UserDTO with all fields initialized.
     *
     * @param id      the unique identifier of the user. (Cannot be null)
     * @param name    the user's first name. (Cannot be null)
     * @param surname the user's last name. (Cannot be null)
     * @param email   the user's primary email address. (Cannot be null)
     * @param emails  the set of additional emails for the user
     * @param phones  the set of phone numbers for the user
     */
    public UserDTO(
            Long id,
            String name,
            String surname,
            String email,
            Set<String> emails,
            Set<String> phones
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.surname = Objects.requireNonNull(surname, "surname cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.emails = emails;
        this.phones = phones;
    }

    /**
     * Default no-argument constructor for creating an empty UserDTO.
     */
    public UserDTO() {
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the new ID to be set for the user
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the user.
     *
     * @return the user's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name of the user.
     *
     * @param name the new first name to be set for the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the last name of the user.
     *
     * @return the user's last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the last name of the user.
     *
     * @param surname the new last name to be set for the user
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the primary email address of the user.
     *
     * @return the user's primary email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the primary email address of the user.
     *
     * @param email the new primary email to be set for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the set of additional email addresses associated with the user.
     *
     * @return a set of emails, or null if no additional emails are present
     */
    public Set<String> getEmails() {
        return emails;
    }

    /**
     * Sets the set of additional email addresses associated with the user.
     *
     * @param emails a set of new emails to be associated with the user
     */
    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    /**
     * Gets the set of phone numbers associated with the user.
     *
     * @return a set of phone numbers
     */
    public Set<String> getPhones() {
        return phones;
    }

    /**
     * Sets the set of phone numbers associated with the user.
     *
     * @param phones a set of new phone numbers to be associated with the user
     */
    public void setPhones(Set<String> phones) {
        this.phones = phones;
    }

    /**
     * Compares this UserDTO with another object for equality.
     *
     * Two UserDTOs are considered equal if their id, primary email, and associated
     * emails and phone numbers are identical.
     *
     * @param o the object to compare
     * @return true if the objects are equal, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (!id.equals(userDTO.id)) return false;
        if (!email.equals(userDTO.email)) return false;
        if (!Objects.equals(emails, userDTO.emails)) return false;

        return phones.equals(userDTO.phones);
    }

    /**
     * Returns the hash code for this UserDTO.
     *
     * The hash code is computed based on the user's id, primary email, additional emails, and phone numbers.
     *
     * @return the hash code for this object
     */
    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + (emails != null ? emails.hashCode() : 0);
        result = 31 * result + phones.hashCode();

        return result;
    }

}
