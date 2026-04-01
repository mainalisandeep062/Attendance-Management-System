package com.texas.developers.ams.configuration;

import com.texas.developers.ams.entity.User;
import com.texas.developers.ams.enums.RoleEnum;
import com.texas.developers.ams.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;

    public CustomLoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        HttpSession session = request.getSession();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        userRepository.findByUsername(userDetails.getUsername())
                .ifPresentOrElse(user -> {
                    setUserSession(session, user);
                    String redirectUrl = getRedirectUrlByRole(user.getRole());
                    sendRedirect(response, redirectUrl);

                }, () -> sendRedirect(response, "/login?error"));
    }

    private void setUserSession(HttpSession session, User user) {
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
    }

    private String getRedirectUrlByRole(RoleEnum role) {
        return switch (role) {
            case RoleEnum.ADMIN -> "/dashboard";
            case RoleEnum.TEACHER -> "/teacher/dashboard";
            case RoleEnum.INTERNAL_USER -> "/internal/dashboard";
            default -> "/";
        };
    }

    private void sendRedirect(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException("Redirect failed", e);
        }
    }
}
