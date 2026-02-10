package com.landriskai.repo;

import com.landriskai.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhoneHash(String phoneHash);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByUserType(UserEntity.UserType userType);
    List<UserEntity> findByStatus(UserEntity.UserStatus status);
    
    @Query("SELECT u FROM UserEntity u WHERE u.dataRetentionUntil < ?1 AND u.status = 'DELETED'")
    List<UserEntity> findExpiredDeletedUsers(Instant now);
}
