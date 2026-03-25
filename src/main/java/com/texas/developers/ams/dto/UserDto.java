package com.texas.developers.ams.dto;


import com.texas.developers.ams.enums.RoleEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String username;

    private String password;

    private String mobileNumber;

    private String email;

    private RoleEnum role;

    private Boolean isActive = Boolean.TRUE;
}