package com.inuvro.saltyserver.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Tracks device sync times for deletion detection.
 * Each device registers and tracks when it last synced,
 * allowing detection of deletions without tombstone records.
 * 
 * All times are stored in UTC for consistent cross-timezone sync.
 */
@Entity
@Table(name = "device_sync")
class DeviceSync {
    
    @Id
    @Column(name = "device_id")
    String deviceId
    
    @Column(name = "device_name")
    String deviceName
    
    @Column(name = "last_sync_date", nullable = false)
    LocalDateTime lastSyncDate
    
    @Column(name = "first_sync_date", nullable = false)
    LocalDateTime firstSyncDate
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    User user
    
    DeviceSync() {}
    
    DeviceSync(String deviceId, String deviceName) {
        this.deviceId = deviceId
        this.deviceName = deviceName
        // Use UTC for consistent timezone handling
        def now = LocalDateTime.now(ZoneOffset.UTC)
        this.lastSyncDate = now
        this.firstSyncDate = now
    }
    
    DeviceSync(String deviceId, String deviceName, User user) {
        this(deviceId, deviceName)
        this.user = user
    }
    
    void updateLastSync() {
        // Use UTC for consistent timezone handling
        this.lastSyncDate = LocalDateTime.now(ZoneOffset.UTC)
    }
}
