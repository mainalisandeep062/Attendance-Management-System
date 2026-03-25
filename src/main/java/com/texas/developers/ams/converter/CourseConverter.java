package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.CourseDto;
import com.texas.developers.ams.entity.Course;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseConverter extends GenericConverter<CourseDto, Course> {

    @Override
    CourseDto toDTO(Course course) {
        return null;
    }

    @Override
    Course toEntity(CourseDto courseDto) {
        return null;
    }

    @Override
    List<CourseDto> toDtoList(List<Course> courses) {
        return List.of();
    }
}
