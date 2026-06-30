package com.texas.developers.ams.converter;

import com.texas.developers.ams.dto.sessiondto.SessionRequestDto;
import com.texas.developers.ams.dto.sessiondto.SessionResponseDto;
import com.texas.developers.ams.entity.Session;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionContverter {

    public SessionResponseDto toDTO(Session entity) {
        String courseName = entity.getCourse() != null ? entity.getCourse().getCourseName() : null;

        return SessionResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .teacherId(entity.getTeacher() != null ? entity.getTeacher().getId() : null)
                .teacherName(entity.getTeacher() != null ? entity.getTeacher().getFullName() : null)
                .courseId(entity.getCourse() != null ? entity.getCourse().getId() : null)
                .courseName(courseName)
                .startDate(entity.getStartDate())
                .sessionTime(entity.getSessionTime())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .build();
    }


    public Session toEntity(SessionRequestDto dto) {

        // Note: Teacher and Course associations should be set separately in the service layer
        return Session.builder()
                .name(dto.getName())
                .build();
    }

    public List<SessionResponseDto> toDtoList(List<Session> sessions) {
        return sessions
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
