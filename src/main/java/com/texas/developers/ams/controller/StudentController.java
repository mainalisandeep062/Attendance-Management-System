package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.StudentServices;
import com.texas.developers.ams.dto.studentDto.StudentRequestDto;
import com.texas.developers.ams.dto.studentDto.StudentUpdateDto;
import com.texas.developers.ams.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final StudentServices studentServices;

    @GetMapping
    public String students(Model model) {
        List<Student> students = studentServices.getAllStudents();
        model.addAttribute("students", students);
        return "/course/student-list";
    }

    @GetMapping("/add")
    public String addStudentForm(Model model){
        Student student = new Student();
        model.addAttribute("student", student);
        return  "/course/student-form";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute StudentRequestDto student) {
        studentServices.addStudent(student);
        return "redirect:/student";
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Integer id, Model model) {
        Student student = studentServices.getStudentById(id);
        model.addAttribute("teacher", student);
        return "/course/student-form";
    }

    @PostMapping("/update")
    public String updateTeacher(@ModelAttribute StudentUpdateDto student) {
        studentServices.updateStudent(student);
        return "redirect:/student";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        studentServices.deleteStudentById(id);
        return "redirect:/student";
    }
}
