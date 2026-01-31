package com.inuvro.saltyserver.config

import com.inuvro.saltyserver.security.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * If SALTY_ADMIN_PASSWORD is set, updates the default admin user's password at startup.
 * Flyway creates admin with a fixed hash; this allows overriding via env (e.g. in Docker).
 */
@Component
@Order(Integer.MAX_VALUE) // run after DB/migrations and other init
class DefaultAdminPasswordInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminPasswordInitializer)

    @Value('${salty.admin.password:}')
    private String adminPassword

    private final UserService userService

    DefaultAdminPasswordInitializer(UserService userService) {
        this.userService = userService
    }

    @Override
    void run(ApplicationArguments args) {
        if (!adminPassword?.trim()) {
            return
        }
        userService.findByUsername('admin').ifPresent { user ->
            userService.changePassword(user.id, adminPassword.trim())
            log.info('Default admin password updated from SALTY_ADMIN_PASSWORD')
        }
    }
}
