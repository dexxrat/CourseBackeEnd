package com.example.gamestore.util;

import com.example.gamestore.model.User;
import com.example.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }

    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            return user.getId();
        }
        return null;
    }
}