package academy.prog.julia.dto;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for representing a user in the context of an answer task.
 */
public class UserFromAnswerTaskDTO {

    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String telegramChatId;
    private Set<String> groups;

    /**
     * Default no-argument constructor for creating an empty UserFromAnswerTaskDTO.
     */
    public UserFromAnswerTaskDTO() {
    }

    /**
     * Constructs a UserFromAnswerTaskDTO with the specified values.
     *
     * @param id the unique identifier of the user. (Cannot be null)
     * @param name the first name of the user. (Cannot be null)
     * @param surname the last name of the user. (Cannot be null)
     * @param phone the phone number of the user. (Cannot be null)
     * @param email the email address of the user. (Cannot be null)
     * @param telegramChatId the Telegram chat ID of the user
     * @param groups the set of group names the user belongs to. (Cannot be null)
     */
    public UserFromAnswerTaskDTO(
            Long id,
            String name,
            String surname,
            String phone,
            String email,
            String telegramChatId,
            Set<String> groups
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.surname = Objects.requireNonNull(surname, "surname cannot be null");
        this.phone = Objects.requireNonNull(phone, "phone cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.telegramChatId = telegramChatId;
        this.groups = Objects.requireNonNull(groups, "groups cannot be null");
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
     * Gets the phone number of the user.
     *
     * @return the user's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phone the new phone number to be set for the user
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address to be set for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the Telegram chat ID of the user.
     *
     * @return the user's Telegram chat ID
     */
    public String getTelegramChatId() {
        return telegramChatId;
    }

    /**
     * Sets the Telegram chat ID of the user.
     *
     * @param telegramChatId the new Telegram chat ID to be set for the user
     */
    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    /**
     * Gets the set of group names the user belongs to.
     *
     * @return the set of group names
     */
    public Set<String> getGroups() {
        return groups;
    }

    /**
     * Sets the set of group names the user belongs to.
     *
     * @param groups the new set of group names to be set for the user
     */
    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    /**
     * Converts a User entity to a UserFromAnswerTaskDTO.
     *
     * @param user the User entity to be converted
     * @return a UserFromAnswerTaskDTO representing the user
     */
    public static UserFromAnswerTaskDTO fromUser(User user) {
        Set<String> groupNames = user.getGroups().stream()
                .map(Group::getName)
                .collect(Collectors.toSet());

        return new UserFromAnswerTaskDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getEmail(),
                user.getTelegramChatId(),
                groupNames
        );
    }

    /**
     * Compares this UserFromAnswerTaskDTO to another object for equality.
     *
     * @param o the object to be compared
     * @return true if this UserFromAnswerTaskDTO is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFromAnswerTaskDTO that = (UserFromAnswerTaskDTO) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(groups, that.groups);
    }

    /**
     * Returns a hash code value for this UserFromAnswerTaskDTO.
     *
     * @return a hash code value for this UserFromAnswerTaskDTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                surname,
                phone,
                email,
                groups
        );
    }

}
