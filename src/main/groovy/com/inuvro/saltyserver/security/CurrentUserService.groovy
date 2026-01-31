package com.inuvro.saltyserver.security

import com.inuvro.saltyserver.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

/**
 * Service to get the currently authenticated user.
 */
@Service
class CurrentUserService {
    
    private final UserRepository userRepository
    
    CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }
    
    /**
     * Get the currently authenticated user.
     * @return The User entity, or null if not authenticated
     */
    User getCurrentUser() {
        def authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication == null || !authentication.isAuthenticated()) {
            return null
        }
        
        def principal = authentication.getPrincipal()
        String username
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername()
        } else if (principal instanceof String) {
            username = principal
        } else {
            return null
        }
        
        return userRepository.findByUsername(username).orElse(null)
    }
    
    /**
     * Get the currently authenticated user, throwing exception if not found.
     */
    User requireCurrentUser() {
        def user = getCurrentUser()
        if (user == null) {
            throw new IllegalStateException("No authenticated user")
        }
        return user
    }
    
    /**
     * Get the current username, or null if not authenticated.
     */
    String getCurrentUsername() {
        def authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication == null || !authentication.isAuthenticated()) {
            return null
        }
        
        def principal = authentication.getPrincipal()
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername()
        } else if (principal instanceof String) {
            return principal
        }
        return null
    }
}
