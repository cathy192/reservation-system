package com.learning.reservation.application.service;

import com.learning.reservation.application.port.in.CancelReservationUseCase;
import com.learning.reservation.domain.model.Reservation;
import com.learning.reservation.domain.model.ReservationId;
import com.learning.reservation.domain.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class CancelReservationService implements CancelReservationUseCase {

    private final ReservationRepository reservationRepository;

    public CancelReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void execute(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(new ReservationId(reservationId))
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));

        reservation.cancel(LocalDateTime.now());
        reservationRepository.save(reservation);
    }
}
