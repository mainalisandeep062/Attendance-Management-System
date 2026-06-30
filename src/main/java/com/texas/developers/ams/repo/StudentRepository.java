package com.texas.developers.ams.repo;

import com.texas.developers.ams.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer > {

    List<Student> findAllByOrderByFullNameAsc();

    List<Student> findByFullNameContainingIgnoreCaseOrderByFullNameAsc(String keyword);

    Boolean existsByEmail(String email);
}
