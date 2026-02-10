package com.inuvro.saltyserver.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter
    
    @Value('${salty.remember-me.seconds:1209600}')
    private int rememberMeTokenValiditySeconds  // default 14 days

    SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter
    }
    
    /**
     * Security filter chain for API endpoints - uses JWT authentication
     */
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf { csrf -> csrf.disable() }  // Disable CSRF for API (using JWT)
            .sessionManagement { session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll()  // Allow login/register
                    .requestMatchers("/api/recipes/images/**").permitAll()  // Allow image access (filenames are UUIDs)
                    .anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        
        return http.build()
    }
    
    /**
     * Security filter chain for web views - uses form-based login with sessions
     */
    @Bean
    @Order(2)
    SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/fonts/**", "/error").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()  // H2 console for dev
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .defaultSuccessUrl("/recipes", true)
                    .permitAll()
            }
            .rememberMe { remember ->
                remember
                    .key("salty-remember-me-key")
                    .tokenValiditySeconds(rememberMeTokenValiditySeconds)
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/login?logout")
                    .deleteCookies("remember-me")
                    .permitAll()
            }
            // Allow H2 console frames
            .headers { headers ->
                headers.frameOptions { frame -> frame.sameOrigin() }
            }
            .csrf { csrf ->
                // Disable CSRF for H2 console
                csrf.ignoringRequestMatchers("/h2-console/**")
            }
        
        return http.build()
    }
    
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return authProvider
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager()
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
