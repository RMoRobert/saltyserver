package com.inuvro.saltyserver.recipe

import com.inuvro.saltyserver.model.DeviceSync
import com.inuvro.saltyserver.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

import java.time.LocalDateTime

@Repository
interface DeviceSyncRepository extends JpaRepository<DeviceSync, String> {
    
    // User-scoped queries
    List<DeviceSync> findByUser(User user)
    Optional<DeviceSync> findByDeviceIdAndUser(String deviceId, User user)
    
    /**
     * Find devices that haven't synced in a while (for cleanup)
     */
    List<DeviceSync> findByLastSyncDateBefore(LocalDateTime before)
    List<DeviceSync> findByUserAndLastSyncDateBefore(User user, LocalDateTime before)
    
    /**
     * Delete inactive devices (haven't synced in a long time)
     */
    @Modifying
    @Query("DELETE FROM DeviceSync d WHERE d.lastSyncDate < :before")
    int deleteByLastSyncDateBefore(LocalDateTime before)
    
    @Modifying
    @Query("DELETE FROM DeviceSync d WHERE d.user = :user AND d.lastSyncDate < :before")
    int deleteByUserAndLastSyncDateBefore(User user, LocalDateTime before)
}
