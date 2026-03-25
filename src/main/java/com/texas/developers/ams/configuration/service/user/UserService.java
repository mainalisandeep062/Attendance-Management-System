package com.texas.developers.ams.configuration.service.user;

import com.texas.developers.ams.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    void saveUser(UserDto dto);

    void deleteUser(Long id);
}