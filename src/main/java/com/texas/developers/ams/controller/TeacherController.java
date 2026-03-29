package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.CourseServices;
import com.texas.developers.ams.configuration.service.TeacherServices;
import com.texas.developers.ams.dto.teacherdto.TeacherUpdateDto;
import com.texas.developers.ams.entity.Teacher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherServices teacherService;
    private final CourseServices courseService;

    @GetMapping
    public String teachers(Model model) {
        populateBaseModel(model);

        if (!model.containsAttribute("teacherForm")) {
            model.addAttribute("teacherForm", new TeacherUpdateDto());
        }

        if (!model.containsAttribute("showTeacherModal")) {
            model.addAttribute("showTeacherModal", false);
        }

        return "/course/teacher";
    }

    @PostMapping("/save")
    public String saveTeacher(@Valid @ModelAttribute("teacherForm") TeacherUpdateDto teacherForm,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBaseModel(model);
            model.addAttribute("showTeacherModal", true);
            return "/course/teacher";
        }

        try {
            teacherService.saveTeacher(teacherForm);
            redirectAttributes.addFlashAttribute("success",
                    teacherForm.getId() == null ? "Teacher added successfully." : "Teacher updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("teacherForm", teacherForm);
            redirectAttributes.addFlashAttribute("showTeacherModal", true);
        }

        return "redirect:/teacher";
    }

    @GetMapping("/edit/{id}")
    public String editTeacherForm(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.getTeacherById(id);
            TeacherUpdateDto teacherForm = new TeacherUpdateDto(
                    teacher.getId(),
                    teacher.getFullName(),
                    teacher.getEmail(),
                    teacher.getMobileNumber(),
                    teacher.getCourses() == null
                            ? List.of()
                            : teacher.getCourses().stream().map(course -> course.getId()).collect(Collectors.toList())
            );

            redirectAttributes.addFlashAttribute("teacherForm", teacherForm);
            redirectAttributes.addFlashAttribute("showTeacherModal", true);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/teacher";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacherById(id);
            redirectAttributes.addFlashAttribute("success", "Teacher deleted successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/teacher";
    }

    private void populateBaseModel(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("courses", courseService.getAllCourses());
    }
}
