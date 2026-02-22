package com.learning.reservation.adapter.out.persistence;

import com.learning.reservation.domain.event.DomainEventPublisher;
import com.learning.reservation.domain.model.*;
import com.learning.reservation.domain.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationPersistenceAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;
    private final DomainEventPublisher eventPublisher;
    public ReservationPersistenceAdapter(ReservationJpaRepository jpaRepository, DomainEventPublisher eventPublisher) {
        this.jpaRepository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Reservation save(Reservation reservation) {
        // If entity already exists in the persistence context / DB, update that instance
        if (reservation.getId() != null && jpaRepository.existsById(reservation.getId().value())) {
            ReservationJpaEntity existing = jpaRepository.findById(reservation.getId().value()).orElseThrow();
            existing.updateFromDomain(reservation);
            ReservationJpaEntity saved = jpaRepository.save(existing);

            eventPublisher.publishAll(reservation.getDomainEvents());//이벤트 발생
            reservation.clearDomainEvents();//이이벤트 초기화
            return saved.toDomain();
        }

        ReservationJpaEntity entity = ReservationJpaEntity.fromDomain(reservation);
        ReservationJpaEntity saved = jpaRepository.save(entity);
            eventPublisher.publishAll(reservation.getDomainEvents());//이벤트 발생
            reservation.clearDomainEvents();//이이벤트 초기화

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
