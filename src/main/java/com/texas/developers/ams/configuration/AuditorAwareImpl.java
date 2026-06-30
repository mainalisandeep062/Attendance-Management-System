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
        try {
            if (session != null) {
                Object user = session.getAttribute("username"); // or user object
                return Optional.ofNullable(user != null ? user.toString() : "SYSTEM");
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Do nothing");
            return Optional.empty();
        }
    }
}