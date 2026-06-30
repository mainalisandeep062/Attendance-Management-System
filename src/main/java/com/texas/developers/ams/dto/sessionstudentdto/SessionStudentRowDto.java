package com.texas.developers.ams.dto.sessionstudentdto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionStudentRowDto {
    private Integer studentId;
    private String fullName;
    private String email;
    private String faculty;
}

