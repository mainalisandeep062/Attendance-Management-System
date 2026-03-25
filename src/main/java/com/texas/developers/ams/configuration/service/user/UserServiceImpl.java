package com.texas.developers.ams.configuration.service.user;


import com.texas.developers.ams.converter.UserConverter;
import com.texas.developers.ams.dto.UserDto;
import com.texas.developers.ams.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserConverter converter ;

    public UserServiceImpl(UserRepository userRepository, UserConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return converter.toDtoList(userRepository.findAll());
    }

    @Override
    public void saveUser(UserDto dto) {
        userRepository.save(converter.toEntity(dto));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}