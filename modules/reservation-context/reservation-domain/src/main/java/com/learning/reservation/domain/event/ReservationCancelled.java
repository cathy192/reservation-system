package com.learning.reservation.domain.event;

import com.learning.reservation.domain.model.ReservationId;
import com.learning.shared.kernel.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ReservationCancelled(
        UUID eventId,
        Instant occurredAt,
        ReservationId reservationId
) implements DomainEvent {

    public ReservationCancelled(ReservationId reservationId) {
        this(UUID.randomUUID(), Instant.now(), reservationId);
    }
}
