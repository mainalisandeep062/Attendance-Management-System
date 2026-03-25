package com.texas.developers.ams.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", 120);
        model.addAttribute("totalTeachers", 25);
        model.addAttribute("totalUsers", 40);
        model.addAttribute("totalCourses", 15);
        model.addAttribute("activeCourses", 10);
        model.addAttribute("completedCourses", 5);
        model.addAttribute("todayAttendance", "90%");
        model.addAttribute("absentStudents", 12);
        return "dashboard";
    }
}