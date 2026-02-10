package com.landriskai.repo;

import com.landriskai.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByOrderId(Long orderId);
    List<NotificationEntity> findByReportId(Long reportId);
    List<NotificationEntity> findByStatus(NotificationEntity.DeliveryStatus status);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.status = 'PENDING' AND n.nextRetryAt < ?1 AND n.retryCount < n.maxRetries")
    List<NotificationEntity> findPendingRetries(Instant now);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.notificationType = ?1 AND n.status = 'DELIVERED' AND n.deliveredAt >= ?2")
    long countSuccessfulDeliveries(NotificationEntity.NotificationType type, Instant since);
}
