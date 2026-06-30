package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.CourseDto;
import com.texas.developers.ams.entity.Course;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseConverter extends GenericConverter<CourseDto, Course> {

    @Override
    public CourseDto toDTO(Course course) {
        if (course == null)
            return null;
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCourseDescription(course.getCourseDescription());

        return dto;
    }

    @Override
    public Course toEntity(CourseDto courseDto) {

        if(courseDto == null){
            return null;
        }
        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setCourseDescription(courseDto.getCourseDescription());
        return course;
    }

    @Override
    List<CourseDto> toDtoList(List<Course> courses) {
        if (courses == null) {
            return List.of();
        }

        List<CourseDto> dtoList = new ArrayList<>();

        for (Course course : courses) {
            CourseDto dto = toDTO(course);
            dtoList.add(dto);
        }

        return dtoList;
    }

}
