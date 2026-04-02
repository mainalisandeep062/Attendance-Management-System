package com.texas.developers.ams.repo;

import com.texas.developers.ams.entity.SessionStudents;
import com.texas.developers.ams.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionStudentRepository extends JpaRepository<SessionStudents, Integer> {

    @Query("""
            SELECT ss
            FROM SessionStudents ss
            JOIN FETCH ss.student s
            JOIN FETCH ss.session se
            WHERE se.id = :sessionId
            ORDER BY s.fullName ASC
            """)
    List<SessionStudents> findBySessionIdOrderByStudentFullNameAsc(@Param("sessionId") Integer sessionId);

    @Query("""
            SELECT ss
            FROM SessionStudents ss
            JOIN FETCH ss.student s
            JOIN FETCH ss.session se
            WHERE se.id = :sessionId
              AND LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY s.fullName ASC
            """)
    List<SessionStudents> findBySessionIdAndStudentNameLike(
            @Param("sessionId") Integer sessionId,
            @Param("keyword") String keyword
    );

    @Query("""
            SELECT ss
            FROM SessionStudents ss
            JOIN FETCH ss.student s
            JOIN FETCH ss.session se
            WHERE s.id IN :studentIds
              AND se.status = :status
              AND se.id <> :sessionId
            """)
    List<SessionStudents> findActiveMappingsForStudentIdsOutsideSession(
            @Param("studentIds") List<Integer> studentIds,
            @Param("status") SessionStatus status,
            @Param("sessionId") Integer sessionId
    );

    boolean existsBySessionIdAndStudentId(Integer sessionId, Integer studentId);

    void deleteBySessionIdAndStudentId(Integer sessionId, Integer studentId);
}
