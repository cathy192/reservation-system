package com.learning.reservation.domain.model;

import com.learning.shared.kernel.domain.ValueObject;

import java.util.UUID;

public record ReservationId(UUID value) implements ValueObject {

    public ReservationId {
        if (value == null) {
            throw new IllegalArgumentException("ReservationId는 null일 수 없습니다.");
        }
    }

    public static ReservationId generate() {
        return new ReservationId(UUID.randomUUID());
    }
}
