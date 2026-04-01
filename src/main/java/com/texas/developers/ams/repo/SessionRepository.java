package com.texas.developers.ams.repo;

import com.texas.developers.ams.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findAllByOrderByIdAsc();
}
