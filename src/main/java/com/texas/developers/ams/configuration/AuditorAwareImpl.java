package com.texas.developers.ams.configuration;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    private final HttpSession session;

    public AuditorAwareImpl(HttpSession session) {
        this.session = session;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Object user = session.getAttribute("username"); // or user object
        return Optional.ofNullable(user != null ? user.toString() : "SYSTEM");
    }
}