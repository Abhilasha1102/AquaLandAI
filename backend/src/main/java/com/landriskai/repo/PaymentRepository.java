package com.landriskai.repo;

import com.landriskai.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentGatewayRef(String gatewayRef);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findByStatus(PaymentEntity.PaymentStatus status);
    
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = 'PENDING' AND p.expiresAt < ?1")
    List<PaymentEntity> findExpiredPendingPayments(Instant now);
}
