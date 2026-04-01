package com.texas.developers.ams.dto.studentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDto {
    private Integer id;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String faculty;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate collegeJoinDate;
}
