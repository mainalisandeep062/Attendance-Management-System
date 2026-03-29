package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.CourseServices;
import com.texas.developers.ams.configuration.service.TeacherServices;
import com.texas.developers.ams.dto.teacherdto.TeacherRequestDto;
import com.texas.developers.ams.dto.teacherdto.TeacherUpdateDto;
import com.texas.developers.ams.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherServices teacherService;   // You implement
    private final CourseServices courseService;     // You implement

    @GetMapping
    public String teachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "/course/teacher";
    }

    @GetMapping("/add")
    public String addTeacherForm(Model model) {
        Teacher teacher = new Teacher();
        teacher.setCourses(new ArrayList<>());

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courseService.getAllCourses());
        return "/course/teacher";
    }

    @PostMapping("/add")
    public String addTeacher(@ModelAttribute TeacherRequestDto teacher) {
        teacherService.addTeacher(teacher);
        return "redirect:/teacher";
    }

    @GetMapping("/edit/{id}")
    public String editTeacherForm(@PathVariable Integer id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courseService.getAllCourses());
        return "/course/teacher";
    }

    @PostMapping("/update")
    public String updateTeacher(@ModelAttribute TeacherUpdateDto teacher) {
        teacherService.updateTeacher(teacher);
        return "redirect:/teacher";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacherById(id);
        return "redirect:/teacher";
    }
}
