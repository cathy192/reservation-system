package com.learning.reservation.application.port.in;

import com.learning.reservation.application.dto.ReservationResponse;

import java.util.UUID;

public interface GetReservationUseCase {

    ReservationResponse execute(UUID reservationId);
}
