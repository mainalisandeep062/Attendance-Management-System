package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.converter.SessionContverter;
import com.texas.developers.ams.dto.sessiondto.SessionRequestDto;
import com.texas.developers.ams.dto.sessiondto.SessionResponseDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentBulkResultDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentCandidateDto;
import com.texas.developers.ams.dto.sessionstudentdto.SessionStudentRowDto;
import com.texas.developers.ams.entity.Course;
import com.texas.developers.ams.entity.Session;
import com.texas.developers.ams.entity.SessionStudents;
import com.texas.developers.ams.entity.Student;
import com.texas.developers.ams.entity.Teacher;
import com.texas.developers.ams.enums.SessionStatus;
import com.texas.developers.ams.repo.CourseRepository;
import com.texas.developers.ams.repo.SessionRepository;
import com.texas.developers.ams.repo.SessionStudentRepository;
import com.texas.developers.ams.repo.StudentRepository;
import com.texas.developers.ams.repo.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final SessionStudentRepository sessionStudentRepository;
    private final SessionContverter sessionContverter;

    public List<SessionResponseDto> getAllSessions() {
        return sessionContverter.toDtoList(sessionRepository.findAllByOrderByIdAsc());
    }

    public SessionResponseDto getSessionById(Integer id) {
        return sessionContverter.toDTO(getSessionOrThrow(id));
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
        String sessionTime = buildSessionTime(dto, startDate);
        Session session = dto.getId() == null ? new Session() : getSessionOrThrow(dto.getId());

        session.setName(dto.getName().trim());
        session.setTeacher(teacher);
        session.setCourse(course);
        session.setStartDate(startDate);
        session.setSessionTime(sessionTime);

        // Allow status change only when editing; creation still defaults to ACTIVE.
        if (dto.getId() != null && dto.getStatus() != null) {
            session.setStatus(dto.getStatus());
        } else if (session.getStatus() == null) {
            session.setStatus(SessionStatus.ACTIVE);
        }

        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<SessionStudentRowDto> getSessionStudents(Integer sessionId, String keyword) {
        getSessionOrThrow(sessionId);
        List<SessionStudents> mappings = hasText(keyword)
                ? sessionStudentRepository.findBySessionIdAndStudentNameLike(sessionId, keyword.trim())
                : sessionStudentRepository.findBySessionIdOrderByStudentFullNameAsc(sessionId);

        return mappings.stream().map(m -> SessionStudentRowDto.builder()
                .studentId(m.getStudent().getId())
                .fullName(m.getStudent().getFullName())
                .email(m.getStudent().getEmail())
                .faculty(m.getStudent().getFaculty() == null ? "-" : m.getStudent().getFaculty().name())
                .build()).toList();
    }

    @Transactional(readOnly = true)
    public List<SessionStudentCandidateDto> getStudentCandidates(Integer sessionId, String keyword) {
        Session session = getSessionOrThrow(sessionId);
        List<Student> students = hasText(keyword)
                ? studentRepository.findByFullNameContainingIgnoreCaseOrderByFullNameAsc(keyword.trim())
                : studentRepository.findAllByOrderByFullNameAsc();

        List<Integer> studentIds = students.stream().map(Student::getId).toList();
        Set<Integer> existing = sessionStudentRepository.findBySessionIdOrderByStudentFullNameAsc(sessionId)
                .stream().map(ss -> ss.getStudent().getId()).collect(Collectors.toSet());

        Map<Integer, SessionStudents> activeMap = new LinkedHashMap<>();
        if (!studentIds.isEmpty()) {
            List<SessionStudents> active = sessionStudentRepository.findActiveMappingsForStudentIdsOutsideSession(
                    studentIds, SessionStatus.ACTIVE, sessionId);
            for (SessionStudents ss : active) activeMap.putIfAbsent(ss.getStudent().getId(), ss);
        }

        boolean targetActive = session.getStatus() == SessionStatus.ACTIVE;
        return students.stream().map(s -> {
            SessionStudents active = activeMap.get(s.getId());
            boolean alreadyInSession = existing.contains(s.getId());
            boolean selectable = alreadyInSession || !targetActive || active == null;
            return SessionStudentCandidateDto.builder()
                    .studentId(s.getId())
                    .fullName(s.getFullName())
                    .email(s.getEmail())
                    .faculty(s.getFaculty() == null ? "-" : s.getFaculty().name())
                    .alreadyInSession(alreadyInSession)
                    .selectable(selectable)
                    .activeSessionName(active == null ? null : active.getSession().getName())
                    .build();
        }).toList();
    }

    public SessionStudentBulkResultDto addStudentsToSession(Integer sessionId, List<Integer> studentIdsRaw) {
        Session session = getSessionOrThrow(sessionId);
        if (studentIdsRaw == null || studentIdsRaw.isEmpty()) {
            throw new IllegalArgumentException("Select at least one student.");
        }

        Set<Integer> studentIds = new LinkedHashSet<>(studentIdsRaw);
        List<Student> students = studentRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            Set<Integer> found = students.stream().map(Student::getId).collect(Collectors.toSet());
            throw new IllegalArgumentException("Students not found: "
                    + studentIds.stream().filter(id -> !found.contains(id)).toList());
        }

        Map<Integer, Student> studentMap = students.stream().collect(Collectors.toMap(Student::getId, s -> s));
        List<String> conflicts = new ArrayList<>();

        if (session.getStatus() == SessionStatus.ACTIVE) {
            List<SessionStudents> active = sessionStudentRepository.findActiveMappingsForStudentIdsOutsideSession(
                    new ArrayList<>(studentIds), SessionStatus.ACTIVE, sessionId);
            for (SessionStudents ss : active) {
                conflicts.add(ss.getStudent().getFullName() + " (active in: " + ss.getSession().getName() + ")");
                studentIds.remove(ss.getStudent().getId());
            }
        }

        int added = 0;
        int skipped = 0;
        for (Integer studentId : studentIds) {
            if (sessionStudentRepository.existsBySessionIdAndStudentId(sessionId, studentId)) {
                skipped++;
                continue;
            }
            sessionStudentRepository.save(SessionStudents.builder()
                    .session(session)
                    .student(studentMap.get(studentId))
                    .build());
            added++;
        }

        String message = added > 0 ? added + " student(s) added successfully."
                : !conflicts.isEmpty() ? "No students added due to active session conflicts."
                : skipped > 0 ? "Selected students are already in this session."
                : "Students updated.";

        return SessionStudentBulkResultDto.builder()
                .success(true)
                .message(message)
                .addedCount(added)
                .skippedCount(skipped)
                .conflicts(conflicts)
                .build();
    }

    public void removeStudentFromSession(Integer sessionId, Integer studentId) {
        getSessionOrThrow(sessionId);
        if (!sessionStudentRepository.existsBySessionIdAndStudentId(sessionId, studentId)) {
            throw new IllegalArgumentException("Student is not mapped to this session.");
        }
        sessionStudentRepository.deleteBySessionIdAndStudentId(sessionId, studentId);
    }

    private Session getSessionOrThrow(Integer id) {
        return sessionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found."));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private LocalDate buildStartDate(SessionRequestDto dto) {
        LocalDate startDate;
        try {
            startDate = LocalDate.of(dto.getStartYear(), dto.getStartMonth(), dto.getStartDay());
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Selected start date is invalid.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Session date cannot be in the past.");
        }
        return startDate;
    }

    private String buildSessionTime(SessionRequestDto dto, LocalDate startDate) {
        LocalTime startTime = to24Hour(dto.getStartHour(), dto.getStartMinute(), dto.getStartMeridiem());
        LocalTime endTime = to24Hour(dto.getEndHour(), dto.getEndMinute(), dto.getEndMeridiem());
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Session end time must be after start time.");
        }
        if (LocalDateTime.of(startDate, startTime).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Session start time must be current or future.");
        }

        return formatDisplayTime(dto.getStartHour(), dto.getStartMinute(), dto.getStartMeridiem())
                + " - " + formatDisplayTime(dto.getEndHour(), dto.getEndMinute(), dto.getEndMeridiem());
    }

    private LocalTime to24Hour(Integer hour12, Integer minute, String meridiemRaw) {
        String meridiem = meridiemRaw.toUpperCase(Locale.ROOT);
        int hour = hour12 % 12;
        if ("PM".equals(meridiem)) hour += 12;
        return LocalTime.of(hour, minute);
    }

    private String formatDisplayTime(Integer hour, Integer minute, String meridiemRaw) {
        return String.format("%02d:%02d %s", hour, minute, meridiemRaw.toUpperCase(Locale.ROOT));
    }
}

