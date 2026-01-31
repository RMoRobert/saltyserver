package com.inuvro.saltyserver.security

import com.inuvro.saltyserver.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username)
    boolean existsByUsername(String username)
}
