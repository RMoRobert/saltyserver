package com.inuvro.saltyserver.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "course")
class Course {
    @Id
    String id
    
    String name
    
    @Column(name = "last_modified_date")
    LocalDateTime lastModifiedDate
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    User user

    Course() {}

    Course(String id, String name) {
        this.id = id
        this.name = name
        this.lastModifiedDate = LocalDateTime.now()
    }
    
    Course(String id, String name, User user) {
        this(id, name)
        this.user = user
    }
}
