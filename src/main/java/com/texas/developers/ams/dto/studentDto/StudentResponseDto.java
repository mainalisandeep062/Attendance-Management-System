package com.texas.developers.ams.dto.studentDto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class StudentResponseDto {
    private Integer id;
    private String fullName;
    private long mobileNumber;
    private String email;
    private String faculty;
    private LocalDate collegeJoinDate;
}
