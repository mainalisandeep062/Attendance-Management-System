package com.texas.developers.ams.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler  {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(CourseAlreasyExistException.class)
    public String handleCourseAlreadyExistException(CourseAlreasyExistException ex,
                                          Locale locale,
                                          RedirectAttributes redirectAttributes) {
        addErrorFlash(ex, locale, redirectAttributes);
        if (ex.getData() != null) {
            redirectAttributes.addFlashAttribute("findRequest", ex.getData());
        }
        return "redirect:/course";
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(UserAlreadyExistException ex,
                                          Locale locale,
                                          RedirectAttributes redirectAttributes) {
        addErrorFlash(ex, locale, redirectAttributes);
        if (ex.getData() != null) {
            redirectAttributes.addFlashAttribute("findRequest", ex.getData());
        }
        return "redirect:/users";
    }
    @ExceptionHandler({MultipartException.class, MaxUploadSizeExceededException.class})
    public Object handleUploadException(Exception ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "File upload failed: " + ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("error", "File upload failed: " + ex.getMessage());
        return buildRedirectForPath(request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex,
                                                 HttpServletRequest request,
                                                 RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", ex.getMessage()));
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return buildRedirectForPath(request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        String message = "Unexpected error occurred. Please try again.";
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", message));
        }
        redirectAttributes.addFlashAttribute("error", message + " " + ex.getMessage());
        return buildRedirectForPath(request.getRequestURI());
    }

    private void addErrorFlash(ApplicationException ex,
                               Locale locale,
                               RedirectAttributes redirectAttributes) {
        String msg = messageSource.getMessage(ex.getMessage(), ex.getArgs(), locale);
        redirectAttributes.addFlashAttribute("error", msg);
    }

    private String buildRedirectForPath(String path) {
        if (path == null) {
            return "redirect:/dashboard";
        }
        if (path.startsWith("/student")) {
            return "redirect:/student";
        }
        if (path.startsWith("/sessions")) {
            return "redirect:/sessions";
        }
        if (path.startsWith("/teacher")) {
            return "redirect:/teacher";
        }
        if (path.startsWith("/course")) {
            return "redirect:/course";
        }
        if (path.startsWith("/users")) {
            return "redirect:/users";
        }
        return "redirect:/dashboard";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        return (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }
}
