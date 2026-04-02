package com.texas.developers.ams.dto.sessiondto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SessionResponseDto {
    private Integer id;
    private String name;
    private Integer teacherId;
    private String teacherName;
    private Integer courseId;
    private String courseName;
    private LocalDate startDate;
    private String sessionTime;
    private String status;
}
