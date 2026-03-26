package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.configuration.service.user.UserService;
import com.texas.developers.ams.dto.DashboardDataDto;
import org.springframework.stereotype.Component;

@Component
public class DashboardServiceImpl implements DashboardService {

    private final CourseServices courseServices;
    private final TeacherServices teacherServices;
    private final UserService userService;

    public DashboardServiceImpl(CourseServices courseServices,
                                TeacherServices teacherServices,
                                UserService userService) {
        this.courseServices = courseServices;
        this.teacherServices = teacherServices;
        this.userService = userService;
    }

    @Override
    public DashboardDataDto getDashboardData() {
        DashboardDataDto dashboardDataDto = new DashboardDataDto();
        dashboardDataDto.setActiveCourses((short) 10);
        dashboardDataDto.setTotalCourses((short) 10);
        dashboardDataDto.setTotalStudents((short) 10);
        dashboardDataDto.setTotalUsers((short) 10);
        dashboardDataDto.setTotalTeachers((short) 10);
        dashboardDataDto.setCompletedCourses((short) 10);
        return dashboardDataDto;
    }
}
