package com.inuvro.saltyserver.security

import com.inuvro.saltyserver.model.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository
    
    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository
    }
    
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow { new UsernameNotFoundException("User not found: ${username}") }
        
        def authorities = user.roles.collect { new SimpleGrantedAuthority(it) }
        
        return new org.springframework.security.core.userdetails.User(
                user.username,
                user.password,
                user.enabled,
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        )
    }
    
    /**
     * Get the User entity (not just UserDetails) for the current username
     */
    User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow { new UsernameNotFoundException("User not found: ${username}") }
    }
}
