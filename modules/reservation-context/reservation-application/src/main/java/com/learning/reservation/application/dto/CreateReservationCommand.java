package com.learning.reservation.application.dto;

import java.time.LocalDateTime;

public record CreateReservationCommand(
        Long memberId,
        Long resourceId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
