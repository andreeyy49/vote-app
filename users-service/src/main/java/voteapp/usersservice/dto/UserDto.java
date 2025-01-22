package voteapp.usersservice.dto;

import voteapp.usersservice.model.RoleType;

import java.util.List;

public class UserDto {
    private String id;
    private String email;
    private String name;
    private String password;
    private List<String> rolls;

    public UserDto(String id, String email, String name, String password, List<String> rolls) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.rolls = rolls;
    }

    public UserDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRolls() {
        return rolls;
    }

    public void setRolls(List<String> rolls) {
        this.rolls = rolls;
    }
}
