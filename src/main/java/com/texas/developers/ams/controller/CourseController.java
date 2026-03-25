package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.CourseServices;
import com.texas.developers.ams.entity.Course;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseServices courseService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "course/course-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        return "course/course-form";
    }

    @PostMapping("/add")
    public String addCourse(@ModelAttribute("course") @Valid Course course,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "course/course-form";
        }
        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("success", "Course added successfully!");
        return "redirect:/course";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "course/course-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute("course") @Valid Course course,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "course/course-form";
        }
        course.setId(id);
        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
        return "redirect:/course";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        courseService.deleteCourse(id);
        redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        return "redirect:/course";
    }
}