package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.studentDto.StudentRequestDto;
import com.texas.developers.ams.dto.studentDto.StudentResponseDto;
import com.texas.developers.ams.entity.Student;
import com.texas.developers.ams.repo.CourseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import com.texas.developers.ams.enums.Faculty;

@Component
@RequiredArgsConstructor
public class StudentConverter {

    private final CourseRepository courseRepository;

    public StudentResponseDto toDTO(Student entity) {
        return StudentResponseDto.builder()
                .fullName(entity.getFullName())
                .mobileNumber(Long.parseLong(entity.getMobileNumber()))
                .email(entity.getEmail())
                .faculty(entity.getFaculty().name())
                .collegeJoinDate(entity.getCollegeJoinDate())
                .build();
    }

    public Student toEntity(StudentRequestDto dto) {
        Student student = Student.builder()
                .fullName(dto.getFullName())
                .mobileNumber(dto.getMobileNumber())
                .email(dto.getEmail())
                .faculty(dto.getFaculty() != null ? Faculty.valueOf(dto.getFaculty()) : null)
                .collegeJoinDate(dto.getCollegeJoinDate())
                .build();
        return student;
    }

    public List<StudentResponseDto> toDtoList(List<Student> students) {
        List<StudentResponseDto> studentDtoList =
                students.stream().map(this::toDTO).toList();
        return studentDtoList;
    }

}
