package com.texas.developers.ams.dto.studentDto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;



@Data
@Builder
public class StudentRequestDto {
    private Integer id;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String faculty;
    private Date collegeJoinDate;
}
