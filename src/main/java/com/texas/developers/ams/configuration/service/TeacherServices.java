package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.converter.TeacherConverter;
import com.texas.developers.ams.dto.teacherdto.TeacherRequestDto;
import com.texas.developers.ams.dto.teacherdto.TeacherUpdateDto;
import com.texas.developers.ams.entity.Teacher;
import com.texas.developers.ams.repo.TeacherRepository;
import com.texas.developers.ams.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeacherServices {

    private final EmailService emailService;
    private final TeacherRepository teacherRepository;
    private final TeacherConverter teacherConverter;

    public void addTeacher(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherConverter.toEntity(teacherRequestDto);
        try{
            emailService.sendEmail(
                    teacher.getEmail(),
                    "Welcome to the School",
                    "email/registration_success",
                    Map.of("name", teacher.getFullName(),
                            "username", teacher.getEmail(),
                            "password", "defaultPassword123"));
        }catch (Exception e){
            throw new RuntimeException("Failed to send email to " + teacher.getEmail(), e);
        }
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


    }

    public void deleteTeacherById(Integer id) {
        teacherRepository.deleteById(id);
    }
}
