package com.example.customeridentitydemo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, UserDetails> users;

    public CustomUserDetailsService() {
        // Use pre-encrypted passwords to avoid circular dependency
        // These are BCrypt hashes for "admin123", "password", and "demo123"
        this.passwordEncoder = null; // Not needed since we use pre-encoded passwords
        this.users = new HashMap<>();
        
        // Initialize with some demo users with pre-encrypted passwords
        // In production, this would come from a database
        users.put("admin", User.builder()
                .username("admin")
                .password("$2a$10$4lhKdcvvejPfyq2Xgnx0UeqRgdWZ6PbnZ3PpSmXf3YmPLQp4fjTui") // admin123
                .authorities(Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
                ))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build());
                
        users.put("user", User.builder()
                .username("user")
                .password("$2a$10$M8j3/./na6RRYaXrXhSd5eYmwSvoG0.wa/d574O./bMEL14mRwSuK") // password
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build());
                
        users.put("demo", User.builder()
                .username("demo")
                .password("$2a$10$G2SNI0NYGI2JbOus9ESWnuHIFcrptDEg/gB9OFyf2EOHIeof8.s..") // demo123
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build());

        log.info("Initialized CustomUserDetailsService with {} demo users", users.size());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        UserDetails templateUser = users.get(username);
        if (templateUser == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        // Create a new User instance to avoid mutation of the stored template
        UserDetails user = User.builder()
                .username(templateUser.getUsername())
                .password(templateUser.getPassword())
                .authorities(templateUser.getAuthorities())
                .accountExpired(!templateUser.isAccountNonExpired())
                .accountLocked(!templateUser.isAccountNonLocked()) 
                .credentialsExpired(!templateUser.isCredentialsNonExpired())
                .disabled(!templateUser.isEnabled())
                .build();
        
        log.debug("User loaded successfully: {} with authorities: {}", 
                username, user.getAuthorities());
        return user;
    }

    // Method to get all usernames for testing/demo purposes
    public List<String> getAllUsernames() {
        return users.keySet().stream().toList();
    }
}