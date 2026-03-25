package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.UserDto;
import com.texas.developers.ams.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverter extends GenericConverter<UserDto, User> {

    @Override
    public UserDto toDTO(User entity) {
        if (entity == null) return null;

        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getMobileNumber(),
                entity.getEmail(),
                entity.getRole(),
                entity.getIsActive()
        );
    }

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        return new User(
                dto.getId(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getMobileNumber(),
                dto.getEmail(),
                dto.getRole(),
                dto.getIsActive()
        );
    }

    @Override
    public List<UserDto> toDtoList(List<User> entityList) {
        return entityList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}