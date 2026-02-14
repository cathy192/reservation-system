package com.learning.reservation.application.port.in;

import java.util.UUID;

public interface CancelReservationUseCase {

    void execute(UUID reservationId);
}
