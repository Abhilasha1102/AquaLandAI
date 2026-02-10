package com.landriskai.repo;

import com.landriskai.entity.ResellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResellerRepository extends JpaRepository<ResellerEntity, Long> {
    Optional<ResellerEntity> findByResellerCode(String resellerCode);
    Optional<ResellerEntity> findByUserId(Long userId);
    List<ResellerEntity> findByStatus(ResellerEntity.ResellerStatus status);
    
    @Query("SELECT COUNT(r) FROM ResellerEntity r WHERE r.status = 'ACTIVE'")
    long countActiveResellers();
}
