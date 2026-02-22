package com.learning.reservation.adapter.out.persistence;

import com.learning.reservation.domain.model.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class ReservationJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Version
    private Long version;

    protected ReservationJpaEntity() {
    }

    // Domain → JPA Entity
    public static ReservationJpaEntity fromDomain(Reservation reservation) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.id = reservation.getId().value();
        entity.memberId = reservation.getMemberId().value();
        entity.resourceId = reservation.getResourceId().value();
        entity.startTime = reservation.getTimeSlot().startTime();
        entity.endTime = reservation.getTimeSlot().endTime();
        entity.status = reservation.getStatus();
        return entity;
    }

    // JPA Entity → Domain
    public Reservation toDomain() {
        return Reservation.reconstitute(
                new ReservationId(id),
                new MemberId(memberId),
                new ResourceId(resourceId),
                new TimeSlot(startTime, endTime),
                status
        );
    }

    public UUID getId() {
        return id;
    }

    // Update existing JPA entity fields from domain object
    public void updateFromDomain(Reservation reservation) {
        this.memberId = reservation.getMemberId().value();
        this.resourceId = reservation.getResourceId().value();
        this.startTime = reservation.getTimeSlot().startTime();
        this.endTime = reservation.getTimeSlot().endTime();
        this.status = reservation.getStatus();
    }
}
