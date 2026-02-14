package com.learning.reservation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, UUID> {

    @Query("""
            SELECT r FROM ReservationJpaEntity r
            WHERE r.resourceId = :resourceId
              AND r.status <> 'CANCELLED'
              AND r.startTime < :endTime
              AND r.endTime > :startTime
            """)
    List<ReservationJpaEntity> findOverlapping(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
