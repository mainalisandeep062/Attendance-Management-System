package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.CourseServices;
import com.texas.developers.ams.configuration.service.SessionService;
import com.texas.developers.ams.configuration.service.TeacherServices;
import com.texas.developers.ams.dto.SessionRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final TeacherServices teacherService;
    private final CourseServices courseService;

    @GetMapping
    public String listSessions(Model model) {
        populateBaseModel(model);

        if (!model.containsAttribute("sessionForm")) {
            model.addAttribute("sessionForm", new SessionRequestDto());
        }

        if (!model.containsAttribute("showSessionModal")) {
            model.addAttribute("showSessionModal", false);
        }

        return "/course/session";
    }

    @PostMapping("/save")
    public String saveSession(@Valid @ModelAttribute("sessionForm") SessionRequestDto sessionForm,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBaseModel(model);
            model.addAttribute("showSessionModal", true);
            return "/course/session";
        }

        try {
            sessionService.saveSession(sessionForm);
            redirectAttributes.addFlashAttribute("success", "Session created successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("sessionForm", sessionForm);
            redirectAttributes.addFlashAttribute("showSessionModal", true);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong while saving session.");
                redirectAttributes.addFlashAttribute("sessionForm", sessionForm);
            redirectAttributes.addFlashAttribute("showSessionModal", true);
        }

        return "redirect:/sessions";
    }

    private void populateBaseModel(Model model) {
        int currentYear = LocalDate.now().getYear();
        List<Integer> years = IntStream.rangeClosed(currentYear - 2, currentYear + 5).boxed().toList();
        List<Integer> months = IntStream.rangeClosed(1, 12).boxed().toList();
        List<Integer> days = IntStream.rangeClosed(1, 31).boxed().toList();
        List<Integer> hours = IntStream.rangeClosed(1, 12).boxed().toList();

        List<Integer> minutes = new ArrayList<>();
        for (int i = 0; i <= 55; i += 5) {
            minutes.add(i);
        }

        model.addAttribute("sessions", sessionService.getAllSessions());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("days", days);
        model.addAttribute("hours", hours);
        model.addAttribute("minutes", minutes);
        model.addAttribute("meridiems", List.of("AM", "PM"));
    }
}
