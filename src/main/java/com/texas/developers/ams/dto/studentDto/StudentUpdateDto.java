package com.texas.developers.ams.dto.studentDto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class StudentUpdateDto {
    private Integer id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String faculty;
    private Date collegeJoinDate;
}
