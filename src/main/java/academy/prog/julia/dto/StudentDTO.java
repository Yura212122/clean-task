package academy.prog.julia.dto;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing student information.
 *
 * This class encapsulates all relevant details about a student. It provides getters and setters
 * for accessing and modifying the student's information. The class is designed to be used for
 * transferring student data between different layers of an application, particularly between
 * service and presentation layers.
 */
public class StudentDTO {

    private String uniqueId;
    private String registerDate;
    private String group;
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;

    /**
     * Constructs a new StudentDTO with the specified values.
     *
     * @param uniqueId the unique identifier for the student (must not be null)
     * @param group the group or class the student belongs to (must not be null)
     * @param registerDate the registration date (must not be null)
     * @param id the student's unique numeric ID (must not be null)
     * @param name the student's first name (must not be null)
     * @param surname the student's last name (must not be null)
     * @param email the student's email address (must not be null)
     * @param phone the student's phone number (must not be null)
     *
     * @throws NullPointerException if any of the required parameters are null
     */
    public StudentDTO(
            String uniqueId,
            String group,
            String registerDate,
            Long id,
            String name,
            String surname,
            String email,
            String phone
    ) {
        this.uniqueId = Objects.requireNonNull(uniqueId, "uniqueId cannot be null");
        this.registerDate = Objects.requireNonNull(registerDate, "registerDate cannot be null");
        this.group = Objects.requireNonNull(group, "registerDate cannot be null");
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.surname = Objects.requireNonNull(surname, "surname cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.phone = Objects.requireNonNull(phone, "phone cannot be null");
    }

    /**
     * Default constructor for creating an empty StudentDTO.
     */
    public StudentDTO() {

    }

    /**
     * Gets the unique identifier for the student.
     *
     * @return the unique ID of the student
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Sets the unique identifier for the student.
     *
     * @param uniqueId the unique ID to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Gets the group to which the student belongs.
     *
     * @return the group of the student
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group to which the student belongs.
     *
     * @param group the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Gets the registration date of the student.
     *
     * @return the registration date
     */
    public String getRegisterDate() {
        return registerDate;
    }

    /**
     * Sets the registration date of the student.
     *
     * @param registerDate the registration date to set
     */
    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    /**
     * Gets the unique ID of the student in the system.
     *
     * @return the unique ID of the student
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the student in the system.
     *
     * @param id the unique ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the student.
     *
     * @return the first name of the student
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name of the student.
     *
     * @param name the first name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the last name of the student.
     *
     * @return the last name of the student
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the last name of the student.
     *
     * @param surname the last name to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the email address of the student.
     *
     * @return the email address of the student
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the student.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the student.
     *
     * @return the phone number of the student
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the student.
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Checks whether two StudentDTO objects are equal based on their field values.
     *
     * This method compares the relevant fields such as uniqueId, group, id, name, surname,
     * email, and phone to determine if two instances represent the same student.
     *
     * @param o the object to compare with the current instance
     * @return true if both objects are of the same class and have matching field values, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDTO that = (StudentDTO) o;

        return Objects.equals(uniqueId, that.uniqueId) &&
                Objects.equals(registerDate, that.registerDate) &&
                Objects.equals(group, that.group) &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone)
        ;
    }

    /**
     * Generates a hash code for the StudentDTO object based on its field values.
     *
     * This method computes the hash code using the fields that uniquely identify
     * the student, such as uniqueId, id, name, surname, email, and phone.
     *
     * @return an integer hash code representing the object, useful for hash-based collections like HashMap or HashSet
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                uniqueId,
                registerDate,
                group,
                id,
                name,
                surname,
                email,
                phone
        );
    }

}
