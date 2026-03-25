package com.texas.developers.ams.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherResponseDto {
    private Short id;
    private String fullName;
    private String email;
    private String mobileNumber;
}
