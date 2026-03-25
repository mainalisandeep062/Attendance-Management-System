package com.texas.developers.ams.exception;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler  {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(CourseAlreasyExistException.class)
    public String handleNotFoundException(CourseAlreasyExistException ex,
                                          Locale locale,
                                          RedirectAttributes redirectAttributes) {
        addErrorFlash(ex, locale, redirectAttributes);
        if (ex.getData() != null) {
            redirectAttributes.addFlashAttribute("findRequest", ex.getData());
        }
        return "redirect:/course";
    }
    private void addErrorFlash(ApplicationException ex,
                               Locale locale,
                               RedirectAttributes redirectAttributes) {
        String msg = messageSource.getMessage(ex.getMessage(), ex.getArgs(), locale);
        redirectAttributes.addFlashAttribute("error", msg);
    }

}
