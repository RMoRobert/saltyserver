package com.inuvro.saltyserver.security

import com.inuvro.saltyserver.model.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService {
    
    private final UserRepository userRepository
    private final PasswordEncoder passwordEncoder
    
    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository
        this.passwordEncoder = passwordEncoder
    }
    
    List<User> findAll() {
        return userRepository.findAll()
    }
    
    Optional<User> findById(String id) {
        return userRepository.findById(id)
    }
    
    Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
    }
    
    boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username)
    }
    
    @Transactional
    User createUser(String username, String password, String email, Set<String> roles) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: ${username}")
        }
        
        User user = new User(
            id: UUID.randomUUID().toString(),
            username: username,
            password: passwordEncoder.encode(password),
            email: email,
            enabled: true,
            createdDate: java.time.LocalDateTime.now(),
            roles: roles ?: ['ROLE_USER'] as Set
        )
        
        return userRepository.save(user)
    }
    
    @Transactional
    User updateUser(String id, String email, Set<String> roles, Boolean enabled) {
        User user = userRepository.findById(id)
            .orElseThrow { new IllegalArgumentException("User not found: ${id}") }
        
        if (email != null) {
            user.email = email
        }
        if (roles != null) {
            user.roles = roles
        }
        if (enabled != null) {
            user.enabled = enabled
        }
        
        return userRepository.save(user)
    }
    
    @Transactional
    void changePassword(String id, String newPassword) {
        User user = userRepository.findById(id)
            .orElseThrow { new IllegalArgumentException("User not found: ${id}") }
        
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }
    
    @Transactional
    void deleteUser(String id) {
        userRepository.deleteById(id)
    }
}
