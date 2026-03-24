package com.texas.developers.texasattendancemanagementsystem.configuration.loader;

import com.texas.developers.texasattendancemanagementsystem.entity.User;
import com.texas.developers.texasattendancemanagementsystem.enums.RoleEnum;
import com.texas.developers.texasattendancemanagementsystem.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            String username = "superadmin";
            if (repo.findByUsername(username).isEmpty()) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(encoder.encode("admin123"));
                user.setRole(RoleEnum.ADMIN);
                repo.save(user);
                log.info("superadmin created successfully !!");
            }
        };
    }
}