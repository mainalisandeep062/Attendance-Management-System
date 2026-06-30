package com.texas.developers.ams.dto.teacherdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateDto {
    private Integer id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Please enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    private List<Integer> courseIds = new ArrayList<>();
}
