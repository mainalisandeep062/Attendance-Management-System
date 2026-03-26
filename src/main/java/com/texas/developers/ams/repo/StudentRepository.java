package com.texas.developers.ams.repo;

import com.texas.developers.ams.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer > {

}
