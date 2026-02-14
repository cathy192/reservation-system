package com.learning.reservation.domain.repository;

import com.learning.reservation.domain.model.Reservation;
import com.learning.reservation.domain.model.ReservationId;
import com.learning.reservation.domain.model.ResourceId;
import com.learning.reservation.domain.model.TimeSlot;

import java.util.List;
import java.util.Optional;

/**
 * Repository 인터페이스 (Port).
 * Domain 계층에 정의하고, Adapter 계층에서 구현한다.
 */
public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(ReservationId id);

    List<Reservation> findByResourceIdAndTimeSlotOverlapping(ResourceId resourceId, TimeSlot timeSlot);
}
