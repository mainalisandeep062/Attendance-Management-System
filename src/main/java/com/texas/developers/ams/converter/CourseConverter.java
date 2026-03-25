package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.CourseCreationDto;
import com.texas.developers.ams.entity.Course;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseConverter extends GenericConverter<CourseCreationDto, Course> {

    @Override
    CourseCreationDto toDTO(Course course) {
        if (course == null)
            return null;
        CourseCreationDto dto = new CourseCreationDto();
        dto.setCourseName(course.getCourseName());
        dto.setCourseDescription(course.getCourseDescription());

        return dto;
    }

    @Override
    Course toEntity(CourseCreationDto courseDto) {
        return null;
    }

    @Override
    List<CourseCreationDto> toDtoList(List<Course> courses) {
        return List.of();
    }
}
