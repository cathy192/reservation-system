package com.learning.reservation.application.port.in;

import com.learning.reservation.application.dto.CreateReservationCommand;
import com.learning.reservation.application.dto.ReservationResponse;

public interface CreateReservationUseCase {

    ReservationResponse execute(CreateReservationCommand command);
}
