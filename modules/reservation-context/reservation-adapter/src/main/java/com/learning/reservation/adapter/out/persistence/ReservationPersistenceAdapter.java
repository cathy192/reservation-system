package com.learning.reservation.adapter.out.persistence;

import com.learning.reservation.domain.model.*;
import com.learning.reservation.domain.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationPersistenceAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    public ReservationPersistenceAdapter(ReservationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity entity = ReservationJpaEntity.fromDomain(reservation);
        ReservationJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Reservation> findById(ReservationId id) {
        return jpaRepository.findById(id.value())
                .map(ReservationJpaEntity::toDomain);
    }

    @Override
    public List<Reservation> findByResourceIdAndTimeSlotOverlapping(ResourceId resourceId, TimeSlot timeSlot) {
        return jpaRepository.findOverlapping(
                        resourceId.value(),
                        timeSlot.startTime(),
                        timeSlot.endTime()
                ).stream()
                .map(ReservationJpaEntity::toDomain)
                .toList();
    }
}
