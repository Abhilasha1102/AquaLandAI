package com.landriskai.repo;

import com.landriskai.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByAdminUserId(Long adminUserId);
    List<AuditLogEntity> findByEntityTypeAndEntityId(AuditLogEntity.EntityType entityType, Long entityId);
    List<AuditLogEntity> findByActionType(AuditLogEntity.ActionType actionType);
    
    @Query("SELECT a FROM AuditLogEntity a WHERE a.createdAt >= ?1 AND a.createdAt <= ?2 ORDER BY a.createdAt DESC")
    List<AuditLogEntity> findAuditsBetween(Instant startTime, Instant endTime);
}
