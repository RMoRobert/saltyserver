package com.inuvro.saltyserver.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
@Table(name = "app_user")  // would call "user" but some DB engines may reserve this name
class User {
    @Id
    String id
    
    @Column(nullable = false, unique = true)
    String username
    
    @Column(nullable = false)
    String password  // BCrypt hashed
    
    @Column
    String email
    
    @Column(nullable = false)
    Boolean enabled = true
    
    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now(ZoneOffset.UTC)
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    Set<String> roles = ['ROLE_USER'] as Set
    
    User() {}
    
    User(String id, String username, String password) {
        this.id = id
        this.username = username
        this.password = password
        this.createdDate = LocalDateTime.now(ZoneOffset.UTC)
    }
    
    User(String id, String username, String password, String email) {
        this(id, username, password)
        this.email = email
    }
}
