package com.texas.developers.ams.dto.teacherdto;

import com.texas.developers.ams.entity.Course;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeacherResponseDto {
    private Integer id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private List<Course> courses;
}
