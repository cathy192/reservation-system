package com.learning.reservation.domain.event;

import com.learning.reservation.domain.model.MemberId;
import com.learning.reservation.domain.model.ReservationId;
import com.learning.reservation.domain.model.ResourceId;
import com.learning.reservation.domain.model.TimeSlot;
import com.learning.shared.kernel.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ReservationCreated(
        UUID eventId,
        Instant occurredAt,
        ReservationId reservationId,
        MemberId memberId,
        ResourceId resourceId,
        TimeSlot timeSlot
) implements DomainEvent {

    public ReservationCreated(ReservationId reservationId, MemberId memberId,
                              ResourceId resourceId, TimeSlot timeSlot) {
        this(UUID.randomUUID(), Instant.now(), reservationId, memberId, resourceId, timeSlot);
    }
}
