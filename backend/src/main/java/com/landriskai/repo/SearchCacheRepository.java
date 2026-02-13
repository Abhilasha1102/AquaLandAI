package com.landriskai.repo;

import com.landriskai.entity.SearchCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchCacheRepository extends JpaRepository<SearchCacheEntity, Long> {
    
    /**
     * Find valid cache by land identifiers AND verify same user (email + whatsapp)
     * Only returns cache if user is the same AND cache hasn't expired
     * Different users pay full price, same user gets 80% discount
     */
    @Query("SELECT c FROM SearchCacheEntity c WHERE c.khata = ?1 AND c.khesra = ?2 AND c.district = ?3 " +
           "AND c.lastUserEmail = ?4 AND c.lastUserWhatsapp = ?5 AND c.expiresAt > current_timestamp")
    SearchCacheEntity findValidByLandAndUser(String khata, String khesra, String district, String userEmail, String userWhatsapp);
    
    /**
     * Find cache by land identifiers (regardless of user) - for analytics
     */
    @Query("SELECT c FROM SearchCacheEntity c WHERE c.khata = ?1 AND c.khesra = ?2 AND c.district = ?3 AND c.expiresAt > current_timestamp")
    SearchCacheEntity findValidByLandIdentifiers(String khata, String khesra, String district);
    
    /**
     * Find cache by land identifiers with circle
     */
    @Query("SELECT c FROM SearchCacheEntity c WHERE c.khata = ?1 AND c.khesra = ?2 AND c.district = ?3 AND c.circle = ?4 AND c.expiresAt > current_timestamp")
    SearchCacheEntity findValidByLandIdentifiersWithCircle(String khata, String khesra, String district, String circle);
    
    /**
     * Find all expired caches for cleanup
     */
    @Query("SELECT c FROM SearchCacheEntity c WHERE c.expiresAt < current_timestamp")
    List<SearchCacheEntity> findExpiredCaches();
    
    /**
     * Get top reused searches for analytics
     */
    @Query(value = "SELECT c FROM SearchCacheEntity c WHERE c.expiresAt > current_timestamp ORDER BY c.reusageCount DESC", nativeQuery = false)
    List<SearchCacheEntity> findTopReusesByExpiry();
    
    /**
     * Calculate total revenue from cache reusages
     */
    @Query("SELECT COALESCE(SUM(c.totalRevenueFromReusagePaise), 0) FROM SearchCacheEntity c")
    Long calculateTotalCacheRevenue();
}

