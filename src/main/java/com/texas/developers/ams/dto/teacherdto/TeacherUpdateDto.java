package com.texas.developers.ams.dto.teacherdto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeacherUpdateDto {
    private String fullName;
    private String email;
    private String mobileNumber;
    private List<Integer> courseIds;
}
