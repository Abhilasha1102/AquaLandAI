package com.landriskai.repo;

import com.landriskai.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Optional<ReportEntity> findByIdAndVerificationCode(Long id, String verificationCode);
    Optional<ReportEntity> findByOrder_Id(Long orderId);
}
