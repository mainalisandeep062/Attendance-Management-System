package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.converter.TeacherConverter;
import com.texas.developers.ams.dto.teacherdto.TeacherRequestDto;
import com.texas.developers.ams.dto.teacherdto.TeacherUpdateDto;
import com.texas.developers.ams.entity.Teacher;
import com.texas.developers.ams.repo.CourseRepository;
import com.texas.developers.ams.repo.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServices {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherConverter teacherConverter;

    public void addTeacher(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherConverter.toEntity(teacherRequestDto);
        teacherRepository.save(teacher);
    }

    public Teacher getTeacherById(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public void updateTeacher(TeacherUpdateDto teacher) {
        Teacher existingTeacher = teacherRepository.findById(teacher.getId()).
                orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacher.getId()));

        if(teacher.getFullName() != null)
            existingTeacher.setFullName(teacher.getFullName());
        if(teacher.getEmail() != null)
            existingTeacher.setEmail(teacher.getEmail());
        if(teacher.getMobileNumber() != null)
            existingTeacher.setMobileNumber(teacher.getMobileNumber());
        existingTeacher.setCourses(teacher.getCourseIds().
                stream()
                .map(courseId -> courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId)))
                        .toList()
                );
    }

    public void deleteTeacherById(Integer id) {
        teacherRepository.deleteById(id);
    }
}
