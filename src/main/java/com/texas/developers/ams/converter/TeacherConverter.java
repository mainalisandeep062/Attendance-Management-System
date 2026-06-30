package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.teacherdto.TeacherRequestDto;
import com.texas.developers.ams.dto.teacherdto.TeacherResponseDto;
import com.texas.developers.ams.entity.Course;
import com.texas.developers.ams.entity.Teacher;
import com.texas.developers.ams.repo.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TeacherConverter {

    private final CourseRepository courseRepository;

    public TeacherResponseDto toDTO(Teacher entity) {
        return TeacherResponseDto.builder()
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .mobileNumber(entity.getMobileNumber())
                .courses(entity.getCourses())
                .build();
    }

    public Teacher toEntity(TeacherRequestDto dto) {
        Teacher teacher = Teacher.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .mobileNumber(dto.getMobileNumber())
                .build();

        teacher.setCourses(new ArrayList<>());

        if (dto.getCourseIds() == null || dto.getCourseIds().isEmpty()) {
            return teacher;
        }

        List<Course> courses = courseRepository.findAllById(dto.getCourseIds());
        teacher.setCourses(courses);

        return teacher;
    }

    public List<TeacherResponseDto> toDtoList(List<Teacher> teachers) {
        return teachers.stream().map(this::toDTO).toList();
    }
}
