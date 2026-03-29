package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.converter.TeacherConverter;
import com.texas.developers.ams.dto.teacherdto.TeacherRequestDto;
import com.texas.developers.ams.dto.teacherdto.TeacherUpdateDto;
import com.texas.developers.ams.entity.Course;
import com.texas.developers.ams.entity.Teacher;
import com.texas.developers.ams.repo.CourseRepository;
import com.texas.developers.ams.repo.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeacherServices {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherConverter teacherConverter;

    public void addTeacher(TeacherRequestDto teacherRequestDto) {
        if (teacherRepository.existsByEmail(teacherRequestDto.getEmail())) {
            throw new IllegalArgumentException("Teacher email already exists.");
        }

        Teacher teacher = teacherConverter.toEntity(teacherRequestDto);
        teacher.setCourses(resolveCourses(teacherRequestDto.getCourseIds()));
        teacherRepository.save(teacher);
    }

    public Teacher getTeacherById(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + id));
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public void updateTeacher(TeacherUpdateDto teacher) {
        Teacher existingTeacher = teacherRepository.findById(teacher.getId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacher.getId()));

        if (teacherRepository.existsByEmailAndIdNot(teacher.getEmail(), teacher.getId())) {
            throw new IllegalArgumentException("Teacher email already exists.");
        }

        existingTeacher.setFullName(teacher.getFullName());
        existingTeacher.setEmail(teacher.getEmail());
        existingTeacher.setMobileNumber(teacher.getMobileNumber());
        existingTeacher.setCourses(resolveCourses(teacher.getCourseIds()));

        teacherRepository.save(existingTeacher);
    }

    public void saveTeacher(TeacherUpdateDto teacherForm) {
        if (teacherForm.getId() == null) {
            TeacherRequestDto requestDto = new TeacherRequestDto(
                    teacherForm.getFullName(),
                    teacherForm.getEmail(),
                    teacherForm.getMobileNumber(),
                    teacherForm.getCourseIds()
            );
            addTeacher(requestDto);
            return;
        }

        updateTeacher(teacherForm);
    }

    public void deleteTeacherById(Integer id) {
        if (!teacherRepository.existsById(id)) {
            throw new IllegalArgumentException("Teacher not found with id: " + id);
        }

        teacherRepository.deleteById(id);
    }

    private List<Course> resolveCourses(List<Integer> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Course> courses = new ArrayList<>();
        for (Integer courseId : courseIds) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
            courses.add(course);
        }
        return courses;
    }
}
