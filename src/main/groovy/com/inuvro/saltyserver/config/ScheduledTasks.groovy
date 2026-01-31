package com.inuvro.saltyserver.config

import com.inuvro.saltyserver.recipe.RecipeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Scheduled tasks for maintenance operations.
 */
@Component
class ScheduledTasks {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks)
    
    private final RecipeService recipeService
    
    // How many days before a device is considered inactive
    private static final int DEVICE_INACTIVE_DAYS = 3650
    
    ScheduledTasks(RecipeService recipeService) {
        this.recipeService = recipeService
    }
    
    /**
     * Clean up inactive devices weekly (Sunday at 3 AM).
     * Devices that haven't synced in DEVICE_INACTIVE_DAYS will be removed.
     */
    @Scheduled(cron = "0 0 3 * * SUN") // Every Sunday at 3:00 AM
    void cleanupInactiveDevices() {
        log.info("Starting scheduled device cleanup (inactive threshold: {} days)", DEVICE_INACTIVE_DAYS)
        int deleted = recipeService.cleanupInactiveDevices(DEVICE_INACTIVE_DAYS)
        log.info("Device cleanup completed: {} inactive devices removed", deleted)
    }
}
