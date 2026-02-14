package com.learning.reservation.adapter.in.web;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        @NotNull Long memberId,
        @NotNull Long resourceId,
        @NotNull @Future LocalDateTime startTime,
        @NotNull @Future LocalDateTime endTime
) {
}
