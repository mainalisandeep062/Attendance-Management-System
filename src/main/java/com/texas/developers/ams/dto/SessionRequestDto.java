package com.texas.developers.ams.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SessionRequestDto {

    private Integer id;

    @NotBlank(message = "Session name is required")
    @Size(max = 50, message = "Session name must be at most 50 characters")
    private String name;

    @NotNull(message = "Teacher is required")
    private Integer teacherId;

    @NotNull(message = "Course is required")
    private Integer courseId;

    @NotNull(message = "Start year is required")
    private Integer startYear;

    @NotNull(message = "Start month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer startMonth;

    @NotNull(message = "Start day is required")
    @Min(value = 1, message = "Day must be between 1 and 31")
    @Max(value = 31, message = "Day must be between 1 and 31")
    private Integer startDay;

    @NotNull(message = "Start hour is required")
    @Min(value = 1, message = "Hour must be between 1 and 12")
    @Max(value = 12, message = "Hour must be between 1 and 12")
    private Integer startHour;

    @NotNull(message = "Start minute is required")
    @Min(value = 0, message = "Minute must be between 0 and 59")
    @Max(value = 59, message = "Minute must be between 0 and 59")
    private Integer startMinute;

    @NotBlank(message = "Start period is required")
    @Pattern(regexp = "AM|PM", message = "Start period must be AM or PM")
    private String startMeridiem;

    @NotNull(message = "End hour is required")
    @Min(value = 1, message = "Hour must be between 1 and 12")
    @Max(value = 12, message = "Hour must be between 1 and 12")
    private Integer endHour;

    @NotNull(message = "End minute is required")
    @Min(value = 0, message = "Minute must be between 0 and 59")
    @Max(value = 59, message = "Minute must be between 0 and 59")
    private Integer endMinute;

    @NotBlank(message = "End period is required")
    @Pattern(regexp = "AM|PM", message = "End period must be AM or PM")
    private String endMeridiem;
}
