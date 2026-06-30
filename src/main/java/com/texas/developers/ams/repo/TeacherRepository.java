package com.texas.developers.ams.repo;

import com.texas.developers.ams.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);
}
