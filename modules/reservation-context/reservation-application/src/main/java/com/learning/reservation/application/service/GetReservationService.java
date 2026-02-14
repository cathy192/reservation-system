package com.learning.reservation.application.service;

import com.learning.reservation.application.dto.ReservationResponse;
import com.learning.reservation.application.port.in.GetReservationUseCase;
import com.learning.reservation.domain.model.Reservation;
import com.learning.reservation.domain.model.ReservationId;
import com.learning.reservation.domain.repository.ReservationRepository;

import java.util.UUID;

public class GetReservationService implements GetReservationUseCase {

    private final ReservationRepository reservationRepository;

    public GetReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ReservationResponse execute(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(new ReservationId(reservationId))
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));

        return ReservationResponse.from(reservation);
    }
}
