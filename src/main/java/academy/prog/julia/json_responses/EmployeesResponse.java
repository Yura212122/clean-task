package academy.prog.julia.json_responses;

import academy.prog.julia.model.User;

/**
 * Represents a response containing information about employees(users into company),
 * including their ID, name, surname, and role.
 */
public class EmployeesResponse {

    private Long id;
    private String name;
    private String surname;
    private String role;

    /**
     * Converts a User object into an EmployeesResponse object.
     *
     * This method extracts the necessary fields from the User model
     * and populates the corresponding fields in the EmployeesResponse object.
     *
     * @param user the User object to be converted
     * @return a populated EmployeesResponse object
     */
    public static EmployeesResponse userToEmployeesResponse (User user) {
        EmployeesResponse employeesResponse = new EmployeesResponse();
        employeesResponse.setId(user.getId());
        employeesResponse.setName(user.getName());
        employeesResponse.setSurname(user.getSurname());
        employeesResponse.setRole(user.getRole().toString());
        return employeesResponse;
    }

    /**
     * Default constructor for EmployeesResponse.
     *
     * This constructor creates an empty EmployeesResponse object.
     *
     */
    public EmployeesResponse() {}

    /**
     * Constructs an EmployeesResponse with the specified details.
     *
     * @param id       the unique identifier of the user as employee
     * @param name     the first name of the user as employee
     * @param surname  the surname of the user as employee
     * @param role     the role of the user as employee
     */
    public EmployeesResponse(
            Long id,
            String name,
            String surname,
            String role
    ) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    /**
     * Gets the ID of the user as employee.
     *
     * @return the employee's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the user as employee.
     *
     * @param id the employee's ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the user as employee.
     *
     * @return the employee's first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name of the user as employee.
     *
     * @param name the employee's first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the surname of the user as employee.
     *
     * @return the employee's surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the user as employee.
     *
     * @param surname the employee's surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the role of the user as employee.
     *
     * @return the employee's role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user as employee.
     *
     * @param role the employee's role
     */
    public void setRole(String role) {
        this.role = role;
    }

}
