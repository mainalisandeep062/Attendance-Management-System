package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.CourseServices;
import com.texas.developers.ams.configuration.service.SessionService;
import com.texas.developers.ams.configuration.service.TeacherServices;
import com.texas.developers.ams.dto.sessiondto.SessionRequestDto;
import com.texas.developers.ams.dto.sessiondto.SessionResponseDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentBulkRequestDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentBulkResultDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentCandidateDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentRowDto;
import com.texas.developers.ams.enums.SessionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getSessionById(@PathVariable Integer id) {
        try {
            SessionResponseDto session = sessionService.getSessionById(id);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @GetMapping("/{sessionId}/students")
    @ResponseBody
    public ResponseEntity<?> getSessionStudents(@PathVariable Integer sessionId,
                                                @RequestParam(required = false) String keyword) {
        try {
            List<SessionStudentRowDto> rows = sessionService.getSessionStudents(sessionId, keyword);
            return ResponseEntity.ok(rows);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @GetMapping("/{sessionId}/student-candidates")
    @ResponseBody
    public ResponseEntity<?> getStudentCandidates(@PathVariable Integer sessionId,
                                                  @RequestParam(required = false) String keyword) {
        try {
            List<SessionStudentCandidateDto> candidates = sessionService.getStudentCandidates(sessionId, keyword);
            return ResponseEntity.ok(candidates);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/{sessionId}/students")
    @ResponseBody
    public ResponseEntity<?> addStudents(@PathVariable Integer sessionId,
                                         @RequestBody SessionStudentBulkRequestDto request) {
        try {
            SessionStudentBulkResultDto result = sessionService.addStudentsToSession(sessionId, request.getStudentIds());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{sessionId}/students/{studentId}")
    @ResponseBody
    public ResponseEntity<?> removeStudent(@PathVariable Integer sessionId, @PathVariable Integer studentId) {
        try {
            sessionService.removeStudentFromSession(sessionId, studentId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Student removed from session."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
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
            redirectAttributes.addFlashAttribute("success", "Session saved successfully.");
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
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();

        List<Integer> years = IntStream.rangeClosed(currentYear, currentYear + 5).boxed().toList();
        List<String> monthNames = List.of(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        List<Integer> days = IntStream.rangeClosed(1, 31).boxed().toList();
        List<Integer> hours = IntStream.rangeClosed(1, 12).boxed().toList();

        List<Integer> minutes = new ArrayList<>();
        for (int i = 0; i <= 55; i += 5) minutes.add(i);

        model.addAttribute("sessions", sessionService.getAllSessions());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("years", years);
        model.addAttribute("monthNames", monthNames);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentDay", currentDay);
        model.addAttribute("days", days);
        model.addAttribute("hours", hours);
        model.addAttribute("minutes", minutes);
        model.addAttribute("meridiems", List.of("AM", "PM"));
        model.addAttribute("sessionStatuses", SessionStatus.values());
    }
}
