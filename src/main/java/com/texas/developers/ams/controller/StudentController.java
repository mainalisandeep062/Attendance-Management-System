package com.texas.developers.ams.controller;

import com.texas.developers.ams.configuration.service.StudentServices;
import com.texas.developers.ams.dto.studentDto.BatchUploadResult;
import com.texas.developers.ams.dto.studentDto.StudentRequestDto;
import com.texas.developers.ams.dto.studentDto.StudentUpdateDto;
import com.texas.developers.ams.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("student", student);
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

    @PostMapping("/upload")
    public String uploadBatch(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        try {
            BatchUploadResult result = studentServices.uploadStudents(file);
            redirectAttributes.addFlashAttribute("uploadResult", result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("uploadResult",
                    BatchUploadResult.failure("Unexpected error: " + e.getMessage()));
        }
        return "redirect:/student";
    }

    @GetMapping("/sample")
    public ResponseEntity<byte[]> downloadSample() {
        try {
            byte[] data = studentServices.generateSampleExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=student_sample.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
