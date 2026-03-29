package com.texas.developers.ams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDto {
    private Short totalStudents;
    private Short totalTeachers;
    private Short totalUsers;
    private Short totalCourses;
    private Short activeCourses;
    private Short completedCourses;
}
