package academy.prog.julia.dto;

import academy.prog.julia.model.User;

public class UserForAdminFindingDTO {
    private String name;
    private String surname;
    private String email;

    public static UserForAdminFindingDTO userToDTO(User user) {
        return new UserForAdminFindingDTO(user.getName(), user.getSurname(), user.getEmail());
    }

    public UserForAdminFindingDTO(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public UserForAdminFindingDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
