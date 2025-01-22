package voteapp.usersservice.util;

import lombok.experimental.UtilityClass;
import voteapp.usersservice.model.RoleType;
import voteapp.usersservice.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {

    public static voteapp.usersservice.dto.UserDto userToDto(User user) {
        voteapp.usersservice.dto.UserDto userDto = new voteapp.usersservice.dto.UserDto();

        userDto.setId(user.getId().toString());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        List<String> roles = new ArrayList<>();

        user.getRole().forEach(role -> roles.add(role.getAuthorities().toString()));
        userDto.setRolls(roles);
        return userDto;
    }
}
