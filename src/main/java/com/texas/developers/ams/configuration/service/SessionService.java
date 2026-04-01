package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.converter.SessionContverter;
import com.texas.developers.ams.dto.SessionRequestDto;
import com.texas.developers.ams.dto.SessionResponseDto;
import com.texas.developers.ams.entity.Course;
import com.texas.developers.ams.entity.Session;
import com.texas.developers.ams.entity.Teacher;
import com.texas.developers.ams.enums.SessionStatus;
import com.texas.developers.ams.repo.CourseRepository;
import com.texas.developers.ams.repo.SessionRepository;
import com.texas.developers.ams.repo.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final SessionContverter sessionContverter;

    public List<SessionResponseDto> getAllSessions() {
        return sessionContverter.toDtoList(sessionRepository.findAllByOrderByIdAsc());
    }

    public void saveSession(SessionRequestDto dto) {
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found."));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));

        boolean teacherHasCourse = teacher.getCourses() != null
                && teacher.getCourses().stream().anyMatch(c -> c.getId().equals(course.getId()));
        if (!teacherHasCourse) {
            throw new IllegalArgumentException("Selected teacher is not assigned to the selected course.");
        }

        LocalDate startDate = buildStartDate(dto);
        String sessionTime = buildSessionTime(dto);

        Session session = dto.getId() == null
                ? new Session()
                : sessionRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        session.setName(dto.getName().trim());
        session.setTeacher(teacher);
        session.setCourse(course);
        session.setStartDate(startDate);
        session.setSessionTime(sessionTime);
        if (session.getStatus() == null) {
            session.setStatus(SessionStatus.ACTIVE);
        }

        sessionRepository.save(session);
    }

    private LocalDate buildStartDate(SessionRequestDto dto) {
        try {
            return LocalDate.of(dto.getStartYear(), dto.getStartMonth(), dto.getStartDay());
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Selected start date is invalid.");
        }
    }

    private String buildSessionTime(SessionRequestDto dto) {
        LocalTime startTime = to24Hour(dto.getStartHour(), dto.getStartMinute(), dto.getStartMeridiem());
        LocalTime endTime = to24Hour(dto.getEndHour(), dto.getEndMinute(), dto.getEndMeridiem());

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Session end time must be after start time.");
        }

        return formatDisplayTime(dto.getStartHour(), dto.getStartMinute(), dto.getStartMeridiem())
                + " - "
                + formatDisplayTime(dto.getEndHour(), dto.getEndMinute(), dto.getEndMeridiem());
    }

    private LocalTime to24Hour(Integer hour12, Integer minute, String meridiemRaw) {
        String meridiem = meridiemRaw.toUpperCase(Locale.ROOT);
        int normalizedHour = hour12 % 12;
        if ("PM".equals(meridiem)) {
            normalizedHour += 12;
        }
        return LocalTime.of(normalizedHour, minute);
    }

    private String formatDisplayTime(Integer hour, Integer minute, String meridiemRaw) {
        String meridiem = meridiemRaw.toUpperCase(Locale.ROOT);
        return String.format("%02d:%02d %s", hour, minute, meridiem);
    }
}
