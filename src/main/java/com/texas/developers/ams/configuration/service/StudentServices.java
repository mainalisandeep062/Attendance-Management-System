package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.converter.StudentConverter;
import com.texas.developers.ams.dto.studentDto.StudentRequestDto;
import com.texas.developers.ams.dto.studentDto.StudentUpdateDto;
import com.texas.developers.ams.entity.Student;
import com.texas.developers.ams.repo.StudentRepository;
import com.texas.developers.ams.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentServices {
    private final EmailService emailService;
    private final StudentRepository studentRepository;
    private final StudentConverter studentConverter;

    public void addStudent(StudentRequestDto studentRequestDto) {
        Student student = studentConverter.toEntity(studentRequestDto);
        try{
            emailService.sendEmail(
                    student.getEmail(),
                    "Welcome to the School",
                    "email/registrationsuccess",
                    Map.of("name", student.getFullName(),
                            "username", student.getEmail(),
                            "password", "defaultPassword123"));
        }catch (Exception e){
            throw new RuntimeException("Failed to send email to " + student.getEmail(), e);
        }
        studentRepository.save(student);
    }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void updateStudent(StudentUpdateDto student) {

    }

    public void deleteStudentById(Integer id) {
        studentRepository.deleteById(id);
    }
}
