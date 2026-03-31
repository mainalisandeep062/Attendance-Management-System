package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.CourseServices;
import com.texas.developers.ams.converter.CourseConverter;
import com.texas.developers.ams.dto.CourseDto;
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
    private final CourseConverter courseConverter;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("course" , new CourseDto());
        return "course/course-form";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new CourseDto());
        return "course/course-form";
    }

    @PostMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model , RedirectAttributes redirectAttributes) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Course not found!");
            return "redirect:/course";
        }
        CourseDto dto = courseConverter.toDTO(course);
        model.addAttribute("course", dto);
        model.addAttribute("courses", courseService.getAllCourses());

        return "course/course-form";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("course") @Valid CourseDto courseDto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "course/course-form";
        }

        Course course;
        if (courseDto.getId() != null) {
            course = courseService.getCourseById(courseDto.getId());
            if (course == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found!");
                return "redirect:/course";
            }
            course.setCourseName(courseDto.getCourseName());
            course.setCourseDescription(courseDto.getCourseDescription());
        } else {
            course = courseConverter.toEntity(courseDto);
        }

        courseService.saveCourse(course);
        redirectAttributes.addFlashAttribute("success",
                courseDto.getId() != null ? "Course updated successfully!" : "Course added successfully!");
        return "redirect:/course";
    }

    @PostMapping("/delete")
    public String deleteCourse(@ModelAttribute("course") CourseDto courseDto,
                               RedirectAttributes redirectAttributes) {
        Course existingCourse = courseService.getCourseById(courseDto.getId());
        if (existingCourse != null) {
            courseService.deleteCourse(existingCourse.getId());
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Course not found!");
        }
        return "redirect:/course";
    }
}