package com.learning.reservation.application.dto;

import com.learning.reservation.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        Long memberId,
        Long resourceId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId().value(),
                reservation.getMemberId().value(),
                reservation.getResourceId().value(),
                reservation.getTimeSlot().startTime(),
                reservation.getTimeSlot().endTime(),
                reservation.getStatus().name()
        );
    }
}
